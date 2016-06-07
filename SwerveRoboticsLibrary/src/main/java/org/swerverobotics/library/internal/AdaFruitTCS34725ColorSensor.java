package org.swerverobotics.library.internal;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.*;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.exceptions.UnexpectedI2CDeviceException;
import org.swerverobotics.library.interfaces.Multiplexable;
import org.swerverobotics.library.interfaces.TCA9548A;
import org.swerverobotics.library.interfaces.TCS34725;

import java.nio.ByteBuffer;

import static org.swerverobotics.library.internal.Util.handleCapturedInterrupt;

/**
 * http://adafru.it/1334
 * https://www.adafruit.com/products/1334?&main_page=product_info&products_id=1334
 * https://github.com/adafruit/Adafruit_TCS34725
 */
public class AdaFruitTCS34725ColorSensor implements TCS34725, IOpModeStateTransitionEvents, Multiplexable
{
    private final OpMode opmodeContext;
    private final I2cMultiplexedDeviceSync deviceClient;

    private TCS34725.Parameters parameters;

    private static final I2cDeviceSynch.ReadMode readMode = I2cDeviceSynch.ReadMode.REPEAT;


    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public AdaFruitTCS34725ColorSensor(OpMode opmodeContext, I2cDevice i2cDevice, TCS34725.Parameters params) {
        this.opmodeContext = opmodeContext;

        this.deviceClient = ClassFactory.createI2cDeviceSynch(i2cDevice, params.i2cAddress.bVal * 2);

        this.engage();

        this.deviceClient.enableWriteCoalescing(false);

        this.deviceClient.setLogging(params.loggingEnabled);
        this.deviceClient.setLoggingTag(params.loggingTag);


        this.parameters = params;
    }

    public static TCS34725 create(OpMode opmodeContext, I2cDevice i2cDevice, TCS34725.Parameters parameters)
    {
        // Create a sensor which is a client of i2cDevice
        TCS34725 result = new AdaFruitTCS34725ColorSensor(opmodeContext, i2cDevice, parameters);

        // Initialize it with the indicated parameters
        result.initialize(parameters);
        return result;
    }

    public void initialize(Parameters parameters)
    {
        // Remember the parameters for future use
        this.parameters = parameters;


        boolean armed = this.deviceClient.isArmed();

        byte id = this.getDeviceID();

        if ( (id != ADAFRUIT_TCS34725_PARTNUM) )
        {
            throw new UnexpectedI2CDeviceException(id);
        }

        // Set the gain and integration time
        setIntegrationTime(parameters.integrationTime);
        setGain(parameters.gain);

        // Enable the device
        enable();
    }

    private synchronized void enable()
    {
        write8(REGISTER.ENABLE, TCS34725_ENABLE_PON );
        delayLore(6); //Adafruit's sample implementation uses 3ms; I'm adding an extra 3ms
        write8(REGISTER.ENABLE, TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN );
    }

    private synchronized void disable()
    {
        /* Turn the device off to save power */
        byte reg = read8(REGISTER.ENABLE);
        write8(REGISTER.ENABLE, reg & ~(TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN));
    }

    private synchronized void setIntegrationTime(INTEGRATION_TIME time)
    {
        write8(REGISTER.ATIME, time.byteVal);
    }

    private synchronized void setGain(GAIN gain)
    {
        write8(REGISTER.CONTROL, gain.byteVal);
    }

    public synchronized byte getState()
    {
        byte b = this.read8(REGISTER.ENABLE);
        return b;
    }

    public synchronized byte getDeviceID()
    {
        byte b = this.read8(REGISTER.DEVICE_ID);
        return b;
    }

    @Override
    public synchronized int red() {
        //try this alternative reading method to see if it give more correct results
        return this.readColorRegister(REGISTER.RED);
    }

    @Override
    public synchronized int green() {
        //try the same reading method that worked for the current sensor
        return this.readColorRegister(REGISTER.GREEN);
    }

    @Override
    public synchronized int blue() { return this.readColorRegister(REGISTER.BLUE); }

    @Override
    public synchronized int alpha() {
        return this.readColorRegister(REGISTER.CLEAR);
    }

    @Override
    public synchronized int argb() {
        return Color.argb(this.alpha(), this.red(), this.green(), this.blue());
    }

    @Override
    public synchronized void enableLed(boolean enable)
    // We can't directly control the LED with I2C; it's always on
    {
        /*
        if (!this.ledStateIsKnown || this.ledIsEnabled != enable) {
            if (enable) {
                this.ledIsEnabled = enable;
                this.ledStateIsKnown = true;
            } else
                throw new IllegalArgumentException("disabling LED is not supported");
        }
        */
        throw new IllegalArgumentException("controlling LED is not supported on the Adafruit color sensor; use a digital channel for that.");
    }

    @Override
    public synchronized int getI2cAddress() {
        return this.deviceClient.getI2cAddr();
    }

    @Override
    public synchronized void setI2cAddress(int i2cAddr8Bit) {
        this.deviceClient.setI2cAddr(i2cAddr8Bit);
    }


    private void engage() {
        this.deviceClient.engage();
    }

    private void disengage() {
        this.deviceClient.disengage();
    }

    //Implement Multiplexable
    public void attachToMultiplexer(TCA9548A mux, TCA9548A.MULTIPLEXER_CHANNEL channel)
    {
        deviceClient.attachToMultiplexer(mux, channel);
    }

    //----------------------------------------------------------------------------------------------
    // IOpModeStateTransitionEvents
    //----------------------------------------------------------------------------------------------

    @Override
    synchronized public boolean onUserOpModeStop() {
        this.disengage();
        return true;    // unregister us
    }

    @Override
    synchronized public boolean onRobotShutdown() {
        // We actually shouldn't be here by now, having received a onUserOpModeStop()
        // after which we should have been unregistered. But we close down anyway.
        this.close();
        return true;    // unregister us
    }

    //----------------------------------------------------------------------------------------------
    // HardwareDevice
    //----------------------------------------------------------------------------------------------

    @Override
    public void close() {
        this.deviceClient.close();
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getConnectionInfo() {
        return this.deviceClient.getConnectionInfo();
    }

    @Override
    public String getDeviceName() {
        return "Swerve AdaFruit I2C Color Sensor";
    }

    //----------------------------------------------------------------------------------------------
    // ColorSensor
    //----------------------------------------------------------------------------------------------

    @Override public synchronized byte read8(final REGISTER reg) {
        return deviceClient.read8(reg.byteVal | TCS34725_COMMAND_BIT);
    }

    @Override public synchronized byte[] read(final REGISTER reg, final int cb) {
        return deviceClient.read(reg.byteVal | TCS34725_COMMAND_BIT, cb);
    }

    @Override public void write8(REGISTER reg, int data) {
        this.deviceClient.write8(reg.byteVal | TCS34725_COMMAND_BIT, data);
        this.deviceClient.waitForWriteCompletions();
    }

    @Override public void write(REGISTER reg, byte[] data) {
        this.deviceClient.write(reg.byteVal | TCS34725_COMMAND_BIT, data);
        this.deviceClient.waitForWriteCompletions();
    }

    public int readColorRegister(TCS34725.REGISTER reg) {
        byte[] bytes = this.read(reg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            //colors are two bytes, unsigned
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            //make unsigned by and'ing with 0x0000FFFF
            //this will cause Java to treat the resulting value as an unsigned int, rather than as a signed short
            //because the most significant bit won't be 1 (which denotes "is negative" in 2's complement numbers)
            result = buffer.getShort() & 0xFFFF;
        }

        return result;
    }


    public int readTwoByteRegister(TCS34725.REGISTER reg) {
        byte[] bytes = this.read(reg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            //read a two-byte signed number
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            result = buffer.getShort();
        }

        return result;
    }

    @Override public void writeTwoByteRegister(TCS34725.REGISTER ireg, int value)
    {
        byte[] b = new byte[2];
        b[0] = (byte) ((value & 0x0000FF00) >> 8); //most significant byte
        b[1] = (byte)  (value & 0x000000FF); //least significant byte
        this.write(ireg, b);
    }


    void delayExtra(int ms) {
        delay(ms + 10);
    }

    /**
     * delayLore() implements a delay that is specified in the device datasheet and therefore should be correct
     *
     * @see #delay(int)
     */
    void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            handleCapturedInterrupt(e);
        }
    }

    /**
     * delayLore() implements a delay that only known by lore and mythology to be necessary.
     *
     * @see #delay(int)
     */
    private void delayLore(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            handleCapturedInterrupt(e);
        }
    }


}

/*
  some code borrowed from Adafruit's sample implementation at:
  https://github.com/adafruit/Adafruit_TCS34725
 */

