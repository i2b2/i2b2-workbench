/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.workplace.ws;

import java.io.StringWriter;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.WorkplaceJAXBUtil;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FoldersType;

abstract public class WorkplaceResponseData {

	public static final String THIS_CLASS_NAME = WorkplaceResponseData.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	
    private ResponseMessageType respMessageType = null;
    
	public WorkplaceResponseData() {}
	
	public StatusType processResult(String response){	
		StatusType status = null;
		try {
			JAXBElement jaxbElement = WorkplaceJAXBUtil.getJAXBUtil().unMashallFromString(response);
			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			
			// Get response message status 
			ResponseHeaderType responseHeader = respMessageType.getResponseHeader();
			status = responseHeader.getResultStatus().getStatus();
			String procStatus = status.getType();
			String procMessage = status.getValue();
			
			if(procStatus.equals("ERROR")){
				log.error("Error reported by Workplace web Service " + procMessage);				
			}
			else if(procStatus.equals("WARNING")){
				log.error("Warning reported by Workplace web Service" + procMessage);
			}	
			
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return status;
	}

	public FoldersType doReadFolders(){
		FoldersType folders = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
				folders = (FoldersType)helper.getObjectByClass(bodyType.getAny(), FoldersType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return folders;		
	}	
	
	public String getXMLString(FolderType folder)throws Exception{ 
		StringWriter strWriter =  new StringWriter();
		try {
			WorkplaceJAXBUtil.getJAXBUtil().marshaller((Object)folder, strWriter);
		} catch (JAXBUtilException e) {
			log.error("Error marshalling Workplace folder");
			throw new JAXBUtilException(e.getMessage(), e);
		} 
		return strWriter.toString();
	}
}

	
