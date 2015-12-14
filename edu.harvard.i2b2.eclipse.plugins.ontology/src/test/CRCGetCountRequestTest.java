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


import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisDefinitionType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisParamType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisResultOptionListType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisResultOptionType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.CrcAnalysisInputParamType;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetPsmRequestMessage;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisDefinitionRequestType;


public class CRCGetCountRequestTest{
		
	public static void main(String[] args) throws Exception {;
	try {	
		
		AnalysisDefinitionRequestType parentNode = new AnalysisDefinitionRequestType();
		AnalysisDefinitionType value = new AnalysisDefinitionType();
		value.setAnalysisPluginName("CALCULATE_PATIENTCOUNT_FROM_CONCEPTPATH");
		
		CrcAnalysisInputParamType input = new CrcAnalysisInputParamType();
		input.setName("ONT request");
		AnalysisParamType param = new AnalysisParamType();
		param.setColumn("item_key");
		param.setType("int");
		param.setValue("\\\\rpdr\\RPDR\\Demographics\\Race\\");
		input.getParam().add(param);
		value.setCrcAnalysisInputParam(input);
		
		AnalysisResultOptionType output = new AnalysisResultOptionType();
		output.setName("XML");
		output.setPriorityIndex(1);
		output.setFullName("XML");
		
		AnalysisResultOptionListType option = new AnalysisResultOptionListType();
		option.getResultOutput().add(output);
		value.setCrcAnalysisResultList(option);
		
		parentNode.setAnalysisDefinition(value);
		
		
		 GetPsmRequestMessage reqMsg = new GetPsmRequestMessage();

		 String getCountRequestString = reqMsg.doBuildXML(parentNode);
		
		
		System.out.println(getCountRequestString);

	}catch(Exception e) {
		e.printStackTrace();
	}
}
	
}