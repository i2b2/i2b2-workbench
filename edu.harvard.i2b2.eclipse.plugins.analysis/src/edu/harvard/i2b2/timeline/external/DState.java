package edu.harvard.i2b2.timeline.external;

/*
 * DState.java
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
 * A class that expresses a state of DFA.
 * DO NOT construct any DState instanses.<br>
 * RegExpDFA do it automatically.
 */
class DState {
    private BitSet nfaStateSet; // a set of NFA state
    private boolean visited;
    private DSList next;
    private boolean accepted;

    /*
     * The constructor.
     */
    DState(BitSet nfaStateSet, boolean visited, boolean accepted, DSList next) {
	this.nfaStateSet = nfaStateSet;
	this.visited = visited;
	this.accepted = accepted;
	this.next = next;
    }

    /**
     * If accepted return true, else return false.
     */
    public boolean accepted() {
	return accepted;
    }

    /**
     * If accepted return true, else return false.
     */
    public BitSet nfaStateSet() {
	return nfaStateSet;
    }

    /**
     * If accepted return true, else return false.
     */
    public boolean visited() {
	return visited;
    }

    public void visit() {
	visited = true;
    }

    /**
     * If accepted return true, else return false.
     */
    public DSList next() {
	return next;
    }

    public void setNext(DSList dsl) {
	next = dsl;
    }
}
