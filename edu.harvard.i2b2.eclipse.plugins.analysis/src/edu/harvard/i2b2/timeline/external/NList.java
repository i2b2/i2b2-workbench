package edu.harvard.i2b2.timeline.external;

/*
 * NList.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
/*
 * A class that express NFA transition.
 */
class NList {
    private Chars chars;
    private int to;
    private NList next;

    /*
     * The constructor.
     */
    NList(Chars chars, int to, NList next) {
	this.chars = chars;
	this.to = to;
	this.next = next;
    }

    public Chars chars() {
	return chars;
    }

    public int to() {
	return to;
    }

    public NList next() {
	return next;
    }
}
