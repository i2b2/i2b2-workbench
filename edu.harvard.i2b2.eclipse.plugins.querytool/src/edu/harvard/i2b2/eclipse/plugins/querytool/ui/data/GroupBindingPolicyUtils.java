package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingPolicyUtils 
{
	
	public static void forceDefaultConformity( Group group, IGroupBindingPolicyProvider provider )
	{
		if ( provider.isByEncounterEnabled(group) && !provider.isByObservationEnabled(group) && group.getBinding() == GroupBinding.BY_OBSERVATION )
			group.setBinding( GroupBinding.BY_ENCOUNTER );
		else if ( provider.isByPatientEnabled(group) && !provider.isByEncounterEnabled(group) && (group.getBinding() == GroupBinding.BY_OBSERVATION || group.getBinding() == GroupBinding.BY_ENCOUNTER ))
			group.setBinding( GroupBinding.BY_PATIENT );			
	}

}
