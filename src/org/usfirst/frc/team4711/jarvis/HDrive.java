package org.usfirst.frc.team4711.jarvis;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

public class HDrive {

	Talon leftMotor, rightMotor, centerMotor;
	
	RobotDrive outsideWheels;
	
	public HDrive(int leftPort, int rightPort, int centerPort) {
		leftMotor = new Talon(leftPort);
		rightMotor = new Talon(rightPort);
		outsideWheels = new RobotDrive(leftMotor, rightMotor);
		
		centerMotor = new Talon(centerPort);
	}

	public void drive(double fwd, double strafe, double turn) {
		outsideWheels.arcadeDrive(fwd, turn);
		centerMotor.set(strafe);
	}
	
}
