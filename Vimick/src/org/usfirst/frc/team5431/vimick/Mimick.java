package org.usfirst.frc.team5431.vimick;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

public class Mimick {
	public static final String sep = ",";
	public static final double version = 1.0;
	public static final String header = "Mimick created by Team5431";
	public static final String headerFormat = "%s\nVersion: %f\nArguments: %s\n";
	public static volatile String formatString = "\n";
	public static final List<String> arguments = new ArrayList<>();
	public static final HashMap<String, Integer> argPair = new HashMap<>();

	public static void addArguments(String ...args) {
		arguments.addAll(Arrays.asList(args));
	}

	public static void build() {
		StringBuilder b = new StringBuilder();
		for(int ind = 0; ind < arguments.size(); ind++) {
			argPair.put(arguments.get(ind), ind);
			b.append("%.4f");
			b.append(sep);
		}
		b.setLength(b.length() - 1);
		b.append("\n");
		formatString = b.toString();
	}

	public static String buildHeader() {
		StringBuilder b = new StringBuilder();
		for(String arg : arguments) {
			b.append(arg);
			b.append(sep);
		}
		b.setLength(b.length() - 1);
		return String.format(headerFormat, header, version, b.toString()); 
	}

	public static Header parseHeader(final String toParse) {

	}

	public static class Header {
		public String Description;
		public String[] Arguments;
		public double Version
	}

	public static class Pair {
		public static <T, U> Map.Entry<T, U> of(T first, U second) {
			return new AbstractMap.SimpleEntry<>(first, second);
		}
	}
	
	public static class Stepper {
		private double data[];

		public Stepper(double ...args) throws Exception {
			if(args.length != arguments.size()) {
				throw new Exception("Arguments mismatch");
			}
			data = args;
		}

		public Stepper(final String parse) throws Exception {	
			final String parts[] = parse.split(sep);
			if(parts.length != arguments.size()) {
				throw new Exception("Arguments mismatch");
			}
			data = new double[parts.length];
			for(int ind = 0; ind < data.length; ind++) {
				data[ind] = Double.valueOf(parts[ind]);
			}
		}

		public double get(String key) {
			return data[argPair.get(key)];
		}

		public boolean is(String key) {
			return get(key) != 0;
		}

		public int round(String key) {
			return ((Double) get(key)).intValue();
		}

		public String toString() {
			return String.format(formatString, data);
		}
	}

	/*
	public static final String mimicFile = "/media/sda1/%s.mimic";
	public static final String formatString = "%.2f,%.2f,%.2f,%.4f,%.4f,%d,%.2f,%.2f,%.2f\n"; //LEFT ENCODER, RIGHT ENCODER, GYRO ANGLE, LEFT POWER, RIGHT POWER, HOME, ELEVATOR_HEIGHT, INTAKE_TILT, INTAKE_SPEED

	public static class Stepper {
		public double leftDistance, rightDistance, angle, leftPower, rightPower, elevatorHeight, intakeTilt, intakeSpeed;
		public boolean isHome;
		
		public Stepper(final double lD, final double rD, final double a, final double lP, final double rP, final boolean h, final double eH, final double iT, final double iS) {
			leftDistance = lD;
			rightDistance = rD;
			angle = a;
			leftPower = lP;
			rightPower = rP;
			isHome = h;
			elevatorHeight = eH;
			intakeTilt = iT;
			intakeSpeed = iS;
		}
		
		public Stepper(final String toParse) {
			try {
				final String parts[] = toParse.split(",");
				leftDistance = getDouble(parts[0]);
				rightDistance = getDouble(parts[1]);
				angle = getDouble(parts[2]);
				leftPower = getDouble(parts[3]);
				rightPower = getDouble(parts[4]);
				isHome = getBoolean(parts[5]); 
				elevatorHeight = getDouble(parts[6]);
				intakeTilt = getDouble(parts[7]);
				intakeSpeed = getDouble(parts[8]);
			} catch (Exception e) {
				Titan.ee("MimicParse", e);
			}
		}
		
		private static final double getDouble(final String data) {
			return getDouble(data, 0.0);
		}
		
		private static final double getDouble(final String data, final double defaultValue) {
			try {
				return Double.parseDouble(data);
			} catch (Throwable e) {
				return defaultValue;
			}
		}
		
		private static final boolean getBoolean(final String data) {
			return getBoolean(data, false);
		}
		
		private static final boolean getBoolean(final String data, final boolean defaultValue) {
			try {
				return Integer.parseInt(data) == 1;
			} catch (Throwable e) {
				return defaultValue;
			}
		}
		
		public String toString() {
			return String.format(formatString, leftDistance, rightDistance, angle, leftPower, rightPower, (isHome) ? 1 : 0, elevatorHeight, intakeTilt, intakeSpeed);
		}
	}*/
	
	public static class Observer {
		private static FileOutputStream log = null;
		private static boolean homed = false;
		private static boolean saved = true;
		
		public static void prepare(final String fileName) {
			final String fName = String.format(mimicFile, fileName);
			try {
				if(Files.deleteIfExists(new File(fName).toPath())) {
					Titan.e("Deleted previous pathfinding data");
				}
				log = new FileOutputStream(fName);
				saved = false;
				Titan.l("Created new pathfinding file");
			} catch (IOException e) {
				Titan.ee("Mimic", e);
			}
		}
		
		public static void addStep(final Robot robot, final double driveVals[]) {
			try {
				final double lDistance = robot.getDriveBase().getLeftDistance();
				final double rDistance = robot.getDriveBase().getRightDistance();
				final float angle = robot.getDriveBase().getNavx().getYaw();
				final double leftPower = driveVals[0];
				final double rightPower = driveVals[1];
				boolean home = robot.getTeleop().getLogitech().getRawButton(Titan.LogitechExtreme3D.Button.FIVE);
				final double elevatorHeight = robot.getElevator().getUpPos();
				final double intakeTilt = robot.getIntake().getTiltPosition();
				double intakeSpeed = Constants.INTAKE_STOPPED_SPEED;
				if(robot.getTeleop().getLogitech().getRawButton(Titan.LogitechExtreme3D.Button.TRIGGER)) {
					intakeSpeed = Constants.OUTTAKE_SPEED;
				} else if(robot.getTeleop().getLogitech().getRawButton(Titan.LogitechExtreme3D.Button.THREE)) {
					intakeSpeed = Constants.INTAKE_SPEED;
				}
				
				if(home && !homed) {
					robot.getDriveBase().setHome();
				}
				if(!saved) log.write(new Stepper(lDistance, rDistance, angle, leftPower, rightPower, home, elevatorHeight, intakeTilt, intakeSpeed).toString().getBytes(StandardCharsets.US_ASCII));
				homed = home;
			} catch (Exception e) {
				Titan.ee("Mimic", e);
			}
		}
		
		public static void saveMimic() {
			try {
				if(log == null || saved) return;
				Titan.l("Finished observing");
				log.flush();
				log.close();
				saved = true;
				Titan.l("Saved the mimic data");
			} catch (IOException e) {
				Titan.ee("Mimic", e);
			}
		}
	}
	
	public static class Repeater {
		private static FileInputStream log = null;
		private static BufferedReader reader = null;
		private static final ArrayList<Stepper> pathData = new ArrayList<Stepper>();
		
		public static void prepare(final String fileName) {
			final String fName = String.format(mimicFile, fileName);
			try {
				Titan.l("Loading the mimic file");
				if(!Files.exists(new File(fName).toPath())) {
					Titan.e("The requested mimic data was not found");
				}
				
				log = new FileInputStream(fName);
				InputStreamReader iReader = new InputStreamReader(log, StandardCharsets.US_ASCII);
				reader = new BufferedReader(iReader);
				pathData.clear(); //Clear all of the pathData
				
				String line;
				while ((line = reader.readLine()) != null) {
					try {
						pathData.add(new Stepper(line));
					} catch (Exception e) {
						Titan.ee("MimicData", e);
					}
				}
			    
				try {
					reader.close();
				} catch (Exception e) {
					Titan.ee("Failed to close the mimic file", e);
				}
				Titan.l("Loaded the mimic file");
			} catch (IOException e) {
				Titan.ee("Mimic", e);
			}
		}
	
		public static ArrayList<Stepper> getData() {
			return pathData;
		}
	}
}
