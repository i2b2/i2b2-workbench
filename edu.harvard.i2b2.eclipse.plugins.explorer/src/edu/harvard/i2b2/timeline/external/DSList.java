package edu.harvard.i2b2.timeline.external;

/*
 * DSList.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
/*
 * A class that expresses a list of taransitions.
 */
class DSList {
	private Chars chars;
	private DState to;
	private DSList next;

	/*
	 * The constructor.
	 */
	public DSList() {
		this(null, null, null);
	}

	/*
	 * The constructor.
	 */
	public DSList(Chars chars, DState to, DSList next) {
		this.chars = chars;
		this.to = to;
		this.next = next;
	}

	public Chars chars() {
		return chars;
	}

	public DState to() {
		return to;
	}

	public DSList next() {
		return next;
	}

	public void setTo(DState ds) {
		to = ds;
	}

	public void setNext(DSList dsl) {
		next = dsl;
	}
}
