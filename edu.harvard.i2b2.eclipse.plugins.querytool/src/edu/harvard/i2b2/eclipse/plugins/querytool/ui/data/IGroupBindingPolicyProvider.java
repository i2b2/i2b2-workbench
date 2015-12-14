package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import edu.harvard.i2b2.query.data.DataConst.GroupBinding;


public interface IGroupBindingPolicyProvider
{
	public static final IGroupBindingPolicyProvider DEFAULT_POLICY = new IGroupBindingPolicyProvider()
	{
		@Override
		public boolean isByPatientEnabled(Group group)		{ return true; } // always enabled
		@Override
		public boolean isByEncounterEnabled(Group group) 	{ return true; } // always enabled
		@Override
		public boolean isByObservationEnabled(Group group)		{ return group.isContainingModifier(); } // enabled only if modifier is in group
		@Override
		public GroupBinding getDefaultBinding( )
		{ return GroupBinding.BY_PATIENT; }
		@Override
		public void forceConformity(Group group) 
		{ GroupBindingPolicyUtils.forceDefaultConformity(group, this); }
	};

	// test only
	public static final IGroupBindingPolicyProvider ALWAYS_NO_POLICY = new IGroupBindingPolicyProvider() 
	{
		@Override
		public boolean isByPatientEnabled(Group group)		{ return false; } // always disabled
		@Override
		public boolean isByEncounterEnabled(Group group) 	{ return false; } // always disabled
		@Override
		public boolean isByObservationEnabled(Group group)		{ return false; } // always disabled
		@Override
		public GroupBinding getDefaultBinding( )
		{ return GroupBinding.BY_PATIENT; }
		@Override
		public void forceConformity(Group group) 
		{ GroupBindingPolicyUtils.forceDefaultConformity(group, this); }
	};

	// test only	
	public static final IGroupBindingPolicyProvider ALWAYS_YES_POLICY = new IGroupBindingPolicyProvider()
	{
		@Override
		public boolean isByPatientEnabled(Group group)		{ return true; } // always enabled
		@Override
		public boolean isByEncounterEnabled(Group group) 	{ return true; } // always enabled
		@Override
		public boolean isByObservationEnabled(Group group)		{ return true; } // always enabled
		@Override
		public GroupBinding getDefaultBinding( )
		{ return GroupBinding.BY_PATIENT; }
		@Override
		public void forceConformity(Group group) 
		{ GroupBindingPolicyUtils.forceDefaultConformity(group, this); }
	};
	
	// test only
	public static final IGroupBindingPolicyProvider ONLY_INSTANCE = new IGroupBindingPolicyProvider()
	{
		@Override
		public boolean isByPatientEnabled(Group group)		{ return false; } // always disabled
		@Override
		public boolean isByEncounterEnabled(Group group) 	{ return false; } // always disabled
		@Override
		public boolean isByObservationEnabled(Group group)		{ return true; } // always enabled
		@Override
		public GroupBinding getDefaultBinding( )
		{ return GroupBinding.BY_PATIENT; }	
		@Override
		public void forceConformity(Group group) 
		{ GroupBindingPolicyUtils.forceDefaultConformity(group, this); }
	};

	
	public boolean isByPatientEnabled( Group group );
	public boolean isByEncounterEnabled( Group group );
	public boolean isByObservationEnabled( Group group );
	
	public GroupBinding getDefaultBinding( );
	public void forceConformity(Group group);
}
