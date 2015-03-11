package org.usfirst.frc.team4711.jarvis;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Forklift {

	Talon forkliftMotorA, forkliftMotorB;
	DigitalInput limitSwitchup, limitSwitchdwn;
	
	public Forklift(int aMotorPort, int bMotorPort, int limitUpPort, int limitDownPort) {
		forkliftMotorA = new Talon(aMotorPort);
		forkliftMotorB = new Talon(bMotorPort);
		
		limitSwitchup = new DigitalInput(limitUpPort);
		limitSwitchdwn = new DigitalInput(limitDownPort);
	}
	
	public void setSpeed(double liftSpeed) {
		boolean up = !limitSwitchup.get();
		boolean down = !limitSwitchdwn.get();

		SmartDashboard.putBoolean("limit up", up);
		SmartDashboard.putBoolean("limit down", down);

		// don't move if the input is small or if we are trying to go up past
		// the limit switch
		if (Math.abs(liftSpeed) < .1) {
			liftSpeed = 0;
		} else {
			// check for top and bottom limits
			if (up && liftSpeed > 0) {
				liftSpeed = 0;
			} else if (down && liftSpeed < 0) {
				liftSpeed = 0;
			}
		}

		forkliftMotorA.set(liftSpeed);
		forkliftMotorB.set(-liftSpeed);
	}
	
}
