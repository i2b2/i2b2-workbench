/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 
 * Wensong Pan - Adding admin tool panels 
 * 
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetOntProcessStatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.OntologyProcessStatusListType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.OntologyProcessStatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.UpdateConceptTotalNumType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.VocabRequestType;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.views.PatientDataMessage;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.CRCServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.GetStatusReportResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.PFTJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.UpdateConceptTotalNumResponseMessage;



import edu.harvard.i2b2.patientMapping.ui.AdminToolJPanel;
import edu.harvard.i2b2.patientMapping.ui.AdminToolMonitorJPanel;
import edu.harvard.i2b2.patientMapping.ui.AdminToolSetKeyJPanel;
import edu.harvard.i2b2.patientMapping.ui.AdminToolSetPasswordJPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
//import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.RowLayout;

import com.cloudgarden.resource.SWTResourceManager;

public class PatientCountView extends ViewPart{

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.admin.patientCount.views.admin.PatientCountView";
	public static final String THIS_CLASS_NAME = PatientCountView.class.getName();

	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.explorer";
	public static final String ADMINTOOL_VIEW_CONTEXT_ID = PREFIX
			+ ".managersTool_view_help_context";
	public static final String OS = System.getProperty("os.name").toLowerCase();

	private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	private PatientDataType patientDataType= null;

	private  Label lblElaspedTime;
	private Label statusLog;
	private Table table;
	private Button btnStop;
	private Button btnStart;
	private Button btnRestartOnly;
	private Button btnSyn;
	private ProgressBar progressBar;
	private Tree tree;
	private Group group;
	private Composite right;
	private Composite composite1;
	private Composite composite;
	private Composite parent_;

	/**
	 * The constructor
	 */
	public PatientCountView() {
		//getPftProperties();
	}

	public String getNoteFromPDO(String xmlstr)
	{
		String note = null;
		try {
			JAXBUtil jaxbUtil = PFTJAXBUtil.getJAXBUtil();
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(xmlstr);
			DndType dndType = (DndType)jaxbElement.getValue();

			if(dndType == null)
				log.info("dndType is null");

			patientDataType = (PatientDataType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),
					PatientDataType.class);

			if(patientDataType == null)
				log.info("patientDataType is null");

			note = (String) patientDataType.getObservationSet().get(0).getObservation().get(0).getObservationBlob().getContent().get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error marshalling Explorer drag text");
		}
		return note;

	}


	/**
	 * This is a callback that will allow us
	 * to create the tabbed viewers and initialize them.
	 * 
	 * @param parent   Composite the PFT UI is contained within
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.info("admin plugin version 1.7.0");
		Font textFont = null;
		parent_ = parent;

		if (OS.startsWith("mac"))
			textFont = new Font(parent.getDisplay(), "Monaco", 12, SWT.NORMAL);
		else
			textFont = new Font(parent.getDisplay(), "Courier New", 10, SWT.NORMAL);

		GridLayout layout = new GridLayout(1, false);
		layout.numColumns = 2;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		new Label(parent, SWT.NONE);

		composite = new Composite(parent, SWT.NONE);
		GridData compositeLData = new GridData();
		compositeLData.verticalAlignment = GridData.FILL;
		compositeLData.horizontalAlignment = GridData.FILL;
		compositeLData.grabExcessHorizontalSpace = true;
		compositeLData.grabExcessVerticalSpace = true;
		composite.setLayoutData(compositeLData);
		composite.setLayout(new GridLayout(2, false));

		tree = new Tree(composite, SWT.BORDER);
		GridData treeLData = new GridData();
		treeLData.verticalAlignment = GridData.FILL;
		treeLData.grabExcessVerticalSpace = true;
		tree.setLayoutData(treeLData);
		tree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				TreeItem[] items = tree.getSelection();
				TreeItem item = items[0];
				System.out.println(item.getText()+" item selected");
				if(item.getText().equalsIgnoreCase("audit")) {
					log.info(item.getText()+" item selected");
					audit();
					/*//group.setVisible(false);
					//composite1.setVisible(true);
					if(right != null && !right.isDisposed()) {
						right.dispose();
						right = null;
					}
					
					right = new Composite(composite, SWT.NONE);
					GridLayout rightLayout1 = new GridLayout();
					GridData rightLData = new GridData();
					rightLData.grabExcessHorizontalSpace = true;
					rightLData.grabExcessVerticalSpace = true;
					rightLData.horizontalAlignment = GridData.FILL;
					rightLData.verticalAlignment = GridData.FILL;
					right.setLayoutData(rightLData);
					right.setLayout(rightLayout1);
					GridData composite1LData = new GridData();
					composite1LData.grabExcessHorizontalSpace = true;
					composite1LData.grabExcessVerticalSpace = true;
					composite1LData.horizontalAlignment = GridData.FILL;
					composite1LData.verticalAlignment = GridData.FILL;
					
					composite1 = new Composite(right, SWT.EMBEDDED);
					composite1.setLayoutData(composite1LData);

					Frame runFrame = SWT_AWT.new_Frame(composite1);
					Panel runPanel = new Panel(new BorderLayout());
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e1) {
						System.out.println("Error setting native LAF: " + e1);
					}

					runFrame.add(runPanel);
					JRootPane runRoot = new JRootPane();
					runPanel.add(runRoot);
					java.awt.Container oAwtContainer = runRoot.getContentPane();

					AdminToolMonitorJPanel runTreePanel = new AdminToolMonitorJPanel();//PatientMappingJPanel();//PreviousQueryPanel(this);
					//runTreePanel.setBackground(Color.BLUE);

					oAwtContainer.add(runTreePanel);
					//oAwtContainer.setVisible(true);
					//oAwtContainer.setSize(500, 500);
					
					//right.layout(true);
					//right.setVisible(true);
					//right.update();
					
					composite.layout(true);
					//composite.setVisible(true);
					//composite.update();*/

				}
				else if(item.getText().equalsIgnoreCase("Set Key")) {
					log.info(item.getText()+" item selected");
					setKey();
					//group.setVisible(false);
					//composite1.setVisible(true);
					/*if(right != null && !right.isDisposed()) {
						right.dispose();
						right = null;
					}
					
					right = new Composite(composite, SWT.NONE);
					GridLayout rightLayout1 = new GridLayout();
					GridData rightLData = new GridData();
					rightLData.grabExcessHorizontalSpace = true;
					rightLData.grabExcessVerticalSpace = true;
					rightLData.horizontalAlignment = GridData.FILL;
					rightLData.verticalAlignment = GridData.FILL;
					right.setLayoutData(rightLData);
					right.setLayout(rightLayout1);
					GridData composite1LData = new GridData();
					composite1LData.grabExcessHorizontalSpace = true;
					composite1LData.grabExcessVerticalSpace = true;
					composite1LData.horizontalAlignment = GridData.FILL;
					composite1LData.verticalAlignment = GridData.FILL;
					
					composite1 = new Composite(right, SWT.EMBEDDED);
					composite1.setLayoutData(composite1LData);

					Frame runFrame = SWT_AWT.new_Frame(composite1);
					Panel runPanel = new Panel(new BorderLayout());
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e1) {
						System.out.println("Error setting native LAF: " + e1);
					}

					runFrame.add(runPanel);
					JRootPane runRoot = new JRootPane();
					runPanel.add(runRoot);
					java.awt.Container oAwtContainer = runRoot.getContentPane();

					AdminToolSetKeyJPanel runTreePanel = new AdminToolSetKeyJPanel();//PatientMappingJPanel();//PreviousQueryPanel(this);
					//runTreePanel.setBackground(Color.BLUE);

					oAwtContainer.add(runTreePanel);
					//oAwtContainer.setVisible(true);
					//oAwtContainer.setSize(500, 500);
					
					//right.layout(true);
					//right.setVisible(true);
					//right.update();
					
					composite.layout(true);
					//composite.setVisible(true);
					//composite.update();*/

				}
				else if(item.getText().equalsIgnoreCase("Set Password")) {
					log.info(item.getText()+" item selected");
					setPassword();
					
					//group.setVisible(false);
					//composite1.setVisible(true);
					/*if(right != null && !right.isDisposed()) {
						right.dispose();
						right = null;
					}
					
					right = new Composite(composite, SWT.NONE);
					GridLayout rightLayout1 = new GridLayout();
					GridData rightLData = new GridData();
					rightLData.grabExcessHorizontalSpace = true;
					rightLData.grabExcessVerticalSpace = true;
					rightLData.horizontalAlignment = GridData.FILL;
					rightLData.verticalAlignment = GridData.FILL;
					right.setLayoutData(rightLData);
					right.setLayout(rightLayout1);
					GridData composite1LData = new GridData();
					composite1LData.grabExcessHorizontalSpace = true;
					composite1LData.grabExcessVerticalSpace = true;
					composite1LData.horizontalAlignment = GridData.FILL;
					composite1LData.verticalAlignment = GridData.FILL;
					
					composite1 = new Composite(right, SWT.EMBEDDED);
					composite1.setLayoutData(composite1LData);

					Frame runFrame = SWT_AWT.new_Frame(composite1);
					Panel runPanel = new Panel(new BorderLayout());
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e1) {
						System.out.println("Error setting native LAF: " + e1);
					}

					runFrame.add(runPanel);
					JRootPane runRoot = new JRootPane();
					runPanel.add(runRoot);
					java.awt.Container oAwtContainer = runRoot.getContentPane();

					AdminToolSetPasswordJPanel runTreePanel = new AdminToolSetPasswordJPanel();//PatientMappingJPanel();//PreviousQueryPanel(this);
					//runTreePanel.setBackground(Color.BLUE);

					oAwtContainer.add(runTreePanel);
					//oAwtContainer.setVisible(true);
					//oAwtContainer.setSize(500, 500);
					
					//right.layout(true);
					//right.setVisible(true);
					//right.update();
					
					composite.layout(true);
					//composite.setVisible(true);
					//composite.update();*/

				}
				else {
					if(right != null && !right.isDisposed()) {
						right.dispose();
						right = null;
					}	
						right = new Composite(composite, SWT.NONE);
						FillLayout rightLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
						right.setLayout(rightLayout);

						group = new Group(right, SWT.NONE);
						group.setLayout(new GridLayout(3, false));
						new Label(group, SWT.NONE);

						Label lblPatientCountProcess = new Label(group, SWT.NONE);
						lblPatientCountProcess.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
						lblPatientCountProcess.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.BOLD));
						lblPatientCountProcess.setText("Update Patient Counts Associated With Terms");

						btnStart = new Button(group, SWT.NONE);
						btnStart.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								updatePatientCount(false).start();
							}
						});
						btnStart.setText("Start");

						Label lblStartPatientCount = new Label(group, SWT.NONE);
						lblStartPatientCount.setText("Start Update Term Usage");
						new Label(group, SWT.NONE);
						new Label(group, SWT.NONE);

						btnSyn = new Button(group, SWT.RADIO);
						btnSyn.setText("Update all patient counts associated with ontology terms");
						btnSyn.setSelection(true);
						new Label(group, SWT.NONE);
						new Label(group, SWT.NONE);

						btnRestartOnly = new Button(group, SWT.RADIO);
						btnRestartOnly.setText("Update only the ontology terms with blank patient counts");
						new Label(group, SWT.NONE);

						btnStop = new Button(group, SWT.NONE);
						btnStop.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								btnStop.setEnabled(false);
								btnStart.setEnabled(true);

							}
						});
						btnStop.setText("Stop");
						btnStop.setEnabled(false);

						Label lblStopPatientCount = new Label(group, SWT.NONE);
						lblStopPatientCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
						lblStopPatientCount.setText("Stop Update Term Usage");
						new Label(group, SWT.NONE);

						progressBar = new ProgressBar(group, SWT.NONE);
						progressBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
						progressBar.setMaximum(100);
						new Label(group, SWT.NONE);

						lblElaspedTime = new Label(group, SWT.NONE);
						lblElaspedTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
						lblElaspedTime.setText("Elasped Time: Not Started");
						new Label(group, SWT.NONE);

						final TabFolder tabFolder = new TabFolder(group, SWT.NONE);
						GridData gd_tabFolder = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
						gd_tabFolder.heightHint = 160;
						tabFolder.setLayoutData(gd_tabFolder);
						tabFolder.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if (tabFolder.getSelectionIndex() == 1)
									updateHistoryTable().start();
							}
						});

						TabItem tbtmLog = new TabItem(tabFolder, SWT.NONE);
						tbtmLog.setText("Log");

						ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
						tbtmLog.setControl(scrolledComposite);
						scrolledComposite.setExpandHorizontal(true);
						scrolledComposite.setExpandVertical(true);

						statusLog = new Label(scrolledComposite, SWT.NONE);
						scrolledComposite.setContent(statusLog);
						scrolledComposite.setMinSize(statusLog.computeSize(SWT.DEFAULT, SWT.DEFAULT));
						statusLog.setText("Loaded Successful");

						TabItem tbtmHistory = new TabItem(tabFolder, SWT.NONE);
						tbtmHistory.setText("History");

						table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
						tbtmHistory.setControl(table);
						table.setHeaderVisible(true);
						table.setLinesVisible(true);

						TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
						tblclmnId.setWidth(20);
						tblclmnId.setText("ID");

						TableColumn tblclmnStatus = new TableColumn(table, SWT.NONE);
						tblclmnStatus.setWidth(100);
						tblclmnStatus.setText("Status");

						TableColumn tblclmnStartDate = new TableColumn(table, SWT.NONE);
						tblclmnStartDate.setWidth(100);
						tblclmnStartDate.setText("Start Date");

						TableColumn tblclmnEndDate = new TableColumn(table, SWT.NONE);
						tblclmnEndDate.setWidth(100);
						tblclmnEndDate.setText("End Date");
						
						composite.layout(true);
				}
			}
			
		});

		TreeItem trtmUpdateTermUsage = new TreeItem(tree, SWT.NONE);
		trtmUpdateTermUsage.setText("Update Term Usage");
		
		TreeItem setkey = new TreeItem(tree, SWT.NONE);
		setkey.setText("Set Key");
		
		TreeItem audit = new TreeItem(tree, SWT.NONE);
		audit.setText("Audit");
		
		//TreeItem password = new TreeItem(tree, SWT.NONE);
		//password.setText("Set Password");

		right = new Composite(composite, SWT.NONE);
		FillLayout rightLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		right.setLayout(rightLayout);

		group = new Group(right, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		new Label(group, SWT.NONE);

		Label lblPatientCountProcess = new Label(group, SWT.NONE);
		lblPatientCountProcess.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblPatientCountProcess.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.BOLD));
		lblPatientCountProcess.setText("Update Patient Counts Associated With Terms");

		btnStart = new Button(group, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePatientCount(false).start();
			}
		});
		btnStart.setText("Start");

		Label lblStartPatientCount = new Label(group, SWT.NONE);
		lblStartPatientCount.setText("Start Update Term Usage");
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);

		btnSyn = new Button(group, SWT.RADIO);
		btnSyn.setText("Update all patient counts associated with ontology terms");
		btnSyn.setSelection(true);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);

		btnRestartOnly = new Button(group, SWT.RADIO);
		btnRestartOnly.setText("Update only the ontology terms with blank patient counts");
		new Label(group, SWT.NONE);

		btnStop = new Button(group, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnStop.setEnabled(false);
				btnStart.setEnabled(true);

			}
		});
		btnStop.setText("Stop");
		btnStop.setEnabled(false);

		Label lblStopPatientCount = new Label(group, SWT.NONE);
		lblStopPatientCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblStopPatientCount.setText("Stop Update Term Usage");
		new Label(group, SWT.NONE);

		progressBar = new ProgressBar(group, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		progressBar.setMaximum(100);
		new Label(group, SWT.NONE);

		lblElaspedTime = new Label(group, SWT.NONE);
		lblElaspedTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblElaspedTime.setText("Elasped Time: Not Started");
		new Label(group, SWT.NONE);

		final TabFolder tabFolder = new TabFolder(group, SWT.NONE);
		GridData gd_tabFolder = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
		gd_tabFolder.heightHint = 160;
		tabFolder.setLayoutData(gd_tabFolder);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tabFolder.getSelectionIndex() == 1)
					updateHistoryTable().start();

			}
		});

		TabItem tbtmLog = new TabItem(tabFolder, SWT.NONE);
		tbtmLog.setText("Log");

		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmLog.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		statusLog = new Label(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(statusLog);
		scrolledComposite.setMinSize(statusLog.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		statusLog.setText("Loaded Successful");

		TabItem tbtmHistory = new TabItem(tabFolder, SWT.NONE);
		tbtmHistory.setText("History");

		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmHistory.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(20);
		tblclmnId.setText("ID");

		TableColumn tblclmnStatus = new TableColumn(table, SWT.NONE);
		tblclmnStatus.setWidth(100);
		tblclmnStatus.setText("Status");

		TableColumn tblclmnStartDate = new TableColumn(table, SWT.NONE);
		tblclmnStartDate.setWidth(100);
		tblclmnStartDate.setText("Start Date");

		TableColumn tblclmnEndDate = new TableColumn(table, SWT.NONE);
		tblclmnEndDate.setWidth(100);
		tblclmnEndDate.setText("End Date");
		
		// Setup help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
						ADMINTOOL_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
		
		//if(right != null) {
			//right.dispose();
		//}

		//composite1.setVisible(true);
		//right.redraw();
		//right.layout();
		
		//updatePatientCount(true).start();
		//Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
	}

	private void addLog(String str)
	{

		statusLog.setText(statusLog.getText() + "\n" + str);
	}

	//  help control

	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action() {
			public void run(){
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.adminTool/html/i2b2_mt_index.html");
			}
		};	
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(PatientCountView.class, "/icons/help.png"));

		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}
	
	private void setPassword() {
		if(right != null && !right.isDisposed()) {
			right.dispose();
			right = null;
		}
		
		right = new Composite(composite, SWT.NONE);
		GridLayout rightLayout1 = new GridLayout();
		GridData rightLData = new GridData();
		rightLData.grabExcessHorizontalSpace = true;
		rightLData.grabExcessVerticalSpace = true;
		rightLData.horizontalAlignment = GridData.FILL;
		rightLData.verticalAlignment = GridData.FILL;
		right.setLayoutData(rightLData);
		right.setLayout(rightLayout1);
		GridData composite1LData = new GridData();
		composite1LData.grabExcessHorizontalSpace = true;
		composite1LData.grabExcessVerticalSpace = true;
		composite1LData.horizontalAlignment = GridData.FILL;
		composite1LData.verticalAlignment = GridData.FILL;
		
		composite1 = new Composite(right, SWT.EMBEDDED);
		composite1.setLayoutData(composite1LData);

		/* Create and setting up frame */
		////for mac fix
		//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
			//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";

		Frame runFrame = SWT_AWT.new_Frame(composite1);
		Panel runPanel = new Panel(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			System.out.println("Error setting native LAF: " + e1);
		}

		runFrame.add(runPanel);
		JRootPane runRoot = new JRootPane();
		runPanel.add(runRoot);
		java.awt.Container oAwtContainer = runRoot.getContentPane();

		AdminToolSetPasswordJPanel runTreePanel = new AdminToolSetPasswordJPanel();//PatientMappingJPanel();//PreviousQueryPanel(this);
		//runTreePanel.setBackground(Color.BLUE);

		oAwtContainer.add(runTreePanel);
		//oAwtContainer.setVisible(true);
		//oAwtContainer.setSize(500, 500);
		
		//right.layout(true);
		//right.setVisible(true);
		//right.update();
		
		composite.layout(true);
		//composite.setVisible(true);
		//composite.update();
	}
	
	
	private void setKey() {
		if(right != null && !right.isDisposed()) {
			right.dispose();
			right = null;
		}
		
		right = new Composite(composite, SWT.NONE);
		GridLayout rightLayout1 = new GridLayout();
		GridData rightLData = new GridData();
		rightLData.grabExcessHorizontalSpace = true;
		rightLData.grabExcessVerticalSpace = true;
		rightLData.horizontalAlignment = GridData.FILL;
		rightLData.verticalAlignment = GridData.FILL;
		right.setLayoutData(rightLData);
		right.setLayout(rightLayout1);
		GridData composite1LData = new GridData();
		composite1LData.grabExcessHorizontalSpace = true;
		composite1LData.grabExcessVerticalSpace = true;
		composite1LData.horizontalAlignment = GridData.FILL;
		composite1LData.verticalAlignment = GridData.FILL;
		
		composite1 = new Composite(right, SWT.EMBEDDED);
		composite1.setLayoutData(composite1LData);

		/* Create and setting up frame */
		////for mac fix
		//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
			//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
		Frame runFrame = SWT_AWT.new_Frame(composite1);
		Panel runPanel = new Panel(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			System.out.println("Error setting native LAF: " + e1);
		}

		runFrame.add(runPanel);
		JRootPane runRoot = new JRootPane();
		runPanel.add(runRoot);
		java.awt.Container oAwtContainer = runRoot.getContentPane();

		AdminToolSetKeyJPanel runTreePanel = new AdminToolSetKeyJPanel();//PatientMappingJPanel();//PreviousQueryPanel(this);
		//runTreePanel.setBackground(Color.BLUE);

		oAwtContainer.add(runTreePanel);
		//oAwtContainer.setVisible(true);
		//oAwtContainer.setSize(500, 500);
		
		//right.layout(true);
		//right.setVisible(true);
		//right.update();
		
		composite.layout(true);
		//composite.setVisible(true);
		//composite.update();
	}
	
	private void audit() {
		//System.out.println(item.getText()+" item selected");
		//group.setVisible(false);
		//composite1.setVisible(true);
		if(right != null && !right.isDisposed()) {
			right.dispose();
			right = null;
		}
		
		right = new Composite(composite, SWT.NONE);
		GridLayout rightLayout1 = new GridLayout();
		GridData rightLData = new GridData();
		rightLData.grabExcessHorizontalSpace = true;
		rightLData.grabExcessVerticalSpace = true;
		rightLData.horizontalAlignment = GridData.FILL;
		rightLData.verticalAlignment = GridData.FILL;
		right.setLayoutData(rightLData);
		right.setLayout(rightLayout1);
		GridData composite1LData = new GridData();
		composite1LData.grabExcessHorizontalSpace = true;
		composite1LData.grabExcessVerticalSpace = true;
		composite1LData.horizontalAlignment = GridData.FILL;
		composite1LData.verticalAlignment = GridData.FILL;
		
		composite1 = new Composite(right, SWT.EMBEDDED);
		composite1.setLayoutData(composite1LData);

		/* Create and setting up frame */
		////for mac fix
		//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
		//	SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";	
		Frame runFrame = SWT_AWT.new_Frame(composite1);
		Panel runPanel = new Panel(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			System.out.println("Error setting native LAF: " + e1);
		}

		runFrame.add(runPanel);
		JRootPane runRoot = new JRootPane();
		runPanel.add(runRoot);
		java.awt.Container oAwtContainer = runRoot.getContentPane();

		AdminToolMonitorJPanel runTreePanel = new AdminToolMonitorJPanel();//PatientMappingJPanel();//PreviousQueryPanel(this);
		//runTreePanel.setBackground(Color.BLUE);

		oAwtContainer.add(runTreePanel);
		//oAwtContainer.setVisible(true);
		//oAwtContainer.setSize(500, 500);
		
		//right.layout(true);
		//right.setVisible(true);
		//right.update();
		
		composite.layout(true);
		//composite.setVisible(true);
		//composite.update();
	}


	/**
	 * Create a new thread to send the message to the CRC Web Service
	 *   and display the response
	 *   
	 */
	public  Thread updatePatientCount(final boolean restarted) {

		final Display theDisplay = Display.getCurrent();
		final boolean restartOnly = btnRestartOnly.getSelection();

		return new Thread() {
			OntologyProcessStatusType currentRun= null;

			String theResult = "test";
			private void waitTilDone(final OntologyProcessStatusType currentRun) throws Exception
			{

				if (currentRun == null) return;

				boolean completed = false;
				do {
					GetOntProcessStatusType vocabType = new GetOntProcessStatusType();
					vocabType.setMaxReturnRecords(100);
					//vocabType.setProcessTypeCd("ONT_PATIENT_COUNT_UPDATE");
					vocabType.setProcessId(currentRun.getProcessId());
					//"<get_ont_process_status_request max_return_records=�100�></get_ont_process_status_request>";
					theResult = OntServiceDriver.getStatusReport(vocabType);	
					if (theResult != null)
					{
						GetStatusReportResponseMessage msg2 = new GetStatusReportResponseMessage();
						//PatientDataMessage msg = new PatientDataMessage();
						//ResultsTab.getInstance().removeLastLine();
						//ResultsTab.getInstance().setPdoMsg(msg.getPatientDataType(theResult));
						StatusType stus2 = msg2.processResult(theResult);
						if (stus2.getType().equals("DONE"))
						{
							OntologyProcessStatusListType statusList = msg2.doReadProcessStatus();


							for (final OntologyProcessStatusType status: statusList.getOntologyProcessStatus()) {


								theDisplay.syncExec(new Runnable() {
									public void run() {
										//long diff = status.getEndDate().toGregorianCalendar()
										//.getTimeInMillis()
										//- status.getStartDate().toGregorianCalendar()
										//.getTimeInMillis();
										long diff = (new java.util.Date()).getTime()
										- status.getStartDate().toGregorianCalendar()
										.getTimeInMillis();

										
										String[] process1 = status.getProcessStepCd().split(" ");
										if (process1.length>1)
										{
											String[] process2 = process1[1].split("/");
											if (process2.length>1)
											{
												progressBar.setMaximum(Integer.parseInt(process2[1]));
												progressBar.setSelection(Integer.parseInt(process2[0]));
												lblElaspedTime.setText("Elapsed Time: " + (diff / 1000) + " sec.  Processed:" + process2[0] + " of " + process2[1]);

											}

										}
									}
								});
								if (!status.getProcessStatusCd().equals("PROCESSING"))
									completed = true;

							}
						}
					}
					Thread.sleep(200000);
				} while (completed == false);
			}
			@Override
			public void run(){

				if (restarted) {
					//Check to see if previous one is running
					try {

						GetOntProcessStatusType vocabType = new GetOntProcessStatusType();
						vocabType.setMaxReturnRecords(100);
						vocabType.setProcessTypeCd("ONT_PATIENT_COUNT_UPDATE");
						//vocabType.setProcessId("3");
						//"<get_ont_process_status_request max_return_records=�100�></get_ont_process_status_request>";
						theResult = OntServiceDriver.getStatusReport(vocabType);	
						if (theResult == null)
							theResult = "No ONT response generated";
					} catch (Exception e) {
						// Exceptions are for AxisFault or OMElement generation
						//log.error(e.getMessage());
						theResult = "ONTService: " + e.getMessage();
					}
					
					theDisplay.syncExec(new Runnable() {
						public void run() {
							// Display the PFT web service response 
							btnStop.setEnabled(true);
							btnStart.setEnabled(false);
						}
					});
					//theDisplay.syncExec(new Runnable() {
					//	public void run() {
							// Display the PFT web service response 
							try {
								//ResponseTab.getInstance().setText(theResult);
								GetStatusReportResponseMessage msg = new GetStatusReportResponseMessage();
								//PatientDataMessage msg = new PatientDataMessage();
								//ResultsTab.getInstance().removeLastLine();
								//ResultsTab.getInstance().setPdoMsg(msg.getPatientDataType(theResult));
								StatusType stus = msg.processResult(theResult);
								if (stus.getType().equals("DONE"))
								{
									OntologyProcessStatusListType statusList = msg.doReadProcessStatus();

									for (OntologyProcessStatusType status: statusList.getOntologyProcessStatus()) {
										if (status.getEndDate() != null && status.getProcessStatusCd().equals("PROCESSING"))
											waitTilDone(status);
									}
								}
							} catch (Exception e)
							{}
						//}
					//});
				}
				else {
					try {
						// make ONT Web service call
						UpdateConceptTotalNumType vocabType = new UpdateConceptTotalNumType();

						if (restartOnly)
						{
							vocabType.setOperationType("restart_only");
						} else
						{
							vocabType.setOperationType("synchronize_all");						
						}
						//"<get_ont_process_status_request max_return_records=�100�></get_ont_process_status_request>";
						theResult = OntServiceDriver.updatePatientCount(vocabType);	
						if (theResult == null)
							theResult = "No ONT response generated";
					} catch (Exception e) {
						// Exceptions are for AxisFault or OMElement generation
						//log.error(e.getMessage());
						theResult = "ONTService: " + e.getMessage();
					}

					theDisplay.syncExec(new Runnable() {
						public void run() {
							// Display the PFT web service response 
							btnStop.setEnabled(true);
							btnStart.setEnabled(false);

							//ResponseTab.getInstance().setText(theResult);
							UpdateConceptTotalNumResponseMessage msg = new UpdateConceptTotalNumResponseMessage();
							//PatientDataMessage msg = new PatientDataMessage();
							//ResultsTab.getInstance().removeLastLine();
							//ResultsTab.getInstance().setPdoMsg(msg.getPatientDataType(theResult));
							StatusType stus = msg.processResult(theResult);


							if (stus.getType().equals("DONE"))
							{
								//lblElaspedTime.setText(string)
								currentRun = msg.doReadProcessStatus();



							} else
							{
								addLog(stus.getValue());
							}
							//msg.doReadConceptCode(theResult);	
							//ResultsTab.getInstance().setName();


						}
					});
					try {

						waitTilDone(currentRun);
					} catch (Exception e) {
						// Log exception
						//log.error(e.getMessage());
					}
					theDisplay.syncExec(new Runnable() {
						public void run() {

							btnStop.setEnabled(false);
							btnStart.setEnabled(true);
							progressBar.setSelection(progressBar.getMaximum());

						}
					});
				}
			}
		};
	}

	/**
	 * Create a new thread to send the message to the CRC Web Service
	 *   and display the response
	 *   
	 */
	public  Thread updateHistoryTable() {

		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			String theResult = "test";
			@Override
			public void run(){
				try {
					// make ONT Web service call
					GetOntProcessStatusType vocabType = new GetOntProcessStatusType();
					vocabType.setMaxReturnRecords(100);
					vocabType.setProcessTypeCd("ONT_PATIENT_COUNT_UPDATE");
					//vocabType.setProcessId("3");
					//"<get_ont_process_status_request max_return_records=�100�></get_ont_process_status_request>";
					theResult = OntServiceDriver.getStatusReport(vocabType);	
					if (theResult == null)
						theResult = "No ONT response generated";
				} catch (Exception e) {
					// Exceptions are for AxisFault or OMElement generation
					//log.error(e.getMessage());
					theResult = "ONTService: " + e.getMessage();
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						// Display the PFT web service response 
						try {
							//ResponseTab.getInstance().setText(theResult);
							GetStatusReportResponseMessage msg = new GetStatusReportResponseMessage();
							//PatientDataMessage msg = new PatientDataMessage();
							//ResultsTab.getInstance().removeLastLine();
							//ResultsTab.getInstance().setPdoMsg(msg.getPatientDataType(theResult));
							StatusType stus = msg.processResult(theResult);
							if (stus.getType().equals("DONE"))
							{
								OntologyProcessStatusListType statusList = msg.doReadProcessStatus();

								table.clearAll();
								table.removeAll();
								for (OntologyProcessStatusType status: statusList.getOntologyProcessStatus()) {
									TableItem item = new TableItem(table, SWT.NONE);
									item.setText(0, status.getProcessId());
									item.setText(1, status.getProcessStatusCd());
									item.setText(2, status.getStartDate().toXMLFormat());
									item.setText(3, status.getEndDate().toXMLFormat());
								}
							}
							//msg.doReadConceptCode(theResult);	
							ResultsTab.getInstance().setName();
						} catch (Exception e) {
							// Log exception
							//log.error(e.getMessage());
						}
					}
				});
			}
		};
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
