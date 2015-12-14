/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		Mike Mendis (MGH)
 */

package edu.harvard.i2b2.timeline.lifelines;

/* Created by Partha on 11/23, mainly to store the settings of the zooming 
options in the Control Panel and pass it back to the timeLinePanel.java at
the appropriate places */
import java.util.Hashtable;

public class ResourceTable
{
    private static final Hashtable _resourceTable = new Hashtable();

    public static void put(String resourceName, Object resource)
    {
        _resourceTable.put(resourceName, resource);
    }

    public static Object get(String resourceName)
    {
        return _resourceTable.get(resourceName);
    }
}