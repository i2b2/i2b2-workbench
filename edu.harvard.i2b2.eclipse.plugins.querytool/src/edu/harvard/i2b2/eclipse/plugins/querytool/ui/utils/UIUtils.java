/*
 * Copyright (c) 2006-2017 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.plugins.querytool.Activator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.GroupPanel;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.QueryToolPanelComposite;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.SkinnyDateConstraintDisplay;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.QueryToolViewAccessor;
import edu.harvard.i2b2.query.data.QueryFactory;
import edu.harvard.i2b2.query.data.QueryMasterData;

public class UIUtils 
{
	public static final String	QUERY_TIMED_OUT			= "Your last query timed out.";
	public static final String 	AN_ERROR_HAS_OCCURRED 	= "An error has occurred.";
	public static final String	CANNOT_DROP_MSG			= "You cannot drop that item here";
	
	public static final String	DEFAULT_CHAR			= "0";
	public static Point			DEFAULT_CHAR_WIDTH		= null;
	
	public static final String	PREV_QUERY_REFRESH		= "refresh";
	
	public static final String	PLUGIN_PREV_QUERY_NAME	= "edu.harvard.i2b2.eclipse.plugins.previousquery.views.PreviousQueryView";	
	public static final String	PLUGIN_ANALYSIS_NAME	= "edu.harvard.i2b2.eclipse.plugins.analysis.views.AnalysisView";
	public static final String	PLUGIN_TIMElINE_NAME	= "edu.harvard.i2b2.eclipse.plugins.explorer.views.ExplorerView";
	public static final String	PLUGIN_TIMEALIGN_NAME	= "edu.harvard.i2b2.eclipse.plugins.timeAlign.views.TimeAlignView";
	
	
	
	private static UIUtils myInstance = null;
	public static UIUtils getIntance()
	{
		if ( myInstance == null )
			myInstance = new UIUtils();
		return myInstance;
	}
	
	private IViewPart myPluginHolder = null;
	
	private UIUtils()
	{}
	
	public IViewPart findActivePlugin( final String pluginViewName )
	{
		myPluginHolder = null;
		if ( Display.getCurrent() == null ) // we are not getting called by UI thread
		{
			Display.getDefault().syncExec( new Runnable()
			{
				@Override
				public void run() 
				{ myPluginHolder = QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getPage().findView( pluginViewName ); }
			});
		}
		else
		{
			Display.getCurrent().syncExec( new Runnable()
			{
				@Override
				public void run() 
				{ myPluginHolder = QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getPage().findView( pluginViewName ); }
			});
		}
		return myPluginHolder;
	}

	
	
	
	
	private static final VerifyListener DECIMAL_INPUT_VERIFY_LISTENER = new VerifyListener()
	{
		  @Override
		  public void verifyText(VerifyEvent e) 
		  {
			Text text = (Text)e.widget;
		    final String oldS = text.getText();
		    final String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
		    try 
		    {
		      Double.parseDouble( newS );
		      if ( newS.endsWith("D") || newS.endsWith("d"))
		    	  e.doit = false;
		      // value is decimal
		    }
		    catch (final NumberFormatException numberFormatException) 
		    {
		      // value is not decimal
		      e.doit = false;
		    }
		  }		
	};

	
	
	
	
	
	
	
	
	
	public static Point computeStringSize( String string, Drawable drawable )
	{
		GC gc = new GC( drawable );
		Point size = gc.textExtent(string);
		gc.dispose ();
		return size;
	}
	
	public static Point computeButtonSizeOffScreen( Control parent, String text, Image image )
	{
		Shell shell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.NO_BACKGROUND );
		
		Button button = new Button(shell, SWT.CHECK | SWT.NO_BACKGROUND  );
		if ( text != null )
			button.setText( text );
		if ( image != null )
			button.setImage( image );
		return button.computeSize( SWT.DEFAULT, SWT.DEFAULT );
	}
	
	public static Point getDefaultCharSize( Drawable drawable )
	{
		if ( DEFAULT_CHAR_WIDTH == null )
			DEFAULT_CHAR_WIDTH = computeStringSize( DEFAULT_CHAR, drawable );
		return DEFAULT_CHAR_WIDTH;
	}

	/*
	 * Verify that inputs to a Text is only double-parseable
	 */
	public static VerifyListener getDecimalInputVerifyListener()
	{
		return DECIMAL_INPUT_VERIFY_LISTENER;
	}
	
	// recursively enable descendants of a control (true/false)
	public static void recursiveSetEnabled(Control control, boolean enabled) 
	{
	    if (control instanceof Composite)
	    {
	        Composite comp = (Composite) control;

	        for (Control c : comp.getChildren())
	            recursiveSetEnabled(c, enabled);

	        if ( control instanceof QueryToolPanelComposite )
	        	control.setEnabled(enabled);
	    }
	    else
	    	control.setEnabled(enabled);
	}
	
	// recursively enable descendants of a control (true/false), remember the controls in the descendant tree
	// that are already in the 'enabled' status, and remember them. 
	// The return value is to be used with recursiveSetEnabled(...) as exclusion to restore the controls' previous state 
	public static void recursiveSetEnabledAndRememberUnchangedControls(Control control, boolean enabled, Set<Control> controls) 
	{
		if ( control.isEnabled() == enabled )
		{
			controls.add( control );
		}
		else
		{
		    if (control instanceof Composite)
		    {
		       if ( control instanceof QueryToolPanelComposite )
		        control.setEnabled(enabled);
		        
		        Composite comp = (Composite) control;
		        for (Control c : comp.getChildren())
		        	recursiveSetEnabledAndRememberUnchangedControls(c, enabled, controls );
		    }
		    else 
		    	control.setEnabled(enabled);
		    
		}    
	}

	
	public static void recursiveSetEnabled( Control control, boolean enabled, Set<Control> exclusions )
	{
        if ( exclusions.contains( control ) )
        	return;

	    if (control instanceof Composite)
	    {
	        Composite comp = (Composite) control;
	        for (Control c : comp.getChildren())
	            recursiveSetEnabled(c, enabled, exclusions);
	        
	        if ( control instanceof QueryToolPanelComposite )
	        	control.setEnabled(enabled);
	    }
	    else
	    	control.setEnabled(enabled);
	}
	
	
	
	
	/*
	 * Updates the Status line at the bottom of the Eclipse Workbench
	 */
	public static void syncSetWorkbenchStatus( final String message )
	{
		Display.getCurrent().syncExec( new Runnable()
		{
			public void run() 
			{ 
				((StatusLineContributionItem)QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getActionBars().getStatusLineManager().find("Status")).setText( message );
			}
		});
	}
	
	/*
	 * Updates the Status line at the bottom of the Eclipse Workbench
	 */
	public static void asyncSetWorkbenchStatus( final String message )
	{
		Display.getCurrent().asyncExec( new Runnable()
		{
			public void run() 
			{ 
				((StatusLineContributionItem)QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getActionBars().getStatusLineManager().find("Status")).setText( message );
			}
		});
	}

	
	public static void refreshPreviousQuery( QueryMasterData queryData )
	{
		// tell Previous Queries to display the new query (only if previous query is available)
		IViewPart previousqueryview = UIUtils.getIntance().findActivePlugin( PLUGIN_PREV_QUERY_NAME );
		//IViewPart previousqueryview = QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getPage().findView( PLUGIN_PREV_QUERY_NAME );
		if ( previousqueryview != null )
			((ICommonMethod) previousqueryview).doSomething( queryData.name() + " [" + QueryFactory.getDayString() + "]" + "#i2b2seperater#" + queryData.id() );
	}

	/* Refresh the entire Prev Query panel */
	public static void refreshPreviousQuery()
	{
		// tell Previous Queries to display the new query (only if previous query is available)
		IViewPart previousqueryview = UIUtils.getIntance().findActivePlugin( PLUGIN_PREV_QUERY_NAME );
		//IViewPart previousqueryview = QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getPage().findView( PLUGIN_PREV_QUERY_NAME );
		if ( previousqueryview != null )
			((ICommonMethod) previousqueryview).doSomething( PREV_QUERY_REFRESH  );
	}

	public static void sendQueryResultsToAnalysis( QueryMasterData queryData, String queryResultInstanceID )
	{
		IViewPart view = UIUtils.getIntance().findActivePlugin( PLUGIN_ANALYSIS_NAME );
		//IViewPart view = QueryToolViewAccessor.getInstance().getQueryToolView().getViewSite().getPage().findView( PLUGIN_ANALYSIS_NAME );
		if ( view != null)
			((ICommonMethod) view).doSomething(queryData.name() + " [" + QueryFactory.getDayString() + "]" + "#i2b2seperater#" + queryResultInstanceID );
	}

	public static void sendQueryResultsToTimeLine( QueryMasterData queryData, String count, String refId  )
	{
		// launch Timeline View
		IViewPart view = UIUtils.getIntance().findActivePlugin( PLUGIN_TIMElINE_NAME );
		if ( view != null )
		{
			((ICommonMethod) view).doSomething( count + "-" + refId );
			((ICommonMethod) view).processQuery( queryData.id() );
			//((ICommonMethod) view).doSomething(queryData.name() + " [" + QueryFactory.getDayString() + "]" + "#i2b2seperater#" + queryResultInstanceID );
		}

		// launch TimeAlign View
		view = UIUtils.getIntance().findActivePlugin( PLUGIN_TIMEALIGN_NAME );
		if ( view != null )
		{
			((ICommonMethod) view).doSomething( count + "-" + refId );
			((ICommonMethod) view).processQuery( queryData.id() );
		}

	}

	/*
	 * ================================================================================================================
	 *  POP UP DIALOGS
	 * ================================================================================================================
	 */
	
	public static void popupError( final String title, final String message, final String reason )
	{
		Display.getCurrent().syncExec( new Runnable()
		{
			public void run() 
			{ 
				ErrorDialog.openError( QueryToolViewAccessor.getInstance().getQueryToolView().getWorkbenchShell(), title, message, new Status( IStatus.ERROR, Activator.PLUGIN_ID, reason) );
			}
		});	
	}
	
	public static void popupMessage( final String title, final String message )
	{
		Display.getCurrent().syncExec( new Runnable()
		{
			public void run() 
			{
				MessageDialog.open(MessageDialog.INFORMATION, QueryToolViewAccessor.getInstance().getQueryToolView().getWorkbenchShell(), title, message, SWT.NONE );
			}
		});	
	}

}
