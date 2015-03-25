package org.usfirst.frc.team4711.jarvis.util;

import edu.wpi.first.wpilibj.Joystick;

public class Button {

	private Joystick stick;
	private int btn;
	
	private boolean last;

	public Button(Joystick stick, int btn) {
		this.stick = stick;
		this.btn = btn;
		reset();
	}
	
	public boolean getPress() {
		boolean state = stick.getRawButton(btn);
		boolean ret = state && !last;
		
		last = state;
		return ret;
	}
	
	public void reset() {
		last = false;
	}
	
}
