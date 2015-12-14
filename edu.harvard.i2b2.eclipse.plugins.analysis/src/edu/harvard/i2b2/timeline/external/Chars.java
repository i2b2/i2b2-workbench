package edu.harvard.i2b2.timeline.external;

/*
 * Chars.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
class Chars {
    public static final int EMPTY = 0;
    public static final int CHARACTER = 1;
    public static final int LINE_HEAD = 2;
    public static final int LINE_TAIL = 3;
    public static final int NONE = 4;
    private char begin;
    private char end;
    private int type;

    private Chars() {
    }

    public Chars(char c) {
	this(c, c);
    }

    public Chars(char begin, char end) {
	this.begin = begin;
	this.end = end;
	type = CHARACTER;
    }

    public Chars(int type) {
	this.type = type;
    }

    public char begin() {
	return begin;
    }

    public char end() {
	return end;
    }

    public void set(char begin, char end) {
	this.begin = begin;
	this.end = end;
    }

    public int type() {
	return type;
    }

    public void setBegin(char c) {
	begin = c;
    }

    public void setEnd(char c) {
	end = c;
    }

    public boolean has(char c) {
	if (type == NONE)
	    return false;
	else
	    return begin <= c && c <= end;
    }

    public boolean hasChars(Chars cs) {
	if (type == NONE)
	    return false;
	else
	    return (begin <= cs.begin() && cs.begin() <= end)
		    || (begin <= cs.end() && cs.end() <= end);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj != null && obj instanceof Chars) {
	    Chars cs = (Chars) obj;
	    if (type == CHARACTER) {
		return begin == cs.begin && end == cs.end;
	    } else {
		return type == cs.type;
	    }
	}
	return false;
    }

    public boolean isEmpty() {
	return type == EMPTY;
    }

    private String escape(char c) {
	String result;

	switch (c) {
	case '\t':
	    result = "\\t";
	    break;
	case '\b':
	    result = "\\b";
	    break;
	case '\r':
	    result = "\\r";
	    break;
	case '\n':
	    result = "\\n";
	    break;
	default:
	    result = (new Character(c)).toString();
	}

	return result;
    }

    @Override
    public String toString() {
	String result = "";

	switch (type) {
	case CHARACTER:
	    if (begin == end)
		result = "'" + escape(begin) + "'";
	    else
		result = "'" + escape(begin) + "-" + escape(end) + "'";
	    break;
	case EMPTY:
	    result = "EMPTY";
	    break;
	case LINE_HEAD:
	    result = "LHEAD";
	    break;
	case LINE_TAIL:
	    result = "LTAIL";
	    break;
	case NONE:
	    result = "NONE";
	    break;
	}
	return result;
    }

    public void setType(int type) {
	this.type = type;
    }
}
