/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package test;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.ontclient.datavo.vdo.MatchStrType;
import edu.harvard.i2b2.ontclient.datavo.vdo.VocabRequestType;

public class OntGetNameInfoRequestTest {
	public static void main(String[] args) throws Exception {;
	try {	
		OntServiceDriver ont = new OntServiceDriver();
		
		VocabRequestType vocab = new VocabRequestType();
		vocab.setCategory("i2b2");
		vocab.setMax(300);
		
		MatchStrType match = new MatchStrType();
		match.setValue("height");	
		match.setStrategy("contains");
		vocab.setMatchStr(match);
		vocab.setType("core");
		
		
		System.out.println(OntServiceDriver.getNameInfo(vocab, "ONT"));

	}catch(Exception e) {
		e.printStackTrace();
	}
}
}
