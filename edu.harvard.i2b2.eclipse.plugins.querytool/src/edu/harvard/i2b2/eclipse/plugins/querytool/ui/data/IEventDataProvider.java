package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import java.util.ArrayList;

public interface IEventDataProvider 
{
	
	public Event getEventByName( String name );
	public ArrayList<Event> getEvents();
	
}
