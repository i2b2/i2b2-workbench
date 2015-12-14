package edu.harvard.i2b2.timeline.external;

/*
 * RTree.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
/*
 * A regular expression tree node.
 */
class RTree {
	/* operations */
	public static final int OP_EMPTY = 0; // empty
	public static final int OP_CHAR = 1; // normal character
	public static final int OP_CONCAT = 2; // XY
	public static final int OP_UNION = 3; // X|Y
	public static final int OP_CLOSURE = 4; // X*
	public static final int OP_LHEAD = 5; // '^'
	public static final int OP_LTAIL = 6; // '$'

	private int operation;
	private Chars chars;
	private RTree left;
	private RTree right;

	/*
	 * Constructs a leaf.
	 */
	public RTree(Chars chars) {
		this(OP_CHAR, chars, null, null);
	}

	/*
	 * Constructs a node.
	 */
	public RTree(int operation, RTree left, RTree right) {
		this(operation, null, left, right);
	}

	/*
	 * Constructs a node.
	 */
	public RTree(int operation, Chars chars, RTree left, RTree right) {
		this.operation = operation;
		this.chars = chars;
		this.left = left;
		this.right = right;
	}

	public int operation() {
		return operation;
	}

	public Chars chars() {
		return chars;
	}

	public RTree left() {
		return left;
	}

	public RTree right() {
		return right;
	}

	public void removeChars(Chars cs) {
		if (operation == RTree.OP_CHAR) {
			if (chars.hasChars(cs)) {
				if (cs.begin() <= chars.begin() && chars.end() <= cs.end()) {
					/* Don't match to any chars. c1 > c2! */
					chars.setType(Chars.NONE);
					return;
				} else if (cs.begin() <= chars.end() && chars.end() <= cs.end()) {
					chars.setEnd((char) (cs.begin() - 1));
				} else if (cs.begin() <= chars.begin()
						&& chars.begin() <= cs.end()) {
					chars.setBegin((char) (cs.end() + 1));
				} else if (chars.begin() <= cs.begin()
						&& cs.end() <= chars.end()) {
					operation = RTree.OP_UNION;
					left = new RTree(new Chars(chars.begin(), (char) (cs
							.begin() - 1)));
					right = new RTree(new Chars((char) (cs.end() + 1), chars
							.end()));
				}
			}
		}
		if (left != null)
			left.removeChars(cs);
		if (right != null)
			right.removeChars(cs);
	}
}
