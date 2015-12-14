package edu.harvard.i2b2.eclipse.plugins.querytool;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class QueryToolRuntime 
{
	/*
	 * Factory Pattern methods and other static vars
	 */
	private static QueryToolRuntime myInstance;

	public static QueryToolRuntime getInstance()
	{
		if (myInstance == null)
			myInstance = new QueryToolRuntime();
		return myInstance;
	}


	/*
	 * Instance vars/methods
	 */
	private Integer myGroupTitleHeight = null;
	private boolean isLaunchedFromWorkbench;	// whether the execution is run from a workbench. If doing testing without launching the workbench, then it will be, as by default, false.

	// Constructor 
	private QueryToolRuntime()
	{ init(); }

	// initialize defauilt values
	private void init()
	{ 
		isLaunchedFromWorkbench		= false;
		testAndSetIfLaunchedFromWorkbench();
	}
	
	public void setGroupTitleHeight( int h )	
	{ 
		if ( myGroupTitleHeight == null )
			myGroupTitleHeight = h;
	}
	
	public boolean isTitleHeightSet()
	{ return myGroupTitleHeight != null; }
	
	public Integer getGroupTitleHeight()
	{ return myGroupTitleHeight; }
	
	public boolean isLaunchedFromWorkbench()	{ return this.isLaunchedFromWorkbench; };
	
	public void	testAndSetIfLaunchedFromWorkbench()	
	{ 
		try
		{
			PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE_DISABLED);
			this.isLaunchedFromWorkbench = true;
		}
		catch( IllegalStateException e )
		{ 
			this.isLaunchedFromWorkbench = false;
		}
	}
}
