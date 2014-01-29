package ca.warp7.frc2014.software;

import ca.warp7.frc2014.hardware.Hardware;
import ca.warp7.frc2014.util.RobotInfo;
import ca.warp7.frc2014.util.Util;

/**
 * Created with IntelliJ IDEA.
 * User: Marcus is dumb
 * Date: 1/29/14
 * Time: 3:22 PM
 */
public class CheesyDrive extends Subsystem {
    private double oldWheel = 0.0;
    private double quickStopAccumulator;

    public CheesyDrive() {

    }

    public void tick() { // Driving Method
        double wheelNonLinearity, wheel, throttle;

        boolean isQuickTurn = Hardware.controller.getPrimaryAction();
        double wheelDeadband = 0.02;
        wheel = handleDeadband(Hardware.controller.getSecondaryX(), wheelDeadband);
        double throttleDeadband = 0.02;
        throttle = handleDeadband(Hardware.controller.getPrimaryY(), throttleDeadband);


        double negInertia = wheel - oldWheel;
        oldWheel = wheel;

        // Low gear

        wheelNonLinearity = 0.5;

        // Apply a sin function that's scaled to make it feel better.
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
                / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
                / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
                / Math.sin(Math.PI / 2.0 * wheelNonLinearity);

        double leftPwm, rightPwm, overPower;
        double sensitivity = 1.7;

        double angularPower;
        double linearPower;

        // Negative inertia!
        double negInertiaAccumulator = 0.0;
        double negInertiaScalar;

        if (wheel * negInertia > 0)
            negInertiaScalar = 2.5;
        else if (Math.abs(wheel) > 0.65)
            negInertiaScalar = 5.0;
        else
            negInertiaScalar = 3.0;

        sensitivity = RobotInfo.cheesyMod.getDouble();

        double negInertiaPower = negInertia * negInertiaScalar;
        negInertiaAccumulator += negInertiaPower;

        wheel = wheel + negInertiaAccumulator;
        if (negInertiaAccumulator > 1)
            negInertiaAccumulator -= 1;
        else if (negInertiaAccumulator < -1)
            negInertiaAccumulator += 1;
        else
            negInertiaAccumulator = 0;
        linearPower = throttle;

        // Quickturn!
        if (isQuickTurn) {
            if (Math.abs(linearPower) < 0.2) {
                double alpha = 0.1;
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator
                        + alpha * Util.limit(wheel, 1.0) * 5;
            }
            overPower = 1.0;
            sensitivity = 1.0;
            angularPower = wheel;
        } else {
            overPower = 0.0;
            angularPower = Math.abs(throttle) * wheel * sensitivity
                    - quickStopAccumulator;
            if (quickStopAccumulator > 1)
                quickStopAccumulator -= 1;
            else if (quickStopAccumulator < -1)
                quickStopAccumulator += 1;
            else
                quickStopAccumulator = 0.0;
        }

        rightPwm = leftPwm = linearPower;
        leftPwm += angularPower;
        rightPwm -= angularPower;

        if (leftPwm > 1.0) {
            rightPwm -= overPower * (leftPwm - 1.0);
            leftPwm = 1.0;
        } else if (rightPwm > 1.0) {
            leftPwm -= overPower * (rightPwm - 1.0);
            rightPwm = 1.0;
        } else if (leftPwm < -1.0) {
            rightPwm += overPower * (-1.0 - leftPwm);
            leftPwm = -1.0;
        } else if (rightPwm < -1.0) {
            leftPwm += overPower * (-1.0 - rightPwm);
            rightPwm = -1.0;
        }

        Hardware.leftDrive.set(leftPwm);
        Hardware.rightDrive.set(rightPwm);
    }

    double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }
}