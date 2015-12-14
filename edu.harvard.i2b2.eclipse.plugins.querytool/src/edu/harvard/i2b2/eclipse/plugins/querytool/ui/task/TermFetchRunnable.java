package edu.harvard.i2b2.eclipse.plugins.querytool.ui.task;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.GroupPanel;
import edu.harvard.i2b2.eclipse.plugins.querytool.utils.DefaultDaemonThreadFactory;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.WorkingTreeNodeData;
import edu.harvard.i2b2.query.data.processor.OntologyTermProcessor;
import edu.harvard.i2b2.query.data.processor.ProcessorConst;

public class TermFetchRunnable implements Runnable 
{
	private static final boolean IS_ERROR_DEBUG = false;

	private static final Random RANDOM = new Random();

	private final ScheduledExecutorService 	myRedrawScheduler 		= Executors.newScheduledThreadPool( 1, new DefaultDaemonThreadFactory("TreeNodeRedrawer") );	// display cfind indicator so users don't think it's stuck
	private ScheduledFuture	<?>				myRedrawFuture			= null;

	private GroupPanel					myGroupPanel;
	private TreeViewer					myViewer;
	private QueryConceptTreeNodeData 	myParent;
	private WorkingTreeNodeData 		myWorkingChild;
	private boolean						cancel  = false;


	public TermFetchRunnable( GroupPanel panel, TreeViewer viewer, QueryConceptTreeNodeData parent, WorkingTreeNodeData child )
	{
		myGroupPanel	= panel;
		myParent 		= parent;
		myWorkingChild	= child;
		myViewer		= viewer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run()
	{
		myRedrawFuture = myRedrawScheduler.scheduleAtFixedRate(new AnimatingRunnable( myViewer, myWorkingChild ), 100, 160, TimeUnit.MILLISECONDS);
		try
		{
			ArrayList<QueryConceptTreeNodeData> childrenNodes = new ArrayList<QueryConceptTreeNodeData>();
			String childrenConceptXML = getChildren( myParent );
			SAXBuilder parser = new SAXBuilder();
			final Document xmlDoc = parser.build(new StringReader( childrenConceptXML ));
			// doc format
			/*
			<response>
				<message_body>
					<concepts>
						<concept>
						*/
			for ( Iterator <Element> it = xmlDoc.getDescendants( new ElementFilter("concept") ) ;it.hasNext(); )
			{
				if ( this.cancel )
				{
					handleCancelation();
					return;
				}
				Element conceptElement = it.next();
				QueryConceptTreeNodeData node =  OntologyTermProcessor.constructQueryConceptTreeNodeData( myParent, conceptElement );
				// create an empty document that has <dnd:plugin_drag_drop> followed by <concepts> followed by the <concept> in question
				Document singleConceptDoc = new Document();
				singleConceptDoc.setRootElement( new Element(ProcessorConst.PLUGIN_DRAG_DROP, ProcessorConst.DND_PREFIX, ProcessorConst.DND_NAMESPACE ) );
				Element newConceptNode = (Element)conceptElement.clone();
				
				Element conceptsNode = new Element(ProcessorConst.CONCEPTS, ProcessorConst.ONT_PREFIX, ProcessorConst.ONT_NAMESPACE );
				newConceptNode.detach();
				conceptsNode.addContent( newConceptNode );				
				singleConceptDoc.getRootElement().addContent( conceptsNode );
				
				// for printing uses
				//StringWriter writerz = new StringWriter();
				//DataUtils.prettyPrintXMLDoc( singleConceptDoc, writerz );
				//System.err.println("GroupPanel.TermFetchRunnable.run: singleConceptDocument: \n" + writerz.toString() );
				
				// randomly generate exceptions when ERROR_DEBUG is on
				if ( IS_ERROR_DEBUG )
					if (RANDOM.nextFloat() < 0.05f)
						throw new IOException("Randomly Generated Test Exception");

				// check the conceptElement to see if XML is complete, if not , we hit ONT to get the metadataXML (if value restrictions are allowed for this Concept)
				OntologyTermProcessor.completeXMLIfNecessary( singleConceptDoc, conceptElement, node );					// now the node is guaranteed to have the comlpete XML, we parse it to set the Value Property schema
				node.parseOriginalXMLAndSetValuePropertySchema();
				childrenNodes.add(node);	// add node with full XML
			}
			if ( this.cancel )
			{
				handleCancelation();
				return;
			}
			handleSuccess( childrenNodes );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			handleException( e );
		}
	}

	protected void handleSuccess( ArrayList<QueryConceptTreeNodeData> childrenNodes )
	{
		for ( QueryConceptTreeNodeData child : childrenNodes )
			this.myParent.addChild( child );
		
		// stop the drawing thread and shutdown the executor service
		this.myRedrawFuture.cancel( true );
		this.myRedrawScheduler.shutdownNow();
		this.myParent.removeChild( this.myWorkingChild );
		
		final int childrenSize = childrenNodes.size();
		Display.getDefault().asyncExec( new Runnable()
		{
			@Override
			public void run() 
			{
				//System.err.println("refreshing parent");
				myViewer.refresh( myParent, true );
				myGroupPanel.addToRowCounts( childrenSize-1 ); // remove the working node, so -1
				myGroupPanel.setMaxAllowablePanelHeight();
			}
		});
	}

	protected void handleException( Exception e )
	{
		System.err.println( "roupPanel.TermFetchRunnable.handleException(): " + e.getMessage() );
		this.myRedrawFuture.cancel( true );
		this.myRedrawScheduler.shutdownNow();
		myWorkingChild.name( WorkingTreeNodeData.DEFAULT_FAILED_LABEL );
		myWorkingChild.visualAttribute( ProcessorConst.ICON_WARNING );
		((WorkingTreeNodeData)myWorkingChild).setState( WorkingTreeNodeData.WorkingTreeNodeState.FAILED );

		Display.getDefault().asyncExec( new Runnable()
		{
			@Override
			public void run() 
			{
				myGroupPanel.setMaxAllowablePanelHeight();
				myViewer.refresh( myWorkingChild, true );
			}
		});
	}

	public void cancel()
	{
		this.cancel = true;
		this.myWorkingChild.name("Canceling...");
		Widget item = myViewer.testFindItem(myWorkingChild);

		if ( !myViewer.getTree().isDisposed() &&  item != null && !item.isDisposed())
		{
			Display.getDefault().asyncExec( new Runnable()
			{
				@Override
				public void run() 
				{				
					myViewer.refresh( myWorkingChild, true );
				}
			});
		}
	}

	protected void handleCancelation()
	{
		if ( myRedrawFuture != null )
		{
			this.myRedrawScheduler.shutdownNow();
			this.myParent.removeChild( this.myWorkingChild );
			Display.getDefault().asyncExec( new Runnable()
			{
				@Override
				public void run() 
				{
					myViewer.setExpandedState( myParent, false ); // important! only set expand state to false will allow the fetch to be restarted again later
					myViewer.refresh( myParent, true );
				}
			});
		}
	}

	private String getChildren( QueryConceptTreeNodeData data) throws Exception 
	{
		GetChildrenType parentType = new GetChildrenType();
		parentType.setHiddens(Boolean.parseBoolean(System.getProperty("OntHiddens")));
		parentType.setSynonyms(Boolean.parseBoolean(System.getProperty("OntSynonyms")));
		parentType.setMax( null ); // set no limits
		parentType.setBlob(true);
		parentType.setParent(data.fullname());
		
		GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
		StatusType procStatus = null;
		String response = OntServiceDriver.getChildren(parentType, "");
		procStatus = msg.processResult(response);
		if (!procStatus.getType().equals("DONE")) 
			throw new Exception("Error returned from server. Status = " + procStatus.getType() );
		return response;	// return the XML containing children cocspets
	}

	class AnimatingRunnable implements Runnable
	{
		TreeViewer					myViewer;
		WorkingTreeNodeData 		myNode;
		int							counter 		= 0;
		int							textCounter		= 0;
		boolean						toDestroyImage 	= false;
		
		public AnimatingRunnable( TreeViewer viewer, WorkingTreeNodeData data )
		{
			myNode 		= data;
			myViewer	= viewer;
		}

		@Override
		public void run()
		{
			if ( counter % 6 == 0 )
				textCounter++;
			myNode.name( WorkingTreeNodeData.DEFAULT_WORKING_LABEL.substring(0, WorkingTreeNodeData.DEFAULT_WORKING_LABEL.length()-3 + (textCounter%4 )) );
			myNode.incrementImageIndex();	// increment index so we get the next image
			Display.getDefault().asyncExec( new Runnable()
			{
				@Override
				public void run() 
				{
					myViewer.update( myNode, null );
				}
			});
			counter++;
			toDestroyImage = true; // destroy all old images except for the 1st one
		}

	}

}
