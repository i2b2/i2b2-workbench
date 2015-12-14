package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.GroupDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DragAndDrop2XML;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;

public class GroupPanelGroupDropHandler implements DropTargetListener 
{
	
	private Group 					myGroup;
	private GroupDroppedDelegator 	myUIDelegator; // tells the UI to update itself
	
	public GroupPanelGroupDropHandler( Group group, GroupDroppedDelegator uiDelegator )
	{
		myGroup = group;
		myUIDelegator = uiDelegator;
	}
	
	@Override
	public void drop(DropTargetEvent arg0) 
	{
		//long tic = System.currentTimeMillis();
		//System.err.println("GroupPanelGroupDropHandler: Group Dropped.");
		//QueryConceptTreeNodeFactoryProduct results = null;
        if (UIConst.DND_TEXT_TRANSFER.isSupportedType(arg0.currentDataType)) 
        {
        	String groupXML = (String)arg0.data;
        	//System.err.println( "GroupPanelGroupDropHandler.drop: " +groupXML);
        	DragAndDrop2XML.setGroupWithDNDXMLAfterDrop(myGroup, groupXML );
        	
        	myUIDelegator.groupDropped();
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
	public void dragOver(DropTargetEvent arg0) {}

	@Override
	public void dropAccept(DropTargetEvent arg0) {}

}
