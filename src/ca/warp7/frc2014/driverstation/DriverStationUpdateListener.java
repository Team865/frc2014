package ca.warp7.frc2014.driverstation;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class DriverStationUpdateListener implements ITableListener {
    public void valueChanged(ITable src, String key, Object value, boolean isNew) {
        /*
        if(key == "gear") {
            Robot.getInstance().hw.drive.shift(((Boolean) value).booleanValue());
        }
        */
    }
}
