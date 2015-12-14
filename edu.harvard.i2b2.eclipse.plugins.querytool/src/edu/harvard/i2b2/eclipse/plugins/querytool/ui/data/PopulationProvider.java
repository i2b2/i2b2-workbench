package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.BasicQueryModePanel.PopulationSetType;

public interface PopulationProvider 
{
	// use Event to represent a Population 
	public Event 				getPopulation();			// return a population as an Event
	public PopulationSetType 	getPopulationType();
	public Long					getPopulationTimestamp(); 	// returns when the PopulationProvider updated its Population (so we can find the freshest one)
}
