/*
 * Copyright (c) 2006-2007 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws;


/**
 * Define JAXB constants here.
 * For dynamic configuration, move these values to property file
 * and read from it.
 *
 * @author rkuttan
 */
public class JAXBConstant {
    public static final String[] DEFAULT_PACKAGE_NAME = new String[] {
            "edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message",
            "edu.harvard.i2b2.common.datavo.pdo",
            "edu.harvard.i2b2.crcxmljaxb.datavo.vdo",
            "edu.harvard.i2b2.crcxmljaxb.datavo.dnd"
        };
    public static final String[] ONT_PACKAGE_NAME = new String[] {
        "edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message",
        "edu.harvard.i2b2.crcxmljaxb.datavo.vdo"
    };
}
