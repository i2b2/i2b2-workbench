/*
* Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Wensong Pan
 * 
 */
package edu.harvard.i2b2.timeline.lifelines;

import java.io.StringReader;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;

import edu.harvard.i2b2.eclipse.UserInfoBean;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.plugins.analysis.ontologyMessaging.*;

import edu.harvard.i2b2.analysis.dataModel.PDOResponseMessageModel;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.MatchStrType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.VocabRequestType;

import edu.harvard.i2b2.common.datavo.pdo.ObservationSet;
import edu.harvard.i2b2.common.datavo.pdo.ObservationType;

public class PDOQueryClient {
	private static final Log log = LogFactory.getLog(PDOQueryClient.class);
	private static EndpointReference targetEPR; 
	private static String getPDOServiceName(){
		
	    return UserInfoBean.getInstance().getCellDataUrl("CRC") + "pdorequest";
	}
	
	public static OMElement getQueryPayLoad(String str) throws Exception {
		StringReader strReader = new StringReader(str);
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader reader = xif.createXMLStreamReader(strReader);

		StAXOMBuilder builder = new StAXOMBuilder(reader);
		OMElement lineItem = builder.getDocumentElement();
		//System.out.println("Line item string " + lineItem.toString());
		return lineItem;
	}
	
	public static String sendQueryRequestREST(String XMLstr) {
		try {			
			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();

			targetEPR = new EndpointReference(getPDOServiceName());
			options.setTo(targetEPR);
            
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);					
			options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(600000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(600000));
			
			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);

			OMElement responseElement = sender.sendReceive(payload);
			//System.out.println("Client Side response " + responseElement.toString());
			
			return responseElement.toString();
			
		} 
		catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
		         	JOptionPane.showMessageDialog(null, "Trouble with connection to the remote server, " +
		         			"this is often a network error, please try again", 
		         			"Network Error", JOptionPane.INFORMATION_MESSAGE);
				}
			});				
			
			return null;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String sendQueryRequestSOAP(String XMLstr) {
		try {
			HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();


			basicAuthentication.setUsername(UserInfoBean.getInstance().getUserName());
			basicAuthentication.setPassword(UserInfoBean.getInstance().getUserPassword());

			
			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();

			// options.setProperty(HTTPConstants.PROXY, proxyProperties);
			targetEPR = new EndpointReference(getPDOServiceName());
			options.setTo(targetEPR);
				
			options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
							basicAuthentication);
            
			options.setTimeOutInMilliSeconds(900000);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

			ConfigurationContext configContext =
				ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
				
			// Blocking invocation
			ServiceClient sender = new ServiceClient(configContext, null);
			sender.setOptions(options);

			OMElement responseElement = sender.sendReceive(payload);
			//System.out.println("Client Side response " + responseElement.toString());
			
			return responseElement.toString();
			
		} 
		catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			if(axisFault.getMessage().indexOf("No route to host")>=0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		            	JOptionPane.showMessageDialog(null, "Unable to make a connection to the remote server,\n this is often a network error, please try again"
								, "Network Error", JOptionPane.INFORMATION_MESSAGE);
		            }
				});				
			}
			else if(axisFault.getMessage().indexOf("Read timed out")>=0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		            	JOptionPane.showMessageDialog(null, "Unable to obtain a response from the remote server, this is often a network error, please try again"
								, "Network Error", JOptionPane.INFORMATION_MESSAGE);
		            }
				});				
			}
			return null;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String[] getNotes(String result) {
		try {
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
			List<ObservationSet> factSets = 
				pdoresponsefactory.getFactSetsFromResponseXML(result);
			ObservationSet observationFactSet = factSets.get(0);
			ObservationType obsFactType = observationFactSet.getObservation().get(0);
			String eNotes = (String)obsFactType.getObservationBlob().getContent().get(0);
//			System.out.println("notes: "+eNotes);		
			
			return new String[]{eNotes, obsFactType.getValuetypeCd(), obsFactType.getValueflagCd().getValue(), result};
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getCodeInfo(String code) {
    	String conceptName = "";
    	try {
			VocabRequestType request = new VocabRequestType();
		
			MatchStrType match = new MatchStrType();
			match.setStrategy("exact");
			match.setValue(code);
			request.setMatchStr(match);
			request.setType("default");
			request.setBlob(false);
			request.setHiddens(true);
			request.setSynonyms(false);

			GetCodeInfoResponseMessage msg = new GetCodeInfoResponseMessage();
			StatusType procStatus = null;	
			//while(procStatus == null || !procStatus.getType().equals("DONE")){
			String response = OntServiceDriver.getCodeInfo(request, "");			
			procStatus = msg.processResult(response);
				//log.info(procStatus.getType());
				//log.info(procStatus.getValue());
				//Error processing goes here
					//procStatus.setType("DONE");
			//}
			if(procStatus.getType().equals("DONE")) {
				ConceptsType allConcepts = msg.doReadConcepts();   	    
				List concepts = allConcepts.getConcept();
				conceptName = ((ConceptType)concepts.get(0)).getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			/*java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
		         	JOptionPane.showMessageDialog(null, "Trouble with connection to the remote server, " +
		         			"this is often a network error, please try again", 
		         			"Network Error", JOptionPane.INFORMATION_MESSAGE);
				}
			});	*/			
			log.error(e.getMessage());
			return conceptName;
		}
		
		return conceptName;
    }
	
	public static void main(String[] args) throws Exception {
		PDORequestMessageFactory pdoFactory = new PDORequestMessageFactory();
				
		String xmlStr = pdoFactory.requestXmlMessage("29", "2002906454", "LCS-I2B2:c1009c", "10020626", null, "3-18-2004 12:00");
		String result = sendQueryRequestREST(xmlStr);
		
		//FileWriter fwr = new FileWriter("c:\\testdir\\response.txt");
		//fwr.write(result);
		System.out.println(result);
		getNotes(result);
	}
}
