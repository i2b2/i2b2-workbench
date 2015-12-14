package edu.harvard.i2b2.query.data.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.jdom.Document;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FolderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FoldersType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType;
import edu.harvard.i2b2.eclipse.plugins.query.utils.XmlUtil;
import edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging.WorkplaceServiceDriver;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;

public class FoldersProcessor implements ProcessorConst
{
	
	public static QueryConceptTreeNodeFactoryProduct process( Document xmlDoc, String originalXml )
	{
		ArrayList<QueryConceptTreeNodeData> newNodes = new ArrayList<QueryConceptTreeNodeData>();
		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		FolderType folder = null;
		try 
		{
			DndType dndType = (DndType) (jaxbUtil.unMashallFromString(originalXml)).getValue();
			folder = ((FoldersType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),FoldersType.class)).getFolder().get(0);
		} 
		catch (JAXBUtilException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			GetChildrenType parentType = new GetChildrenType();
			parentType.setBlob(true);
			parentType.setParent(folder.getIndex());
			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;
			
			//bugbug: does this loop do anything (aside from msg.processResult(...)??
			while (procStatus == null || !procStatus.getType().equals( DONE )) 
			{
				String response = WorkplaceServiceDriver.getChildren(parentType);
				procStatus = msg.processResult(response);
				if (procStatus.getValue().equals( MAX_EXCEEDED )) 
				{
				} 
				else if (procStatus.getType().equals( ERROR )) 
				{
					System.setProperty(ERROR_MESSAGE,procStatus.getValue());
				}
			}
			
			FoldersType allFolders = msg.doReadFolders();
			if (allFolders != null) 
			{
				List<FolderType> folders1 = allFolders.getFolder();
				String id = null;
				for (int i = 0; i < folders1.size(); i++) 
				{
					id = XmlUtil.getPatientId(folders1.get(i).getWorkXml());
					if (id == null)
						continue;
					QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
					String source = XmlUtil.getSiteId(folders1.get(i).getWorkXml());
					if (source == null) 
						source = HIVE;
					id = XmlUtil.getPatientId(folders1.get(i).getWorkXml());
					node.fullname(PATIENT + ":" + source + ":" + id);
					node.name( PATIENT + " " + source + ":" + id );
					node.finalizeOriginalXML(folders1.get(i).getWorkXml().toString());
					node.visualAttribute( PT );
					newNodes.add( node );
				}
			}
		} 
		catch (AxisFault e) 
		{ e.printStackTrace(); } 
		catch (Exception e) 
		{ e.printStackTrace(); }
		return new QueryConceptTreeNodeFactoryProduct(newNodes);

	}
}
