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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;


public class OntResponseMessageTest{
	
	/**
	 * Test code to generate a ONT responsePdo String that can be parsed for ontology info
	 * 
	 * @return A String containing a ONT response
	 */
	public static String getResponseText() {
		StringBuffer queryStr = new StringBuffer();
		try {
			// Modify to point to your sample response 
			DataInputStream dataStream = new DataInputStream(new FileInputStream("rpdr/getterminfo_response.xml"));
			while(dataStream.available()>0) {
				queryStr.append(dataStream.readLine() + "\n");
			}
			// Log query string
		//	System.out.println("queryStr " + queryStr);
		} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return queryStr.toString();
	}
	

	
	public static void main(String[] args) throws Exception {
		String response = getResponseText();
		System.out.println(response);
		OntologyResponseMessage msg = new OntologyResponseMessage();
		try {	
			StatusType procStatus = msg.processResult(response);	
			System.out.println(procStatus.getType());
			ConceptsType allConcepts = msg.doReadConcepts();   	    
			List concepts = allConcepts.getConcept();
			ConceptType concept = (ConceptType)concepts.get(0);
			System.out.println(concept.getName());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}