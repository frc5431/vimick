package org.usfirst.frc.team5431.vimick;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

public class Mimick {
	public static final String sep = ",";
	public static final double ver = 1.0;
	public static final String header = "Mimick created by Team5431";
	public static final String headerFormat = "%s\nVersion: %.1f\n%s\n";

	/*
	 * public static void main(String args[]) { try { Observer o = new
	 * Observer("C:\\Users\\AcademyHSRobotics\\Downloads\\test.mimic");
	 * o.addArguments("left","right","home"); o.prepare(); Stepper step =
	 * o.createStep(); step.set("left", 0.2); step.set("home", true);
	 * step.set("right", 0.1); o.addStep(step); o.save();
	 * 
	 * Repeater r = new
	 * Repeater("C:\\Users\\AcademyHSRobotics\\Downloads\\test.mimic"); r.prepare();
	 * List<Stepper> steps = r.getData(); for(Stepper stepr : steps) {
	 * System.out.println("Left: " + stepr.get("left"));
	 * System.out.println("Right: " + stepr.get("right"));
	 * System.out.println("Home: " + stepr.get("home")); } } catch (MimickException
	 * e) { e.printStackTrace(); }
	 * 
	 * p(formatString); p(buildHeader()); try { Header head =
	 * getHeader(buildHeader() + "sdfsdf"); p(Arrays.toString(head.arguments));
	 * p(head.description); p(String.valueOf(head.version)); p(getBody(buildHeader()
	 * + "sdfsdf")); } catch (Exception e) { e.printStackTrace(); } }
	 */

	public static class MimickException extends Exception {
		public MimickException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
	}

	public static class Header {
		public String description;
		public String[] arguments;
		public double version;

		public Header(final String parse) throws MimickException {
			final String[] parts = parse.split("\n");
			try {
				description = parts[0];
				version = Double.valueOf(parts[1].substring(parts[1].indexOf(": ") + 2));
				arguments = parts[2].split(sep);
			} catch (Exception err) {
				throw new MimickException("Not a valid Mimick file");
			}
		}

		public String getDescription() {
			return description;
		}

		public String[] getArguments() {
			return arguments;
		}

		public double getVersion() {
			return version;
		}

		public boolean isCurrentVersion() {
			return version == ver;
		}
	}

	public static class Stepper {
		public volatile String formatString = "\n";
		public HashMap<String, Integer> argPair;
		public Double data[];

		public Stepper(String format, HashMap<String, Integer> argP, Double... args) {
			formatString = format;
			argPair = argP;
			data = args;
		}

		public Stepper(String format, HashMap<String, Integer> argP, final String parse) throws MimickException {
			final String parts[] = parse.split(sep);
			if (parts.length != argP.keySet().size()) {
				throw new MimickException("Arguments mismatch");
			}
			formatString = format;
			argPair = argP;
			data = new Double[parts.length];
			for (int ind = 0; ind < data.length; ind++) {
				data[ind] = Double.valueOf(parts[ind]);
			}
		}

		public double get(String key) {
			try {
				return data[argPair.get(key)];
			} catch (Throwable ignored) {
				System.err.println("Mimic Error: Failed to get key " + key);
				return 0.0;
			}
		}

		public boolean is(String key) {
			return get(key) != 0;
		}

		public int round(String key) {
			return ((Double) get(key)).intValue();
		}

		public void set(String key, double value) {
			try {
				data[argPair.get(key)] = value;
			} catch (Throwable ignored) {
				System.err.println("Mimic Error: Failed to set value for " + key);
			}
		}

		public void set(String key, boolean value) {
			set(key, (value) ? 1.0 : 0.0);
		}

		public void set(String key, int value) {
			set(key, ((Integer) value).doubleValue());
		}

		public String toString() {
			return String.format(formatString, (Object[]) data);
		}
	}

	public static class Observer {
		private final File file;
		private final List<String> arguments;
		private final List<Stepper> data;

		public Observer(final File f, final List<String> args, final List<Stepper> data) {
			this.file = f;
			this.arguments = args;
			this.data = data;
		}

		public void save() throws MimickException, IOException {
			if (arguments.size() == 0)
				throw new MimickException("No arguments provided!");
			file.createNewFile();
			try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
				final HashMap<String, Integer> argPair = new HashMap<>();
				final StringBuilder ab = new StringBuilder();
				final StringBuilder b = new StringBuilder();
				for (int ind = 0; ind < arguments.size(); ind++) {
					ab.append(arguments.get(ind));
					ab.append(sep);
					argPair.put(arguments.get(ind), ind);
					b.append("%.4f");
					b.append(sep);
				}
				ab.setLength(ab.length() - 1);
				b.setLength(b.length() - 1);
				b.append(System.lineSeparator());
				//formatString = b.toString();
				writer.write(String.format(headerFormat, header, ver, ab.toString()));
				for (final Stepper step : data) {
					writer.write(step.toString());
				}
			} catch (final IOException e) {
				throw new MimickException("Failed to save mimic file! " + e.getMessage());
			}
		}
	}

	public static class Repeater {
		private FileInputStream log = null;
		private BufferedReader reader = null;
		private List<String> arguments;
		private volatile String formatString = "\n";
		private HashMap<String, Integer> argPair = new HashMap<>();
		private final ArrayList<Stepper> pathData = new ArrayList<Stepper>();
		private final String filePath;

		public Repeater(final String fPath) {
			filePath = fPath;
		}

		public Header prepare() throws MimickException {
			try {
				if (!Files.exists(new File(filePath).toPath())) {
					throw new MimickException("The file" + filePath + " doesn't exist!");
				}

				log = new FileInputStream(filePath);
				InputStreamReader iReader = new InputStreamReader(log, StandardCharsets.US_ASCII);
				reader = new BufferedReader(iReader);
				pathData.clear(); // Clear all of the pathData

				StringBuilder h = new StringBuilder();
				boolean parsedHeader = false;
				Header header = null;

				String line;
				while ((line = reader.readLine()) != null) {
					if (!parsedHeader) {
						if (!line.matches(".*[a-zA-Z].*")) {
							header = new Header(h.toString());
							arguments = Arrays.asList(header.arguments);
							if (ver < header.version) {
								throw new MimickException(String.format(
										"The Mimick file is newer than the current version (File: %.1f, Current: %.1f)",
										header.version, ver));
							}
							StringBuilder b = new StringBuilder();
							for (int ind = 0; ind < arguments.size(); ind++) {
								argPair.put(arguments.get(ind), ind);
								b.append("%.4f");
								b.append(sep);
							}
							b.setLength(b.length() - 1);
							b.append("\n");
							formatString = b.toString();

							try {
								pathData.add(new Stepper(formatString, argPair, line));
							} catch (Exception e) {
								throw new MimickException("Failed to add first step! " + e.getMessage());
							}

							parsedHeader = true;
						} else {
							h.append(line);
							h.append("\n");
						}
					} else {
						try {
							pathData.add(new Stepper(formatString, argPair, line));
						} catch (Exception e) {
							throw new MimickException("Failed to add step! " + e.getMessage());
						}
					}
				}

				try {
					reader.close();
				} catch (Exception e) {
					throw new MimickException("Failed to close the file");
				}

				return header;
			} catch (IOException e) {
				throw new MimickException("Failed to replay Mimick file! " + e.getMessage());
			}
		}

		public ArrayList<Stepper> getData() {
			return pathData;
		}

		public List<String> getArguments() {
			return arguments;
		}
	}
}
