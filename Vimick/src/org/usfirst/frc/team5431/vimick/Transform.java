package org.usfirst.frc.team5431.vimick;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.usfirst.frc.team5431.vimick.Mimick.MimickException;
import org.usfirst.frc.team5431.vimick.Mimick.Stepper;

public class Transform {
	private double field_width, field_height, position_offset_x, position_offset_y;
	private List<Stepper> pathData = new ArrayList<>();
	private List<String> arguments = new ArrayList<>();

	public enum StepMode {
		LEFT_ENCODER, RIGHT_ENCODER, BOTH_ENCODERS
	}

	private StepMode mode = StepMode.LEFT_ENCODER;

	public static class AbsoluteStep extends Stepper {
		public AbsoluteStep(final String format, final HashMap<String, Integer> argP, final Double[] data)
				throws MimickException {
			super(format, argP, data);
		}

		public double x, y, yaw;
	}

	public Transform(final double width, final double height, final double position_ox, final double position_oy) {
		field_width = width;
		field_height = height;
		position_offset_x = position_ox;
		position_offset_y = position_oy;
	}

	public static void main(String args[]) {
		Transform t = new Transform(324, 312, 160, 20);
		try {
			t.parse("C:\\Users\\AcademyHSRobotics\\Downloads\\LEFT_LEFT_SWITCH.mimic");
			t.toAbsolute();
			final List<Stepper> steps = t.getData();
			System.out.println("Total steps: " + steps.size());
			for (int ind = 0; ind < 1; ind++) {
				System.out.print(steps.get(ind).toString());
			}
		} catch (MimickException err) {
			err.printStackTrace();
		}
	}

	public void parse(final String fileName) throws MimickException {
		Mimick.Repeater repeat = new Mimick.Repeater(fileName);
		repeat.prepare();
		arguments = repeat.getArguments(); // Load the same arguments from the repeater
		pathData = repeat.getData();
	}

	public void toRelative(final List<AbsoluteStep> aSteps) throws MimickException {
		double left_pos = 0.0;
		double right_pos = 0.0;
		double c_x = position_offset_x / field_width;
		// double c_y = position_offset_y / field_height;
		double c_a = 0.0;
		double last_homed = 0.0;
		final List<Stepper> newSteps = new ArrayList<>();
		for (int ind = 0; ind < aSteps.size(); ind++) {
			final AbsoluteStep aStep = aSteps.get(ind);
			final Stepper step = new Stepper(aStep.formatString, aStep.argPair, (Double[]) aStep.data);

			// final AbsoluteStep aStep = new AbsoluteStep(step.formatString, step.argPair,
			// (Double[]) step.data);
			// aStep.yaw = c_a + step.get("yaw");

			left_pos = step.get("left_encoder");
			right_pos = step.get("right_encoder");
			last_homed = aStep.yaw;
			c_x = aStep.x;
			// c_y = aStep.y;

			if (step.is("home")) {
				c_a -= last_homed;
			}

			switch (mode) {
			case LEFT_ENCODER:
			case RIGHT_ENCODER: {
				// double hyp = aStep.get((mode == StepMode.LEFT_ENCODER) ? "left_encoder" :
				// "right_encoder");
				// hyp += (mode == StepMode.LEFT_ENCODER) ? left_pos : right_pos;
				double hyp = (aStep.yaw != 0.0) ? (field_width * (aStep.x - c_x)) / Math.sin(Math.toRadians(aStep.yaw))
						: 0.0;
				hyp += (mode == StepMode.LEFT_ENCODER) ? left_pos : right_pos;
				step.set((mode == StepMode.LEFT_ENCODER) ? "left_encoder" : "right_encoder", hyp);
			}
				break;
			case BOTH_ENCODERS: {
				throw new MimickException("Currently both encoders is not implemented!");
			}
			}

			step.set("yaw", aStep.yaw - c_a);

			// @TODO CALCULATE THE ARC OF BOTH WHEELS
			// BY GETTING THE OFFSET OF THE LEFT ENCODER AND THE OFFSET OF THE RIGHT ENCODER
			// @TODO DETECT TURNING

			newSteps.add(aStep);

			// System.out.format("X %.4f Y %.4f THETA %.4f\n", aStep.x, aStep.y, aStep.yaw);
		}
		// return newSteps;
	}

	/*
	 * public double getDiffEncoder(final Stepper step, final String key) {
	 * 
	 * }
	 */

	public List<AbsoluteStep> toAbsolute() throws MimickException {
		double left_pos = 0.0;
		double right_pos = 0.0;
		double c_x = position_offset_x / field_width;
		double c_y = position_offset_y / field_height;
		double c_a = 0.0;
		double last_homed = 0.0;
		boolean homed = false;
		final List<AbsoluteStep> newSteps = new ArrayList<>();
		for (int ind = 0; ind < pathData.size(); ind++) {
			Stepper step = pathData.get(ind);
			final AbsoluteStep aStep = new AbsoluteStep(step.formatString, step.argPair, (Double[]) step.data);
			aStep.yaw = c_a + step.get("yaw");

			switch (mode) {
			case LEFT_ENCODER:
			case RIGHT_ENCODER: {
				double hyp = step.get((mode == StepMode.LEFT_ENCODER) ? "left_encoder" : "right_encoder");
				hyp -= (mode == StepMode.LEFT_ENCODER) ? left_pos : right_pos;
				aStep.x = c_x + (((Math.sin(Math.toRadians(aStep.yaw)) * hyp)) / field_width);
				aStep.y = c_y + (((Math.cos(Math.toRadians(aStep.yaw)) * hyp)) / field_height);
			}
				break;
			case BOTH_ENCODERS: {
				throw new MimickException("Currently both encoders is not implemented!");
			}
			}

			// @TODO CALCULATE THE ARC OF BOTH WHEELS
			// BY GETTING THE OFFSET OF THE LEFT ENCODER AND THE OFFSET OF THE RIGHT ENCODER
			// @TODO DETECT TURNING

			if (step.is("home") && !homed) {
				c_a += last_homed;
			} else if (!step.is("home") && !homed) {
				last_homed = aStep.yaw;
			}

			newSteps.add(aStep);

			// System.out.format("X %.4f Y %.4f THETA %.4f\n", aStep.x, aStep.y, aStep.yaw);

			left_pos = step.get("left_encoder");
			right_pos = step.get("right_encoder");
			c_x = aStep.x;
			c_y = aStep.y;
			// last_homed = aStep.yaw;
			homed = step.is("home");
		}
		return newSteps;
	}

	public List<Node> toNodes(final List<AbsoluteStep> abs) {
		final List<Node> nodes = new ArrayList<>();
		for (final AbsoluteStep step : abs) {
			final Node node = new Node(step.x, 1.0 - step.y);
			for (final String key : step.argPair.keySet()) {
				node.getProperties().put(key, step.get(key));
			}
			nodes.add(node);
		}
		return nodes;
	}

	public List<AbsoluteStep> toSteps(final List<NodeDisplay> nDisp) throws MimickException {
		final List<AbsoluteStep> steps = new ArrayList<>();
		if (pathData.size() == 0)
			throw new MimickException("There must be at least one node to convert");
		final Stepper format = pathData.get(0);
		for (final NodeDisplay node : nDisp) {
			final Set<String> keySet = node.getNode().getProperties().keySet();
			final Double[] data = new Double[keySet.size()];
			for (final String key : keySet) {
				if (!(key.equals("X_POS") || key.equals("Y_POS"))) {
					data[format.argPair.get(key)] = node.getNode().getProperties().get(key);
				}
			}
			final AbsoluteStep step = new AbsoluteStep(format.formatString, format.argPair, data);
			step.x = node.getNode().getX();
			step.y = node.getNode().getY();
			steps.add(step);
		}
		return steps;
	}

	public List<Stepper> getData() {
		return pathData;
	}

	public void save(final File fileObj) throws MimickException {
		save(fileObj.getAbsolutePath());
	}

	public void save(final String fileName) throws MimickException {
		Mimick.Observer observe = new Mimick.Observer(fileName);
		observe.addArguments((String[]) arguments.toArray());
		observe.prepare();
		observe.addData(pathData);
		observe.save();
	}
}
