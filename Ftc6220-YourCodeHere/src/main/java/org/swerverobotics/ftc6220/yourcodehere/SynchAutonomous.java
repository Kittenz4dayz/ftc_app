package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Everything", group = "Swerve Examples")

public class SynchAutonomous extends MasterAutonomous
{
    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        HikerDropper.halfDeploy();

        if (autoWaitAtStart)
        {
            pause(14000);
        }
        else
        {
            pause(100);
        }

        //Red 1 NoRamp
        if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.RED == autoSide) && (Constants.NO_RAMP))
        {
            setAutoStartPosition(90);

            driveStraight(272, Constants.BACKWARDS, false);
            turnTo(135);
            driveStraight(70, Constants.BACKWARDS, false);
            pause(100);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(70, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(70, Constants.BACKWARDS * 0.4, false);
        }

        //Red 1 Ramp
        if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.RED == autoSide) && (Constants.RAMP))
        {
            setAutoStartPosition(90);

            driveStraight(272, Constants.BACKWARDS, false);
            turnTo(135);
            driveStraight(70, Constants.BACKWARDS, false);
            pause(100);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(25, Constants.FORWARDS, false);
            HikerDropper.slowToggle();
            turnTo(90);
            pause(400);
            driveStraight(75, Constants.FORWARDS, false);
            turnTo(0);
            pause(600);
            driveStraight(72, Constants.BACKWARDS, false);
            turnTo(270);
            pause(600);
            driveStraight(220, Constants.BACKWARDS, false);
        }
        //Red 2 NoRamp
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.RED == autoSide) && (Constants.NO_RAMP))
        {
            setAutoStartPosition(135);

            driveStraight(110, Constants.BACKWARDS, false);
            turnTo(180);
            driveStraight(88, Constants.BACKWARDS, false);
            turnTo(225);
            driveStraight(62, Constants.BACKWARDS, false);
            pause(400);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(25, Constants.FORWARDS, false);
            HikerDropper.slowToggle();
        }
        //Red 2 Ramp
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.RED == autoSide) && (Constants.RAMP))
        {
            setAutoStartPosition(135);

            driveStraight(110, Constants.BACKWARDS, false);
            turnTo(180);
            driveStraight(88, Constants.BACKWARDS, false);
            turnTo(225);
            driveStraight(62, Constants.BACKWARDS, false);
            pause(400);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(25, Constants.FORWARDS, false);
            HikerDropper.slowToggle();
            turnTo(90);
            pause(400);
            driveStraight(75, Constants.FORWARDS, false);
            turnTo(0);
            pause(600);
            driveStraight(72, Constants.BACKWARDS, false);
            turnTo(270);
            pause(600);
            driveStraight(220, Constants.BACKWARDS, false);
        }
        //Blue 1 NoRamp
        else if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.BLUE == autoSide) && (Constants.NO_RAMP))
        {
            setAutoStartPosition(90);

            driveStraight(260, Constants.BACKWARDS, false);
            turnTo(45);
            driveStraight(70, Constants.BACKWARDS, false);
            pause(100);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(70, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(70, Constants.BACKWARDS * 0.4, false);
        }
        //Blue 1 Ramp
        else if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.BLUE == autoSide) && (Constants.RAMP))
        {
            setAutoStartPosition(90);

            driveStraight(260, Constants.BACKWARDS, false);
            turnTo(45);
            driveStraight(70, Constants.BACKWARDS, false);
            pause(100);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(25, Constants.FORWARDS, false);
            HikerDropper.slowToggle();
            turnTo(90);
            pause(400);
            driveStraight(75, Constants.FORWARDS, false);
            turnTo(180);
            pause(600);
            driveStraight(72, Constants.BACKWARDS, false);
            turnTo(270);
            pause(600);
            driveStraight(220, Constants.BACKWARDS, false);
        }
        //Blue 2 NoRamp
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.BLUE == autoSide) && (Constants.NO_RAMP)) {
            setAutoStartPosition(45);

            driveStraight(110, Constants.BACKWARDS, false);
            turnTo(0);
            driveStraight(88, Constants.BACKWARDS, false);
            turnTo(315);
            driveStraight(62, Constants.BACKWARDS, false);
            pause(400);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(25, Constants.FORWARDS, false);
            HikerDropper.slowToggle();
        }
        //Blue 2 Ramp
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.BLUE == autoSide) && (Constants.RAMP))
        {
            setAutoStartPosition(45);

            driveStraight(110, Constants.BACKWARDS, false);
            turnTo(0);
            driveStraight(88, Constants.BACKWARDS, false);
            turnTo(315);
            driveStraight(62, Constants.BACKWARDS, false);
            pause(400);
            HikerDropper.slowToggle();
            pause(800);
            driveStraight(25, Constants.FORWARDS, false);
            HikerDropper.slowToggle();
            turnTo(90);
            pause(400);
            driveStraight(75, Constants.FORWARDS, false);
            turnTo(180);
            pause(600);
            driveStraight(72, Constants.BACKWARDS, false);
            turnTo(270);
            pause(600);
            driveStraight(220, Constants.BACKWARDS, false);
        }
    }
}
