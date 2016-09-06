/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     
 */
package edu.harvard.i2b2.analysis.datavo;

public class AnalysisJAXBUtil {
    private static edu.harvard.i2b2.common.util.jaxb.JAXBUtil jaxbUtil = null;

    public static edu.harvard.i2b2.common.util.jaxb.JAXBUtil getJAXBUtil() {
	if (jaxbUtil == null) {
	    jaxbUtil = new edu.harvard.i2b2.common.util.jaxb.JAXBUtil(
		    JAXBConstant.DEFAULT_PACKAGE_NAME);
	}

	return jaxbUtil;
    }
}
