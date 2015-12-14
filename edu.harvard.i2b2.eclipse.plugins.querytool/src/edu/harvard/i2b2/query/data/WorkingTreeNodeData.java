package edu.harvard.i2b2.query.data;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.task.TermFetchRunnable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.query.data.processor.ProcessorConst;

public class WorkingTreeNodeData extends QueryConceptTreeNodeData 
{
	public static final String	DEFAULT_WORKING_LABEL	= "Fetching children (double click to cancel)...";
	public static final String	DEFAULT_FAILED_LABEL	= "Failed to fetch children. Double click to retry.";
	public enum WorkingTreeNodeState  { RUNNING, FAILED, CANCELED }

	private WorkingTreeNodeState 	myState;
	private TermFetchRunnable		myRunner;
	//private ImageLoader				myImageLoader; // object that holds the animated gif used to represent this 
	private int						myImageIndex;
	
	public WorkingTreeNodeData( QueryConceptTreeNodeData parent )
	{ 
		super( parent );
		this.visualAttribute( ProcessorConst.ICON_WORKING );
		this.name( DEFAULT_WORKING_LABEL );
		this.myState = WorkingTreeNodeState.RUNNING;
		//this.myImageLoader = Images.getImageLoader( Images.WORKING );
		this.myImageIndex = 0;		
	}

	public void setState( WorkingTreeNodeState state )
	{ this.myState= state; }

	public WorkingTreeNodeState getState()
	{ return this.myState; }

	public void setTerMFetchRunnable( TermFetchRunnable runner )
	{ myRunner = runner; }
	
	public TermFetchRunnable getTermFetchRunnable()
	{ return this.myRunner; }
	
	public void cancelRunner()
	{ myRunner.cancel(); }

	public void incrementImageIndex()
	{ myImageIndex = (myImageIndex+1) % Images.getImageLoader( Images.WORKING ).data.length; }

	@Override
	public Image getImage()
	{
		if ( this.visualAttribute().equals( ProcessorConst.ICON_WARNING ))
			return Images.getEclipseImagesByKey( ISharedImages.IMG_OBJS_WARN_TSK );

		Image frameImage = Images.getImageByKeyAndFrame( Images.WORKING, myImageIndex );
		return frameImage;
	}

}
