package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.query.data.DragAndDrop2XML;

public class GroupPanelGroupDragHandler implements DragSourceListener
{
	private Group myGroup;
	
	public GroupPanelGroupDragHandler( Group g )
	{ 
		myGroup = g;
	}
	
	// allow only groups that have content to be dragged
	public void dragStart(DragSourceEvent event) 
    {
        if ( myGroup.isContainingTerm() )
          event.doit = true;				          
        else 
          event.doit = false;
    };

    public void dragSetData(DragSourceEvent event) 
    { 
    	event.data = DragAndDrop2XML.makeDNDXMLFromGroup( myGroup );
    }

    public void dragFinished(DragSourceEvent event) 
    {}
}
