package org.usfirst.frc.team4711.jarvis;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
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

	Joystick xbox;

	CameraServer server;

	RobotDrive drive;
	Talon forkliftMotorA, forkliftMotorB;

	DigitalInput limitSwitchup;
	DigitalInput limitSwitchdwn;

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

		forkliftMotorA = new Talon(5);
		forkliftMotorB = new Talon(6);

		limitSwitchup = new DigitalInput(0);
		limitSwitchdwn = new DigitalInput(1);

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
			forkliftMotorA.set(1);
			forkliftMotorB.set(-1);
		} else if (time < 12) {
			forkliftMotorA.set(0);
			forkliftMotorB.set(0);
			// } else {
			// forkliftMotorA.set(-.5);
			// forkliftMotorB.set(.5);
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

		boolean up = !limitSwitchup.get();
		boolean down = !limitSwitchdwn.get();

		SmartDashboard.putBoolean("limit up", up);
		SmartDashboard.putBoolean("limit down", down);

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

	@Override
	public void disabledInit() {
		forkliftMotorA.set(0);
		forkliftMotorB.set(0);
	}

}
