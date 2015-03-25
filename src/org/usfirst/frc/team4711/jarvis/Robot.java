package org.usfirst.frc.team4711.jarvis;


import org.usfirst.frc.team4711.jarvis.util.Button;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	private static final double FIRST_TARGET = 0,
			TOLERANCE = .05,
			POSITIONING_SPEED = .5;
	
	
	Joystick xbox;

	CameraServer server;

	RobotDrive drive;
	Forklift forklift;

	Timer timer;
	
	AnalogPotentiometer logPoten;
	Button gotoPositionButton;
	boolean isPositioning = false;
	double targetPosition;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		System.out.println("====> Jarvis <====");
		   
		
		xbox = new Joystick(0);

		drive = new RobotDrive(2, 3, 0, 1);
		drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
		drive.setMaxOutput(.45);

		forklift = new Forklift(5, 6, 0, 1);
		
		timer = new Timer();

		logPoten = new AnalogPotentiometer(0);
		gotoPositionButton = new Button(xbox, 1);
		
		server = CameraServer.getInstance();
		server.setQuality(50);
		// the camera name (ex "cam0") can be found through the roborio web
		// interface
		server.startAutomaticCapture("cam0");
	}

	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		double time = timer.get();

		// forklift (forklift time upped from 2 to 2.5 on 3/8/15)
		if (time < 2) {
			forklift.setSpeed(1);
		} else if (time < 12) {
			forklift.setSpeed(0);
			// } else {
			// forklift.setSpeed(-.5);
		}

		// drive (time changed from 7 to 6.5 on 3/8/15)
		if (time > 1.5 && time < 6.5) {
			drive.arcadeDrive(.75, 0);
		} else {
			drive.arcadeDrive(0, 0);
		}
	}

	@Override
	public void teleopInit() {
		gotoPositionButton.reset();
		isPositioning = false;
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		// drive the robot

		double fwd = xbox.getRawAxis(1); // left y axis
		double turn = xbox.getRawAxis(0); // left x axis

		drive.arcadeDrive(fwd, turn, true);

		// control the forklift

		// double liftSpeed = xbox.getRawAxis(2) - xbox.getRawAxis(3); // using
		// triggers
		double liftSpeed = -xbox.getRawAxis(5); // using right y axis (inverted
												// on controller)

		// Forklift button control (added 3/10/15)

		// get input from potentiometer
		double currentPosition = logPoten.get();
		SmartDashboard.putNumber("pot", currentPosition);
		
		// check for input
		if (gotoPositionButton.getPress()) {
			targetPosition = FIRST_TARGET;
			isPositioning = true;
		}
		
		if (isPositioning) {
			
			// compare to target angle
			if (Math.abs(targetPosition - currentPosition) < TOLERANCE) {
				isPositioning = false;
			} else {
				if (targetPosition > currentPosition) {
					liftSpeed = POSITIONING_SPEED;
				} else {
					liftSpeed = -POSITIONING_SPEED;
				}
			}
		}
		
		// send output to motors
		forklift.setSpeed(liftSpeed);

	}

	@Override
	public void disabledInit() {
		forklift.setSpeed(0);
	}

}
