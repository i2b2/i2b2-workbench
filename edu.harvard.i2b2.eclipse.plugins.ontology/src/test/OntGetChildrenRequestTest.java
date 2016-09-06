/*
* Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package test;


import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetChildrenType;

public class OntGetChildrenRequestTest{
		
	public static void main(String[] args) throws Exception {;
	try {	
		
		GetChildrenType parentNode = new GetChildrenType();
		parentNode.setParent("\\\\i2b2\\i2b2");
		parentNode.setMax(300);
		
		
		System.out.println(OntServiceDriver.getChildren(parentNode, null));

	}catch(Exception e) {
		e.printStackTrace();
	}
}
	
}