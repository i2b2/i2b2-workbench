package edu.harvard.i2b2.query.data.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;

public class QueryMasterProcessor implements ProcessorConst
{
	
	@SuppressWarnings("unchecked")
	public static QueryConceptTreeNodeFactoryProduct process( Element mainXMLElement, String originalXml )
	{
		ArrayList<QueryConceptTreeNodeData> newNodes = new ArrayList<QueryConceptTreeNodeData>();
		String description = null;
		List <Element> children = mainXMLElement.getChildren();
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
		//bugbug:  why do we need a loop? we only have one node
		for (Iterator <Element> itr = children.iterator(); itr.hasNext();) 
		{
			Element element = itr.next();
			if (element.getName().equalsIgnoreCase( QUERY_MASTER_ID )) 
			{
				node.fullname( MASTER_ID_PREFIX + element.getText().trim());
			} 
			else if (element.getName().equalsIgnoreCase( NAME )) 
			{
				//System.err.println("A Patient Count??");
				description = element.getText().trim();
				if ( !description.startsWith( PREV_QUERY_PREFIX ))
					description =  PREV_QUERY_PREFIX + description;
				node.name( description );
				node.tooltip(description);
			}
		}
		node.finalizeOriginalXML( originalXml );
		node.visualAttribute( PQ );
		
		newNodes.add( node );
		return new QueryConceptTreeNodeFactoryProduct(newNodes);
	}
	
}
