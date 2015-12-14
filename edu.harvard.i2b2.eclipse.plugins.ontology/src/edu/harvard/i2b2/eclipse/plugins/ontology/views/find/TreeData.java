/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.find; 

import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;

public class TreeData extends ConceptType
{
	private String tableCd;
  	private String fullName;
  	
  	public TreeData(int level, String fullName, String name, String visualAttributes)
  	{
  		this.level = level;
  		this.fullName = fullName;
  		this.name = name;
  		this.visualattributes = visualAttributes;
  	}

  	public TreeData() {}

  	public TreeData(ConceptType concept)
  	{
  		this.fullName = concept.getKey();
  		this.key = concept.getKey();
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
  		this.synonymCd = concept.getSynonymCd();
  		this.tablename = concept.getTablename();
  		this.facttablecolumn = concept.getFacttablecolumn();
  		this.tooltip = concept.getTooltip();
  		this.totalnum = concept.getTotalnum();
  		this.valuetypeCd = concept.getValuetypeCd();
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
	
}