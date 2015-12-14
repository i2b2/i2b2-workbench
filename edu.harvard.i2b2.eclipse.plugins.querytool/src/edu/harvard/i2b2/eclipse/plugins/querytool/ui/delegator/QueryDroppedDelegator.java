package edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;



public interface QueryDroppedDelegator 
{
	/* Allows a delegator to do some UI work after a previous query item has been dropped */
	public void queryDropped( QueryDefinitionType queryDefinitionType );
}
