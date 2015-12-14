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

public class OntGetCodeInfoRequestTest {
	public static void main(String[] args) throws Exception {;
	try {	
		OntServiceDriver ont = new OntServiceDriver();
		
		VocabRequestType vocab = new VocabRequestType();
		vocab.setCategory(null);
		vocab.setMax(300);
		vocab.setSynonyms(true);
		
		MatchStrType match = new MatchStrType();
		match.setValue("2015");	
		match.setStrategy("exact");
		vocab.setMatchStr(match);
		vocab.setType("default");
		
		
		System.out.println(OntServiceDriver.getCodeInfo(vocab, "ONT"));

	}catch(Exception e) {
		e.printStackTrace();
	}
	}
}
