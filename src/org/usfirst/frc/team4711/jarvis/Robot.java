package org.usfirst.frc.team4711.jarvis;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Joystick xbox;

	CameraServer server;

	RobotDrive drive;
	Forklift forklift;

	Timer timer;

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
		if (xbox.getRawButton(4)) {
			timer.reset();
			timer.start();
		} else if (timer.get() > 2) {
			liftSpeed = .75;
		}

		forklift.setSpeed(liftSpeed);

	}

	@Override
	public void disabledInit() {
		forklift.setSpeed(0);
	}

}
