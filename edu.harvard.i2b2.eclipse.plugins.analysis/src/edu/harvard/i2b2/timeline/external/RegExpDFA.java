package edu.harvard.i2b2.timeline.external;

/*
 * RegExpDFA.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
import java.util.*;

/**
 * A class that builds DFA.
 */
class RegExpDFA {
    private RegExpNFA nfa;
    private Hashtable dfa; // a set of DState
    private DState initialDfaState;
    private int count = 0;
    private boolean hasLHead = false;
    private boolean hasLTail = false;

    /*
     * Constructs a RegExpDFA.
     */
    public RegExpDFA(RegExpNFA nfa) {
	dfa = new Hashtable();
	this.nfa = nfa;
	convertNfaToDfa();
    }

    /*
     * Add state to NFA state set and marks empty transitions.
     */
    private void markEmptyTransition(BitSet nfaStateSet, int s) {
	NList nl;

	nfaStateSet.set(s);
	for (nl = nfa.getNList(s); nl != null; nl = nl.next()) {
	    if (nl.chars().isEmpty() && !nfaStateSet.get(nl.to())) {
		markEmptyTransition(nfaStateSet, nl.to());
	    }
	}
    }

    /*
     * Do epsilin-closure operation with state.
     */
    private void collectEmptyTransition(BitSet nfaStateSet) {
	int i;

	for (i = 0; i < nfa.count(); i++) {
	    if (nfaStateSet.get(i)) {
		markEmptyTransition(nfaStateSet, i);
	    }
	}
    }

    /**
     * Registers a NFA state set to DFA and returns DFA state.
     */
    private DState registerDState(BitSet s) {
	Enumeration states;
	DState ds;

	states = dfa.elements();
	while (states.hasMoreElements()) {
	    ds = (DState) states.nextElement();
	    if (ds.nfaStateSet().equals(s))
		return ds;
	}

	ds = new DState(s, false, s.get(nfa.exit()), null);
	dfa.put(new Integer(count), ds);
	count++;

	return ds;
    }

    /**
     * Fetches a DFA state that is still not visited.
     */
    private DState fetchUnvisitedDState() {
	Enumeration states;
	DState ds;

	states = dfa.elements();
	while (states.hasMoreElements()) {
	    ds = (DState) states.nextElement();
	    if (!ds.visited())
		return ds;
	}
	return null;
    }

    /**
     * Computes reachable NFA states and returns a list.
     */
    private DList computeReachableNState(DState dstate) {
	int i;
	NList nl;
	DList result, a, b;
	BitSet nfaStateSet;
	boolean added = false;

	nfaStateSet = dstate.nfaStateSet();
	result = null;

	for (i = 0; i < nfa.count(); i++) {
	    if (nfaStateSet.get(i)) {
		for (nl = nfa.getNList(i); nl != null; nl = nl.next()) {
		    if (!nl.chars().isEmpty()) {
			added = false;
			for (a = result; a != null; a = a.next()) {
			    if (a.chars().equals(nl.chars())) {
				a.to().set(nl.to());
				added = true;
				break;
			    }
			}
			if (!added) {
			    b = new DList(nl.chars(), new BitSet(), null);
			    b.to().set(nl.to());
			    b.setNext(result);
			    result = b;
			}
		    }
		}
	    }
	}

	return result;
    }

    /**
     * Converts NFA to DFA.
     */
    private void convertNfaToDfa() {
	BitSet nfaStateSet;
	DState t;
	DList x;
	DSList p;

	nfaStateSet = new BitSet();
	nfaStateSet.set(nfa.entry());
	collectEmptyTransition(nfaStateSet);
	initialDfaState = registerDState(nfaStateSet);

	while ((t = fetchUnvisitedDState()) != null) {
	    t.visit();

	    for (x = computeReachableNState(t); x != null; x = x.next()) {
		collectEmptyTransition(x.to());
		p = new DSList(x.chars(), null, null);

		p.setTo(registerDState(x.to()));
		p.setNext(t.next());
		t.setNext(p);
	    }
	}

	hasLHead = nfa.hasLHead();
	hasLTail = nfa.hasLTail();
    }

    public boolean hasLHead() {
	return hasLHead;
    }

    public boolean hasLTail() {
	return hasLTail;
    }

    /**
     * Returns the next DFA state.
     * <p>
     * 
     * @param state
     *            the current state.
     * @param c
     *            the next character.
     */
    public DState nextState(DState state, char c) {
	DSList p;

	for (p = state.next(); p != null; p = p.next()) {
	    if (p.chars().has(c))
		return p.to();
	}
	return null;
    }

    /**
     * Returns the next DFA state.
     * <p>
     * 
     * @param state
     *            the current state.
     * @param c
     *            the next character.
     */
    public DState nextState(DState state, int type) {
	DSList p;

	for (p = state.next(); p != null; p = p.next()) {
	    if (p.chars().type() == type)
		return p.to();
	}
	return null;
    }

    /**
     * Returns the initial DFA state.
     */
    public DState initialState() {
	return initialDfaState;
    }

    public int count() {
	return count;
    }

    public DState getDState(int i) {
	Integer key = new Integer(i);
	return (DState) dfa.get(key);
    }

    public RegExpNFA getNfa() {
	return nfa;
    }

    public RTree getTree() {
	return nfa.getTree();
    }
}
