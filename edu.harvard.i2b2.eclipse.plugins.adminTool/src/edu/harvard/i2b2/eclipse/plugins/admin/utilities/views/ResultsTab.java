/*
 * Copyright (c) 2006-2007 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.views;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.PFTJAXBUtil;


/**
 * The ResultsTab class provides the Results table 
 * 
 * @author Lori Phillips   
 */


public class ResultsTab {
	private Table table;
	private Label pftText;
	private PatientDataType pdo;
	private static ResultsTab instance;
	private Log log = LogFactory.getLog(ResultsTab.class.getName());
	
	/**
	 * The constructor
	 */
	private ResultsTab(Composite  comp, Font font) {
		pdo = null;
		
	    pftText = new Label(comp, SWT.SINGLE|SWT.BORDER|SWT.DragDetect);
    	GridData data = new GridData(GridData.FILL_HORIZONTAL);
    	data.heightHint = 15;
       	pftText.setLayoutData(data);
    	pftText.setText("");
    	
	    TableLayout tableLayout = new TableLayout();
	    tableLayout.addColumnData(new ColumnWeightData(33, 75, false));
	    tableLayout.addColumnData(new ColumnWeightData(33, 75, false));
	    tableLayout.addColumnData(new ColumnWeightData(33, 75, false));
	 
	    GridData tableGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
	    tableGridData.grabExcessHorizontalSpace = true;
	    tableGridData.grabExcessVerticalSpace = true;
	    tableGridData.verticalIndent = 5;
	    	    
		table = new Table(comp, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		table.setLayout(tableLayout);
		table.setLayoutData(tableGridData);
	
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font);	
		
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		TableColumn col3 = new TableColumn(table, SWT.LEFT);
		col1.setText("Name");
		col2.setText("Value/Units");
		col3.setText("Code");
		
		
		// Set up drag source
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };      
		DragSource source = new DragSource(pftText, DND.DROP_COPY);
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
			
			public void dragStart(DragSourceEvent event) {
				event.doit = true;	
			}

			public void dragSetData(DragSourceEvent event) {			
				StringWriter strWriter = null;
				
				PatientDataType pdo = ResultsTab.getInstance().getPdo();
				
				try {
					strWriter = new StringWriter();
					DndType dnd = new DndType();
					 edu.harvard.i2b2.common.datavo.pdo.ObjectFactory pdoOf = new  edu.harvard.i2b2.common.datavo.pdo.ObjectFactory();
					dnd.getAny().add( pdoOf.createPatientData((pdo)));

					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
					PFTJAXBUtil.getJAXBUtil().marshaller(of.createPluginDragDrop(dnd), strWriter);
					
					
				} catch (JAXBUtilException e) {
					log.error("Error marshalling PFT drag text");
				} 

				event.data = strWriter.toString();
			}
			
			public void dragFinished(DragSourceEvent event) {
				
			}
		});
		
	}
	/**
	 * Function to set the initial ResponseTab instance
	 * 
	 * @param tabFolder Composite to place tabFolder into
	 * @return  ResultsTab object
	 */
	public static void setInstance(Composite tabFolder, Font font) {
		instance = new ResultsTab(tabFolder, font);
	}

	/**
	 * Function to return the ResultsTab instance
	 * 
	 * @return  ResultsTab object
	 */
	public static ResultsTab getInstance() {
		return instance;
	}

	/**
	 * Function to return the Table widget
	 * 
	 * @return  Table object
	 */
	public Table getTable(){
		return table;
	}

	public void setName(){
		String name = " PFT Result@" + getTimestamp();
		pftText.setText(name);
	}
	
	/**
	 * Function to add an item to the Table widget
	 * 
	 * @param name,value_units,code Strings to place in Table widget
	 */
	public void setItem(String name, String value_units, String code){
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText( new String[] {name, value_units, code });
	}

	/**
	 * Function to store responseMsg pdo message body
	 * 
	 * @param responseMsg
	 */
	public void setPdoMsg(PatientDataType responsePdo){
		
		pdo = responsePdo;
	}
	/**
	 * Function to return pdo message body
	 * 
	 * @param responseMsg
	 */
	
	public PatientDataType getPdo(){
		return pdo;
	}
	
	/**
	 * Function to clear the Table widget
	 * 
	 */
	public void clear() {
		pdo = null;
		int count = table.getItemCount();
		for(int i=0; i < count ; i++ )	    			
			table.remove(0);
	}

	/**
	 * Function to remove the last item in the Table widget
	 * 
	 */
	public void removeLastLine() {
		int count = table.getItemCount();
		if(count != 0)
			table.remove(count-1);
	}
	
	private String getTimestamp(){		
		Calendar cldr = Calendar.getInstance(Locale.getDefault());
		String atTimestamp = "@"
				+ addZero(cldr.get(Calendar.HOUR_OF_DAY)) + ":"
				+ addZero(cldr.get(Calendar.MINUTE)) + ":"
				+ addZero(cldr.get(Calendar.SECOND));
		
		return atTimestamp;
	}
		
	private String addZero(int number) {
		String result = new Integer(number).toString();
		if (number < 10 && number >= 0) {
			result = "0" + result;
		}
		return result;
	}
}
