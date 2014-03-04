package ca.warp7.frc2014.robot;// Time Created: 1/4/14 4:57 PM

import ca.warp7.frc2014.autonomous.DetectHotTarget;
import ca.warp7.frc2014.driverstation.DriverStation;
import ca.warp7.frc2014.modules.CheesyDrive;
import ca.warp7.frc2014.modules.Compressor;
import ca.warp7.frc2014.modules.Shifter;
import ca.warp7.frc2014.modules.WingController;
import ca.warp7.frc2014.util.RobotInfo;
import ca.warp7.frc2014.util.Util;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

    public ModuleController modules;
    public DriverStation ds;
    public HardwareController hw;
    private static Robot instance;

    public Robot() {
        instance = this;
    }


    public void robotInit() {
        hw = new HardwareController();
        ds = new DriverStation();
        modules = new ModuleController();

        modules.add(new CheesyDrive());
        modules.add(new Compressor());
        modules.add(new Shifter());
        modules.add(new WingController());

        getWatchdog().setEnabled(false);

        Util.log("Main", "Robot has booted, ready to go.");
    }

    public void autonomousInit() {
        ds.setMode("Autonomous");
        new DetectHotTarget().run();
    }

    public void autonomousPeriodic() {
        ds.sendSensorInfo();
    }

    public void teleopInit() {
        ds.setMode("Teleoperated");
        ds.loadModuleInfo();
        Util.log("Main", "Module Init");
        modules.loadModules();
        hw.load();
    }

    public void teleopPeriodic() {
        modules.runModulesPeriodic();
        ds.sendSensorInfo();
    }


    public void disabledInit() {
        Util.log("Main", "Disabled initializing.");
        ds.setMode("Disabled");
        Util.log("Main", "Loading InfoValues from file.");
        RobotInfo.readInfoFromFile();
        ds.loadModuleInfo();
    }

    public void disabledPeriodic() {
        ds.sendSensorInfo();
    }

    public void testInit() {
        // wat
    }

    public void testPeriodic() {
        ds.sendSensorInfo();
    }

    public static Robot getInstance() {
        return instance;
    }
}
