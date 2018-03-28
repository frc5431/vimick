package org.usfirst.frc.team5431.vimick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.usfirst.frc.team5431.vimick.Mimick.MimickException;
import org.usfirst.frc.team5431.vimick.Mimick.Stepper;

public class Transform {
	private double field_width, field_height;
	private List<Stepper> pathData = new ArrayList<>();
	
	public static class AbsoluteStep extends Stepper {
		public AbsoluteStep(String format, HashMap<String, Integer> argP, Double[] data) throws MimickException {
			super(format, argP, data);
		}

		public double x, y, yaw;
	}
	
	public Transform(final double width, final double height) {
		field_width = width;
		field_height = height;
	}
	
	public static void main(String args[]) {
		Transform t = new Transform(600, 200);
		try {
			t.parse("C:\\Users\\AcademyHSRobotics\\Downloads\\CENTER_RIGHT_SWITCH.mimic");
			
			t.toAbsolute();
			final List<Stepper> steps = t.getData();
			System.out.println("Total steps: " + steps.size());
			for(int ind = 0; ind < 1; ind++) {
				System.out.print(steps.get(ind).toString());
			}
		} catch(MimickException err) {
			err.printStackTrace();
		}
	}
	
	public void parse(final String fileName) throws MimickException {
		Mimick.Repeater repeat = new Mimick.Repeater(fileName);
		repeat.prepare();
		pathData = repeat.getData();
	}
	
	public void toAbsolute() throws MimickException {
		double left_pos = 0.0;
		double right_pos = 0.0;
		double yaw = 0.0;
		double c_x = 0.0;
		double c_y = 0.0;
		List<AbsoluteStep> newSteps = new ArrayList<>();
		for(int ind = 0; ind < pathData.size(); ind++) {
			Stepper step = pathData.get(ind);
			double diff_left = step.get("left_encoder") - left_pos;
			double diff_right = step.get("right_encoder") - right_pos;
			double diff_yaw = step.get("yaw") - yaw;
			System.out.println("DIFF " + diff_yaw);
			double hyp = (diff_left + diff_right) / 2.0;
			
			//@TODO DETECT TURNING
			
			AbsoluteStep aStep = new AbsoluteStep(step.formatString, step.argPair, (Double[]) step.data);
			aStep.x = (c_x + (Math.cos(Math.toRadians(diff_yaw)) * hyp)) / field_width;
			aStep.y = (c_y + (Math.sin(Math.toRadians(diff_yaw)) * hyp)) / field_height;
			aStep.yaw = diff_yaw;
			
			System.out.println("X " + aStep.x + " Y " + aStep.y);
			
			left_pos = step.get("left_encoder");
			right_pos = step.get("right_encoder");
			yaw = step.get("yaw");
		}
	}
	
	public List<Stepper> getData() {
		return pathData;
	}
}
