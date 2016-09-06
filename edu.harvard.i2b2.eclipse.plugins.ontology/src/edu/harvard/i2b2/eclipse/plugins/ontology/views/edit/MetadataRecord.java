/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeViewer;

import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DeleteChildType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifierType;

public class MetadataRecord {

	private ConceptType metadata = null;
	private NodeBrowser browser = null;
	private String type = null;
	private TreeData parentData = null;
	private String symbol = null;
	private String synonym = null;
	private List<String> synonyms = new ArrayList<String>();
	private boolean synonymEditFlag = false;
	private boolean updateSyncIconFlag = false;
	private boolean valueMetadataFlag = false;
	private IAction syncAction = null;
	private TreeViewer modifierViewer = null;


	private static MetadataRecord thisInstance;
	static {
		thisInstance = new MetadataRecord();
	}

	public static MetadataRecord getInstance() {
		return thisInstance;
	}
	
	public ConceptType getMetadata(){
		if (metadata == null)
			metadata = new ConceptType();
		
		return metadata;
	}
	
	public TreeData getParentData(){		
		return parentData;
	}
	public void setParentData(TreeData data){
		parentData = new TreeData();
		parentData.setName(data.getName());
		parentData.setFullName(data.getFullName());
		parentData.setVisualattributes(data.getVisualattributes());
		parentData.setColumndatatype(data.getColumndatatype());
		parentData.setFacttablecolumn(data.getFacttablecolumn());
		parentData.setColumnname(data.getColumnname());
		parentData.setKey(data.getKey());
		parentData.setLevel(data.getLevel());
		parentData.setOperator(data.getOperator());
		parentData.setSynonymCd(data.getSynonymCd());
		parentData.setTablename(data.getTablename());
		parentData.setTooltip(data.getTooltip());
		parentData.setDimcode(data.getDimcode());
		parentData.setBasecode(data.getBasecode());
	}
	
	
	
	public void setParentData(ModifierType modifier){
		parentData = new TreeData();
		parentData.setName(modifier.getName());
		parentData.setFullName(modifier.getFullname());
		parentData.setVisualattributes(modifier.getVisualattributes());
		parentData.setColumndatatype(modifier.getColumndatatype());
		parentData.setFacttablecolumn(modifier.getFacttablecolumn());
		parentData.setKey(modifier.getKey());
		parentData.setLevel(modifier.getLevel());
		parentData.setOperator(modifier.getOperator());
		parentData.setSynonymCd(modifier.getSynonymCd());
		parentData.setTablename(modifier.getTablename());
		parentData.setDimcode(modifier.getDimcode());
		parentData.setBasecode(modifier.getBasecode());
		parentData.setModifier(modifier);
	}
	
	public void setMetadata(TreeNode node){
		if (metadata == null)
			metadata = new ConceptType();
		
		setParentData(node.getData());
		
		if((System.getProperty("OntEdit_ViewOnly") != null) && (System.getProperty("OntEdit_ViewOnly").equals("true"))){
			metadata.setName(node.getData().getName());
			metadata.setBasecode(node.getData().getBasecode());
			metadata.setSourcesystemCd(node.getData().getSourcesystemCd());		 
			metadata.setLevel(node.getData().getLevel());
			metadata.setVisualattributes(node.getData().getVisualattributes());
		}

		
		metadata.setColumndatatype(node.getData().getColumndatatype());
		metadata.setColumnname(node.getData().getColumnname());
		metadata.setFacttablecolumn(node.getData().getFacttablecolumn());
		metadata.setKey(node.getData().getKey());
		metadata.setLevel(node.getData().getLevel()+1);
		metadata.setOperator(node.getData().getOperator());
		metadata.setTablename(node.getData().getTablename());
		metadata.setDimcode(node.getData().getDimcode());
		metadata.setTooltip(node.getData().getTooltip());
		metadata.setSynonymCd("N");
		metadata.setValuetypeCd(node.getData().getValuetypeCd());
		metadata.setDimcode(node.getData().getDimcode());
		if(metadata.getValuetypeCd() == null)
			metadata.setValuetypeCd("");
	}
	
	public void setMetadata(ModifierType node){
		if (metadata == null)
			metadata = new ConceptType();
		if(metadata.getModifier() == null)
			metadata.setModifier(new ModifierType());
			
		metadata.getModifier().setColumndatatype(node.getColumndatatype());
		metadata.getModifier().setColumnname(node.getColumnname());
		metadata.getModifier().setFacttablecolumn(node.getFacttablecolumn());
		metadata.getModifier().setKey(node.getKey());
		metadata.getModifier().setLevel(node.getLevel()+1);
		metadata.getModifier().setOperator(node.getOperator());
		metadata.getModifier().setTablename(node.getTablename());
		metadata.getModifier().setDimcode(node.getDimcode());
		metadata.getModifier().setTooltip(node.getTooltip());
		metadata.getModifier().setSynonymCd("N");
		metadata.getModifier().setAppliedPath(node.getAppliedPath());
	
	}
	
	
	
	public void setType(String recordType){
		type = recordType;
		if (metadata == null)
			metadata = new ConceptType();
				
		if(type.equals("Folder"))
			metadata.setVisualattributes("FAE");
		else if (type.equals("Item"))
			metadata.setVisualattributes("LAE");
		else if (type.equals("ModifierFolder")){
			if(metadata.getModifier() == null){
				metadata.setModifier(new ModifierType());
				metadata.getModifier().setSynonymCd("N");
				metadata.getModifier().setLevel(1);
				metadata.getModifier().setVisualattributes("DAE");
			}
			else{
				metadata.getModifier().setSynonymCd("N");
				metadata.getModifier().setLevel(1);
				metadata.getModifier().setVisualattributes("DAE");
			}
		}
		else if (type.equals("ModifierItem")){
			if(metadata.getModifier() == null){
				metadata.setModifier(new ModifierType());
				metadata.getModifier().setSynonymCd("N");
				metadata.getModifier().setLevel(1);
				metadata.getModifier().setVisualattributes("RAE");
			}
			else{
				metadata.getModifier().setSynonymCd("N");
				metadata.getModifier().setLevel(1);
				metadata.getModifier().setVisualattributes("RAE");
			}
		}
		else if (type.equals("Container"))
			metadata.setVisualattributes("CAE");	
		else if (type.equals("ModifierContainer")){
			if(metadata.getModifier() == null){
				metadata.setModifier(new ModifierType());
				metadata.getModifier().setSynonymCd("N");
				metadata.getModifier().setLevel(1);
				metadata.getModifier().setVisualattributes("OAE");
			}
			else{
				metadata.getModifier().setSynonymCd("N");
				metadata.getModifier().setLevel(1);
				metadata.getModifier().setVisualattributes("OAE");
			}
		}
	}
	
	public String getType(){
		return type;
	}
	
	public void setSymbol(String symbol){
		this.symbol = symbol;
	}
	public String getSymbol(){
		return symbol;
	}
	
	public void setSynonym(String synonym){
		this.synonym = synonym;
	}
	public String getSynonym(){
		return synonym;
	}
	
	public void registerBrowser(NodeBrowser nodeBrowser){
		browser = nodeBrowser;
	}
	
	public NodeBrowser getBrowser(){
		return browser;
	}
	
	public void registerModifierViewer(TreeViewer viewer){
		modifierViewer = viewer;
	}
	
	public TreeViewer getModifierViewer(){
		return modifierViewer;
	}
	


	
	public void addSynonym(String synonym){
		synonyms.add(synonym);
	}
	
	public void removeSynonym(String synonym){
		synonyms.remove(synonym);
	}
	public List<String> getSynonyms(){
		return synonyms;
	}
	
	public boolean isUpdateSyncIconFlag() {
		return updateSyncIconFlag;
	}

	public void setUpdateSyncIconFlag(boolean updateSyncIconFlag) {
		this.updateSyncIconFlag = updateSyncIconFlag;
	}
	

	public void setSynonymEditFlag(boolean flag){
		this.synonymEditFlag = flag;
	}
	public boolean isSynonymEditFlag(){
		return this.synonymEditFlag;
	}
	
	public void setValueMetadataFlag(boolean flag){
		this.valueMetadataFlag = flag;
	}
	public boolean isValueMetadataFlag(){
		return this.valueMetadataFlag;
	}
	
	public IAction getSyncAction() {
		return syncAction;
	}

	public void setSyncAction(IAction action) {
		this.syncAction = action;
	}
	
	  public DeleteChildType getDeleteChildType(){
	    	DeleteChildType delChild = new DeleteChildType();
	    	
	    	delChild.setKey(metadata.getKey());
	    	delChild.setBasecode(metadata.getBasecode());
	    	delChild.setLevel(metadata.getLevel());
	    	delChild.setName(metadata.getName());
	    	delChild.setSynonymCd(metadata.getSynonymCd());
	    	delChild.setVisualattribute(metadata.getVisualattributes());
	    	
	    	return delChild;
	    }
	
	public void clear(){
		if(metadata != null) {
			metadata.setColumndatatype("");
			metadata.setColumnname("");
			metadata.setFacttablecolumn("");
			metadata.setKey("");
			metadata.setOperator("");
			metadata.setTablename("");
			metadata.setDimcode("");
			metadata.setTooltip("");
			metadata.setVisualattributes("");
			if(metadata.getModifier() != null)
				metadata.setModifier(new ModifierType());
		}
		if((synonyms != null) || (!synonyms.isEmpty()))
			synonyms.clear();
		synonymEditFlag = false;
		updateSyncIconFlag = false;
		valueMetadataFlag = false;
	}

	public String validateModifier(){
		String message = "";
		
		if(metadata.getModifier().getKey() == null)
			message = message + "Modifier key is empty \n";
		
		if(metadata.getModifier().getName() == null)
			message = message + "Modifier name is empty \n";
		
		if(metadata.getModifier().getSynonymCd() == null)
			message = message + "Synonym code is empty \n";
		
		if(metadata.getModifier().getVisualattributes() == null)
			message = message + "Modifier visual attribute is empty \n";
		
		if(metadata.getModifier().getBasecode() == null)
			message = message + "Modifier code is empty \n";
		
		if(metadata.getModifier().getFacttablecolumn() == null)
			message = message + "Fact table column name is empty \n";
		
		if(metadata.getModifier().getTablename() == null)
			message = message + "Table name is empty \n";
		
		if(metadata.getModifier().getColumnname() == null)
			message = message + "Column name is empty \n";
		
		if(metadata.getModifier().getColumndatatype() == null)
			message = message + "Column data type is empty \n";
		
		if(metadata.getModifier().getOperator() == null)
			message = message + "Operator is empty \n";
		
		if(metadata.getModifier().getDimcode() == null)
			message = message + "Dimension code is empty \n";
		
		return message;
	}

	public String validate(){
		String message = "";
		
		if(metadata.getKey() == null)
			message = message + "Term key is empty \n";
		
		if(metadata.getName() == null)
			message = message + "Term name is empty \n";
		
		if(metadata.getSynonymCd() == null)
			message = message + "Synonym code is empty \n";
		
		if(metadata.getVisualattributes() == null)
			message = message + "Modifier visual attribute is empty \n";
		
		if(metadata.getBasecode() == null)
			message = message + "Concept code is empty \n";
		
		if(metadata.getFacttablecolumn() == null)
			message = message + "Fact table column name is empty \n";
		
		if(metadata.getTablename() == null)
			message = message + "Table name is empty \n";
		
		if(metadata.getColumnname() == null)
			message = message + "Column name is empty \n";
		
		if(metadata.getColumndatatype() == null)
			message = message + "Column data type is empty \n";
		
		if(metadata.getOperator() == null)
			message = message + "Operator is empty \n";
		
		if(metadata.getDimcode() == null)
			message = message + "Dimension code is empty \n";
		
		return message;
	}
}
