/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 
 * 		Wensong Pan 
 */

package edu.harvard.i2b2.eclipse.login;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.util.Messages;
import edu.harvard.i2b2.pm.datavo.pm.ParamType;
import edu.harvard.i2b2.pm.datavo.pm.ProjectType;

public class ProjectDialog {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label projectlabel = null;
	private Combo projectcombo = null;
	private Button gobutton = null;
	private Table IRBtable = null;
	private boolean cancelSelected = true;
	
	private List<ProjectType> projects = null;
	
	private ProjectType selectedProject = null;
	
	private int selectedIndex = 0;

	public boolean getCancelSelected()
	{
		return cancelSelected;
	}
	public ProjectDialog() {
		projects = UserInfoBean.getInstance().getProjects();
		selectedProject = projects.get(0);
		UserInfoBean.selectedProject(selectedProject);
	}

	/**
	 * This method initializes projectcombo	
	 *
	 */
	private void createProjectcombo() {
		projectcombo = new Combo(sShell, SWT.NONE);
		projectcombo.setBounds(new Rectangle(60, 14, 364, 21));
		projectcombo.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				selectedIndex = projectcombo.getSelectionIndex();
				selectedProject = projects.get(selectedIndex);
				
				IRBtable.removeAll();
				List<ParamType> params = selectedProject.getParam();
				for(int i=0; i<params.size(); i++) {
					TableItem item = new TableItem(IRBtable, SWT.NULL);
					ParamType param = params.get(i);
					item.setText(new String[] {
							param.getName(), 
							param.getValue()
					});
				}
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
					
			}
				
		});
		
		//List<ProjectType> projects = UserInfoBean.getInstance().getProjects();
		for(int i=0; i<projects.size(); i++) {
			ProjectType project = projects.get(i);
			projectcombo.add(project.getName());
		}
		
		projectcombo.select(0);
	}
	
	public void open(Display parent) {
		Display display = parent;
		createSShell();
		sShell.setLocation(350, 200);
		sShell.open();
		
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		//display.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
		 * for the correct SWT library path in order to run with the SWT dlls. 
		 * The dlls are located in the SWT plugin jar.  
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		Display display = Display.getDefault();
		ProjectDialog thisClass = new ProjectDialog();
		thisClass.createSShell();
		thisClass.sShell.open();

		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText(Messages.getString("ProjectDialog.Text")); //$NON-NLS-1$
		sShell.setSize(new Point(500, 203));
		sShell.setLayout(null);
		
		projectlabel = new Label(sShell, SWT.NONE);
		projectlabel.setBounds(new Rectangle(14, 20, 45, 20));
		projectlabel.setText(Messages.getString("ProjectDialog.Project")); //$NON-NLS-1$
		createProjectcombo();
		
		gobutton = new Button(sShell, SWT.NONE);
		gobutton.setBounds(new Rectangle(439, 14, 44, 24));
		gobutton.setText(Messages.getString("ProjectDialog.Go")); //$NON-NLS-1$
		gobutton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				UserInfoBean.selectedProject(selectedProject);
				//if(UserInfoBean.getInstance().getUserPassword().equalsIgnoreCase("***")) {
				//	System.out.println("Password: ***");
				//	PasswordDialog passwordDialog = new PasswordDialog(new Shell());
				//	passwordDialog.open();
				//}
				cancelSelected = false;
				
				sShell.close();
			}
		});
		
		IRBtable = new Table(sShell, SWT.BORDER);
		IRBtable.setHeaderVisible(true);
		IRBtable.setLinesVisible(true);
		IRBtable.setBounds(new Rectangle(11, 55, 467, 93));
		
		TableColumn id = new TableColumn(IRBtable, SWT.LEFT);
		id.setText(Messages.getString("ProjectDialog.Info")); //$NON-NLS-1$
		id.setWidth(100);
		TableColumn info = new TableColumn(IRBtable, SWT.LEFT);
		
		info.setText(Messages.getString("ProjectDialog.Description")); //$NON-NLS-1$
		info.setWidth(345);	
		
		List<ParamType> params = selectedProject.getParam();
		for(int i=0; i<params.size(); i++) {
			TableItem item = new TableItem(IRBtable, SWT.NULL);
			ParamType param = params.get(i);
			item.setText(new String[] {
					param.getName(), 
					param.getValue()
			});
		}
		sShell.setDefaultButton(gobutton);	
	}

}
