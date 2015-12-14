/*
 * Copyright (c) 2006-2015 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.ConceptDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeDataFactory;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;

public class GroupPanelConceptDropHandler implements DropTargetListener 
{
	
	private 	ConceptDroppedDelegator myDelegator;
	
	public GroupPanelConceptDropHandler( ConceptDroppedDelegator delegator )
	{
		myDelegator = delegator;
	}
	
	
	/* 12/20/2012 profiling results:
	 *  	- 50 drops of single concept. average 0.51 sec
	 *  	- high: 1.556 sec. low: 0.28. ideal: 0.16 of less
	 *  	- dropping 5 concepts at once is about 5x single drops
	 */
	@Override
	public void drop(DropTargetEvent dropEvent) 
	{
		//long tic = System.currentTimeMillis();
		QueryConceptTreeNodeFactoryProduct results = null;
        if (UIConst.DND_TEXT_TRANSFER.isSupportedType(dropEvent.currentDataType)) 
        {
        	String text = (String)dropEvent.data;
			//System.err.println( "GroupPanelConceptDropListener.drop: " +text);
			results = QueryConceptTreeNodeDataFactory.getInstance().makeTreeNodeData( text ); //bugbug: may throw org.jdom.input.JDOMParseException if text is not proper XML. Should try/catch and show error dialog here.
			
			if ( results.hasError() )
			{
				UIUtils.popupError("Error",  results.getErrorMessage(), results.getErrorReason() );
				return;
			}
        } 
        // tell the delegator do do some stuff
        myDelegator.conceptDropped( results ); 
		//System.err.println( "PROFILE: GroupPanelConceptDropListener.drop: " + (System.currentTimeMillis() - tic)/1000f + " sec");
	}

	
	@Override
	public void dragEnter(DropTargetEvent arg0) 
	{
		arg0.detail = DND.DROP_COPY;
		//System.err.println( "Drag.enter Data: "  + arg0.data );
	}

	@Override
	public void dragLeave(DropTargetEvent arg0) {}

	@Override
	public void dragOperationChanged(DropTargetEvent arg0) {}

	@Override
	public void dragOver(DropTargetEvent arg0) 
	{

	}

	@Override
	public void dropAccept(DropTargetEvent arg0) 
	{
		//System.err.println( "Drag.Accept Data: "  + arg0.data );
	}
	
}
