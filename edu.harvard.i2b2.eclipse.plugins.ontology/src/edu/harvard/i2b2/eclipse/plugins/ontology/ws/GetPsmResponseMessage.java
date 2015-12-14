/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.ws;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.OntologyJAXBUtil;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.i2b2result.DataType;
import edu.harvard.i2b2.ontclient.datavo.i2b2result.ResultEnvelopeType;
import edu.harvard.i2b2.ontclient.datavo.i2b2result.ResultType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisPluginMetadataResponseType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisPluginMetadataTypeType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.CrcXmlResultResponseType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.MasterInstanceResultResponseType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryMasterType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.XmlValueType;




/**
 * @author Lori Phillips
 *
 */
public class GetPsmResponseMessage {
	
	public static final String THIS_CLASS_NAME = GetPsmResponseMessage.class.getName();

        private Log log = LogFactory.getLog(THIS_CLASS_NAME);	
        private ResponseMessageType respMessageType = null;
        
        public GetPsmResponseMessage() {}
    	
    	
    	public StatusType processResult(String response){	
    		StatusType status = null;
    		try {
    			JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(response);
    			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
    			
    			// Get response message status 
    			ResponseHeaderType responseHeader = respMessageType.getResponseHeader();
    			status = responseHeader.getResultStatus().getStatus();
    			String procStatus = status.getType();
    			String procMessage = status.getValue();
    			
    			if(procStatus.equals("ERROR")){
    				log.info("Error reported by Crc web Service " + procMessage);				
    			}
    			else if(procStatus.equals("WARNING")){
    				log.info("Warning reported by Crc web Service" + procMessage);
    			}	
    			
    		} catch (JAXBUtilException e) {
    			// TODO Auto-generated catch block
    			log.error(e.getMessage());
    		}
    		return status;
    	}

    	public QueryResultInstanceType extractResultInstance(String response){	
    		QueryResultInstanceType resultInstance = null;
    		try {
    			JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(response);
    			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
    			
    			// Get response message status 
    			BodyType body = respMessageType.getMessageBody();
    			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
    		
    			MasterInstanceResultResponseType responseType = (MasterInstanceResultResponseType) helper.getObjectByClass(body.getAny(),
    					MasterInstanceResultResponseType.class);
    			resultInstance = responseType.getQueryResultInstance().get(0);

    		} catch (JAXBUtilException e) {
    			// TODO Auto-generated catch block
    			log.error(e.getMessage());
    			return null;
    		}
    		return resultInstance;
    	}
	
     	public List extractXMLResult(String response){	
     		 List<DataType>  counts = null;
    		try {
    			JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(response);
    			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
    			
    			// Get response message status 
    			BodyType body = respMessageType.getMessageBody();
    			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
    		
    			CrcXmlResultResponseType responseType = (CrcXmlResultResponseType) helper.getObjectByClass(body.getAny(),
    					CrcXmlResultResponseType.class);
    			XmlValueType xml = responseType.getCrcXmlResult().getXmlValue();
    			
    		    String xmlString = (String) xml.getContent().get(0);  		    
    		    jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(xmlString);    		    
    		    ResultEnvelopeType resultEnvelopeType1 = (ResultEnvelopeType) jaxbElement.getValue();
    		    ResultType resultType = (ResultType) helper.getObjectByClass(resultEnvelopeType1.getBody().getAny(), ResultType.class);
    		    counts = resultType.getData();

    		} catch (JAXBUtilException e) {
    			// TODO Auto-generated catch block
    			log.error(e.getMessage());
    			return null;
    		}
    		return counts;
    	}
    	
     	public QueryMasterType extractQueryMaster(String response){	
    		QueryMasterType queryMaster = null;
    		try {
    			JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(response);
    			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
    			
    			// Get response message status 
    			BodyType body = respMessageType.getMessageBody();
    			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
    		
    			MasterInstanceResultResponseType responseType = (MasterInstanceResultResponseType) helper.getObjectByClass(body.getAny(),
    					MasterInstanceResultResponseType.class);
    			queryMaster = responseType.getQueryMaster();
    			

    		} catch (JAXBUtilException e) {
    			// TODO Auto-generated catch block
    			log.error(e.getMessage());
    			return null;
    		}
    		return queryMaster;
    	}

     	public AnalysisPluginMetadataTypeType extractAnalysisPluginMetadata(String response){	
    		try {
    			JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(response);
    			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
    			
    			// Get response message status 
    			BodyType body = respMessageType.getMessageBody();
    			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
    		
    			AnalysisPluginMetadataResponseType responseType = (AnalysisPluginMetadataResponseType) helper.getObjectByClass(body.getAny(),
    					AnalysisPluginMetadataResponseType.class);
    			List<AnalysisPluginMetadataTypeType> plugins = responseType.getAnalysisPluginMetadataType();
    			
    			if(plugins.isEmpty())
    				return null;
    			
    			AnalysisPluginMetadataTypeType pluginMetadata = (AnalysisPluginMetadataTypeType) plugins.get(0);	
    			return pluginMetadata;
    			
    			
    		} catch (JAXBUtilException e) {
    			// TODO Auto-generated catch block
    			log.error(e.getMessage());
    			return null;
    		}
    		
    	}
    }

    	

    
	

	
	