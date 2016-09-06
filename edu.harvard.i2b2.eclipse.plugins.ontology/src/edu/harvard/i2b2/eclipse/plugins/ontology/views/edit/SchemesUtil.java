/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Mike Mendis
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;


import java.util.List;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;

public class SchemesUtil 
{
	private Log log = LogFactory.getLog(SchemesUtil.class.getName());
	private List<ConceptType> schemes = null;
	  //to make this class singleton
    private static SchemesUtil thisInstance;
    
    static {
            thisInstance = new SchemesUtil();
    }
    
    public static SchemesUtil getInstance() {
        return thisInstance;
    }


    public List<ConceptType> getSchemes() 
    {
    	if(schemes == null){
    		try {
    			GetReturnType request = new GetReturnType();
    			request.setType("default");

    			OntologyResponseMessage msg = new OntologyResponseMessage();
    			StatusType procStatus = null;	
    			while(procStatus == null || !procStatus.getType().equals("DONE")){
    				String response = OntServiceDriver.getSchemes(request, "EDIT");
    				procStatus = msg.processResult(response);
    				//				if  error 
    				//				TABLE_ACCESS_DENIED and USER_INVALID, DATABASE ERROR
    				if (procStatus.getType().equals("ERROR")){					
    					System.setProperty("errorMessage", procStatus.getValue());
    					return null;
    				}
    				procStatus.setType("DONE");
    			}
    			ConceptsType allConcepts = msg.doReadConcepts();   	    
    			schemes = allConcepts.getConcept();
    		 	ConceptType emptyScheme = new ConceptType();
    	    	emptyScheme.setKey("");
    	    	emptyScheme.setName("");
    	    	schemes.add(0, emptyScheme);

    		} catch (AxisFault e) {
    			log.error(e.getMessage());
    			System.setProperty("errorMessage", "Ontology cell unavailable");	
    		} catch (I2B2Exception e) {
    			log.error(e.getMessage());
    			System.setProperty("errorMessage", e.getMessage());
    		} catch (Exception e) {
    			log.error(e.getMessage());
    			System.setProperty("errorMessage", "Remote service unavailable");
    		}
    	}
   
    	return schemes;
	}

}
