package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryMasterType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.RequestXmlType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.QueryMasterData;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;
import edu.harvard.i2b2.query.serviceClient.QueryListNamesClient;

public class QueryDropHandler implements DropTargetListener, DataConst
{
	
	private QueryDroppedDelegator myDelegator;
	
	public QueryDropHandler( QueryDroppedDelegator delegator )
	{
		myDelegator = delegator;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	/*
	 * Dropped query can come from 2 sources: Previous Query or Workplace.
	 * 	If it's from Previous Query, we get an XML with master query ID:
	 * 		<dnd:plugin_drag_drop>
	 * 			<crc/psm/1.1/:query_master>
	 * 				<query_master_id>
	 * 				<name>
	 * 				<user>
	 * 				<group_id>
	 * 
	 * If it's from Workplace, we get an XML with query definition:
	 * 		<dnd:plugin_drag_drop>
	 * 			<query_name>
	 * 			<specificity_scale>
	 * 			<panel>
	 * 				<panel_number>
	 * 				<panel_accuracy_scale>
	 * 				<invert>
	 * 				<total_item_occurrences>
	 * 				<item>
	 * 					<hlevel>
	 * 					<item_name>
	 * 					<item_key>
	 */
	public void drop(DropTargetEvent dropEvent) 
	{
		if (UIConst.DND_TEXT_TRANSFER.isSupportedType(dropEvent.currentDataType)) 
        {
        	String xmlstr = (String)dropEvent.data;        	
        	try
        	{
        		//bugbug: printout to be removed
        		//System.err.println( this.getClass().getName() + "." + this.getClass().getMethod("drop", DropTargetEvent.class ).getName() + "()" +xmlstr);
        		
    			JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
    			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(xmlstr);
    			DndType dndType = (DndType) jaxbElement.getValue();
    			QueryDefinitionType queryDefinitionType = null;
    			
    			Object xmlTest = new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(), QueryMasterType.class);
    			// see if the dropped XML contains query master id
    			if (xmlTest != null && xmlTest.getClass().getSimpleName().equalsIgnoreCase( QUERY_MASTER_TYPE )) 
    			{
    				QueryMasterType queryMasterType = (QueryMasterType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),QueryMasterType.class);
    				QueryMasterData qmData = new QueryMasterData();
    				qmData.name(queryMasterType.getName());
    				qmData.xmlContent(null);
    				qmData.id(queryMasterType.getQueryMasterId());
    				qmData.userId(queryMasterType.getUserId());

    				// retrieve query definition
    				String xmlrequest = qmData.writeDefinitionQueryXML();
    				String xmlcontent = null;
    				if (System.getProperty("webServiceMethod").equals("SOAP"))
    					xmlcontent = QueryListNamesClient.sendQueryRequestSOAP(xmlrequest);
    				else 
    					xmlcontent = QueryListNamesClient.sendQueryRequestREST(xmlrequest);

    				if (xmlcontent == null)
    					throw new Exception("Failed retrieving previous query definition: XML Content is null.");
    				else 
    					qmData.xmlContent(xmlcontent);
    				
    				System.err.println( "QueryDropHandler.drop(): " + xmlcontent );
    				
    				jaxbElement = jaxbUtil.unMashallFromString(qmData.xmlContent());
    				ResponseMessageType messageType = (ResponseMessageType) jaxbElement.getValue();

    				BodyType bt = messageType.getMessageBody();
    				MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper().getObjectByClass(bt.getAny(), MasterResponseType.class);
    				RequestXmlType requestXmlType = masterResponseType.getQueryMaster().get(0).getRequestXml();

    				org.w3c.dom.Element element = (org.w3c.dom.Element) requestXmlType.getContent().get(0);

    				String domString = edu.harvard.i2b2.common.util.xml.XMLUtil.convertDOMElementToString(element);
    				JAXBContext jc1 = JAXBContext.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
    				Unmarshaller unMarshaller = jc1.createUnmarshaller();
    				JAXBElement queryDefinitionJaxbElement = (JAXBElement) unMarshaller.unmarshal(new StringReader(domString));

    				queryDefinitionType = (QueryDefinitionType) queryDefinitionJaxbElement.getValue();
    			}
    			else
    				queryDefinitionType = (QueryDefinitionType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),QueryDefinitionType.class);    			
    			this.myDelegator.queryDropped( queryDefinitionType );    			
        	}
        	catch ( Exception e )
        	{ 
        		e.printStackTrace();
        		UIUtils.popupError("An Error Occurred while retrieving your Previous Query", e.getMessage(), "This is often a network error, please try again later." );
        	}
        }
	}

	
	@Override
	public void dragEnter(DropTargetEvent arg0) 
	{
		arg0.detail = DND.DROP_COPY;
	}

	@Override
	public void dragLeave(DropTargetEvent arg0) {}

	@Override
	public void dragOperationChanged(DropTargetEvent arg0) {}

	@Override
	public void dragOver(DropTargetEvent arg0) 
	{}

	@Override
	public void dropAccept(DropTargetEvent arg0) 
	{}

}
