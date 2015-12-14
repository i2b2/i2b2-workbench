package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import edu.harvard.i2b2.query.data.DataUtils;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.WorkingTreeNodeData;

public class GroupPanelConceptDragHandler implements DragSourceListener
{
	private static final boolean		IS_DEBUG		= false;
	
	private TreeViewer					myTreeViewer 	= null;
	private QueryConceptTreeNodeData 	myDraggedNode 	= null;
	
	public GroupPanelConceptDragHandler( TreeViewer tv )
	{
		myTreeViewer = tv;
	}
	
	public void dragStart(DragSourceEvent event) 
    {					
        IStructuredSelection selection = (IStructuredSelection)myTreeViewer.getSelection();
        myDraggedNode = (QueryConceptTreeNodeData)selection.getFirstElement();
                
        if ( !myTreeViewer.getSelection().isEmpty() ) 
          event.doit = true;				          
        else 
          event.doit = false;
        
        if ( myDraggedNode instanceof WorkingTreeNodeData )
        	event.doit = false;
    };

    public void dragSetData(DragSourceEvent event) 
    { 
    	event.data =  myDraggedNode.getOriginalXML();
    	
    	// Debugging printouts
    	if ( IS_DEBUG )
    	{
    		System.err.println( "GroupPanelConceptDragHandler.dragSetData(): " );
    		DataUtils.prettyPrintXMLDoc( (String)event.data, System.err );
    	}
    }

    public void dragFinished(DragSourceEvent event) 
    {}

}
