package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupManager implements DataChangedListener
{
	public static final int PANEL_SPACING = UIConst.GROUP_PANEL_MARGIN;

	protected Composite myParent;
	protected ArrayList<GroupPanel> myGroupPanels;

	protected ArrayList<UIManagerContentChangedListener> myContentListeners;	// notified when new Groups are added/removed
	protected ArrayList<DataChangedListener> 	myDataChangedListeners;			// notified when Groups changed its data (add/remove terms, change Bniding, etc.)
	protected QueryDateConstraintProvider 		myDateConstraintProvider;
	
	// keep track of whether we are using group-specific dates or Event/Query-wide Dates. We do this so we know when to keep DateConstraintDisplay disabled/enabled properly
	protected boolean isUsingGroupSpecificDateConstraint 	= true;
	
	protected IGroupBindingPolicyProvider myGroupBindingPolicyProvider = IGroupBindingPolicyProvider.DEFAULT_POLICY;
	
	public GroupManager( Composite parent, QueryDateConstraintProvider provider ) // use default GroupBindingPolicyProvider (no GroupBindingPolicy)
	{
		myGroupPanels 				= new ArrayList<GroupPanel>();
		myContentListeners			= new ArrayList<UIManagerContentChangedListener>();
		myDataChangedListeners 		= new ArrayList<DataChangedListener>();
		myDateConstraintProvider	= provider;
		myParent 					= parent;
	}

	// designate a GroupBindingPolicyProvider
	public GroupManager( Composite parent, IGroupBindingPolicyProvider gbpProvider, QueryDateConstraintProvider dateProvider )
	{
		myGroupBindingPolicyProvider= gbpProvider;
		myGroupPanels 				= new ArrayList<GroupPanel>();
		myContentListeners			= new ArrayList<UIManagerContentChangedListener>();
		myDataChangedListeners 		= new ArrayList<DataChangedListener>();
		myDateConstraintProvider	= dateProvider;
		myParent 					= parent;
	}

	// UIContentChangedListener to listen to Group addition/removal
	public void addUIContentListener( UIManagerContentChangedListener listener )
	{ myContentListeners.add( listener ); }
	public boolean removeUICOntentListener( UIManagerContentChangedListener listener )
	{ return myContentListeners.remove( listener ); }
	public void removeAllListeners()
	{ myContentListeners = new ArrayList<UIManagerContentChangedListener>(); }

	protected void notifyUIContentChangeListeners()
	{
		for ( int i = 0; i < myContentListeners.size(); i++ )
			myContentListeners.get(i).groupManagerContentChanged( this );
	}

	// DataChangedListener to listen to data changes on Groups
	public void addDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.add( list ); }
	public void removeDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.remove( list ); }

	// all panel layout data are produced here
	protected FormData makeLayoutData( Control previousControl )
	{
		if ( previousControl == null )
			return FormDataMaker.makeFormData( 0, PANEL_SPACING, (Integer)null, 0, 0, PANEL_SPACING, 100, -PANEL_SPACING );
		else
			return FormDataMaker.makeFormData( previousControl, PANEL_SPACING, (Integer)null, 0, 0, PANEL_SPACING, 100, -PANEL_SPACING );
	}

	// cancel all tasks that are running and shutdown the execdutor (should be called before makeGroupData())
	public void cancelAllRunningTasks()
	{
		for ( GroupPanel g : myGroupPanels )
			g.cancelWorkingNodesAndShutDownExecutor();
	}
	// make a clone of the Groups
	public ArrayList<Group> makeGroupData()
	{
		ArrayList<Group> groups = new ArrayList<Group>(); 
		for ( GroupPanel g : myGroupPanels )
			groups.add( g.getGroupData() );
		return groups;
	}

	public void addPanels( int number )
	{ addPanels( number, true, null, null ); }

	public void addPanels( int number, boolean isEnabled, GregorianCalendar startDate, GregorianCalendar endDate )
	{		
		Control previousControl = null;
		if ( myGroupPanels.size() != 0)
			previousControl = myGroupPanels.get( myGroupPanels.size()-1 );
		int counter = myGroupPanels.size()+1;
		//GroupBinding defaultBinding = this.myGroupBindingPolicyProvider.getDefaultBinding();
		for ( int i = 0; i < number; i++)
		{
			GroupPanel panel = new GroupPanel( myParent, SWT.None, this, UIConst.GROUP + " " + (counter+i), isEnabled, startDate, endDate, 0, UIConst.GREATER_THAN );
			if ( !isEnabled )
				panel.useCentralDateConstraint(  startDate, endDate );
			FormData fd = makeLayoutData( previousControl);
			fd.height= getDefaultPanelHeight();
			panel.setLayoutData( fd );
			previousControl = panel;
			myGroupPanels.add( panel );
			panel.getGroupData().setGroupBindingPolicyProvider( myGroupBindingPolicyProvider ); // set GroupBindingPolicyProvider for each new Group according to this GroupManager
			panel.getGroupData().setBinding( myGroupBindingPolicyProvider.getDefaultBinding() ); // set Group's default binding
		}
		notifyUIContentChangeListeners();
	}

	public void addPanels( Event event )
	{
		List<Group> groups = event.getGroups();
		Control previousControl = null;
		if ( myGroupPanels.size() != 0)
			previousControl = myGroupPanels.get( myGroupPanels.size()-1 );
		int counter = myGroupPanels.size()+1;
		for ( int i = 0; i < groups.size(); i++)
		{
			Group group = groups.get(i);
			group.setGroupBindingPolicyProvider( myGroupBindingPolicyProvider ); // set GroupBindingPolicyProvider for each new Group according to this GroupManager
			myGroupBindingPolicyProvider.forceConformity( group );
			GroupPanel panel = new GroupPanel( myParent, SWT.None, group, this, UIConst.GROUP + " " + (counter+i), event.isUsingGroupSpecificDates(), group.getStartDate(), group.getEndDate(), group.getNumber(), group.getOperator() );
			if ( !event.isUsingGroupSpecificDates() )
				panel.useCentralDateConstraint(  event.getStartDate(), event.getEndDate() );
			FormData fd = makeLayoutData( previousControl );			
			panel.setLayoutData( fd );
			fd.height= panel.getAdjustedPanelHeight();//
			previousControl = panel;			
			myGroupPanels.add( panel );
		}
		notifyUIContentChangeListeners();
	}

	// remove specified panel. Rename remaining panels
	public void removePanel( GroupPanel p )
	{
		int index = -1;
		for ( int i = 0; i < myGroupPanels.size(); i++)
		{
			if ( myGroupPanels.get(i) == p )
			{
				index = i;
				break;
			}
		}
		if ( index == -1)
			return; // panel p not found, do nothing]
		p.cancelWorkingNodesAndShutDownExecutor();		// cancel all working nodes and shut down ExecutorService
		myGroupPanels.remove( index );
		p.getGroupData().removeAllDataChangedListeners(); 	// remove the listeners to the removed Group
		
		Control previousControl = null;
		if ( index > 0 )
			previousControl = myGroupPanels.get( index-1 );
		// relayout all grouppanels after and including index
		for ( int i = index; i < myGroupPanels.size(); i++)
		{
			GroupPanel panel = myGroupPanels.get(i);
			FormData fd = makeLayoutData( previousControl);
			fd.height= getDefaultPanelHeight();
			panel.setLayoutData( fd );
			previousControl = panel;
			panel.setName( UIConst.GROUP + " " + (i+1) );
		}
		p.dispose();	// free OS resources
		notifyUIContentChangeListeners();		
	}

	// remove all panels that do not contain terms. Rename remaining panels
	public void consolidatePanels()
	{		
		ArrayList<GroupPanel> toRemove = new ArrayList<GroupPanel>();
		for ( int i = 0; i < myGroupPanels.size(); i++)
			if ( myGroupPanels.get(i).getTermSize() == 0)
				toRemove.add( myGroupPanels.get(i) );
		if ( toRemove.size() == 0 )
			return;
		myGroupPanels.removeAll( toRemove );
		
		// remove the listening link from a GroupPanel to its Group
		for ( GroupPanel gp : toRemove )
		{
			gp.cancelWorkingNodesAndShutDownExecutor();
			gp.getGroupData().removeAllDataChangedListeners();
		}
		Control previousControl = null;
		for ( int i = 0; i < myGroupPanels.size(); i++)
		{
			GroupPanel panel = myGroupPanels.get(i);
			FormData fd = makeLayoutData( previousControl);
			fd.height= getDefaultPanelHeight();
			panel.setLayoutData( fd );
			previousControl = panel;
			panel.setName( UIConst.GROUP + " " + (1+i));
		}
		notifyUIContentChangeListeners();
		for ( int i = 0; i < toRemove.size(); i++ ) // free OS resources
			toRemove.get(i).dispose();
	}

	public int getNumberOfPanels()
	{
		return this.myGroupPanels.size();
	}
	
	public int getIndexOf( GroupPanel panel )
	{
		return this.myGroupPanels.indexOf( panel );
	}
	
	public GroupPanel getLastPanel()
	{
		if ( myGroupPanels.isEmpty() )
			return null;
		return myGroupPanels.get( myGroupPanels.size()-1 );
	}

	public void removeAllPanels()
	{
		for ( GroupPanel panel : myGroupPanels )
		{
			// remove the listening link from a GroupPanel to its Group
			panel.getGroupData().removeAllDataChangedListeners(); 
			panel.dispose();
			panel = null;
		}
		myGroupPanels.clear();
	}
	
	public void removeAllPanelsWithNotification()
	{
		removeAllPanels();
		notifyUIContentChangeListeners();
	}

	public void useGroupSpecificDateConstraint()
	{
		for ( GroupPanel panel : myGroupPanels )
		{
			panel.useGroupSpecificDateConstraint();
		}
		isUsingGroupSpecificDateConstraint = true;
	}

	public void useCentralDateConstraint( GregorianCalendar start, GregorianCalendar end )
	{
		for ( GroupPanel panel : myGroupPanels )
		{
			panel.useCentralDateConstraint( start, end );
		}
		isUsingGroupSpecificDateConstraint = false;
	}

	public boolean isUsingGroupSpecificDateConstraint()
	{
		return this.isUsingGroupSpecificDateConstraint;
	}
	
	// called after a Group is dropped. Use this.IGroupBindingPolicyProvider to force GroupBinding to conform
	public void forceGroupBindingConformity(Group group) 
	{
		this.myGroupBindingPolicyProvider.forceConformity( group );
	}
	
	/* Allow modification of Groups */
	public void batchSetBinding( GroupBinding binding )
	{
		// set binding
		for ( GroupPanel panel : myGroupPanels )
			panel.setBinding( binding );			
	}
	
	public void batchSetBindingAtLeast( GroupBinding binding )
	{
		if ( binding == GroupBinding.BY_PATIENT )
			return; // do nothing
		if ( binding == GroupBinding.BY_ENCOUNTER )
		{
			for ( GroupPanel panel : myGroupPanels )
				if ( panel.getGroupData().getBinding() == GroupBinding.BY_PATIENT )
					panel.setBinding(  GroupBinding.BY_ENCOUNTER );
		}
	}

	/* tell this Groupmanager that Grouppanel's layout/size have changed. This GroupManager may need to do some extra work like notifying UIContentChangeListeners*/
	public void panelLayoutChanged()
	{ 
		notifyUIContentChangeListeners();
	}
	
	/*
	 * 	Methods subclasses can override to adjust layout parameters 
	 */
	protected int getDefaultPanelHeight()
	{
		return UIConst.DEFAULT_GROUPPANEL_HEIGHT;
	}

	protected int getMaxPanelHeight()
	{
		return UIConst.DEFAULT_GROUPPANEL_MAX_HEIGHT;
	}

	
	@Override /* DataChangedListener */
	public void dataChanged(Object source) 
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( source );
	}



}
