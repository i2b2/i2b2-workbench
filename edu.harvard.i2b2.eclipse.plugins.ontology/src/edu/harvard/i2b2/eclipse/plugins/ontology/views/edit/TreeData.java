/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit; 


import java.util.Iterator;
import java.util.List;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.StringUtil;
import edu.harvard.i2b2.ontclient.datavo.i2b2result.DataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;

public class TreeData extends ConceptType
{
	private String tableCd;
  	private String fullName;
  	private String numPatients;
  	
  	public TreeData(int level, String fullName, String name, String visualAttributes)
  	{
  		this.level = level;
  		this.fullName = fullName;
  		this.name = name;
  		this.visualattributes = visualAttributes;
  	}
  	
  	public TreeData(String tableCd, String fullName, String name, String visualAttributes, String tooltip)
  	{
  		this.tableCd = tableCd;
  		this.fullName = fullName;
  		this.name = name;
  		this.visualattributes = visualAttributes;
  		this.tooltip = tooltip;
  	}
  	
  	public TreeData() {}
  	  	
 	public TreeData(ConceptType concept)
  	{ 		
 		this.fullName = concept.getKey();
 		this.key = concept.getKey();
 		this.tableCd = StringUtil.getTableCd(concept.getKey());
 		this.name = concept.getName();
 		if (name.toLowerCase().startsWith("zz"))
 			name = name.substring(2).trim();
 		this.visualattributes = concept.getVisualattributes().trim();
 		this.level = concept.getLevel();
 		this.basecode = concept.getBasecode();
 		this.columndatatype = concept.getColumndatatype();
 		this.columnname = concept.getColumnname();
 		this.comment = concept.getComment();
 		this.dimcode = concept.getDimcode();
 		this.metadataxml = concept.getMetadataxml();	
 		this.operator = concept.getOperator();
 		this.synonymCd= concept.getSynonymCd();
 		this.tablename = concept.getTablename();
 		this.facttablecolumn = concept.getFacttablecolumn();
 		this.tooltip = concept.getTooltip();
 		this.totalnum = concept.getTotalnum();		
 		this.valuetypeCd = concept.getValuetypeCd();
 		this.modifier = concept.getModifier();
  	}
  	
  	public String getFullName() {
  		return fullName;
  	}

  	public void setFullName(String fullName) {
  		this.fullName = fullName;
  	}

	public String getTableCd() {
		return tableCd;
	}
	public void setTableCd(String tableCd) {
		this.tableCd = tableCd;
	}	
	public String getNumPatients() {
  		return numPatients;
  	}

  	public void setNumPatients(String num) {
  		this.numPatients = num;
  	}
  	
  	public void setNumPatients(List<DataType> counts) {
  		if(counts != null) {
    		Iterator it = counts.iterator();

    		while(it.hasNext()){
    			DataType data = (DataType) it.next();
    			String colName = data.getColumn().toLowerCase();
    			if (colName.startsWith("zz"))
    	 			colName = colName.substring(2).trim();
    			
    			if(colName.equals(this.getName().toLowerCase())){
    				this.numPatients = data.getValue();
    				return;
    			}
    		}
  		}
  	}
  	
}