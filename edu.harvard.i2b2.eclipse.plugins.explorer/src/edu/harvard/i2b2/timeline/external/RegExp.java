package edu.harvard.i2b2.timeline.external;

/*
 * RegExp.java
 *
 * Copyright (C) 1997 MAEDA Shugo
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

//package JP.ac.osaka_u.ender.util.regex;
import java.util.*;

/**
 * A class that handles regular expressions.
 * <p>
 * 
 * @version 0.08, 02/16/97
 * @author Shugo Maeda
 */
public class RegExp extends RegExpCore {

	/**
	 * Constructs a RegExp instance.
	 */
	public RegExp() {
		super();
	}

	/**
	 * Constructs a RegExp instance with the specified regular expression.
	 */
	public RegExp(String pattern) throws RegExpSyntaxException,
			NFABuildException {
		super(pattern);
	}

	/**
	 * Pattern match with the current specified regular expression.
	 * <p>
	 * 
	 * @param text
	 *            the target text.
	 * @return Returns the information on pattern matching.
	 */
	public MatchInfo match(String text) {
		return match(text, 0, text.length());
	}

	/**
	 * Pattern match with the current specified regular expreshassion.
	 * <p>
	 * 
	 * @param text
	 *            the target text.
	 * @param begin
	 *            the begining index for the target.
	 * @param end
	 *            the ending index for the target.
	 * @return Returns the information on pattern matching.
	 */
	public MatchInfo match(String text, int begin, int end) {
		int start, matchEnd;
		DState initialState;

		initialState = dfa.initialState();

		if ((begin == 0 || text.charAt(begin - 1) == '\n') && dfa.hasLHead()) {
			matchEnd = matchHead(text, begin, end, initialState);
			if (matchEnd != -1) {
				return new MatchInfo(begin, matchEnd, text.substring(begin,
						matchEnd));
			}
		}

		for (start = begin; start < end; start++) {
			matchEnd = matchInside(text, start, end, initialState);
			if (matchEnd != -1) {
				return new MatchInfo(start, matchEnd, text.substring(start,
						matchEnd));
			} else if (text.charAt(start) == '\n' && dfa.hasLHead()) {
				if (start < text.length() - 1) {
					matchEnd = matchHead(text, start + 1, end, initialState);
					if (matchEnd != -1) {
						return new MatchInfo(start + 1, matchEnd, text
								.substring(start + 1, matchEnd));
					}
				}
			}
		}

		// when regexp = "$"
		if (dfa.hasLTail()
				&& dfa.nextState(initialState, Chars.LINE_TAIL) != null) {
			return new MatchInfo(start, start, "");
		}

		return null;
	}

	/**
	 * Pattern match on the top of line.
	 */
	protected int matchHead(String text, int start, int end, DState state) {
		int result, temp;

		result = matchInside(text, start, end, dfa.nextState(state,
				Chars.LINE_HEAD));
		if (result != -1) {
			temp = matchInside(text, start, end, state);
			if (temp > result)
				result = temp;
		}
		return result;
	}

	/**
	 * Pattern match in the line.
	 */
	protected int matchInside(String text, int start, int end, DState state) {
		int result, p;
		char c = '\0';
		DState prevState = state;

		result = -1;
		p = start;
		if (state == null)
			return result;
		while (state != null) {
			if (state.accepted())
				result = p;
			if (p > end - 1) {
				if (end == text.length()
						|| (text.charAt(end + 1) == '\n' || text
								.charAt(end + 1) == '\r')) {
					if (dfa.nextState(state, Chars.LINE_TAIL) != null)
						result = p--;
				}
				break;
			}
			prevState = state;
			c = text.charAt(p++);
			state = dfa.nextState(state, c);
		}
		if ((c == '\n' || c == '\r')
				&& dfa.nextState(prevState, Chars.LINE_TAIL) != null) {
			result = --p;
		}
		return result;
	}

	/**
	 * Global pattern match.
	 * <p>
	 * 
	 * @param text
	 *            the target text.
	 * @return Returns Enumeration of MatchInfo.
	 */
	public MatchInfo[] globalMatch(String text) {
		int start = 0;
		MatchInfo info;
		MatchInfo result[];
		Vector matches = new Vector();
		while ((info = match(text, start, text.length())) != null) {
			matches.addElement(info);
			if (info.start() == start && info.start() == info.end()) {
				start = info.end() + 1;
			} else
				start = info.end();
			if (start > text.length() - 1)
				break;
		}
		result = new MatchInfo[matches.size()];
		matches.copyInto(result);
		return result;
	}

	/**
	 * Splits a text by the matched strings.
	 * <p>
	 * 
	 * @param text
	 *            the target text.
	 * @return Returns Enumeration of the splited texts.
	 */
	public String[] split(String text) {
		String result[];
		Vector subStrings = new Vector();
		MatchInfo matches[];
		int i, start = 0;

		matches = globalMatch(text);
		for (i = 0; i < matches.length; i++) {
			if (start != matches[i].start()) {
				subStrings
						.addElement(text.substring(start, matches[i].start()));
			}
			start = matches[i].end();
		}
		if (start < text.length()) {
			subStrings.addElement(text.substring(start, text.length()));
		}
		result = new String[subStrings.size()];
		subStrings.copyInto(result);
		return result;
	}

	/**
	 * Substitutes subText for matched strings.
	 * <p>
	 * 
	 * @param text
	 *            the target text.
	 * @param subText
	 *            substitutes for matched strings.
	 * @param global
	 *            if true substitutes for all matched strings.
	 * @param Returns
	 *            the modified text.
	 */
	public String substitute(String text, String replacement, boolean global) {
		if (global)
			return globalSubstitute(text, replacement);
		else
			return simpleSubstitute(text, replacement);
	}

	/**
	 * Substitutes only one time.
	 */
	protected String simpleSubstitute(String text, String replacement) {
		MatchInfo info;

		info = match(text);
		if (info == null)
			return text;
		return text.substring(0, info.start())
				+ processReplacement(replacement, info.matchString())
				+ text.substring(info.end(), text.length());
	}

	/**
	 * Substitutes for all matched strings.
	 */
	protected String globalSubstitute(String text, String replacement) {
		int start = 0;
		MatchInfo info;
		String result = "";

		while ((info = match(text, start, text.length())) != null) {
			result += text.substring(start, info.start)
					+ processReplacement(replacement, info.matchString());
			start = info.end();
			if (info.start() == start && info.start() == info.end())
				break;
			if (info.end() > text.length() - 1)
				break;
		}
		if (start < text.length())
			result += text.substring(start, text.length());

		return result;
	}

	/**
	 * Substitutes the matched string for '&' and processes escape sequences.
	 */
	protected String processReplacement(String replacement, String matchString) {
		int i;
		char c;
		String result = "";

		for (i = 0; i < replacement.length(); i++) {
			c = replacement.charAt(i);
			switch (c) {
			case '\\':
				if (++i == replacement.length())
					new RegExpSyntaxException("Invalid escape sequence.");
				result += getQuotedChar(replacement.charAt(i));
				break;
			case '&':
				result += matchString;
				break;
			default:
				result += c;
			}
		}
		return result;
	}

	/**
	 * Gets a quoted char.
	 */
	protected char getQuotedChar(char c) {
		if (c == '0')
			return '\0';
		else if (c == 'b')
			return '\b';
		else if (c == 't')
			return '\t';
		else if (c == 'r')
			return '\r';
		else if (c == 'n')
			return '\n';
		return c;
	}
}
