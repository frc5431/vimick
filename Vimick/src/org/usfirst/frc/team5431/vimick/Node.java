package org.usfirst.frc.team5431.vimick;

import java.util.HashMap;
import java.util.Map;

public class Node {
	private final Map<String, Double> properties;
	public int index = 0;
	
	public Node(final Map<String, Double> props) {
		this.properties = props;
	}
	
	//this constructor is jut for testing until the parsing works
	public Node(final double x, final double y) {
		this();
		properties.put("X_POS", x);
		properties.put("Y_POS", y);
	}

	public Node() {
		this(new HashMap<>());
	}

	public Map<String, Double> getProperties() {
		return properties;
	}

	public double getX() {
		// has to be mapped as a double from 0.0 (far left of field) to 1.0 (far right
		// of field)
		return properties.getOrDefault("X_POS", 0.0);// david change this to whatever you want
	}

	public double getY() {
		// has to be mapped as a double from 0.0 (alliance station wall) to 1.0 (center
		// line going through the scale)
		return properties.getOrDefault("Y_POS", 0.0);// same with this
	}
	
	public void setX(final double x) {
		properties.put("X_POS", x);
	}
	
	public void setY(final double y) {
		properties.put("Y_POS", y);
	}
	
	public boolean isHome() {
		return properties.getOrDefault("HOME", 0.0) > 0.0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Node other = (Node) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
}
