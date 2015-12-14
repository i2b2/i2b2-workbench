package edu.harvard.i2b2.timeline.external;

/*
 * DList.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
import java.util.*;

/*
 * A class that express list of reachable NFA state set.
 */
class DList {
	private Chars chars;
	private BitSet to; // a set of NFA state
	private DList next;

	/*
	 * The constructor.
	 */
	public DList() {
		this(null, new BitSet(), null);
	}

	/*
	 * The constructor.
	 */
	public DList(Chars chars, BitSet to, DList next) {
		this.chars = chars;
		this.to = to;
		this.next = next;
	}

	public Chars chars() {
		return chars;
	}

	public BitSet to() {
		return to;
	}

	public DList next() {
		return next;
	}

	public void setNext(DList dl) {
		next = dl;
	}
}
