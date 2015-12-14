/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.util.*;

public class conflictResolver {

    private Vector positions;
    private Integer identifier;

    public conflictResolver() {

	positions = new Vector();
	identifier = null; // so can tell not yet used

    }

    public void setIdentifier(int setToThis) {

	identifier = new Integer(setToThis);

    }

    public boolean identifierIsSet() {

	return !(identifier == null);

    }

    public boolean identifierIsEqualTo(int couldBeEqual) {

	return ((new Integer(couldBeEqual)).equals(identifier));

    }

    public boolean resolveConflicts(int position1, int position2) {

	boolean result = true;

	for (int i = 0; i < (position2 - position1); i++)
	    if (positions.contains(new Integer(position1 + i)))
		result = false;

	if (result)
	    for (int i = 0; i < (position2 - position1); i++) {
		positions.addElement(new Integer(position1 + i));
		// System.out.println("add " + (position1+i));
	    }

	return result;

    }

    public void deleteConflicts(int position1, int position2) {
	for (int i = 0; i < (position2 - position1); i++)
	    if (positions.contains(new Integer(position1 + i))) {
		positions.removeElement(new Integer(position1 + i));
		// System.out.println("remove " + (position1+i));
	    }
    }

    public boolean isEmpty() {
	return positions.isEmpty();
    }

}
