package ca.warp7.frc2014.hardware;

import ca.warp7.frc2014.util.RobotInfo;
import ca.warp7.robotlib.parents.HardwareBase;
import ca.warp7.robotlib.util.Util;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Talon;

public class Wing extends HardwareBase {
    //Pins
    private final String name;
    private final RobotInfo.InfoValue wristPin, rollerPin1, rollerPin2, wristEncoderPin;
    private Talon wrist, roller1, roller2;
    private AnalogChannel wristEncoder;
    private PIDController controller;
    private final RobotInfo.InfoValue P, I, D, zeroPoint;

    public Wing(String name,
                RobotInfo.InfoValue wristPin,
                RobotInfo.InfoValue rollerPin1,
                RobotInfo.InfoValue rollerPin2,
                RobotInfo.InfoValue wristEncoderPin,
                RobotInfo.InfoValue P,
                RobotInfo.InfoValue I,
                RobotInfo.InfoValue D,
                RobotInfo.InfoValue zeroPoint) {
        this.name = name;
        this.wristPin = wristPin;
        this.rollerPin1 = rollerPin1;
        this.rollerPin2 = rollerPin2;
        this.wristEncoderPin = wristEncoderPin;
        this.P = P;
        this.I = I;
        this.D = D;
        this.zeroPoint = zeroPoint;
        Util.log(this, "Constructed successfully");
    }

    public void init() {
        wrist = new Talon(wristPin.getInt());
        roller1 = new Talon(rollerPin1.getInt());
        roller2 = new Talon(rollerPin2.getInt());
        wristEncoder = new AnalogChannel(wristEncoderPin.getInt());
        controller = new PIDController(0, 0, 0, wristEncoder, wrist);
        controller.setInputRange(0.0, 970.0);
        controller.setOutputRange(-1.0, 1.0);
        controller.setContinuous(true);
        controller.setPercentTolerance(5);

        controller.setPID(P.getDouble(), I.getDouble(), D.getDouble());
        Util.log(this, "P: " + controller.getP() + "I: " + controller.getI() + "D: " + controller.getD());
    }

    public void free() {
        //probably not best practice, but it might work
        wrist.free();
        wrist = null;

        roller1.free();
        roller1 = null;

        roller2.free();
        roller2 = null;

        wristEncoder.free();
        wristEncoder = null;

        controller.free();
        controller = null;
    }

    public String getName() {
        return name;
    }

    public void disable() {
        setTargetAngle(0);
        controller.disable();
        wrist.set(0);
        roller1.set(0);
        roller2.set(0);

    }

    public void setTargetAngle(double angle) {

        if (!controller.isEnable()) {
            controller.enable();
        }
        while (angle < 0) {
            angle += 360;
        }

        angle /= 360;
        angle *= 970;
        double num = angle + zeroPoint.getDouble();

        num %= 970;

        controller.setSetpoint(num);
    }

    public void startRollersUp() {
        roller1.set(1.0);
        roller2.set(1.0);
    }

    public void startRollersDown() {
        roller1.set(-1.0);
        roller2.set(-1.0);
    }

    public void stopRollers() {
        roller1.set(0.0);
        roller2.set(0.0);
    }

    public double getWristPosition() {
        return wristEncoder.getAverageValue();
    }

    public boolean isAtSetpoint() {
        return controller.onTarget();
    }
}