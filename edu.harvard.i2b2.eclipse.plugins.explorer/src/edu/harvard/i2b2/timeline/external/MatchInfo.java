package edu.harvard.i2b2.timeline.external;

/*
 * MatchInfo.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
/**
 * A class used in pattern matching.
 */
public class MatchInfo {
	int start;
	int end;
	String matchString;

	/**
	 * Constructor.
	 */
	public MatchInfo() {
		this(0, 0, "");
	}

	/**
	 * Constructor.
	 */
	public MatchInfo(int start, int end, String matchString) {
		this.start = start;
		this.end = end;
		this.matchString = matchString;
	}

	/**
	 * Returns the start index of the matched string.
	 */
	public int start() {
		return start;
	}

	/**
	 * Returns the end index of the matched string.
	 */
	public int end() {
		return end;
	}

	/**
	 * Returns the matched string.
	 */
	public String matchString() {
		return matchString;
	}

	@Override
	public String toString() {
		return "(" + start + ":" + (end - start) + ":\"" + matchString + "\")";
	}
}
