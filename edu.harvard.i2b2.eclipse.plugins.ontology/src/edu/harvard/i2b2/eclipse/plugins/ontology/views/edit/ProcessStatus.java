package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import edu.harvard.i2b2.ontclient.datavo.vdo.DirtyValueType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyProcessStatusType;

public class ProcessStatus {
	
	private OntologyProcessStatusType status;
	private DirtyValueType dirtyState = DirtyValueType.NONE;


	private static ProcessStatus thisInstance;
	static {
		thisInstance = new ProcessStatus();
	}

	public static ProcessStatus getInstance() {
		return thisInstance;
	}
	
	public OntologyProcessStatusType getStatus() {
		return status;
	}

	public void setStatus(OntologyProcessStatusType status) {
		this.status = status;
	}
	
	public String getProcessId(){
		return status.getProcessId();
	}
	public String getProcessStatusCd(){
		return status.getProcessStatusCd();
	}
	
	public DirtyValueType getDirtyState() {
		return dirtyState;
	}

	public void setDirtyState(String dirtyState) {
		this.dirtyState = DirtyValueType.valueOf(dirtyState);
	}
	public void setDirtyState(DirtyValueType dirtyState) {
		this.dirtyState = dirtyState;
	}
}
