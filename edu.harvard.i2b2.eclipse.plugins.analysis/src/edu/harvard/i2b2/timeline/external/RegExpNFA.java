package edu.harvard.i2b2.timeline.external;

/*
 * RegExpNFA.java
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
 * A class that builds NFA.
 */
class RegExpNFA {
    private Hashtable nfa; // a set of NList
    private int nfaEntry;
    private int nfaExit;
    private int count = 0;
    private RTree tree;
    private boolean hasLHead = false;
    private boolean hasLTail = false;

    private static final String MSG_INVALID_OPERATION = "Invalid operation.";

    /*
     * Constructs a RegExpNFA.
     */
    public RegExpNFA(RTree tree) throws NFABuildException {
	nfa = new Hashtable();
	this.tree = tree;
	buildNfa();
    }

    /*
     * Applys number to node.
     */
    private int genNode() throws NFABuildException {
	return count++;
    }

    /*
     * Add an transition to NFA
     */
    private void addTransition(int from, int to, Chars chars) {
	Integer key = new Integer(from);
	NList nl = new NList(chars, to, (NList) nfa.get(key));
	nfa.put(key, nl);
    }

    /*
     * Build NFA
     */
    private void genNfa(RTree tree, int entry, int exit)
	    throws NFABuildException {
	int a1, a2;

	switch (tree.operation()) {
	case RTree.OP_CHAR:
	    addTransition(entry, exit, tree.chars());
	    break;
	case RTree.OP_LHEAD:
	    if (entry != nfaEntry) {
		addTransition(entry, exit, tree.chars());
	    } else {
		hasLHead = true;
		addTransition(entry, exit, new Chars(Chars.LINE_HEAD));
	    }
	    break;
	case RTree.OP_LTAIL:
	    if (exit != nfaExit) {
		addTransition(entry, exit, tree.chars());
	    } else {
		hasLTail = true;
		addTransition(entry, exit, new Chars(Chars.LINE_TAIL));
	    }
	    break;
	case RTree.OP_EMPTY:
	    addTransition(entry, exit, new Chars(Chars.EMPTY));
	    break;
	case RTree.OP_UNION:
	    genNfa(tree.left(), entry, exit);
	    genNfa(tree.right(), entry, exit);
	    break;
	case RTree.OP_CLOSURE:
	    a1 = genNode();
	    a2 = genNode();
	    addTransition(entry, a1, new Chars(Chars.EMPTY));
	    genNfa(tree.left(), a1, a2);
	    addTransition(a2, a1, new Chars(Chars.EMPTY));
	    addTransition(a1, exit, new Chars(Chars.EMPTY));
	    break;
	case RTree.OP_CONCAT:
	    a1 = genNode();
	    genNfa(tree.left(), entry, a1);
	    genNfa(tree.right(), a1, exit);
	    break;
	default:
	    throw new NFABuildException(MSG_INVALID_OPERATION);
	}
    }

    /*
     * Build NFA
     */
    private void buildNfa() throws NFABuildException {
	nfaEntry = genNode();
	nfaExit = genNode();
	genNfa(tree, nfaEntry, nfaExit);
    }

    public NList getNList(int i) {
	Integer key = new Integer(i);
	return (NList) nfa.get(key);
    }

    public int entry() {
	return nfaEntry;
    }

    public int exit() {
	return nfaExit;
    }

    public int count() {
	return count;
    }

    public boolean hasLHead() {
	return hasLHead;
    }

    public boolean hasLTail() {
	return hasLTail;
    }

    public RTree getTree() {
	return tree;
    }
}
