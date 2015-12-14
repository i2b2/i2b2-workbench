package edu.harvard.i2b2.query.data.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;

public class PatientSetProcessor implements ProcessorConst 
{
	
	/*
	 * Example of an originalXml for a set of loose patients
	 * 
		<ns5:plugin_drag_drop xmlns:ns2="http://www.i2b2.org/xsd/hive/pdo/1.1/" xmlns:ns4="http://www.i2b2.org/xsd/cell/crc/psm/1.1/" xmlns:ns3="http://www.i2b2.org/xsd/cell/crc/pdo/1.1/" xmlns:ns5="http://www.i2b2.org/xsd/hive/plugin/" xmlns:ns6="http://www.i2b2.org/xsd/hive/msg/1.1/" xmlns:ns7="http://www.i2b2.org/xsd/cell/crc/psm/querydefinition/1.1/" xmlns:ns8="http://www.i2b2.org/xsd/cell/pm/1.1/">
		    <ns5:patient_set patient_set_name="(PrevQuery)All @02:33:00 [09-12-2013 ] [demo]" patient_set_id="3956">
		        <patient>
		            <patient_id>1000000001</patient_id>
		        </patient>
		    </ns5:patient_set>
		</ns5:plugin_drag_drop>
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static QueryConceptTreeNodeFactoryProduct process( Element mainXMLElement, String originalXml )
	{
		ArrayList<QueryConceptTreeNodeData> newNodes = new ArrayList<QueryConceptTreeNodeData>();
		List children = mainXMLElement.getChildren();
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
		//bugbug:  why do we need a loop? we only have one node
		for (Iterator itr = children.iterator(); itr.hasNext();) 
		{
			Element element = (org.jdom.Element) itr.next();
			if (element.getName().equalsIgnoreCase( PATIENTS )) 
			{
				children = element.getChildren();
				for (Iterator itr1 = children.iterator(); itr1.hasNext();) 
				{
					Element element1 = (org.jdom.Element) itr1.next();
					if (element1.getName().equalsIgnoreCase( PATIENT_ID )) 
					{
						String source = element1.getAttributeValue( SOURCE );
						if (source == null) 
							source = HIVE;
						String patientIDText = element1.getText().replace( PATIENT+":", "").replace(HIVE+":", "").trim(); // trim the heading "PATIENT:HIVE:" if it exists
						node.fullname( PATIENT + ":" + source + ":" + patientIDText);
						node.name( PATIENT + " " + source + ":" + patientIDText);
					}
				}
			}
		}
		node.finalizeOriginalXML( originalXml );
		node.visualAttribute( PT );
		newNodes.add( node );
		return new QueryConceptTreeNodeFactoryProduct(newNodes);

	}
	
}
