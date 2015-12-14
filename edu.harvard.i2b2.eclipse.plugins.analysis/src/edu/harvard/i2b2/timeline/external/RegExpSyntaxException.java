package edu.harvard.i2b2.timeline.external;

/*
 * RegExpSyntaxException.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
/**
 * Signals that the regular expression is invalid.
 */
public class RegExpSyntaxException extends Exception {

    /**
     * Constructs a RegExpSyntaxException with no detail message.
     */
    public RegExpSyntaxException() {
	super();
    }

    /**
     * Constructs a RegExpSyntaxException with the specified detail message.
     */
    public RegExpSyntaxException(String s) {
	super(s);
    }
}
