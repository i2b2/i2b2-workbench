package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

public interface PopulationLoader
{
	// use Event to represent a Population 
	public void loadPopulation( Event event );
	
	// reset populatino
	public void resetPopulation();
}