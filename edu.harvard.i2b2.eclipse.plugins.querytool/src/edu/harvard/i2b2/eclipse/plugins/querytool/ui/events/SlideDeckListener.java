package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.SlideDeck;

public interface SlideDeckListener 
{

	public void slideOccurred( SlideDeck source, SlideDeck.SlideEventType eventType, int fromIndex, int toIndex );
	

}
