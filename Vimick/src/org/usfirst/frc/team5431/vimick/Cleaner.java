package org.usfirst.frc.team5431.vimick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.usfirst.frc.team5431.vimick.Mimick.MimickException;
import org.usfirst.frc.team5431.vimick.Mimick.Stepper;

public class Cleaner {
	private final List<Stepper> pathData;
	private final HashMap<String, Integer> argPair;
	private final HashMap<String, CleanPair> cleanArgs = new HashMap<>();
	
	public enum Constraint {
		MIN, MAX, MIN_DIFF, MAX_DIFF
	}
	
	public static class CleanPair {
		public Constraint constraint;
		public double value;
		
		public CleanPair(Constraint c, double v) {
			constraint = c;
			value = v;
		}
	}
	
	public Cleaner(final List<Stepper> steps) throws MimickException {
		pathData = steps;
		
		if(pathData.size() == 0) throw new Mimick.MimickException("The cleaner has to have at least one step");
		
		argPair = pathData.get(0).argPair;
	}
	
	public void addCleanCheck(final String key, final Constraint constraint, final double arg) throws MimickException {
		if(!argPair.containsKey(key)) throw new Mimick.MimickException("The mimick doesn't contain this key!");
		cleanArgs.put(key, new CleanPair(constraint, arg));
	}
	
	public List<Stepper> clean() {
		final List<Stepper> cleaned = new ArrayList<>();
		for(int ind = 0; ind < pathData.size(); ind++) {
			final Stepper prevStep = pathData.get((ind > 0) ? (ind - 1) : 0);
			final Stepper step = pathData.get(ind);
			final Stepper nStep = new Stepper(step.formatString, step.argPair, step.data);
			boolean allPassed = false;
			for(final String key : cleanArgs.keySet()) {
				boolean passed = true;
				final CleanPair cP = cleanArgs.get(key);
				switch(cP.constraint) {
				case MAX:
					if(step.get(key) >= cP.value) passed = false;
					break;
				case MAX_DIFF:
					if(ind > 0) {
						if(Math.abs(step.get(key) - prevStep.get(key)) >= cP.value) passed = false;
					}
					break;
				case MIN:
					if(step.get(key) <= cP.value) passed = false;
					break;
				case MIN_DIFF:
					if(ind > 0) {
						if(Math.abs(step.get(key) - prevStep.get(key)) <= cP.value) passed = false;
					}
					break;
				default:
					break;
				}
				if(passed) allPassed = true;
			}
			if(allPassed) cleaned.add(nStep);
		}
		return cleaned;
	}
}
