/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.explorer.serviceClient;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JOptionPane;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.explorer.utils.MessageUtil;
import edu.harvard.i2b2.explorer.dataModel.PDOItem;
import edu.harvard.i2b2.explorer.dataModel.PDORequestMessageModel;
import edu.harvard.i2b2.explorer.dataModel.PDOResponseMessageModel;
import edu.harvard.i2b2.explorer.dataModel.PDOValueModel;
import edu.harvard.i2b2.explorer.dataModel.TimelineFactory;
import edu.harvard.i2b2.explorer.dataModel.TimelineRow;
import edu.harvard.i2b2.explorer.ui.MainComposite;

public class PDOQueryClient {
	private static final Log log = LogFactory.getLog(PDOQueryClient.class);

	private static EndpointReference targetEPR;

	private static String getPDOServiceName() {
		return UserInfoBean.getInstance().getCellDataUrl("CRC") + "pdorequest";
	}

	private static String getPDQServiceName() {
		return UserInfoBean.getInstance().getCellDataUrl("CRC") + "request";
	}

	public static OMElement getQueryPayLoad(String str) throws Exception {
		StringReader strReader = new StringReader(str);
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader reader = xif.createXMLStreamReader(strReader);

		StAXOMBuilder builder = new StAXOMBuilder(reader);
		OMElement lineItem = builder.getDocumentElement();
		// log.debug("Line item string " + lineItem.toString());
		return lineItem;
	}

	public static String sendPDQQueryRequestREST(String XMLstr) {
		try {
			MessageUtil.getInstance().setRequest(
					"URL: " + getPDQServiceName() + "\n" + XMLstr);

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();

			targetEPR = new EndpointReference(getPDQServiceName());
			options.setTo(targetEPR);

			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(600000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					600000));

			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);

			OMElement responseElement = sender.sendReceive(payload);
			MessageUtil.getInstance().setResponse(
					"URL: " + getPDQServiceName() + "\n"
							+ responseElement.toString());

			return responseElement.toString();
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					JOptionPane
							.showMessageDialog(
									null,
									"Trouble with connection to the remote server, "
											+ "this is often a network error, please try again",
									"Network Error",
									JOptionPane.INFORMATION_MESSAGE);
				}
			});

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendPDQQueryRequestSOAP(String XMLstr) {
		try {
			MessageUtil.getInstance().setRequest(
					"URL: " + getPDQServiceName() + "\n" + XMLstr);

			HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();

			basicAuthentication.setUsername(UserInfoBean.getInstance()
					.getUserName());
			basicAuthentication.setPassword(UserInfoBean.getInstance()
					.getUserPassword());

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();

			// options.setProperty(HTTPConstants.PROXY, proxyProperties);
			targetEPR = new EndpointReference(getPDQServiceName());
			options.setTo(targetEPR);

			options.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
					basicAuthentication);

			options.setTimeOutInMilliSeconds(900000);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

			ConfigurationContext configContext =

			ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(null, null);

			// Blocking invocation
			ServiceClient sender = new ServiceClient(configContext, null);
			sender.setOptions(options);

			OMElement responseElement = sender.sendReceive(payload);
			// log.debug("Client Side response " + responseElement.toString());

			MessageUtil.getInstance().setResponse(
					"URL: " + getPDQServiceName() + "\n"
							+ responseElement.toString());
			return responseElement.toString();

		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			if (axisFault.getMessage().indexOf("No route to host") >= 0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										null,
										"Unable to make a connection to the remote server,\n this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			} else if (axisFault.getMessage().indexOf("Read timed out") >= 0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										null,
										"Unable to obtain a response from the remote server, this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendPDOQueryRequestREST(String XMLstr) {
		try {
			
				SAXBuilder parser = new SAXBuilder();
				String xmlContent = XMLstr;
				java.io.StringReader xmlStringReader = new java.io.StringReader(
						xmlContent);
				org.jdom.Document tableDoc = parser.build(xmlStringReader);
				XMLOutputter o = new XMLOutputter();
				o.setFormat(Format.getPrettyFormat());
				StringWriter str = new StringWriter();
				o.output(tableDoc, str);
				//jMessageTextArea.setText(str.toString());
				//text.setText(str.toString());
			
			MessageUtil.getInstance().setRequest(
					"URL: " + getPDOServiceName() + "\n" + str);
			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();

			targetEPR = new EndpointReference(getPDOServiceName());
			options.setTo(targetEPR);

			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(600000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					600000));

			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);

			OMElement responseElement = sender.sendReceive(payload);
			// log.debug("Client Side response " + responseElement.toString());
			xmlStringReader = new java.io.StringReader(
					responseElement.toString());
			tableDoc = parser.build(xmlStringReader);
			o = new XMLOutputter();
			o.setFormat(Format.getPrettyFormat());
			str = new StringWriter();
			o.output(tableDoc, str);
			MessageUtil.getInstance().setResponse(
					"URL: " + getPDOServiceName() + "\n"
							+ str);  //responseElement.toString());

			return responseElement.toString();

		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					JOptionPane
							.showMessageDialog(
									null,
									"Trouble with connection to the remote server, "
											+ "this is often a network error, please try again",
									"Network Error",
									JOptionPane.INFORMATION_MESSAGE);
				}
			});

			return null;
		} catch (java.lang.OutOfMemoryError e) {
			e.printStackTrace();
			return "memory error";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendPDOQueryRequestSOAP(String XMLstr) {
		try {
			MessageUtil.getInstance().setRequest(
					"URL: " + getPDOServiceName() + "\n" + XMLstr);
			ServiceClient sender = AxisServiceClient.getServiceClient();
			OperationClient operationClient = sender
					.createClient(ServiceClient.ANON_OUT_IN_OP);

			// creating message context
			MessageContext outMsgCtx = new MessageContext();
			// assigning message context's option object into instance variable
			Options opts = outMsgCtx.getOptions();
			// setting properties into option

			targetEPR = new EndpointReference(UserInfoBean.getInstance()
					.getCellDataUrl("IM"));

			log.debug(targetEPR);
			opts.setTo(targetEPR);
			opts.setAction("http://rpdr.partners.org/GetPatientDataObject");
			opts.setTimeOutInMilliSeconds(180000);

			log.debug(XMLstr);

			SOAPEnvelope envelope = null;
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/", "rpdr");

			// creating the SOAP payload
			OMElement method = fac
					.createOMElement("GetPatientDataObject", omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(XMLstr);
			method.addChild(value);
			envelope.getBody().addChild(method);

			outMsgCtx.setEnvelope(envelope);

			operationClient.addMessageContext(outMsgCtx);
			operationClient.execute(true);

			MessageContext inMsgtCtx = operationClient.getMessageContext("In");
			SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();

			OMElement soapResponse = responseEnv.getBody().getFirstElement();
			// log.debug("Sresponse: "+ soapResponse.toString());
			OMElement soapResult = soapResponse.getFirstElement();
			// log.debug("Sresult: "+ soapResult.toString());

			String i2b2Response = soapResult.getText();
			log.debug(i2b2Response);
			MessageUtil.getInstance().setResponse(
					"URL: " + getPDOServiceName() + "\n" + i2b2Response);
			return i2b2Response;

		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			if (axisFault.getMessage().indexOf("No route to host") >= 0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										null,
										"Unable to make a connection to the remote server,\n this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			} else if (axisFault.getMessage().indexOf("Read timed out") >= 0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										null,
										"Unable to obtain a response from the remote server, this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getlldString(ArrayList<TimelineRow> tlrows,
			String patientRefId, int minPatient, int maxPatient,
			boolean bDisplayAll, boolean writeFile,
			boolean displayDemographics, MainComposite explorer,
			String filter) {

		try {
			HashSet<String> conceptPaths = new HashSet<String>();
			// HashSet<String> providerPaths = new HashSet<String>();
			// HashSet<String> visitPaths = new HashSet<String>();
			ArrayList<PDOItem> items = new ArrayList<PDOItem>();

			for (int i = 0; i < tlrows.size(); i++) {
				for (int j = 0; j < tlrows.get(i).pdoItems.size(); j++) {
					PDOItem pdoItem = tlrows.get(i).pdoItems.get(j);
					String path = pdoItem.fullPath;

					if (conceptPaths.contains(path)) {
						//continue;
					}
					conceptPaths.add(path);
					// for(int k=0; k<pdoItem.valDisplayProperties.size(); k++)
					// {
					items.add(pdoItem);
					// }
				}
			}

			PDORequestMessageModel pdoFactory = new PDORequestMessageModel();
			String pid = null;
			if (patientRefId.equalsIgnoreCase("All")) {
				pid = "-1";
			} else {
				pid = patientRefId;
			}
			String xmlStr = pdoFactory.requestXmlMessage(items, pid,
					new Integer(minPatient), new Integer(maxPatient), false, filter);
			// explorer.lastRequestMessage(xmlStr);

			String result = null;// sendPDOQueryRequestREST(xmlStr);
			if (System.getProperty("webServiceMethod").equals("SOAP")) {
				result = PDOQueryClient.sendPDOQueryRequestSOAP(xmlStr);
			} else {
				result = PDOQueryClient.sendPDOQueryRequestREST(xmlStr);
			}

			if (result == null || result.equalsIgnoreCase("memory error")) {
				return result;
			}
			// explorer.lastResponseMessage(result);

			return new TimelineFactory().generateTimelineData(result, tlrows,
					writeFile, bDisplayAll, displayDemographics, explorer);
		}
		/*
		 * catch(org.apache.axis2.AxisFault e) { e.printStackTrace();
		 * java.awt.EventQueue.invokeLater(new Runnable() { public void run() {
		 * JOptionPane.showMessageDialog(null,
		 * "Trouble with connection to the remote server, " +
		 * "this is often a network error, please try again", "Network Error",
		 * JOptionPane.INFORMATION_MESSAGE); } });
		 * 
		 * return null; }
		 */
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void testWriteTableFile(String result) {
		StringBuilder resultFile = new StringBuilder();

		try {
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
			PatientSet patientDimensionSet = pdoresponsefactory
					.getPatientSetFromResponseXML(result);
			if (patientDimensionSet != null) {
				log.debug("Total patient: "
						+ patientDimensionSet.getPatient().size());
				for (int i = 0; i < patientDimensionSet.getPatient().size(); i++) {
					PatientType patientType = patientDimensionSet.getPatient()
							.get(i);
					log.debug("PatientNum: " + patientType.getPatientId());
					// + ","+patientType.getRaceCd()
					// +","+patientType.getSexCd()
					// +","+patientType.getAgeInYearsNum()
					// +","+patientType.getVitalStatusCd());
					resultFile.append(patientType.getPatientId());
					// +","+patientType.getRaceCd()
					// +","+patientType.getSexCd()
					// +","+patientType.getAgeInYearsNum()
					// +","+patientType.getVitalStatusCd()+"\n");
				}
			}

			/*
			 * List<PatientDataType.ObservationFactSet> factSets =
			 * pdoresponsefactory.getFactSetsFromResponseXML(result);
			 * 
			 * for(int j=0; j<factSets.size(); j++) {
			 * PatientDataType.ObservationFactSet observationFactSet =
			 * factSets.get(j);
			 * //pdoresponsefactory.getFactSetFromResponseXML(result); if
			 * (observationFactSet != null) {
			 * log.debug("Total fact: "+observationFactSet
			 * .getObservationFact().size()
			 * +" for "+observationFactSet.getPath()
			 * +"-"+observationFactSet.getConceptName()); for(int i=0;
			 * i<observationFactSet.getObservationFact().size(); i++) {
			 * ObservationFactType obsFactType =
			 * observationFactSet.getObservationFact().get(i);
			 * log.debug("PatientNum: "+obsFactType.getPatientNum()
			 * +" concept_cd: " + obsFactType.getConceptCd() +" start_date: " +
			 * obsFactType.getStartDate().getYear()
			 * +"_"+obsFactType.getStartDate().getMonth()
			 * +"_"+obsFactType.getStartDate().getDay()
			 * +"_"+obsFactType.getNvalNum() +"_"+obsFactType.getConceptCd()
			 * +"_"+obsFactType.getPatientNum()); } } }
			 */

			String tableFile = "C:\\tableview\\data\\patienttable.txt";
			File oDelete = new File(tableFile);
			if (oDelete != null)
				oDelete.delete();
			RandomAccessFile f = new RandomAccessFile(tableFile, "rw");
			TimelineFactory.append(f, "PatientNumber,Race,Sex,Age,Dead\n");
			TimelineFactory.append(f, resultFile.toString());
			f.close();

			/*
			 * log.debug("\nTesting lld:"); for(int i=0;
			 * i<patientDimensionSet.getPatientDimension().size();i++) {
			 * PatientDimensionType patientType =
			 * patientDimensionSet.getPatientDimension().get(i); Integer pnum =
			 * patientType.getPatientNum(); log.debug("PatientNum: " +
			 * patientType.getPatientNum());
			 * 
			 * for(int j=0; j<factSets.size(); j++) {
			 * PatientDataType.ObservationFactSet observationFactSet =
			 * factSets.get(j); String path = observationFactSet.getPath();
			 * StringBuilder resultString = new StringBuilder(); int total = 0;
			 * XMLGregorianCalendar curStartDate = null; for(int k=0;
			 * k<observationFactSet.getObservationFact().size(); k++) {
			 * ObservationFactType obsFactType =
			 * observationFactSet.getObservationFact().get(k);
			 * 
			 * if(pnum.intValue() == obsFactType.getPatientNum().intValue()) {
			 * if((curStartDate != null) &&
			 * (obsFactType.getStartDate().compare(curStartDate) ==
			 * DatatypeConstants.EQUAL)) { continue; }
			 * 
			 * resultString.append("PatientNum: "+obsFactType.getPatientNum()
			 * //+" for "+path +" concept_cd: " + obsFactType.getConceptCd()
			 * +" start_date: " + obsFactType.getStartDate().getYear()
			 * +"_"+obsFactType.getStartDate().getMonth()
			 * +"_"+obsFactType.getStartDate().getDay()
			 * +"_"+obsFactType.getStartDate().getHour()
			 * +":"+obsFactType.getStartDate().getMinute()
			 * +"_"+obsFactType.getNvalNum() +"_"+obsFactType.getConceptCd()
			 * +"_"+obsFactType.getPatientNum()
			 * +"_"+obsFactType.getEndDate()+"\n"); total++; curStartDate =
			 * obsFactType.getStartDate(); } }
			 * 
			 * log.debug("-- "+path+" has "+total+" events");
			 * log.debug(resultString.toString()); } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		PDORequestMessageModel pdoFactory = new PDORequestMessageModel();
		String conceptPath = new String(
				"\\RPDR\\Labtests\\LAB\\(LLB16) Chemistry\\(LLB21) General Chemistries\\CA");

		ArrayList<String> paths = new ArrayList<String>();
		// paths.add(conceptPath);

		conceptPath = new String(
				"\\RPDR\\Labtests\\LAB\\(LLB16) Chemistry\\(LLB21) General Chemistries\\GGT");
		paths.add(conceptPath);

		// conceptPath = new
		// String("\\RPDR\\Labtests\\LAB\\(LLB16) Chemistry\\(LLB21) General Chemistries\\GGT");
		// paths.add(conceptPath);
		ArrayList<String> ppaths = new ArrayList<String>();
		conceptPath = new String("\\Providers\\BWH");
		// ppaths.add(conceptPath);

		String xmlStr = pdoFactory.requestXmlMessage(null, "1545", new Integer(
				1), new Integer(20), false, "");
		String result = sendPDOQueryRequestREST(xmlStr);

		// FileWriter fwr = new FileWriter("c:\\testdir\\response.txt");
		// fwr.write(result);
		log.debug(result);

		PDOItem set = new PDOItem();
		set.fullPath = "\\RPDR\\Labtests\\LAB\\(LLB16) Chemistry\\(LLB21) General Chemistries\\CA";
		set.hasValueDisplayProperty = true;
		PDOValueModel valdp = new PDOValueModel();
		valdp.left = 0.0;
		valdp.right = 8.4;
		valdp.color = "red";
		valdp.height = "Very Low";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 8.4;
		valdp.right = 8.9;
		valdp.color = "gold";
		valdp.height = "Low";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 8.9;
		valdp.right = 10.0;
		valdp.color = "green";
		valdp.height = "Medium";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 10.0;
		valdp.right = 10.6;
		valdp.color = "gold";
		valdp.height = "Tall";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 10.6;
		valdp.right = Integer.MAX_VALUE;
		valdp.color = "red";
		valdp.height = "Very Tall";
		set.valDisplayProperties.add(valdp);
		set.tableType = "fact";

		TimelineRow row = new TimelineRow();
		row.pdoItems.add(set);
		row.displayName = "Calcium (Group:CA)";

		ArrayList<TimelineRow> rows = new ArrayList<TimelineRow>();
		rows.add(row);

		set = new PDOItem();
		set.fullPath = "\\RPDR\\Labtests\\LAB\\(LLB16) Chemistry\\(LLB21) General Chemistries\\GGT";
		set.hasValueDisplayProperty = true;
		valdp = new PDOValueModel();
		valdp.left = 0.0;
		valdp.right = 1.0;
		valdp.color = "red";
		valdp.height = "Very Low";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 1.0;
		valdp.right = 19.0;
		valdp.color = "gold";
		valdp.height = "Low";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 19.0;
		valdp.right = 34.0;
		valdp.color = "green";
		valdp.height = "Medium";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 34.0;
		valdp.right = 82.0;
		valdp.color = "gold";
		valdp.height = "Tall";
		set.valDisplayProperties.add(valdp);

		valdp = new PDOValueModel();
		valdp.left = 82.0;
		valdp.right = Integer.MAX_VALUE;
		valdp.color = "red";
		valdp.height = "Very Tall";
		set.valDisplayProperties.add(valdp);
		set.tableType = "fact";

		row = new TimelineRow();
		row.pdoItems.add(set);
		row.displayName = "Gamma Glutamyltrans (Group:GGT)";

		rows.add(row);

		// testWritelld(result, rows, true);
		testWriteTableFile(result);
	}
}
