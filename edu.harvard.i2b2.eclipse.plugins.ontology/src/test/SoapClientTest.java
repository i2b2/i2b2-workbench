
package test;

import java.io.DataInputStream;
import java.io.FileInputStream;

import javax.xml.stream.FactoryConfigurationError;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetNameInfoRequestMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.ontclient.datavo.vdo.MatchStrType;
import edu.harvard.i2b2.ontclient.datavo.vdo.VocabRequestType;

public class SoapClientTest {
	
	private static EndpointReference targetEPR = new EndpointReference(
				"https://localhost:8080/Services/OntServices.asmx"
			);

	
	public static String getQueryString() throws Exception  { 
		StringBuffer queryStr = new StringBuffer();
		DataInputStream dataStream = new DataInputStream(new FileInputStream("rpdr/getTermInfo_request.xml"));  //reqTest.xml
		while(dataStream.available()>0) {
			queryStr.append(dataStream.readLine());// + "\n");
		}
		//System.out.println("queryStr: " + queryStr);
		return //"<HelloWorld  xmlns=\"http://rpdr.partners.org/\">" 
		  //+"<PatientDataRequestXml>"
		  //+"<string>"
		  //+"<?xml version=\"1.0\" encoding=\"utf-8\" ?>" 
		  //+"<string xmlns=\"http://rpdr.partners.org/\">"
		  //+"<TestXMLString>"
	      queryStr.toString();
			//+"</TestXMLString>"
		  //+""
	      //+"</string>"
	      //+"</PatientDataRequestXml>"
	      //+"</HelloWorld >";
		//return queryStr.toString();
	
	}
	
	public static String generateQueryString() throws Exception {

/*	 getCategories request
 * 		GetReturnType data = new GetReturnType();
		data.setType("core");
		data.setBlob(false);*/
		
		// getChildren request
		 //		GetChildrenType data = new GetChildrenType();
		//		data.setType("core");
		//		data.setBlob(false);
		//		data.setParent("\\\\i2b2\\RPDR\\Diagnosis\\Circulatory system (390-459)");
		
		// getTermInfo request
		/*  		GetTermInfoType data = new GetTermInfoType();
				data.setType("core");
				data.setBlob(false);
				data.setSelf("\\\\i2b2\\RPDR\\drg\\(20) Alcohol and Drug Abuse");
		*/
				// getNameInfo request
				  		VocabRequestType data = new VocabRequestType();
						data.setCategory("diagnosis");
						data.setBlob(false);
					    data.setMax(100);
					    MatchStrType match = new MatchStrType();
					    match.setStrategy("contains");
					    match.setValue("asthma");
					    data.setMatchStr(match);
			
	
		
		String requestString = null;
		
	//	GetCategoriesRequestMessage reqMsg = new GetCategoriesRequestMessage();
	//	GetChildrenRequestMessage reqMsg = new GetChildrenRequestMessage();
	//	GetTermInfoRequestMessage reqMsg = new GetTermInfoRequestMessage();
		GetNameInfoRequestMessage reqMsg = new GetNameInfoRequestMessage();
						try {
							MessageHeaderType messageHeader = reqMsg.getMessageHeader(); 
							messageHeader.getSecurity().setDomain("partners");			 
							messageHeader.getSecurity().setUsername("demo");
							//
//							messageHeader.getSecurity().setPassword("demouser");
							RequestHeaderType reqHeader  = reqMsg.getRequestHeader();
							BodyType bodyType = reqMsg.getBodyType(data) ;
							RequestMessageType reqMessageType = reqMsg.getRequestMessageType(messageHeader,
									reqHeader, bodyType);
							requestString = reqMsg.getXMLString(reqMessageType);
						} catch (JAXBUtilException e) {
							System.out.println(e.getMessage());
						} 
					 

		//	System.out.println(getCategoriesRequestString); 
			return requestString;
		}
		
	public static void main(String[] args) throws Exception {
		ServiceClient client = new ServiceClient();
		OperationClient operationClient = client
				.createClient(ServiceClient.ANON_OUT_IN_OP);
		
		// creating message context
		MessageContext outMsgCtx = new MessageContext();
		// assigning message context's option object into instance variable
		Options opts = outMsgCtx.getOptions();
		// setting properties into option
		opts.setTo(targetEPR);
		opts.setAction("http://rpdr.partners.org/GetTermInfo");
		opts.setTimeOutInMilliSeconds(180000);
		
		SOAPEnvelope request = createSOAPEnvelope(getQueryString());
	//	SOAPEnvelope request = createSOAPEnvelope(generateQueryString());
		System.out.println("request: "+ request);
		outMsgCtx.setEnvelope(request);
		
		operationClient.addMessageContext(outMsgCtx);
		operationClient.execute(true);
		
		MessageContext inMsgtCtx = operationClient.getMessageContext("In");
		SOAPEnvelope response = inMsgtCtx.getEnvelope();
		System.out.println("response: "+response.getBody().getFirstElement().toStringWithConsume());
	}

	public static SOAPEnvelope createSOAPEnvelope(String xmlStr) {
		SOAPEnvelope envelope = null;
	
		try {
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/",                                   
					"rpdr");
			// creating the payload
			OMElement method = fac.createOMElement("GetTermInfo", omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(xmlStr);
			method.addChild(value);
			envelope.getBody().addChild(method);
		}
		catch (FactoryConfigurationError e) {
		
			e.printStackTrace();
			return envelope;
			//log.error(e.getMessage());
			//throw new Exception(e);
		}
		
		return envelope;
	}
}
