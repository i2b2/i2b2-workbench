/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.patientMapping.ui;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.InstanceResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.RequestXmlType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.patientMapping.utils.StackData;
import edu.harvard.i2b2.patientMapping.data.QueryInstanceData;
import edu.harvard.i2b2.patientMapping.data.QueryMasterData;
import edu.harvard.i2b2.patientMapping.dataModel.ConceptTableModel;
import edu.harvard.i2b2.patientMapping.dataModel.ConceptTableRow;
import edu.harvard.i2b2.patientMapping.dataModel.KTable;
import edu.harvard.i2b2.patientMapping.dataModel.KTableCellResizeListener;
import edu.harvard.i2b2.patientMapping.dataModel.KTableCellSelectionListener;
import edu.harvard.i2b2.patientMapping.dataModel.ModifierData;
import edu.harvard.i2b2.patientMapping.dataModel.QueryModel;
import edu.harvard.i2b2.patientMapping.dataModel.TimelineRow;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;
import edu.harvard.i2b2.patientMapping.serviceClient.PatientMappingQueryClient;
import edu.harvard.i2b2.smlib.DBLib;
//import edu.harvard.i2b2.timeline.lifelines.QueryClient;
//import edu.harvard.i2b2.timeline.lifelines.Record;

public class MainComposite extends Composite {
	public static String noteKey = null;
	private static final Log log = LogFactory.getLog(MainComposite.class);
	private static final int MAX_STACK_SIZE = 28;

	public String msTitle = "I2B2 CRC Navigator in";
	public String msUsername = "";
	public String msPassword = "";
	public boolean bWantStatusLine = false;

	private Composite oTheParent;
	private StatusLineManager slm = new StatusLineManager();
	private KTable table;
	private Text patientMinNumText;
	private Text patientMaxNumText;
	private Text mrnlistText;
	private boolean bDisplayAllData = false;
	private boolean bDisplayDemographics = true;
	private WaitPanel p = null;
	private TabFolder tabFolder = null;
	private Thread visualizationQueryThread = null;
	private Connection oConnection = null;
	private LinkedHashMap<String, String> values = null;
	private String valueType = null;
	private int curRowNumber = 0;
	private ArrayList<ArrayList<ConceptTableRow>> rowData = null;
	private java.awt.Container oAwtContainer;
	private Button rightArrowButton;
	private Button leftArrowButton;

	private Text patientSetText;
	private boolean isAll = false;
	private Label queryNamemrnlistText;
	private Label groupNameText;

	//private Record theRecord = null;
	private FramedComposite oModelQueryComposite;
	private FramedComposite oModelGroupComposite;

	private int returnedNumber = -1;

	public void returnedNumber(int i) {
		returnedNumber = i;
	}

	private int runMode = 0;

	public int runMode() {
		return runMode;
	}

	//public Record getRecord() {
	//	return theRecord;
	//}

	private String patientRefId = null;

	public void patientRefId(String str) {
		patientRefId = new String(str);
	}

	private int patientSetSize = 0;

	private boolean drawLeft = true;

	public boolean drawLeft() {
		return drawLeft;
	}

	private String lastRequestMessage;

	public String lastRequestMessage() {
		return lastRequestMessage;
	}

	private List<StackData> xmlStack;

	public List<StackData> getXmlStack() {
		return xmlStack;
	}

	private String lastResponseMessage;

	public void lastRequestMessage(String msg) {
		lastRequestMessage = msg;
		if (xmlStack == null)
			xmlStack = new ArrayList<StackData>();
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(msg);
		stackData.setName("Sent" + stackData.getTimestamp());
		xmlStack.add(stackData);
	}

	public void lastResponseMessage(String msg) {
		lastResponseMessage = msg;
		if (xmlStack == null)
			xmlStack = new ArrayList<StackData>();
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(msg);
		stackData.setName("Received" + stackData.getTimestamp());
		xmlStack.add(stackData);
	}

	private void checkXmlStackSize() {
		if (xmlStack.size() == MAX_STACK_SIZE) {
			xmlStack.remove(0);
			xmlStack = xmlStack.subList(1, MAX_STACK_SIZE - 1);
		}
	}

	public String lastResponseMessage() {
		return lastResponseMessage;
	}

	public MainComposite(Composite parent) {
		super(parent, SWT.FLAT);

		oTheParent = parent;
		values = new LinkedHashMap<String, String>();
		rowData = new ArrayList<ArrayList<ConceptTableRow>>();

		xmlStack = new ArrayList<StackData>();

		createContents(parent);
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
			}
		});
	}

	public MainComposite(Composite parent, boolean drawleft) {
		super(parent, SWT.FLAT);

		oTheParent = parent;
		values = new LinkedHashMap<String, String>();
		rowData = new ArrayList<ArrayList<ConceptTableRow>>();
		xmlStack = new ArrayList<StackData>();

		drawLeft = drawleft;
		createContents(parent);
	}

	public void setPatientSetText(final String str) {
		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				patientSetText.setText(str);
			}
		});
	}

	public void selectTab(final int index) {
		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				tabFolder.setSelection(index);
			}
		});
	}

	public void setPatientMinNumText(final String str) {
		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				patientMinNumText.setText(str);
			}
		});
	}

	public void setPatientSetSize(final String str) {
		patientSetSize = new Integer(str).intValue();
		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				int maxPatientNum = new Integer(patientMaxNumText.getText())
						.intValue();
				if (patientSetSize > maxPatientNum) {
					rightArrowButton.setEnabled(true);
					patientMaxNumText.setText("10");
				} else {
					rightArrowButton.setEnabled(false);
					// if(patientSetSize>0) {
					// patientMaxNumText.setText(setSize);
					// }
				}
			}
		});
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("serial")
	protected Control createContents(Composite parent) {
		GridLayout topGridLayout = new GridLayout(1, false);
		topGridLayout.numColumns = 1;
		topGridLayout.marginWidth = 2;
		topGridLayout.marginHeight = 2;
		setLayout(topGridLayout);

		Composite oTreeComposite = new Composite(this, SWT.NONE);
		oTreeComposite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		oTreeComposite.setLayoutData(gridData2);

		// the horizontal sash form
		SashForm horizontalForm = new SashForm(oTreeComposite, SWT.HORIZONTAL);
		horizontalForm.setOrientation(SWT.HORIZONTAL);
		horizontalForm.setLayout(new GridLayout());

		if (drawLeft) {
			// left sash form
			SashForm leftVerticalForm = new SashForm(horizontalForm,
					SWT.VERTICAL);
			leftVerticalForm.setOrientation(SWT.VERTICAL);
			leftVerticalForm.setLayout(new GridLayout());

			if (bWantStatusLine) {
				slm.createControl(this, SWT.NULL);
			}
			slm.setMessage("i2b2 Explorer Version 2.0");
			slm.update(true);

			// Create the tab folder
			final TabFolder oTabFolder = new TabFolder(leftVerticalForm,
					SWT.NONE);

			// Create each tab and set its text, tool tip text,
			// image, and control
			TabItem oTreeTab = new TabItem(oTabFolder, SWT.NONE);
			oTreeTab.setText("Concept trees");
			oTreeTab
					.setToolTipText("Hierarchically organized patient characteristics");
			oTreeTab.setControl(getConceptTreeTabControl(oTabFolder));

			// Select the first tab (index is zero-based)
			oTabFolder.setSelection(0);

			// Create the tab folder
			final TabFolder queryRunFolder = new TabFolder(leftVerticalForm,
					SWT.NONE);

			TabItem previousRunTab = new TabItem(queryRunFolder, SWT.NONE);
			previousRunTab.setText("Patient Sets and Previous Queries");
			previousRunTab.setToolTipText("Patient Sets & Previous Queries");
			final Composite runComposite = new Composite(queryRunFolder,
					SWT.EMBEDDED);
			previousRunTab.setControl(runComposite);

			/* Create and setting up frame */
			////for mac fix
			//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
				//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
			Frame runFrame = SWT_AWT.new_Frame(runComposite);
			Panel runPanel = new Panel(new BorderLayout());
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				log.debug("Error setting native LAF: " + e);
			}

			runFrame.add(runPanel);
			JRootPane runRoot = new JRootPane();
			runPanel.add(runRoot);

			// Select the first tab (index is zero-based)
			queryRunFolder.setSelection(0);
		}

		SashForm verticalForm = new SashForm(horizontalForm, SWT.VERTICAL);
		verticalForm.setOrientation(SWT.VERTICAL);
		verticalForm.setLayout(new GridLayout());

		// put a tab folder in it...
		tabFolder = new TabFolder(verticalForm, SWT.NONE);

		Composite patientNumsComposite = new Composite(verticalForm, SWT.NONE);
		GridData patientNumData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		patientNumData.grabExcessHorizontalSpace = true;
		patientNumsComposite.setLayoutData(patientNumData);
		patientNumsComposite.setLayout(null);

		Label patientset = new Label(patientNumsComposite, SWT.NONE);
		patientset.setText("Patient Set: ");
		patientset.setBounds(5, 9, 60, 22);

		patientSetText = new Text(patientNumsComposite, SWT.SINGLE | SWT.BORDER);
		patientSetText.setText("");
		patientSetText.setEditable(false);
		patientSetText.setBounds(70, 5, 300, 35);

		leftArrowButton = new Button(patientNumsComposite, SWT.PUSH);
		leftArrowButton.setText("<<<");
		leftArrowButton.setEnabled(false);
		leftArrowButton.setBounds(380, 5, 38, 22);
		leftArrowButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				runMode = -1;
				ConceptTableModel i2b2Model = (ConceptTableModel) table
						.getModel();
				i2b2Model.fillDataFromTable(rowData);
				if (rowData.size() == 0) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("The set up table is empty.");
					mBox.open();
					return;
				}

				String patientSetStr = patientSetText.getText();
				if (patientSetStr.equals("") && !isAll) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox
							.setMessage("Please set a patient set or choose all datamart option.");
					mBox.open();
					return;
				}

				if (tabFolder.getSelectionIndex() == 1) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							DestroyMiniVisualization(oAwtContainer);
						}
					});
				} else if (tabFolder.getSelectionIndex() == 0) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(1);
						}
					});
				}

				if (patientSetStr.equalsIgnoreCase("All")) {
					int minPatient = 0;
					try {
						String minText = patientMinNumText.getText();
						minPatient = Integer.parseInt(minText);
					} catch (Exception e1) {
						minPatient = -1;
					}

					int maxPatient = 0;
					try {
						maxPatient = Integer.parseInt(patientMaxNumText
								.getText());
					} catch (Exception e2) {
						maxPatient = -1;
					}

					PerformVisualizationQuery(oAwtContainer, "All", minPatient,
							maxPatient, bDisplayAllData);
				} else {
					int min = Integer.parseInt(patientMinNumText.getText());
					int max = Integer.parseInt(patientMaxNumText.getText());
					int start = new Integer(patientMinNumText.getText())
							.intValue();
					int inc = new Integer(patientMaxNumText.getText())
							.intValue();
					if (start - inc <= 1) {
						leftArrowButton.setEnabled(false);
					}

					if (start <= patientSetSize) {
						rightArrowButton.setEnabled(true);
					} else {
						rightArrowButton.setEnabled(false);
					}

					//if ((start - inc) > 1) {
						//patientMinNumText.setText("" + (start - inc));
					//} else {
						//patientMinNumText.setText("1");
					//}
					PerformVisualizationQuery(oAwtContainer, patientRefId, min
							- max, max - 1, bDisplayAllData);
				}

			}
		});

		final Label patNum1 = new Label(patientNumsComposite, SWT.NONE);
		patNum1.setText(" start: ");
		patNum1.setBounds(425, 9, 31, 20);

		patientMinNumText = new Text(patientNumsComposite, SWT.SINGLE
				| SWT.BORDER);
		patientMinNumText.setText("1");
		patientMinNumText.setBounds(460, 5, 45, 22);

		final Label patNum2 = new Label(patientNumsComposite, SWT.NONE);
		patNum2.setText("increment:");
		patNum2.setBounds(515, 9, 57, 20);

		patientMaxNumText = new Text(patientNumsComposite, SWT.SINGLE
				| SWT.BORDER);
		patientMaxNumText.setText("10");
		patientMaxNumText.setBounds(572, 5, 45, 22);

		rightArrowButton = new Button(patientNumsComposite, SWT.PUSH);
		rightArrowButton.setText(">>>");
		rightArrowButton.setEnabled(false);
		rightArrowButton.setBounds(626, 5, 38, 20);
		rightArrowButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				runMode = 1;
				ConceptTableModel i2b2Model = (ConceptTableModel) table
						.getModel();
				i2b2Model.fillDataFromTable(rowData);
				if (rowData.size() == 0) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("The set up table is empty.");
					mBox.open();
					return;
				}

				String patientSetStr = patientSetText.getText();
				if (patientSetStr.equals("") && !isAll) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox
							.setMessage("Please set a patient set or choose all datamart option.");
					mBox.open();
					return;
				}

				if (tabFolder.getSelectionIndex() == 1) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							DestroyMiniVisualization(oAwtContainer);
						}
					});
				} else if (tabFolder.getSelectionIndex() == 0) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(1);
						}
					});
				}

				if (patientSetStr.equalsIgnoreCase("All")) {
					int minPatient = 0;
					try {
						String minText = patientMinNumText.getText();
						minPatient = Integer.parseInt(minText);
					} catch (Exception e1) {
						minPatient = -1;
					}

					int maxPatient = 0;
					try {
						maxPatient = Integer.parseInt(patientMaxNumText
								.getText());
					} catch (Exception e2) {
						maxPatient = -1;
					}

					PerformVisualizationQuery(oAwtContainer, "All", minPatient,
							maxPatient, bDisplayAllData);
				} else {
					int min = Integer.parseInt(patientMinNumText.getText());
					int max = Integer.parseInt(patientMaxNumText.getText());
					int start = new Integer(patientMinNumText.getText())
							.intValue();
					int inc = new Integer(patientMaxNumText.getText())
							.intValue();
					if (start + inc > patientSetSize) {
						rightArrowButton.setEnabled(false);
					}
					//patientMinNumText.setText("" + (start + inc));
					leftArrowButton.setEnabled(true);
					PerformVisualizationQuery(oAwtContainer, patientRefId, min,
							max - 1, bDisplayAllData);
				}
				// getDisplay().syncExec(new Runnable() {
				// public void run() {
				// if(returnedNumber >= 0) {
				// setIncrementNumber(returnedNumber);
				// MessageBox mBox = new MessageBox(getShell(),
				// SWT.ICON_INFORMATION
				// | SWT.OK);
				// mBox.setText("Please Note ...");
				// mBox.setMessage(/*"Can't return all the requested "+
				// requestIndex+" patients, */"Only "+returnedNumber+" patients
				// returned");
				// mBox.open();
				// }
				// }
				// });
			}
		});

		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				int w = getBounds().width;
				patientSetText.setBounds(70, 5, w - 357, 24);
				leftArrowButton.setBounds(w - 281, 5, 38, 24);
				patNum1.setBounds(w - 239, 9, 31, 24);
				patientMinNumText.setBounds(w - 204, 5, 45, 24);
				patNum2.setBounds(w - 149, 9, 57, 24);
				patientMaxNumText.setBounds(w - 92, 5, 45, 24);
				rightArrowButton.setBounds(w - 42, 5, 37, 24);
			}
		});

		verticalForm.setWeights(new int[] { 25, 3 });

		// Item 1: a Text Table
		TabItem item1 = new TabItem(tabFolder, SWT.NONE);
		item1.setText("Create model for Timeline");

		Composite oModelComposite = new Composite(tabFolder, SWT.NONE);
		item1.setControl(oModelComposite);

		GridLayout gridLayout = new GridLayout(2, false);

		// gridLayout.marginHeight = 0;
		// gridLayout.marginWidth = 0;
		// gridLayout.verticalSpacing = 0;
		// gridLayout.horizontalSpacing = 0;

		gridLayout.marginTop = 2;
		gridLayout.marginLeft = 0;
		gridLayout.marginBottom = 2;
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 1;
		oModelComposite.setLayout(gridLayout);

		oModelQueryComposite = new FramedComposite(oModelComposite,
				SWT.SHADOW_NONE);
		// GridLayout gLq = new GridLayout(25, false);
		oModelQueryComposite.setLayout(new GridLayout(25, false));
		oModelQueryComposite.setForeground(oTheParent.getDisplay()
				.getSystemColor(SWT.COLOR_BLACK));
		GridData oModelQueryButtonGridData = new GridData(
				GridData.FILL_HORIZONTAL);
		oModelQueryButtonGridData.grabExcessHorizontalSpace = false;
		oModelQueryButtonGridData.horizontalSpan = 2;
		oModelQueryButtonGridData.verticalAlignment = SWT.CENTER;
		oModelQueryButtonGridData.verticalIndent = 5;
		oModelQueryComposite.setLayoutData(oModelQueryButtonGridData);

		queryNamemrnlistText = new Label(oModelQueryComposite, SWT.NONE);
		queryNamemrnlistText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		queryNamemrnlistText.setText("Query Name: ");
		// queryNamemrnlistText.setBounds(5, 4, 600, 20);

		queryNamemrnlistText.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent arg0) {
				oModelQueryComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_YELLOW));
			}

			public void mouseExit(MouseEvent arg0) {
				oModelQueryComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
			}

			public void mouseHover(MouseEvent arg0) {
				oModelQueryComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_YELLOW));
			}

		});

		oModelQueryComposite.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent arg0) {

			}

		});

		oModelQueryComposite.addDragDetectListener(new DragDetectListener() {

			public void dragDetected(DragDetectEvent arg0) {

			}

		});

		Composite oGroupComposite = new Composite(oModelComposite, SWT.NONE);
		// GridLayout gLq = new GridLayout(25, false);
		oGroupComposite.setLayout(gridLayout);
		GridData oGroupGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		oGroupGridData.grabExcessHorizontalSpace = true;
		oGroupGridData.grabExcessVerticalSpace = true;
		oGroupGridData.verticalIndent = 1;
		oGroupGridData.verticalSpan = GridData.VERTICAL_ALIGN_FILL;
		// oGroupGridData.verticalAlignment = GridData.VERTICAL_ALIGN_FILL;
		oGroupComposite.setLayoutData(oGroupGridData);

		oModelGroupComposite = new FramedComposite(oGroupComposite,
				SWT.SHADOW_NONE);
		// GridLayout gLq = new GridLayout(25, false);
		oModelGroupComposite.setLayout(new GridLayout(25, false));
		GridData oModelGroupButtonGridData = new GridData(
				GridData.FILL_HORIZONTAL);
		oModelGroupButtonGridData.grabExcessHorizontalSpace = false;
		oModelGroupButtonGridData.horizontalSpan = 2;
		oModelGroupButtonGridData.verticalAlignment = SWT.CENTER;
		oModelGroupButtonGridData.verticalIndent = 5;
		oModelGroupComposite.setLayoutData(oModelGroupButtonGridData);

		// GridData gdAdd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gdDel1 = new GridData(GridData.FILL_HORIZONTAL);

		groupNameText = new Label(oModelGroupComposite, SWT.NONE);
		gdDel1.horizontalSpan = 4;
		groupNameText.setLayoutData(gdDel1);
		groupNameText.setText("Panel Name: ");
		// groupNameText.setBounds(80, 8, 400, 24);
		// groupNameText.setAlignment(SWT.CENTER);
		// createDragSource(queryNamemrnlistText);
		// createDropTarget(queryNamemrnlistText);

		groupNameText.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent arg0) {
				oModelGroupComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_YELLOW));
			}

			public void mouseExit(MouseEvent arg0) {
				oModelGroupComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
			}

			public void mouseHover(MouseEvent arg0) {
				oModelGroupComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_YELLOW));
			}

		});

		// put a table in tabItem1...
		table = new KTable(oGroupComposite, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);
		table.setFocus();
		table.setBackground(oTheParent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableGridData.verticalIndent = 5;
		table.setLayoutData(tableGridData);
		table.setRowSelectionMode(true);
		// table.setMultiSelectionMode(true);
		// table.setModel(new KTableForModel());
		table.setModel(new ConceptTableModel());
		// table.getModel().setColumnWidth(0, oModelComposite.getBounds().width
		// - 35);
		table.addCellSelectionListener(new KTableCellSelectionListener() {
			public void cellSelected(int col, int row, int statemask) {
				log.debug("Cell [" + col + ";" + row + "] selected.");
				// System.out.println("Cell [" + col + ";" + row +
				// "] selected.");
				table.selectedRow = row;
				table.selectedColumn = col;
			}

			public void fixedCellSelected(int col, int row, int statemask) {
				log.debug("Header [" + col + ";" + row + "] selected.");
				// System.out.println("Header [" + col + ";" + row +
				// "] selected.");
			}

		});

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		// create drag source
		/*
		 * DragSource source = new DragSource(queryNamemrnlistText,
		 * DND.DROP_COPY); source.setTransfer(types); source.addDragListener(new
		 * DragSourceAdapter() {
		 * 
		 * @Override public void dragSetData(DragSourceEvent event) {
		 * //DragSource ds = (DragSource) event.widget; StringWriter strWriter =
		 * new StringWriter(); DndType dndType = new DndType();
		 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory pdoFactory
		 * = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		 * PanelType panelType = new PanelType();
		 * 
		 * //get table rows and fill the panel object for(int i=0; i<3; i++) {
		 * ItemType itemType = new ItemType();
		 * panelType.getItem().add(itemType);
		 * 
		 * dndType.getAny().add(pdoFactory.createPanel(panelType));
		 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory dndFactory = new
		 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory(); try {
		 * ExplorerJAXBUtil
		 * .getJAXBUtil().marshaller(dndFactory.createPluginDragDrop(dndType),
		 * strWriter); } catch(JAXBUtilException e) { e.printStackTrace(); }
		 * 
		 * //put the data into the event event.data = strWriter.toString(); }
		 * 
		 * 
		 * });
		 */

		Composite oModelAddDelButtonComposite = new Composite(oModelComposite,
				SWT.NONE);
		GridLayout gL = new GridLayout(25, false);
		oModelAddDelButtonComposite.setLayout(gL);
		GridData oModelAddDelButtonGridData = new GridData(
				GridData.FILL_HORIZONTAL);// HORIZONTAL_ALIGN_FILL);// |
		// GridData.VERTICAL_ALIGN_FILL);
		oModelAddDelButtonGridData.grabExcessHorizontalSpace = false;
		oModelAddDelButtonGridData.horizontalSpan = 2;
		oModelAddDelButtonComposite.setLayoutData(oModelAddDelButtonGridData);

		// GridData gdAdd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gdDel = new GridData(GridData.FILL_HORIZONTAL);

		Button deleteArrowButton = new Button(oModelAddDelButtonComposite,
				SWT.PUSH);
		gdDel.horizontalSpan = 4;
		deleteArrowButton.setLayoutData(gdDel);
		deleteArrowButton.setText("Delete From List");
		deleteArrowButton.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent event) {
				curRowNumber = 0;
				ConceptTableModel m_Model = (ConceptTableModel) table
						.getModel();
				int[] selectedRow = table.getRowSelection();
				m_Model.fillDataFromTable(rowData);

				if ((selectedRow != null) && (selectedRow.length > 0)) {
					String conceptName = (String) m_Model.getContentAt(1,
							selectedRow[0]);
					if (conceptName.equals("Encounter Range Line")) {
					} else if (conceptName.equals("Vital Status Line")) {
					}

					int rowNumber = new Integer((String) (m_Model.getContentAt(
							0, selectedRow[0]))).intValue();
					int rid = selectedRow[0];
					ArrayList list = (ArrayList) rowData.get(rowNumber - 1);
					for (int i = 0; i < list.size(); i++) {
						ConceptTableRow tr = (ConceptTableRow) list.get(i);
						if (tr.rowId == rid) {
							list.remove(i);
							break;
						}
					}
					if (list.size() == 0) {
						rowData.remove(rowNumber - 1);
					}
					curRowNumber = rowData.size();
					resetRowNumber();
					// m_Model.deleteRow(selectedRow[0]);
					((ConceptTableModel) table.getModel()).deleteAllRows();
					((ConceptTableModel) table.getModel())
							.populateTable(rowData);
					/*
					 * int newRow = 0; for(int i=0; i<rowData.size(); i++) {
					 * ArrayList alist = (ArrayList) rowData.get(i); for(int
					 * j=0; j<alist.size(); j++) { TableRow r = (TableRow)
					 * alist.get(j); newRow++; r.rowId = newRow;
					 * table.getModel().setContentAt(0, newRow, new
					 * Integer(r.rowNumber).toString());
					 * table.getModel().setContentAt(1, newRow, r.conceptName);
					 * table.getModel().setContentAt(2, newRow, r.valueType);
					 * table.getModel().setContentAt(3, newRow, r.valueText);
					 * table.getModel().setContentAt(4, newRow, r.height);
					 * table.getModel().setContentAt(5, newRow, r.color);
					 * table.getModel().setContentAt(6, newRow, r.conceptXml); }
					 * }
					 */
					table.redraw();
					queryNamemrnlistText.setText("Query Name: ");
					groupNameText.setText("Panel Name: ");
				}
			}
		});

		Button deleteAllButton = new Button(oModelAddDelButtonComposite,
				SWT.PUSH);
		gdDel = new GridData(GridData.FILL_HORIZONTAL);
		gdDel.horizontalSpan = 4;
		deleteAllButton.setLayoutData(gdDel);
		deleteAllButton.setText("Delete All ");
		deleteAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ConceptTableModel m_Model = (ConceptTableModel) table
						.getModel();
				m_Model.deleteAllRows();
				curRowNumber = 0;
				rowData.clear();
				queryNamemrnlistText.setText("Query Name: ");
				groupNameText.setText("Panel Name: ");
				table.redraw();
			}
		});

		Button putInOrderButton = new Button(oModelAddDelButtonComposite,
				SWT.PUSH);
		gdDel = new GridData(GridData.FILL_HORIZONTAL);
		gdDel.horizontalSpan = 4;
		putInOrderButton.setLayoutData(gdDel);
		putInOrderButton.setText("Put In Order ");
		putInOrderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				ConceptTableModel m_Model = (ConceptTableModel) table
						.getModel();
				curRowNumber = 0;
				m_Model.fillDataFromTable(rowData);

				Collections.sort(rowData, new Comparator<Object>() {
					@SuppressWarnings("unchecked")
					public int compare(Object o1, Object o2) {
						int i1 = ((ConceptTableRow) ((ArrayList) o1).get(0)).rowNumber;
						int i2 = ((ConceptTableRow) ((ArrayList) o2).get(0)).rowNumber;
						if (i1 > i2) {
							return 1;
						} else if (i1 < i2) {
							return -1;
						} else {
							return 0;
						}
					}
				});
				m_Model.deleteAllRows();
				m_Model.populateTable(rowData);
				table.redraw();
			}
		});

		Button upArrowButton = new Button(oModelAddDelButtonComposite, SWT.PUSH);
		upArrowButton.setText("Move Up");
		upArrowButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				ConceptTableModel m_Model = (ConceptTableModel) table
						.getModel();
				int[] selectedRow = table.getRowSelection();
				curRowNumber = 0;
				// KTableI2B2Model m_Model = (KTableI2B2Model) table.getModel();
				// int[] selectedRow = table.getRowSelection();
				m_Model.fillDataFromTable(rowData);
				int index = new Integer((String) (m_Model.getContentAt(0,
						selectedRow[0]))).intValue() - 1;
				if (index < 1) {
					return;
				}
				if ((selectedRow != null) && (selectedRow.length > 0)) {
					// m_Model.moveRow(selectedRow[0], selectedRow[0] -1);
					ArrayList<ConceptTableRow> list = rowData.get(index);
					rowData.remove(index);
					rowData.add(index - 1, list);
					resetRowNumber();
					m_Model.populateTable(rowData);
				}
				table.setSelection(0, selectedRow[0] - 1, true);
				table.redraw();
			}
		});

		Button downArrowButton = new Button(oModelAddDelButtonComposite,
				SWT.PUSH);
		downArrowButton.setText("Move Down");
		downArrowButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				ConceptTableModel m_Model = (ConceptTableModel) table
						.getModel();
				int[] selectedRow = table.getRowSelection();
				curRowNumber = 0;
				// KTableI2B2Model m_Model = (KTableI2B2Model) table.getModel();
				// int[] selectedRow = table.getRowSelection();
				m_Model.fillDataFromTable(rowData);
				int index = new Integer((String) (m_Model.getContentAt(0,
						selectedRow[0]))).intValue() - 1;
				if (index == (rowData.size() - 1)) {
					return;
				}
				if ((selectedRow != null) && (selectedRow.length > 0)) {
					// m_Model.moveRow(selectedRow[0], selectedRow[0] -1);
					ArrayList<ConceptTableRow> list = rowData.get(index);
					rowData.remove(index);
					rowData.add(index + 1, list);
					resetRowNumber();
					m_Model.populateTable(rowData);
				}
				table.setSelection(0, selectedRow[0] + 1, true);
				table.redraw();
			}
		});

		Composite oModelCheckButtonComposite = new Composite(oModelComposite,
				SWT.NONE);
		GridLayout gL1 = new GridLayout(20, true);
		oModelCheckButtonComposite.setLayout(gL1);
		GridData oModelCheckButtonGridData = new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		oModelCheckButtonGridData.grabExcessHorizontalSpace = true;
		oModelCheckButtonGridData.horizontalSpan = 2;
		oModelCheckButtonComposite.setLayoutData(oModelCheckButtonGridData);

		Button displayOrNotButton = new Button(oModelCheckButtonComposite,
				SWT.CHECK);
		displayOrNotButton.setText("Display concepts with no data");
		displayOrNotButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// do something here- return null
				bDisplayAllData = !bDisplayAllData;
			}
		});

		Button displayDemographicsOrNotButton = new Button(
				oModelCheckButtonComposite, SWT.CHECK);
		displayDemographicsOrNotButton.setText("Display patient demographics");
		if ((System.getProperty("applicationName") != null)
				&& System.getProperty("applicationName").equals("BIRN")) {
			displayDemographicsOrNotButton.setSelection(false);
			displayDemographicsOrNotButton.setEnabled(false);
			bDisplayDemographics = false;
		} else if ((System.getProperty("applicationName") == null)
				|| System.getProperty("applicationName").equals("i2b2")) {
			displayDemographicsOrNotButton.setSelection(true);
		}
		displayDemographicsOrNotButton
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						bDisplayDemographics = !bDisplayDemographics;
					}
				});

		if (UserInfoBean.getInstance().getCellDataUrl("identity") != null) {
			Composite oPatientSetComposite = new Composite(oModelComposite,
					SWT.NONE);
			GridData patientSetData = new GridData(
					GridData.HORIZONTAL_ALIGN_FILL);
			patientSetData.grabExcessHorizontalSpace = true;
			oPatientSetComposite.setLayoutData(patientSetData);
			oPatientSetComposite.setLayout(null);

			Label mrnlabel = new Label(oPatientSetComposite, SWT.NONE);
			mrnlabel.setText("MRN site:");
			mrnlabel.setBounds(5, 9, 50, 20);

			final Combo siteCombo = new Combo(oPatientSetComposite, SWT.NULL);
			siteCombo.add("BWH");
			siteCombo.add("MGH");
			siteCombo.setBounds(57, 5, 60, 20);
			siteCombo.select(1);

			Label mrnNumber = new Label(oPatientSetComposite, SWT.NONE);
			mrnNumber.setText("number:");
			mrnNumber.setBounds(121, 9, 40, 20);

			mrnlistText = new Text(oPatientSetComposite, SWT.SINGLE
					| SWT.BORDER);
			mrnlistText.setBounds(164, 5, 150, 20);
			mrnlistText.setText("");

			Button runButton = new Button(oPatientSetComposite, SWT.PUSH);
			runButton.setText("Search By MRN");
			runButton.setBounds(315, 5, 85, 23);
			runButton.addSelectionListener(new SelectionAdapter() {
				@SuppressWarnings("unchecked")
				public void widgetSelected(SelectionEvent event) {
					String mrns = mrnlistText.getText();
					if (mrns.equals("")) {
						return;
					}

					String[] mrnArray = mrns.split(",");
					int[] idlist = new int[mrnArray.length];
					String username = UserInfoBean.getInstance().getUserName();
					String password = UserInfoBean.getInstance()
							.getUserPassword();
					// log.debug("User name: "+username+" password: "+password);
					String site = siteCombo.getText();
					for (int i = 0; i < mrnArray.length; i++) {
						// String[] tmps = new String[2];
						String tmp = mrnArray[i].replaceAll(" ", "");
						// tmps = tmp.split(":");

						String queryStr = "<?xml version=\"1.0\" standalone=\"yes\"?>\n"
								+ "<search_by_local>"
								+ "<match_id site=\""
								+ site.toUpperCase()/* EMPI */
								+ "\">" + tmp/* 100016900 */
								+ "</match_id>\n" + "</search_by_local>";

						//String resultStr = QueryClient.query(queryStr,
							//	username, password);
						//log.debug(queryStr);
						//log.debug(resultStr);

						/*SAXBuilder parser = new SAXBuilder();
						String masterID = null;
						java.io.StringReader xmlStringReader = new java.io.StringReader(
								resultStr);
						try {
							org.jdom.Document tableDoc = parser
									.build(xmlStringReader);
							org.jdom.Element tableXml = tableDoc
									.getRootElement();
							Element responseXml = (Element) tableXml
									.getChild("person_list");
							// Element mrnXml = (Element)
							// responseXml.getChild("MRN");
							java.util.List listChildren = responseXml
									.getChildren();
							if (listChildren.isEmpty()) {
								MessageBox mBox = new MessageBox(table
										.getShell(), SWT.ICON_INFORMATION
										| SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("No master id found");
								mBox.open();
								return;
							}

							Element masterXml = (Element) responseXml
									.getChild("master_record");
							masterID = masterXml.getAttributeValue("id");
							log.debug("Patient id: " + masterID);
							idlist[i] = new Integer(masterID).intValue();
							log.debug("MRN: " + site + "-" + tmp);
						} catch (Exception e1) {
							e1.printStackTrace();
						}*/
					}

					if (tabFolder.getSelectionIndex() == 1) {
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								DestroyMiniVisualization(oAwtContainer);
							}
						});
					} else if (tabFolder.getSelectionIndex() == 0) {
						oTheParent.getDisplay().syncExec(new Runnable() {
							public void run() {
								tabFolder.setSelection(1);
							}
						});
					}

					PerformVisualizationQuery(oAwtContainer, idlist,
							bDisplayAllData);
				}
			});
		}

		DropTarget targetLable = new DropTarget(oModelQueryComposite,
				DND.DROP_COPY);
		targetLable.setTransfer(types);
		targetLable.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragLeave(DropTargetEvent event) {
				super.dragLeave(event);
				oModelQueryComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
			}

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				oModelQueryComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_YELLOW));
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				try {
					SAXBuilder parser = new SAXBuilder();
					String xmlContent = (String) event.data;
					java.io.StringReader xmlStringReader = new java.io.StringReader(
							xmlContent);
					org.jdom.Document tableDoc = parser.build(xmlStringReader);
					org.jdom.Element tableXml = tableDoc
							.getRootElement()
							.getChild(
									"concepts",
									Namespace
											.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));

					if (tableXml != null) {
						MessageBox mBox = new MessageBox(table.getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("You can not drop this item here.");
						mBox.open();
						event.detail = DND.DROP_NONE;
						return;
					}

					boolean isQuery = false;
					if (tableXml == null) {
						tableXml = tableDoc
								.getRootElement()
								.getChild(
										"query_master",
										Namespace
												.getNamespace("http://www.i2b2.org/xsd/cell/crc/psm/1.1/"));
					}

					if (tableXml != null) {
						isQuery = true;
					} else {

						MessageBox mBox = new MessageBox(table.getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("You can not drop this item here.");
						mBox.open();
						event.detail = DND.DROP_NONE;
						return;
					}

					if (isQuery) {
						ArrayList<QueryModel> nodeXmls = new ArrayList<QueryModel>();
						try {
							JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
							QueryMasterData ndata = new QueryMasterData();
							ndata.name(tableXml.getChildText("name"));
							queryNamemrnlistText.setText("Query Name: "
									+ ndata.name());
							groupNameText
									.setText("Panel Name: All items of Query "
											+ ndata.name());
							ndata.xmlContent(null);
							ndata.id(tableXml
									.getChildTextTrim("query_master_id"));
							ndata.userId(tableXml.getChildTextTrim("user_id"));

							String xmlcontent = null;
							String xmlrequest = null;

							xmlrequest = ndata.writeDefinitionQueryXML();
							lastRequestMessage(xmlrequest);

							if (System.getProperty("webServiceMethod").equals(
									"SOAP")) {
								xmlcontent = PatientMappingQueryClient
										.sendPDQQueryRequestSOAP(xmlrequest);
							} else {
								xmlcontent = PatientMappingQueryClient
										.sendPDQQueryRequestREST(xmlrequest);
							}
							lastResponseMessage(xmlcontent);

							if (xmlcontent == null) {

								return;
							} else {
								log.debug("Query content response: "
										+ xmlcontent);
								ndata.xmlContent(xmlcontent);
							}

							JAXBElement jaxbElement = jaxbUtil
									.unMashallFromString(ndata.xmlContent());
							ResponseMessageType messageType = (ResponseMessageType) jaxbElement
									.getValue();

							BodyType bt = messageType.getMessageBody();
							MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(),
											MasterResponseType.class);
							RequestXmlType requestXmlType = masterResponseType
									.getQueryMaster().get(0).getRequestXml();

							org.w3c.dom.Element element = (org.w3c.dom.Element) requestXmlType
									.getContent().get(0);
							if (element != null) {
								log.debug("query definition not null");
							} else {
								log.error("query definition is null");
							}

							String domString = edu.harvard.i2b2.common.util.xml.XMLUtil
									.convertDOMElementToString(element);
							log.debug("string output" + domString);

							JAXBContext jc1 = JAXBContext
									.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
							Unmarshaller unMarshaller = jc1
									.createUnmarshaller();
							JAXBElement queryDefinitionJaxbElement = (JAXBElement) unMarshaller
									.unmarshal(new StringReader(domString));

							QueryDefinitionType queryDefinitionType = (QueryDefinitionType) queryDefinitionJaxbElement
									.getValue();
							int numOfPanels = queryDefinitionType.getPanel()
									.size();
							int conceptCount = 0;
							for (int i = 0; i < numOfPanels; i++) {
								PanelType panelType = queryDefinitionType
										.getPanel().get(i);

								for (int j = 0; j < panelType.getItem().size(); j++) {
									ItemType itemType = panelType.getItem()
											.get(j);
									
									///////////////////////////////////////////////
									QueryModel nodedata = null;
									ConstrainByModifier md = itemType.getConstrainByModifier();
									if(md != null) {
										nodedata = new ModifierData();
										nodedata.isModifier(true);
										((ModifierData)nodedata).modifier_key(md.getModifierKey());
										((ModifierData)nodedata).applied_path(md.getAppliedPath());
										((ModifierData)nodedata).modifier_name(md.getModifierName());
										((ModifierData)nodedata).setModifierValueConstraint(md.getConstrainByValue());
									}
									else {
										nodedata = new QueryModel();
									}

									nodedata.name(itemType.getItemName());
									nodedata.visualAttribute("FA");
									nodedata.tooltip(itemType.getTooltip());
									nodedata.fullname(itemType.getItemKey());
									nodedata.hlevel(new Integer(itemType
											.getHlevel()).toString());

									if (itemType.getItemShape() != null) {
										nodedata.tableRow().height = new String(
												itemType.getItemShape());
										nodedata.tableRow().color = ((ConceptTableModel) table
												.getModel()).getColor(itemType
												.getItemColor());
										// nodedata.tableRow().rowNumber =
										// Integer
										// .parseInt(itemType
										// .getItemRowNumber());
										nodedata.tableRow().rowNumber = conceptCount + 1;
									} else {
										nodedata.tableRow().height = "Medium";
										nodedata.tableRow().color = new RGB(0,
												0, 128);
										nodedata.tableRow().rowNumber = conceptCount + 1;
									}

									nodedata.constrainByValue(itemType
											.getConstrainByValue());
									if (itemType.getConstrainByValue().size() > 0) {
										nodedata.setValueConstrains(itemType
												.getConstrainByValue());

										if (itemType.getConstrainByValue()
												.size() > 0) {
											nodedata
													.setValueConstrains(itemType
															.getConstrainByValue());
											if (nodedata.valueModel()
													.hasEnumValue()) {
												if (nodedata.valueModel()
														.useTextValue()) {
													ArrayList<String> results = new ArrayList<String>();
													results
															.toArray(nodedata
																	.valueModel()
																	.value()
																	.split(","));
													nodedata.valueModel().selectedValues = results;
												}
											}
										}
									}

									// Handle Constrain By Dates
									for (int u = 0; u < itemType
											.getConstrainByDate().size(); u++) {
										nodedata.setTimeConstrain(itemType
												.getConstrainByDate().get(u)
												.getDateFrom(), itemType
												.getConstrainByDate().get(u)
												.getDateTo());
									}

									nodedata.updateNodeMetaDataXML();
									//if (status.equalsIgnoreCase("error")) {
										// MessageBox mBox = new
										// MessageBox(table
										// .getShell(),
										// SWT.ICON_INFORMATION | SWT.OK);
										// mBox.setText("Please Note ...");
										// mBox.setMessage("Response delivered from the remote server could not be understood,\n"
										// +
										// "you may wish to retry your last action.");
										// mBox.open();
										// event.detail = DND.DROP_NONE;

										//continue;
									//} else {
										nodeXmls.add(nodedata);
										conceptCount++;
									//}
								}
							}
							if (nodeXmls.size() == 0) {
								MessageBox mBox = new MessageBox(table
										.getShell(), SWT.ICON_INFORMATION
										| SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("No valid concept was found.");
								mBox.open();
								event.detail = DND.DROP_NONE;
								return;
							}
							populateTable(nodeXmls);

							// get query instance
							String xmlRequest = ndata.writeContentQueryXML();
							lastRequestMessage(xmlRequest);
							String xmlResponse = PatientMappingQueryClient
									.sendPDQQueryRequestREST(xmlRequest);
							lastResponseMessage(xmlResponse);

							jaxbElement = jaxbUtil
									.unMashallFromString(xmlResponse);
							messageType = (ResponseMessageType) jaxbElement
									.getValue();
							bt = messageType.getMessageBody();
							InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(),
											InstanceResponseType.class);

							QueryInstanceData instanceData = null;
							XMLGregorianCalendar startDate = null;
							for (QueryInstanceType queryInstanceType : instanceResponseType
									.getQueryInstance()) {
								QueryInstanceData runData = new QueryInstanceData();

								runData.visualAttribute("FA");
								runData.tooltip("The results of the query run");
								runData.id(new Integer(queryInstanceType
										.getQueryInstanceId()).toString());
								XMLGregorianCalendar cldr = queryInstanceType
										.getStartDate();
								runData.name("Results of " + "["
										+ cldr.getMonth() + "-" + cldr.getDay()
										+ "-" + cldr.getYear() + " "
										+ cldr.getHour() + ":"
										+ cldr.getMinute() + ":"
										+ cldr.getSecond() + "]");

								if (instanceData == null) {
									startDate = cldr;
									instanceData = runData;
								} else {
									if (cldr.toGregorianCalendar().compareTo(
											startDate.toGregorianCalendar()) > 0) {
										startDate = cldr;
										instanceData = runData;
									}
								}
							}
							// get patient set
							if (instanceData == null) {
								event.detail = DND.DROP_NONE;
								return;
							}
							log.debug("Got query instance: "
									+ instanceData.name());

							xmlRequest = instanceData.writeContentQueryXML();
							lastRequestMessage(xmlRequest);

							xmlResponse = PatientMappingQueryClient
									.sendPDQQueryRequestREST(xmlRequest);
							lastResponseMessage(xmlResponse);

							jaxbElement = jaxbUtil
									.unMashallFromString(xmlResponse);
							messageType = (ResponseMessageType) jaxbElement
									.getValue();
							bt = messageType.getMessageBody();
							ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(),
											ResultResponseType.class);

							for (QueryResultInstanceType queryResultInstanceType : resultResponseType
									.getQueryResultInstance()) {
								if (!(queryResultInstanceType
										.getQueryResultType().getName()
										.equalsIgnoreCase("PATIENTSET"))) {
									continue;
								}

								String status = queryResultInstanceType
										.getQueryStatusType().getName();

								if (status.equalsIgnoreCase("FINISHED")) {

									String setId = new Integer(
											queryResultInstanceType
													.getResultInstanceId())
											.toString();
									String setSize = new Integer(
											queryResultInstanceType
													.getSetSize()).toString();
									String description = queryResultInstanceType
											.getDescription();
									if (description != null) {
										patientSetText.setText(description);
									} else {
										patientSetText.setText("Patient Set: "
												+ setSize + " patients");
									}
									patientRefId = new String(setId);
									patientMinNumText.setText("1");
									leftArrowButton.setEnabled(false);

									int maxPatientNum = new Integer(
											patientMaxNumText.getText())
											.intValue();
									patientSetSize = queryResultInstanceType
											.getSetSize();
									if (patientSetSize > maxPatientNum) {
										rightArrowButton.setEnabled(true);
										patientMaxNumText.setText("10");
									} else {
										rightArrowButton.setEnabled(false);
										if (patientSetSize > 0) {
											patientMaxNumText.setText(setSize);
										}
									}

									log.debug("Dropped set of: " + setSize
											+ " patients"/* strs[0] */
											+ " with refId: " + setId/*
																	 * strs[ 1 ]
																	 */);
								} else {
									// message
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					} /*
					 * else { List conceptChildren = tableXml.getChildren();
					 * parseDropConcepts(conceptChildren, event);
					 * table.redraw(); }
					 */

					event.detail = DND.DROP_NONE;
				} catch (JDOMException e) {
					System.err.println(e.getMessage());
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("You can not drop this item here.");
					mBox.open();
					event.detail = DND.DROP_NONE;
					e.printStackTrace();
					return;
				} catch (Exception e) {
					System.err.println(e.getMessage());
					event.detail = DND.DROP_NONE;
					e.printStackTrace();
					return;
				}
			}
		});

		// create drag source
		DragSource source1 = new DragSource(groupNameText, DND.DROP_COPY);
		source1.setTransfer(types);
		source1.addDragListener(new DragSourceAdapter() {

			@Override
			public void dragSetData(DragSourceEvent event) {

				ConceptTableModel i2b2Model = (ConceptTableModel) table
						.getModel();
				i2b2Model.fillDataFromTable(rowData);
				if (rowData.size() == 0) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("The set up table is empty.");
					mBox.open();
					return;
				}

				StringWriter strWriter = new StringWriter();
				DndType dndType = new DndType();
				edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory pdoFactory = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				PanelType panelType = new PanelType();
				panelType.setInvert(0);
				PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
				totalOccurrences.setValue(1);
				panelType.setTotalItemOccurrences(totalOccurrences);
				panelType.setPanelNumber(0 + 1);
				// panelType.setName(panelData.getItems().get(0).name() + "_"
				// + generateMessageId().substring(0, 4));
				panelType.setName("Panel-1");

				// TO DO: get table rows and fill the panel object
				for (int i = 1; i < i2b2Model.getRowCount(); i++) {
					QueryModel node = (QueryModel) i2b2Model.getContentAt(7, i);
					ItemType itemType = new ItemType();
					itemType.setItemKey(node.fullname());
					itemType.setItemName(node.name());
					itemType.setTooltip(node.tooltip());
					itemType.setHlevel(Integer.parseInt(node.hlevel()));
					itemType.setClazz("ENC");
					itemType.setItemIcon(node.visualAttribute().trim());
					itemType
							.setItemColor(i2b2Model
									.getColorString((RGB) i2b2Model
											.getContentAt(5, i)));
					itemType.setItemRowNumber((String) i2b2Model.getContentAt(
							0, i));
					itemType
							.setItemShape((String) i2b2Model.getContentAt(4, i));

					itemType.getConstrainByValue().add(
							node.valueModel().writeValueConstraint());
					itemType.getConstrainByDate()
							.add(node.writeTimeConstrain());

					panelType.getItem().add(itemType);
				}

				dndType.getAny().add(pdoFactory.createPanel(panelType));
				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory dndFactory = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				try {
					PatientMappingJAXBUtil.getJAXBUtil()
							.marshaller(
									dndFactory.createPluginDragDrop(dndType),
									strWriter);
				} catch (JAXBUtilException e) {
					e.printStackTrace();
				}

				// put the data into the event
				event.data = strWriter.toString();
			}

		});

		DropTarget nameTarget = new DropTarget(groupNameText, DND.DROP_COPY);
		nameTarget.setTransfer(types);
		nameTarget.addDropListener(new DropTargetAdapter() {

			@Override
			public void dragLeave(DropTargetEvent event) {
				super.dragLeave(event);
				oModelGroupComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				ArrayList<QueryModel> nodeXmls = new ArrayList<QueryModel>();
				try {
					SAXBuilder parser = new SAXBuilder();
					String xmlContent = (String) event.data;
					java.io.StringReader xmlStringReader = new java.io.StringReader(
							xmlContent);
					org.jdom.Document panelDoc = parser.build(xmlStringReader);
					org.jdom.Element panelXml = panelDoc
							.getRootElement()
							.getChild(
									"panel",
									Namespace
											.getNamespace("http://www.i2b2.org/xsd/cell/crc/psm/querydefinition/1.1/"));

					if (panelXml == null) {
						MessageBox mBox = new MessageBox(table.getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("You can not drop this item here.");
						mBox.open();
						event.detail = DND.DROP_NONE;
						return;
					}

					else {

						String domString = (new XMLOutputter())
								.outputString(panelXml);
						JAXBContext jc1 = JAXBContext
								.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
						Unmarshaller unMarshaller = jc1.createUnmarshaller();
						JAXBElement panelJaxbElement = (JAXBElement) unMarshaller
								.unmarshal(new StringReader(domString));

						PanelType panelType = (PanelType) panelJaxbElement
								.getValue();
						String panelname = panelType.getName();
						groupNameText.setText("Panel Name: " + panelname);
						queryNamemrnlistText.setText("Query Name: ");
						for (int j = 0; j < panelType.getItem().size(); j++) {
							ItemType itemType = panelType.getItem().get(j);
							QueryModel nodedata = new QueryModel();

							nodedata.name(itemType.getItemName());
							nodedata.visualAttribute("FA");
							nodedata.tooltip(itemType.getTooltip());
							nodedata.fullname(itemType.getItemKey());
							nodedata.hlevel(new Integer(itemType.getHlevel())
									.toString());

							// / need to handle query tool generated panels
							if (itemType.getItemShape() != null) {
								nodedata.tableRow().height = new String(
										itemType.getItemShape());
								nodedata.tableRow().color = ((ConceptTableModel) table
										.getModel()).getColor(itemType
										.getItemColor());
								nodedata.tableRow().rowNumber = Integer
										.parseInt(itemType.getItemRowNumber());
							} else {
								nodedata.tableRow().height = "Medium";
								nodedata.tableRow().color = new RGB(0, 0, 128);
								nodedata.tableRow().rowNumber = j + 1;
							}

							nodedata.constrainByValue(itemType
									.getConstrainByValue());
							if (itemType.getConstrainByValue().size() > 0) {
								nodedata.setValueConstrains(itemType
										.getConstrainByValue());

								if (itemType.getConstrainByValue().size() > 0) {
									nodedata.setValueConstrains(itemType
											.getConstrainByValue());
									if (nodedata.valueModel().hasEnumValue()) {
										if (nodedata.valueModel()
												.useTextValue()) {
											ArrayList<String> results = new ArrayList<String>();
											results.toArray(nodedata
													.valueModel().value()
													.split(","));
											nodedata.valueModel().selectedValues = results;
										}
									}
								}
							}

							// Handle Constrain By Dates
							for (int u = 0; u < itemType.getConstrainByDate()
									.size(); u++) {
								nodedata.setTimeConstrain(itemType
										.getConstrainByDate().get(u)
										.getDateFrom(), itemType
										.getConstrainByDate().get(u)
										.getDateTo());
							}

							String status = nodedata.setXmlContent();
							if (status.equalsIgnoreCase("error")) {
								MessageBox mBox = new MessageBox(table
										.getShell(), SWT.ICON_INFORMATION
										| SWT.OK);
								mBox.setText("Please Note ...");
								mBox
										.setMessage("Response delivered from the remote server could not be understood,\n"
												+ "you may wish to retry your last action.");
								mBox.open();
								event.detail = DND.DROP_NONE;

								return;
							}
							nodeXmls.add(nodedata);
						}
						// event.detail = DND.DROP_NONE;
					}
					populateTable(nodeXmls);

				}

				catch (JDOMException e) {
					System.err.println(e.getMessage());
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("You can not drop this item here.");
					mBox.open();
					event.detail = DND.DROP_NONE;
					e.printStackTrace();
					return;
				} catch (Exception e) {
					System.err.println(e.getMessage());
					event.detail = DND.DROP_NONE;
					e.printStackTrace();
					return;
				}
			}

			public void dragEnter(DropTargetEvent event) {
				TextTransfer textTransfer = TextTransfer.getInstance();
				if (textTransfer.isSupportedType(event.currentDataType)) {

					event.detail = DND.DROP_COPY;
				}

				oModelGroupComposite.setForeground(oTheParent.getDisplay()
						.getSystemColor(SWT.COLOR_YELLOW));
			}
		});

		DropTarget target = new DropTarget(table, DND.DROP_COPY);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			@SuppressWarnings("unchecked")
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				try {
					SAXBuilder parser = new SAXBuilder();
					String xmlContent = (String) event.data;
					java.io.StringReader xmlStringReader = new java.io.StringReader(
							xmlContent);
					org.jdom.Document tableDoc = parser.build(xmlStringReader);
					org.jdom.Element tableXml = tableDoc
							.getRootElement()
							.getChild(
									"concepts",
									Namespace
											.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));

					boolean isQuery = false;
					if (tableXml == null) {
						MessageBox mBox = new MessageBox(table.getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("You can not drop this item here.");
						mBox.open();
						event.detail = DND.DROP_NONE;
						return;
					}

					if (isQuery) {
						ArrayList<QueryModel> nodeXmls = new ArrayList<QueryModel>();
						try {
							JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
							QueryMasterData ndata = new QueryMasterData();
							ndata.name(tableXml.getChildText("name"));
							queryNamemrnlistText.setText(ndata.name());
							ndata.xmlContent(null);
							ndata.id(tableXml
									.getChildTextTrim("query_master_id"));
							ndata.userId(tableXml.getChildTextTrim("user_id"));

							String xmlcontent = null;
							String xmlrequest = null;

							xmlrequest = ndata.writeDefinitionQueryXML();
							lastRequestMessage(xmlrequest);

							if (System.getProperty("webServiceMethod").equals(
									"SOAP")) {
								xmlcontent = PatientMappingQueryClient
										.sendPDQQueryRequestSOAP(xmlrequest);
							} else {
								xmlcontent = PatientMappingQueryClient
										.sendPDQQueryRequestREST(xmlrequest);
							}
							lastResponseMessage(xmlcontent);

							if (xmlcontent == null) {

								return;
							} else {
								log.debug("Query content response: "
										+ xmlcontent);
								ndata.xmlContent(xmlcontent);
							}

							JAXBElement jaxbElement = jaxbUtil
									.unMashallFromString(ndata.xmlContent());
							ResponseMessageType messageType = (ResponseMessageType) jaxbElement
									.getValue();

							BodyType bt = messageType.getMessageBody();
							MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(),
											MasterResponseType.class);
							RequestXmlType requestXmlType = masterResponseType
									.getQueryMaster().get(0).getRequestXml();

							org.w3c.dom.Element element = (org.w3c.dom.Element) requestXmlType
									.getContent().get(0);
							if (element != null) {
								log.debug("query definition not null");
							} else {
								log.error("query definition is null");
							}

							String domString = edu.harvard.i2b2.common.util.xml.XMLUtil
									.convertDOMElementToString(element);
							log.debug("string output" + domString);

							JAXBContext jc1 = JAXBContext
									.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
							Unmarshaller unMarshaller = jc1
									.createUnmarshaller();
							JAXBElement queryDefinitionJaxbElement = (JAXBElement) unMarshaller
									.unmarshal(new StringReader(domString));

							QueryDefinitionType queryDefinitionType = (QueryDefinitionType) queryDefinitionJaxbElement
									.getValue();
							int numOfPanels = queryDefinitionType.getPanel()
									.size();
							for (int i = 0; i < numOfPanels; i++) {
								PanelType panelType = queryDefinitionType
										.getPanel().get(i);

								for (int j = 0; j < panelType.getItem().size(); j++) {
									ItemType itemType = panelType.getItem()
											.get(j);
									QueryModel nodedata = new QueryModel();

									nodedata.name(itemType.getItemName());
									nodedata.visualAttribute("FA");
									nodedata.tooltip(itemType.getTooltip());
									nodedata.fullname(itemType.getItemKey());
									nodedata.hlevel(new Integer(itemType
											.getHlevel()).toString());

									String status = nodedata.setXmlContent();
									if (status.equalsIgnoreCase("error")) {
										MessageBox mBox = new MessageBox(table
												.getShell(),
												SWT.ICON_INFORMATION | SWT.OK);
										mBox.setText("Please Note ...");
										mBox
												.setMessage("Response delivered from the remote server could not be understood,\n"
														+ "you may wish to retry your last action.");
										mBox.open();
										event.detail = DND.DROP_NONE;

										return;
									}
									nodeXmls.add(nodedata);
								}
							}
							populateTable(nodeXmls);

							// get query instance
							String xmlRequest = ndata.writeContentQueryXML();
							lastRequestMessage(xmlRequest);
							// log.debug(xmlRequest);
							String xmlResponse = PatientMappingQueryClient
									.sendPDQQueryRequestREST(xmlRequest);
							lastResponseMessage(xmlResponse);

							jaxbElement = jaxbUtil
									.unMashallFromString(xmlResponse);
							messageType = (ResponseMessageType) jaxbElement
									.getValue();
							bt = messageType.getMessageBody();
							InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(),
											InstanceResponseType.class);

							QueryInstanceData instanceData = null;
							XMLGregorianCalendar startDate = null;
							for (QueryInstanceType queryInstanceType : instanceResponseType
									.getQueryInstance()) {
								QueryInstanceData runData = new QueryInstanceData();

								runData.visualAttribute("FA");
								runData.tooltip("The results of the query run");
								runData.id(new Integer(queryInstanceType
										.getQueryInstanceId()).toString());
								XMLGregorianCalendar cldr = queryInstanceType
										.getStartDate();
								runData.name("Results of " + "["
										+ cldr.getMonth() + "-" + cldr.getDay()
										+ "-" + cldr.getYear() + " "
										+ cldr.getHour() + ":"
										+ cldr.getMinute() + ":"
										+ cldr.getSecond() + "]");

								if (instanceData == null) {
									startDate = cldr;
									instanceData = runData;
								} else {
									if (cldr.toGregorianCalendar().compareTo(
											startDate.toGregorianCalendar()) > 0) {
										startDate = cldr;
										instanceData = runData;
									}
								}
							}
							// get patient set
							if (instanceData == null) {
								event.detail = DND.DROP_NONE;
								return;
							}
							log.debug("Got query instance: "
									+ instanceData.name());

							xmlRequest = instanceData.writeContentQueryXML();
							lastRequestMessage(xmlRequest);

							xmlResponse = PatientMappingQueryClient
									.sendPDQQueryRequestREST(xmlRequest);
							lastResponseMessage(xmlResponse);

							jaxbElement = jaxbUtil
									.unMashallFromString(xmlResponse);
							messageType = (ResponseMessageType) jaxbElement
									.getValue();
							bt = messageType.getMessageBody();
							ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(),
											ResultResponseType.class);

							for (QueryResultInstanceType queryResultInstanceType : resultResponseType
									.getQueryResultInstance()) {
								if (!(queryResultInstanceType
										.getQueryResultType().getName()
										.equalsIgnoreCase("PATIENTSET"))) {
									continue;
								}

								String status = queryResultInstanceType
										.getQueryStatusType().getName();

								if (status.equalsIgnoreCase("FINISHED")) {
									// resultData.name("Patient Set - "+resultData
									// .patientCount()+" Patients");
									// QueryResultData resultData = new
									// QueryResultData();
									String setId = new Integer(
											queryResultInstanceType
													.getResultInstanceId())
											.toString();
									String setSize = new Integer(
											queryResultInstanceType
													.getSetSize()).toString();
									patientSetText.setText("Patient Set: "
											+ setSize + " patients");// strs[0]);
									patientRefId = new String(setId);// strs[1]);
									patientMinNumText.setText("1");
									leftArrowButton.setEnabled(false);

									int maxPatientNum = new Integer(
											patientMaxNumText.getText())
											.intValue();
									patientSetSize = queryResultInstanceType
											.getSetSize();
									if (patientSetSize > maxPatientNum) {
										rightArrowButton.setEnabled(true);
										patientMaxNumText.setText("10");
									} else {
										rightArrowButton.setEnabled(false);
										if (patientSetSize > 0) {
											patientMaxNumText.setText(setSize);
										}
									}

									log.debug("Dropped set of: " + setSize
											+ " patients"/* strs[0] */
											+ " with refId: " + setId/*
																	 * strs[ 1 ]
																	 */);
								} else {
									// message
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					} else {
						List conceptChildren = tableXml.getChildren();
						parseDropConcepts(conceptChildren, event);
						// System.setProperty("XMLfrommodel",(String)
						// event.data);
						table.redraw();
						queryNamemrnlistText.setText("Query Name: ");
						groupNameText.setText("Panel Name: ");
					}

					event.detail = DND.DROP_NONE;
				} catch (JDOMException e) {
					System.err.println(e.getMessage());
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("You can not drop this item here.");
					mBox.open();
					event.detail = DND.DROP_NONE;
					e.printStackTrace();
					return;
				} catch (Exception e) {
					System.err.println(e.getMessage());
					event.detail = DND.DROP_NONE;
					e.printStackTrace();
					return;
				}
			}

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}
		});

		DropTarget target1 = new DropTarget(patientSetText, DND.DROP_COPY);
		target1.setTransfer(types);
		target1.addDropListener(new DropTargetAdapter() {
			@SuppressWarnings("unchecked")
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				String tmp = patientSetText.getText();
				String dragStr = (String) event.data;
				String[] strs = dragStr.split(":");
				if (strs[0].equalsIgnoreCase("logicquery")) {
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox
							.setMessage("You can not drop this item here. It accepts a patient set only.");
					mBox.open();
					event.detail = DND.DROP_NONE;
					patientSetText.setText(tmp);
					return;
				}

				JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

				try {
					JAXBElement jaxbElement = jaxbUtil
							.unMashallFromString(dragStr);
					DndType dndType = (DndType) jaxbElement.getValue();
					QueryResultInstanceType queryResultInstanceType = (QueryResultInstanceType) new JAXBUnWrapHelper()
							.getObjectByClass(dndType.getAny(),
									QueryResultInstanceType.class);

					if (queryResultInstanceType == null) {
						MessageBox mBox = new MessageBox(table.getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox
								.setMessage("You can not drop this item here. It accepts a patient set only.");
						mBox.open();
						event.detail = DND.DROP_NONE;
						patientSetText.setText(tmp);
						return;
					}

					String resultTypeName = queryResultInstanceType
							.getQueryResultType().getName();
					if (resultTypeName == null
							|| !resultTypeName.equalsIgnoreCase("PATIENTSET")) {
						MessageBox mBox = new MessageBox(table.getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox
								.setMessage("You can not drop this item here. It accepts a patient set only.");
						mBox.open();
						event.detail = DND.DROP_NONE;
						patientSetText.setText(tmp);
						return;
					}

					String setId = queryResultInstanceType
							.getResultInstanceId();
					String setSize = new Integer(queryResultInstanceType
							.getSetSize()).toString();
					patientSetText.setText(queryResultInstanceType
							.getDescription());// "Patient Set: " + setSize
					// + " patients");// strs[0]);
					patientRefId = new String(setId);// strs[1]);
					patientMinNumText.setText("1");
					leftArrowButton.setEnabled(false);
					queryNamemrnlistText.setText("Query Name: ");
					// groupNameText.setText("Panel Name: ");

					int maxPatientNum = new Integer(patientMaxNumText.getText())
							.intValue();
					patientSetSize = queryResultInstanceType.getSetSize();
					if (patientSetSize > maxPatientNum) {
						rightArrowButton.setEnabled(true);
						patientMaxNumText.setText("10");
					} else {
						rightArrowButton.setEnabled(false);
						// if(patientSetSize>0) {
						// patientMaxNumText.setText(setSize);
						// }
					}

					log.debug("Dropped set of: " + setSize + " patients"/*
																		 * strs[0
																		 * ]
																		 */
							+ " with refId: " + setId/* strs[1] */);
				} catch (Exception e) {
					e.printStackTrace();
					event.detail = DND.DROP_NONE;
					return;
				}
			}

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}
		});

		table.addCellResizeListener(new KTableCellResizeListener() {
			public void columnResized(int col, int newWidth) {
				log.debug("Column " + col + " resized to " + newWidth);
			}

			public void rowResized(int newHeight) {
				log.debug("Rows resized to " + newHeight);
			}

		});

		table.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				int tableWidth = table.getBounds().width;
				table.getModel().setColumnWidth(1, tableWidth - 505);
			}
		});

		table.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent arg0) {

			}

			public void mouseExit(MouseEvent arg0) {

			}

			public void mouseHover(MouseEvent arg0) {
				MouseEvent evt = arg0;

				Rectangle rect = table.getCellRect(3, getRowNumber());
				Rectangle rect1 = table.getCellRect(5, 1);

				// System.out.println("rect X and width: "+rect.x+","+rect.width)
				// ;
				// System.out.println("mouse X and Y: "+evt.x+","+evt.y);
				if (evt.y < rect.y && evt.x > rect1.x
						&& evt.x < rect1.x + rect1.width) {
					table
							.setToolTipText("Double click the cell to change color.");
				} else {
					table.setToolTipText("");
				}
			}

		});

		// Item 2: a Color Palette
		TabItem item2 = new TabItem(tabFolder, SWT.NONE);
		item2.setText("Render a Timeline");
		final Composite comp2 = new Composite(tabFolder, SWT.NONE);
		item2.setControl(comp2);

		GridLayout oGridLayout0 = new GridLayout();
		oGridLayout0.marginWidth = 1;
		oGridLayout0.marginHeight = 5;
		comp2.setLayout(oGridLayout0);

		if (false) {
			Composite composite = new Composite(comp2, SWT.NO_BACKGROUND
					| SWT.EMBEDDED);

			/*
			 * Set a Windows specific AWT property that prevents heavyweight
			 * components from erasing their background. Note that this is a
			 * global property and cannot be scoped. It might not be suitable
			 * for your application.
			 */
			try {
				// System.setProperty("sun.awt.noerasebackground", "true");
			} catch (NoSuchMethodError error) {
			}

			/* Create and setting up frame */
			////for mac fix
			//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
				//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
			Frame frame = SWT_AWT.new_Frame(composite);
			Panel panel = new Panel(new BorderLayout()) {
				public void update(java.awt.Graphics g) {
					/* Do not erase the background */
					paint(g);
				}
			};
			frame.add(panel);
			JRootPane root = new JRootPane();
			panel.add(root);
			java.awt.Container contentPane = root.getContentPane();
			log.debug("got to here");

			//Record record1 = new Record();
			// record1.start();
			//record1.init();

			//JScrollPane scrollPane = new JScrollPane(record1);
			//contentPane.setLayout(new BorderLayout());
			//contentPane.add(scrollPane);
		}

		if (true) {
			Composite composite = new Composite(comp2, SWT.NO_BACKGROUND
					| SWT.EMBEDDED);
			GridData gridData3 = new GridData();
			gridData3.horizontalIndent = 0;
			gridData3.verticalIndent = 0;
			gridData3.horizontalAlignment = GridData.FILL;
			gridData3.verticalAlignment = GridData.FILL;
			gridData3.grabExcessHorizontalSpace = true;
			gridData3.grabExcessVerticalSpace = true;
			composite.setLayoutData(gridData3);
			/* Create and setting up frame */
			////for mac fix
			//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
				//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
			Frame frame = SWT_AWT.new_Frame(composite);
			Panel panel = new Panel(new BorderLayout());// {

			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				log.debug("Error setting native LAF: " + e);
			}

			frame.add(panel);
			JRootPane root = new JRootPane();
			panel.add(root);
			oAwtContainer = root.getContentPane();
			log.debug("got to here");
		}

		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				runMode = 0;
				ConceptTableModel i2b2Model = (ConceptTableModel) table
						.getModel();
				i2b2Model.fillDataFromTable(rowData);
				if (rowData.size() == 0) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("The set up table is empty.");
					mBox.open();
					return;
				}

				String patientSetStr = patientSetText.getText();
				if (patientSetStr.equals("") && !isAll) {
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					MessageBox mBox = new MessageBox(table.getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox
							.setMessage("Please set a patient set or choose all datamart option.");
					mBox.open();
					return;
				}

				if (tabFolder.getSelectionIndex() == 1) {
					if (patientSetStr.equalsIgnoreCase("All")) {
						int minPatient = 0;
						try {
							String minText = patientMinNumText.getText();
							minPatient = Integer.parseInt(minText);
						} catch (Exception e1) {
							minPatient = -1;
						}

						int maxPatient = 0;
						try {
							maxPatient = Integer.parseInt(patientMaxNumText
									.getText());
						} catch (Exception e2) {
							maxPatient = -1;
						}
						PerformVisualizationQuery(oAwtContainer, "All",
								minPatient, maxPatient, bDisplayAllData);
					} else {
						int min = Integer.parseInt(patientMinNumText.getText());
						int max = Integer.parseInt(patientMaxNumText.getText());
						int start = new Integer(patientMinNumText.getText())
								.intValue();
						int inc = new Integer(patientMaxNumText.getText())
								.intValue();
						if (start + inc - 1 > patientSetSize) {
							rightArrowButton.setEnabled(false);
						}
						//patientMinNumText.setText("" + (start + inc));
						leftArrowButton.setEnabled(true);

						PerformVisualizationQuery(oAwtContainer, patientRefId,
								min, max - 1, bDisplayAllData);
					}

					// getDisplay().syncExec(new Runnable() {
					// public void run() {
					// if(returnedNumber >= 0) {
					// //setDecreaseNumber(returnedNumber);
					// MessageBox mBox = new MessageBox(getShell(),
					// SWT.ICON_INFORMATION
					// | SWT.OK);
					// mBox.setText("Please Note ...");
					// mBox.setMessage(/*"Can't return all the requested "+
					// requestIndex+" patients, */"Only "+returnedNumber+"
					// patients returned");
					// mBox.open();
					// }
					// }
					// });
				} else {
					patientMinNumText.setText("1");
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {							
							DestroyMiniVisualization(oAwtContainer);						
						}
					});
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if (drawLeft) {
			horizontalForm.setWeights(new int[] { 30, 70 });
		}

		return parent;
	}

	@SuppressWarnings("unchecked")
	private void setTableRowData(QueryModel panelData, boolean isRawData) {

		try {
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					panelData.xmlContent());
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc
					.getRootElement()
					.getChild(
							"concepts",
							Namespace
									.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));
			List conceptChildren = tableXml.getChildren();
			for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) {
				Element conceptXml = (org.jdom.Element) itr.next();
				Element conTableXml = (Element) conceptXml;

				Element metadataAttribs = conTableXml.getChild("metadataxml");
				Element valuedataAttribs = null;
				if (metadataAttribs != null) {
					valuedataAttribs = metadataAttribs
							.getChild("ValueMetadata");
				}

				String val = null;
				values.clear();
				valueType = null;
				String tmp = null;
				double ll = -1.0;
				double hl = -1.0;
				double lh = -1.0;
				double hh = -1.0;
				boolean hasValueSet = false;

				if (valuedataAttribs != null) {
					if ((tmp = valuedataAttribs
							.getChildTextTrim("Oktousevalues")) != null) {

						hasValueSet = true;

						if (tmp.equalsIgnoreCase("Y")) {
							valueType = "NVAL_NUM";
						} else if (tmp.equalsIgnoreCase("N")) {
							valueType = "TVAL_CHAR";
						}
					}

					if ((val = valuedataAttribs
							.getChildTextTrim("LowofLowValue")) != null
							&& !(val.equals(""))) {
						ll = new Double(val).doubleValue();
						val = null;
					}
					if ((val = valuedataAttribs
							.getChildTextTrim("HighofLowValue")) != null
							&& !(val.equals(""))) {
						hl = new Double(val).doubleValue();
						val = null;
					}
					if ((val = valuedataAttribs
							.getChildTextTrim("LowofHighValue")) != null
							&& !(val.equals(""))) {
						lh = new Double(val).doubleValue();
						val = null;
					}
					if ((val = valuedataAttribs
							.getChildTextTrim("HighofHighValue")) != null
							&& !(val.equals(""))) {
						hh = new Double(val).doubleValue();
						val = null;
					}
				}

				if (ll >= 0) {
					values.put("LL", new Double(ll).toString());
				}
				if (hl > 0 && hl >= ll) {
					values.put("HL", new Double(hl).toString());
				}
				if (lh > 0 && lh >= hl) {
					values.put("NM", new Double(lh).toString());
					values.put("LH", new Double(lh).toString());
				}
				if (hh > 0 && hh >= lh) {
					values.put("HH", new Double(hh).toString());
				}

				log.debug("Got values: " + values.size());
				for (int i = 0; i < values.size(); i++) {
					log.debug("Got value: "
							+ values.get(values.keySet().toArray()[i]));
				}

				org.jdom.Element nameXml = conTableXml.getChild("name");
				String cname = nameXml.getText();
				if (cname.toLowerCase().startsWith("zz")) {
					cname = cname.substring(2).trim();
				}

				curRowNumber = rowData.size() + 1;
				// ConceptTableRow row = null;
				ArrayList<ConceptTableRow> alist = new ArrayList<ConceptTableRow>();
				XMLOutputter outputter = new XMLOutputter();
				String xmlOutput = outputter.outputString(conceptXml);
				ConceptTableModel m_Model = (ConceptTableModel) table
						.getModel();

				if (!isRawData) {
					ConceptTableRow row = panelData.tableRow();
					// row.rowNumber = curRowNumber;
					if(panelData.isModifier()) {
						row.conceptName = new String(cname)+" ["+((ModifierData)panelData).modifier_name()+"]";
					}
					else {
						row.conceptName = new String(cname);
					}
					row.dateText = "";
					row.valueType = "";
					row.valueText = "Not Applicable";
					row.modifierText = "Not Applicable";
					// row.height = "Medium";
					// row.color = new RGB(0, 0, 128);
					row.conceptXml = new String(xmlOutput);
					//QueryModel data = m_Model.parseXMLData(xmlOutput);
					//m_Model.parseXMLData(panelData, xmlOutput);
					panelData.hasValueSet(hasValueSet);
					row.data(panelData);
					if ((row.data.isModifier())) {
						//ModifierData mdata = (ModifierData) panelData;
						if ((((ModifierData)row.data).hasModifierValue())) {
							//row.data.setValueConstrains(panelData.constrainByValue);

							if (((ModifierData)row.data).modifierValuePropertyData().hasEnumValue()) {
								//((ModifierData)row.data).modifierValuePropertyData().useTextValue(
								//		panelData.valueModel().useTextValue());
								if (((ModifierData)row.data).modifierValuePropertyData().useTextValue()) {
									String tmpStr1 = ((ModifierData)row.data).modifierValuePropertyData().value()
											.replaceAll("'", "");
									String tmpStr = tmpStr1.substring(1, tmpStr1
											.length() - 1);
									String pValue[] = tmpStr.split(",");
									ArrayList<String> results = new ArrayList<String>();
									for (String data1 : pValue) {
										results.add(data1);
									}

									//((ModifierData)row.data).modifierValuePropertyData().selectedValues = results;
									((ModifierData)row.data).modifier_display_name(((ModifierData)row.data).modifierValuePropertyData()
											.getDisplayTexts());
								} else {//if(((ModifierData)row.data).modifierValuePropertyData().useValueFlag()){
									((ModifierData)row.data).modifier_display_name(" = Abnormal");
								}
							}
						}
						if (!((ModifierData)row.data).modifier_display_name().equalsIgnoreCase("")) {
							row.modifierText = ((ModifierData)row.data).modifier_display_name();
						} else {
							//if (hasValueSet) {
								row.modifierText = new String("All Values");
							//}
						}

						if (((ModifierData)row.data).modifier_display_name().equalsIgnoreCase(" H")) {
							row.valueText = new String(" = HIGH");
						} else if (((ModifierData)row.data).modifier_display_name().equalsIgnoreCase(" L")) {
							row.valueText = new String(" = LOW");
						}
					}
					
					if ((panelData.hasValue())) {
						row.data.setValueConstrains(panelData.constrainByValue);

						if (row.data.valueModel().hasEnumValue()) {
							row.data.valueModel().useTextValue(
									panelData.valueModel().useTextValue());
							if (row.data.valueModel().useTextValue()) {
								String tmpStr1 = panelData.valueModel().value()
										.replaceAll("'", "");
								String tmpStr = tmpStr1.substring(1, tmpStr1
										.length() - 1);
								String pValue[] = tmpStr.split(",");
								ArrayList<String> results = new ArrayList<String>();
								for (String data1 : pValue) {
									results.add(data1);
								}

								row.data.valueModel().selectedValues = results;
								row.data.valueName(row.data.valueModel()
										.getSelectedTexts());
							} else {
								row.data.valueName(" = Abnormal");
							}
						}

					}
					if (!row.data.valueName().equalsIgnoreCase("")) {
						row.valueText = row.data.valueName();
					} else {
						if (hasValueSet) {
							row.valueText = new String("All Values");
						}
					}

					if (row.data.valueName().equalsIgnoreCase(" H")) {
						row.valueText = new String(" = HIGH");
					} else if (row.data.valueName().equalsIgnoreCase(" L")) {
						row.valueText = new String(" = LOW");
					}

					String dateConstraint = "";
					if (panelData.startTime() != -1) {
						row.data.startYear(panelData.startYear());
						row.data.startMonth(panelData.startMonth());
						row.data.startDay(panelData.startDay());
						row.data.startTime(panelData.startTime());
						dateConstraint = (row.data.startMonth() + 1) + "/"
								+ row.data.startDay() + "/"
								+ (row.data.startYear()) + " -> ";

					}
					if (panelData.endTime() != -1) {
						row.data.endYear(panelData.endYear());
						row.data.endMonth(panelData.endMonth());
						row.data.endDay(panelData.endDay());
						row.data.endTime(panelData.endTime());
						if (dateConstraint.equals(""))
							dateConstraint = " -> ";
						dateConstraint += (row.data.endMonth() + 1) + "/"
								+ row.data.endDay() + "/"
								+ (row.data.endYear());

					}
					row.dateText = dateConstraint;
					alist.add(row);
				} else {
					if (values.size() == 0) {
						ConceptTableRow row = panelData.tableRow();
						row.rowNumber = curRowNumber;
						row.valueText = "All Values";
						row.height = "Medium";
						row.color = new RGB(0, 0, 128);
						row.conceptXml = new String(xmlOutput);
						QueryModel data = m_Model.parseXMLData(xmlOutput);
						data.hasValueSet(hasValueSet);
						row.data(data);
						alist.add(row);
					} else {
						Set s = values.keySet();
						String op = null;
						Object[] strs = s.toArray();

						for (int n = 0; n < values.size(); n++) {
							ConceptTableRow row = new ConceptTableRow();
							row.rowNumber = curRowNumber;
							row.conceptName = new String(cname);
							row.valueType = new String(valueType);
							QueryModel data = m_Model.parseXMLData(xmlOutput);

							op = (String) strs[n];
							if (op.equalsIgnoreCase("LL")) {
								row.valueText = new String(" < "
										+ values.get(strs[n]));
								row.height = "Very Low";
								row.color = new RGB(255, 0, 0);
								data.valueModel().operator("LESS THAN (<)");
								data.valueModel().value(values.get(strs[n]));
							} else if (op.equalsIgnoreCase("HL")) {
								row.valueText = new String(" between "
										+ values.get(strs[n - 1]) + " and "
										+ values.get(strs[n]));
								row.height = "Low";
								row.color = new RGB(255, 215, 0);
								data.valueModel().operator("BETWEEN");
								data.valueModel().lowValue(
										values.get(strs[n - 1]));
								data.valueModel()
										.highValue(values.get(strs[n]));
							} else if (op.equalsIgnoreCase("NM")) {
								row.valueText = new String(" between "
										+ values.get(strs[n - 1]) + " and "
										+ values.get(strs[n]));
								row.height = "Medium";
								row.color = new RGB(0, 255, 0);
								data.valueModel().operator("between");
								data.valueModel().lowValue(
										values.get(strs[n - 1]));
								data.valueModel()
										.highValue(values.get(strs[n]));
							} else if (op.equalsIgnoreCase("LH")) {
								row.valueText = new String(" between "
										+ values.get(strs[n]) + " and "
										+ values.get(strs[n + 1]));
								row.height = "Tall";
								row.color = new RGB(255, 215, 0);
								data.valueModel().operator("between");
								data.valueModel().lowValue(values.get(strs[n]));
								data.valueModel().highValue(
										values.get(strs[n + 1]));
							} else if (op.equalsIgnoreCase("HH")) {
								row.valueText = new String(" > "
										+ values.get(strs[n]));
								row.height = "Very Tall";
								row.color = new RGB(255, 0, 0);
								data.valueModel().operator("GREATER THAN (>)");
								data.valueModel().value(values.get(strs[n]));
							}
							row.conceptXml = new String(xmlOutput);
							data.hasValueSet(hasValueSet);
							data.valueModel().useNumericValue(true);
							row.data(data);

							alist.add(row);
						}
					}
				}
				rowData.add(alist);
			}
		} catch (JDOMException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void parseListOfConcepts(List conceptChildren) {

		for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) {
			Element conceptXml = (org.jdom.Element) itr.next();
			Element conTableXml = (Element) conceptXml;

			Element metadataAttribs = conTableXml.getChild("metadataxml");
			Element valuedataAttribs = null;
			if (metadataAttribs != null) {
				valuedataAttribs = metadataAttribs.getChild("ValueMetadata");
			}

			String val = null;
			values.clear();
			valueType = null;
			String tmp = null;
			double ll = -1.0;
			double hl = -1.0;
			double lh = -1.0;
			double hh = -1.0;

			if (valuedataAttribs != null) {
				if ((tmp = valuedataAttribs.getChildTextTrim("Oktousevalues")) != null) {
					if (tmp.equalsIgnoreCase("Y")) {
						valueType = "";
					} else if (tmp.equalsIgnoreCase("N")) {
						valueType = "";
					}
				}
				if ((val = valuedataAttribs.getChildTextTrim("LowofLowValue")) != null
						&& !(val.equals(""))) {
					ll = new Double(val).doubleValue();
					val = null;
				}
				if ((val = valuedataAttribs.getChildTextTrim("HighofLowValue")) != null
						&& !(val.equals(""))) {
					hl = new Double(val).doubleValue();
					val = null;
				}
				if ((val = valuedataAttribs.getChildTextTrim("LowofHighValue")) != null
						&& !(val.equals(""))) {
					lh = new Double(val).doubleValue();
					val = null;
				}
				if ((val = valuedataAttribs.getChildTextTrim("HighofHighValue")) != null
						&& !(val.equals(""))) {
					hh = new Double(val).doubleValue();
					val = null;
				}
			}

			if (ll >= 0) {
				values.put("LL", new Double(ll).toString());
			}
			if (hl > 0 && hl >= ll) {
				values.put("HL", new Double(hl).toString());
			}
			if (lh > 0 && lh >= hl) {
				values.put("NM", new Double(lh).toString());
				values.put("LH", new Double(lh).toString());
			}
			if (hh > 0 && hh >= lh) {
				values.put("HH", new Double(hh).toString());
			}

			log.debug("Got values: " + values.size());
			for (int i = 0; i < values.size(); i++) {
				log.debug("Got value: "
						+ values.get(values.keySet().toArray()[i]));
			}

			org.jdom.Element nameXml = conTableXml.getChild("name");
			String cname = nameXml.getText();
			if (cname.toLowerCase().startsWith("zz")) {
				cname = cname.substring(2).trim();
			}

			curRowNumber = rowData.size() + 1;
			ConceptTableRow row = null;
			ArrayList<ConceptTableRow> alist = new ArrayList<ConceptTableRow>();
			XMLOutputter outputter = new XMLOutputter();
			String xmlOutput = outputter.outputString(conceptXml);
			ConceptTableModel m_Model = (ConceptTableModel) table.getModel();

			if (values.size() == 0) {
				row = new ConceptTableRow();
				row.rowNumber = curRowNumber;
				row.conceptName = new String(cname);
				row.dateText = "";
				row.valueType = "";
				row.valueText = "";
				row.height = "Medium";
				row.color = new RGB(0, 0, 128);
				row.conceptXml = new String(xmlOutput);
				row.data(m_Model.parseXMLData(xmlOutput));

				alist.add(row);
			} else {
				Set s = values.keySet();
				String op = null;
				Object[] strs = s.toArray();

				for (int n = 0; n < values.size(); n++) {
					row = new ConceptTableRow();
					row.rowNumber = curRowNumber;
					row.conceptName = new String(cname);
					row.valueType = new String(valueType);

					op = (String) strs[n];
					if (op.equalsIgnoreCase("LL")) {
						row.valueText = new String(" < " + values.get(strs[n]));
						row.height = "Very Low";
						row.color = new RGB(255, 0, 0);
					} else if (op.equalsIgnoreCase("HL")) {
						row.valueText = new String(" between "
								+ values.get(strs[n - 1]) + " and "
								+ values.get(strs[n]));
						row.height = "Low";
						row.color = new RGB(255, 215, 0);
					} else if (op.equalsIgnoreCase("NM")) {
						row.valueText = new String(" between "
								+ values.get(strs[n - 1]) + " and "
								+ values.get(strs[n]));
						row.height = "Medium";
						row.color = new RGB(0, 255, 0);
					} else if (op.equalsIgnoreCase("LH")) {
						row.valueText = new String(" between "
								+ values.get(strs[n]) + " and "
								+ values.get(strs[n + 1]));
						row.height = "Tall";
						row.color = new RGB(255, 215, 0);
					} else if (op.equalsIgnoreCase("HH")) {
						row.valueText = new String(" > " + values.get(strs[n]));
						row.height = "Very Tall";
						row.color = new RGB(255, 0, 0);
					}
					row.conceptXml = new String(xmlOutput);
					row.data(m_Model.parseXMLData(xmlOutput));

					alist.add(row);
				}
			}
			rowData.add(alist);
		}
	}

	@SuppressWarnings("unchecked")
	private void setupTable(String xmlContent) {
		try {
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc
					.getRootElement()
					.getChild(
							"concepts",
							Namespace
									.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));
			List conceptChildren = tableXml.getChildren();
			parseListOfConcepts(conceptChildren);
		} catch (JDOMException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void populateTable(ArrayList<QueryModel> xmlContents) {
		ConceptTableModel m_Model = (ConceptTableModel) table.getModel();
		m_Model.deleteAllRows();
		curRowNumber = 0;
		rowData.clear();

		for (int i = 0; i < xmlContents.size(); i++) {
			setTableRowData(xmlContents.get(i), false);
		}
		
		m_Model.populateTable(rowData);

		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				table.redraw();
			}
		});
	}

	public void populateTableString(ArrayList<String> xmlContents) {
		ConceptTableModel m_Model = (ConceptTableModel) table.getModel();
		m_Model.deleteAllRows();
		curRowNumber = 0;
		rowData.clear();

		for (int i = 0; i < xmlContents.size(); i++) {
			setupTable(xmlContents.get(i));
			// parseListOfConcepts(xmlContents.get(i));
		}
		m_Model.populateTable(rowData);

		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				table.redraw();
			}
		});
	}

	private void performVisualization() {
		if (tabFolder.getSelectionIndex() == 1) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					DestroyMiniVisualization(oAwtContainer);
				}
			});

		} else if (tabFolder.getSelectionIndex() == 0) {
			tabFolder.setSelection(1);
		}
		String patientSetStr = patientSetText.getText();

		if (patientSetStr.equalsIgnoreCase("All")) {
			int minPatient = 0;
			try {
				String minText = patientMinNumText.getText();
				minPatient = Integer.parseInt(minText);
			} catch (Exception e1) {
				minPatient = -1;
			}

			int maxPatient = 0;
			try {
				maxPatient = Integer.parseInt(patientMaxNumText.getText());
			} catch (Exception e2) {
				maxPatient = -1;
			}

			PerformVisualizationQuery(oAwtContainer, "All", minPatient,
					maxPatient, bDisplayAllData);
		} else {

			int min = Integer.parseInt(patientMinNumText.getText());
			int max = Integer.parseInt(patientMaxNumText.getText());

			PerformVisualizationQuery(oAwtContainer, patientRefId, min, max,
					bDisplayAllData);
		}
	}

	public void generateTimeLine() {
		oTheParent.getDisplay().syncExec(new Runnable() {
			public void run() {
				performVisualization();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void resetRowNumber() {
		for (int i = 0; i < rowData.size(); i++) {
			ArrayList list = (ArrayList) rowData.get(i);
			for (int j = 0; j < list.size(); j++) {
				ConceptTableRow row = (ConceptTableRow) list.get(j);
				row.rowNumber = i + 1;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseDropConcepts(List conceptChildren, DropTargetEvent event) {
		for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) {
			Element conceptXml = (org.jdom.Element) itr.next();
			String conceptText = conceptXml.getText().trim();
			if (conceptText.equals("null")) // this is root level node
			{
				MessageBox mBox = new MessageBox(table.getShell(),
						SWT.ICON_INFORMATION | SWT.OK);
				mBox.setText("Please Note ...");
				mBox.setMessage("You can not use this item in a query, it is only used for organizing the lists.");
				mBox.open();
				event.detail = DND.DROP_NONE;
				return;
			}
			Element conTableXml = (Element) conceptXml;

			Element visualAttribs = conTableXml.getChild("visualattributes");
			String sVisualAttribs = visualAttribs.getText().trim();
			if (sVisualAttribs.toUpperCase().startsWith("C")) {
				MessageBox mBox = new MessageBox(table.getShell(),
						SWT.ICON_INFORMATION | SWT.OK);
				mBox.setText("Please Note ...");
				mBox.setMessage("You can not use this item in a query, it is only used for organizing the lists.");
				mBox.open();
				event.detail = DND.DROP_NONE;
				return;
			}
			
			Element metadataAttribs = conTableXml.getChild("metadataxml");
			Element valuedataAttribs = null;
			if (metadataAttribs != null) {
				valuedataAttribs = metadataAttribs.getChild("ValueMetadata");
			}

			String val = null;
			values.clear();
			valueType = null;
			String tmp = null;
			double ll = -1.0;
			double hl = -1.0;
			double lh = -1.0;
			double hh = -1.0;
			boolean hasValueSet = false;

			if (valuedataAttribs != null) {
				if ((tmp = valuedataAttribs.getChildTextTrim("Oktousevalues")) != null) {
					hasValueSet = true;

					if (tmp.equalsIgnoreCase("Y")) {
						valueType = "NVAL_NUM";
					} else if (tmp.equalsIgnoreCase("N")) {
						valueType = "TVAL_CHAR";
					}
				}
				if ((val = valuedataAttribs.getChildTextTrim("LowofLowValue")) != null
						&& !(val.equals(""))) {
					ll = new Double(val).doubleValue();
					val = null;
				}
				if ((val = valuedataAttribs.getChildTextTrim("HighofLowValue")) != null
						&& !(val.equals(""))) {
					hl = new Double(val).doubleValue();
					val = null;
				}
				if ((val = valuedataAttribs.getChildTextTrim("LowofHighValue")) != null
						&& !(val.equals(""))) {
					lh = new Double(val).doubleValue();
					val = null;
				}
				if ((val = valuedataAttribs.getChildTextTrim("HighofHighValue")) != null
						&& !(val.equals(""))) {
					hh = new Double(val).doubleValue();
					val = null;
				}
			}

			if (ll >= 0) {
				values.put("LL", new Double(ll).toString());
			}
			if (hl > 0 && hl >= ll) {
				values.put("HL", new Double(hl).toString());
			}
			if (lh > 0 && lh >= hl) {
				values.put("NM", new Double(lh).toString());
				values.put("LH", new Double(lh).toString());
			}
			if (hh > 0 && hh >= lh) {
				values.put("HH", new Double(hh).toString());
			}

			log.debug("Got values: " + values.size());
			for (int i = 0; i < values.size(); i++) {
				log.debug("Got value: "
						+ values.get(values.keySet().toArray()[i]));
			}

			org.jdom.Element nameXml = conTableXml.getChild("name");
			String cname = nameXml.getText();
			if (cname.toLowerCase().startsWith("zz")) {
				cname = cname.substring(2).trim();
			}

			// org.jdom.Element fullnameXml = conTableXml.getChild("key");
			// String cfullname = fullnameXml.getText();

			curRowNumber = rowData.size();
			ConceptTableModel m_Model = (ConceptTableModel) table.getModel();
			m_Model.fillDataFromTable(rowData);

			curRowNumber = rowData.size() + 1;
			ConceptTableRow row = null;
			ArrayList<ConceptTableRow> alist = new ArrayList<ConceptTableRow>();
			XMLOutputter outputter = new XMLOutputter();
			String xmlOutput = outputter.outputString(conceptXml);

			if (values.size() == 0) {
				QueryModel data = m_Model.parseXMLData(xmlOutput);
				row = new ConceptTableRow();
				row.rowNumber = curRowNumber;
				if(data.isModifier()) {
					row.conceptName = new String(cname+" ["+((ModifierData)data).modifier_name()+"]");
				}
				else{
					row.conceptName = new String(cname);
				}
				row.dateText = "";
				row.valueType = "";
				row.valueText = "All Values";
				row.modifierText = "Not Applicable";
				row.height = "Medium";
				row.color = new RGB(0, 0, 128);
				row.conceptXml = new String(xmlOutput);
				row.data(data);
				alist.add(row);
			} else {
				Set s = values.keySet();
				String op = null;
				Object[] strs = s.toArray();

				for (int n = 0; n < values.size(); n++) {
					row = new ConceptTableRow();

					row.rowNumber = curRowNumber;
					row.conceptName = new String(cname);
					row.valueType = new String(valueType);
					QueryModel data = m_Model.parseXMLData(xmlOutput);

					op = (String) strs[n];
					if (op.equalsIgnoreCase("LL")) {
						row.valueText = new String(" < " + values.get(strs[n]));
						row.height = "Very Low";
						row.color = new RGB(255, 0, 0);
						data.valueModel().operator("LESS THAN (<)");
						data.valueModel().value(values.get(strs[n]));
					} else if (op.equalsIgnoreCase("HL")) {
						row.valueText = new String(" between "
								+ values.get(strs[n - 1]) + " and "
								+ values.get(strs[n]));
						row.height = "Low";
						row.color = new RGB(255, 215, 0);
						data.valueModel().operator("BETWEEN");
						data.valueModel().lowValue(values.get(strs[n - 1]));
						data.valueModel().highValue(values.get(strs[n]));
					} else if (op.equalsIgnoreCase("NM")) {
						row.valueText = new String(" between "
								+ values.get(strs[n - 1]) + " and "
								+ values.get(strs[n]));
						row.height = "Medium";
						row.color = new RGB(0, 255, 0);
						data.valueModel().operator("between");
						data.valueModel().lowValue(values.get(strs[n - 1]));
						data.valueModel().highValue(values.get(strs[n]));
					} else if (op.equalsIgnoreCase("LH")) {
						row.valueText = new String(" between "
								+ values.get(strs[n]) + " and "
								+ values.get(strs[n + 1]));
						row.height = "Tall";
						row.color = new RGB(255, 215, 0);
						data.valueModel().operator("between");
						data.valueModel().lowValue(values.get(strs[n]));
						data.valueModel().highValue(values.get(strs[n + 1]));
					} else if (op.equalsIgnoreCase("HH")) {
						row.valueText = new String(" > " + values.get(strs[n]));
						row.height = "Very Tall";
						row.color = new RGB(255, 0, 0);
						data.valueModel().operator("GREATER THAN (>)");
						data.valueModel().value(values.get(strs[n]));
					}

					row.conceptXml = new String(xmlOutput);
					data.hasValueSet(hasValueSet);
					data.valueModel().useNumericValue(true);
					data.valueModel().noValue(false);
					row.data(data);

					alist.add(row);
				}
			}
			rowData.add(alist);

			m_Model.deleteAllRows();
			m_Model.populateTable(rowData);
		}
	}

	public boolean PerformVisualizationQuery(
			final java.awt.Container oAwtContainer, final int minPatient,
			final int maxPatient, final boolean bDisplayAll) {
		bStillPerformingVisualizationQuery = true;
		bNoError = true;

		p = new WaitPanel((int) (oAwtContainer.getWidth() * 0.40),
				(int) (oAwtContainer.getHeight() * 0.40),
				(maxPatient - minPatient));
		oAwtContainer.add(p);
		p.setBounds(0, 0, p.getParent().getWidth(), p.getParent().getHeight());
		p.init((int) (p.getParent().getWidth() * 0.40), (int) (p.getParent()
				.getHeight() * 0.40));
		p.go();
		p.setVisible(true);

		removelldFile();

		visualizationQueryThread = new Thread() {
			public void run() {
				log.info("before getResultSetAsi2b2XML: " + new Date());
				try {
					ConceptTableModel i2b2Model = (ConceptTableModel) table
							.getModel();
					String xmlContent = i2b2Model.getContentXml();

					oConnection = DBLib.openJDBCConnection(System
							.getProperty("datamartURL"), System
							.getProperty("datamartDriver"), System
							.getProperty("datamartUser"), System
							.getProperty("datamartPassword"));

					Properties properties = new Properties();
					String writeFileStr = "";
					String filename = "crcnavigator.properties";
					try {
						properties.load(new FileInputStream(filename));
						writeFileStr = properties
								.getProperty("writeTimelineFile");
						log.debug("Properties writeFile: =" + writeFileStr);
					} catch (IOException e) {
						log.error(e.getMessage());
					}

					boolean writeFile = false;
					if ((writeFileStr != null)
							&& (writeFileStr.equalsIgnoreCase("yes"))) {
						writeFile = true;
					}

					String result = DBLib.getResultSetFromI2B2Xml(xmlContent,
							minPatient, maxPatient, bDisplayAll, oConnection,
							writeFile, bDisplayDemographics);

					DBLib.closeConnection(oConnection);
					if (result != null) {
						if (result.equalsIgnoreCase("memory error")) {
							JOptionPane
									.showMessageDialog(
											oAwtContainer,
											"Running out of memory while loading "
													+ (maxPatient - minPatient)
													+ " patients."
													+ "\nPlease try it again with a smaller number of patients.");

							bNoError = false;
						} else {
							PerformMiniVisualization(oAwtContainer, result,
									writeFile);
						}
					}

					p.stop();
					p.setVisible(false);
					if (result.equalsIgnoreCase("memory error")) {
						oTheParent.getDisplay().syncExec(new Runnable() {
							public void run() {
								tabFolder.setSelection(0);
							}
						});
					}
				} catch (Exception e) {
					p.stop();
					p.setVisible(false);
					oTheParent.getDisplay().syncExec(new Runnable() {
						public void run() {
							tabFolder.setSelection(0);
						}
					});
					log.error(e.getMessage());
					bNoError = false;
				}
				log.info("after getResultSetAsi2b2XML: " + new Date());
			}
		};

		try {
			visualizationQueryThread.start();
		} catch (Exception e) {
			p.stop();
			p.setVisible(false);
			oTheParent.getDisplay().syncExec(new Runnable() {
				public void run() {
					tabFolder.setSelection(0);
				}
			});
			log.error(e.getMessage());
			return false;
		}

		return bNoError;
	}

	public boolean PerformVisualizationQuery(
			final java.awt.Container oAwtContainer, final int[] patientIds,
			final boolean bDisplayAll) {
		bStillPerformingVisualizationQuery = true;
		bNoError = true;

		p = new WaitPanel((int) (oAwtContainer.getWidth() * 0.40),
				(int) (oAwtContainer.getHeight() * 0.40), patientIds.length);
		oAwtContainer.add(p);
		p.setBounds(0, 0, p.getParent().getWidth(), p.getParent().getHeight());
		p.init((int) (p.getParent().getWidth() * 0.40), (int) (p.getParent()
				.getHeight() * 0.40));
		p.go();
		p.setVisible(true);

		removelldFile();

		visualizationQueryThread = new Thread() {
			public void run() {
				log.info("before getResultSetAsi2b2XML: " + new Date());
				try {
					ConceptTableModel i2b2Model = (ConceptTableModel) table
							.getModel();
					String xmlContent = i2b2Model.getContentXml();

					oConnection = DBLib.openJDBCConnection(System
							.getProperty("datamartURL"), System
							.getProperty("datamartDriver"), System
							.getProperty("datamartUser"), System
							.getProperty("datamartPassword"));

					Properties properties = new Properties();
					String writeFileStr = "";
					String filename = "crcnavigator.properties";
					try {
						properties.load(new FileInputStream(filename));
						writeFileStr = properties
								.getProperty("writeTimelineFile");
						log.debug("Properties writeFile: =" + writeFileStr);
					} catch (IOException e) {
						log.error(e.getMessage());
					}

					boolean writeFile = true;
					if ((writeFileStr != null)
							&& (writeFileStr.equalsIgnoreCase("no"))) {
						writeFile = false;
					}

					String result = DBLib.getResultSetFromI2B2Xml(xmlContent,
							patientIds, bDisplayAll, oConnection, writeFile,
							bDisplayDemographics);

					DBLib.closeConnection(oConnection);
					if (result != null) {
						if (result.equalsIgnoreCase("memory error")) {
							JOptionPane
									.showMessageDialog(
											oAwtContainer,
											"Running out of memory while loading "
													+ patientIds.length
													+ " patients."
													+ "\nPlease try it again with a smaller number of patients.");

							bNoError = false;
						} else {
							PerformMiniVisualization(oAwtContainer, result,
									writeFile);
						}
					}

					p.stop();
					p.setVisible(false);
					if (result.equalsIgnoreCase("memory error")) {
						oTheParent.getDisplay().syncExec(new Runnable() {
							public void run() {
								tabFolder.setSelection(0);
							}
						});
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					bNoError = false;
				}
				log.info("after getResultSetAsi2b2XML: " + new Date());
			}
		};

		try {
			visualizationQueryThread.start();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return bNoError;
	}

	public boolean PerformVisualizationQuery(
			final java.awt.Container oAwtContainer, final String patientRefId,
			final int minPatient, final int maxPatient,
			final boolean bDisplayAll) {
		bStillPerformingVisualizationQuery = true;
		bNoError = true;

		p = new WaitPanel((int) (oAwtContainer.getWidth() * 0.40),
				(int) (oAwtContainer.getHeight() * 0.40),
				(maxPatient - minPatient));
		oAwtContainer.add(p);
		p.setBounds(0, 0, p.getParent().getWidth(), p.getParent().getHeight());
		p.init((int) (p.getParent().getWidth() * 0.40), (int) (p.getParent()
				.getHeight() * 0.40));
		p.go();
		p.setVisible(true);

		removelldFile();

		final MainComposite explorer = this;
		visualizationQueryThread = new Thread() {
			public void run() {
				log.info("before getResultSetAsi2b2XML: " + new Date());
				try {
					ConceptTableModel i2b2Model = (ConceptTableModel) table
							.getModel();
					i2b2Model.fillDataFromTable(rowData);

					Properties properties = new Properties();
					String writeFileStr = "";
					String filename = "i2b2workbench.properties";
					try {
						properties.load(new FileInputStream(filename));
						writeFileStr = properties
								.getProperty("writeTimelineFile");
						log.debug("Properties writeFile: =" + writeFileStr);
					} catch (IOException e) {
						log.error(e.getMessage());
					}

					boolean writeFile = false;
					if ((writeFileStr != null)
							&& (writeFileStr.equalsIgnoreCase("yes"))) {
						writeFile = true;
					}

					ArrayList<TimelineRow> tlrows = i2b2Model
							.getTimelineRows(rowData);
					String result = PatientMappingQueryClient.getlldString(tlrows,
							patientRefId, minPatient, minPatient + maxPatient,
							bDisplayAll, writeFile, bDisplayDemographics,
							explorer);

					if (result != null) {
						if (result.equalsIgnoreCase("memory error")) {
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													oAwtContainer,
													"Running out of memory while loading "
															+ (maxPatient - minPatient)
															+ " patients."
															+ "\nPlease try it again with a smaller number of patients.");
								}
							});

							bNoError = false;
						} else if (result.equalsIgnoreCase("error")) {
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													oAwtContainer,
													"Response delivered from the remote server could not be "
															+ "understood, you may wish to retry your last action");
								}
							});

							bNoError = false;
						} else {
							PerformMiniVisualization(oAwtContainer, result,
									writeFile);
						}
					} else {
						// JOptionPane.showMessageDialog(oAwtContainer,
						// "Response delivered from the remote server could not be understood, you may wish to retry your last action"
						// );
						bNoError = false;
					}

					p.stop();
					p.setVisible(false);
					if (result == null
							|| result.equalsIgnoreCase("memory error")
							|| result.equalsIgnoreCase("error")) {
						oTheParent.getDisplay().syncExec(new Runnable() {
							public void run() {
								tabFolder.setSelection(0);
							}
						});
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					bNoError = false;
					e.printStackTrace();
				}
				log.info("after getResultSetAsi2b2XML: " + new Date());
			}
		};

		try {
			visualizationQueryThread.start();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return bNoError;

	}

	public void PerformMiniVisualization(java.awt.Container poAwtContainer,
			String result, boolean writeFile) {
		try {
			poAwtContainer.removeAll();

			log.info("Got to PerformMiniVisualization");
			//Record record1 = new Record();
			//poAwtContainer.add(record1);
			// record1.start();
			if (writeFile) {
				//record1.init();
			} else {
				//record1.init(result);
			}
			//theRecord = record1;
			if (returnedNumber >= 0) {
				getDisplay().syncExec(new Runnable() {
					public void run() {

						MessageBox mBox = new MessageBox(getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Only " + (returnedNumber)
								+ " patients returned");
						mBox.open();
					}
				});
			}

		} catch (Exception e) {
			log.error("done"); //
		}
	}

	@SuppressWarnings("deprecation")
	public void DestroyMiniVisualization(final java.awt.Container poAwtContainer) {
		try {
			if (p != null) {
				p.stop();
				p.setVisible(false);
				p = null;
			}
			if (visualizationQueryThread != null) {
				visualizationQueryThread.stop();
				visualizationQueryThread = null;
			}
			if (oConnection != null) {
				oConnection = null;
			}
			log.debug("got to destroy");
			//theRecord.removeAll();
			//theRecord = null;
			poAwtContainer.removeAll();
		} catch (Exception e) {
			// log.error("done");
		}
	}

	protected Control getConceptTreeTabControl(TabFolder tabFolder) {
		Composite compositeQueryTree = new Composite(tabFolder, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		compositeQueryTree.setLayout(gridLayout);

		// *Group compositeQueryTreeTop = new Group(compositeQueryTree,
		// SWT.NULL);
		// *compositeQueryTreeTop.setText("Query Items");
		GridLayout gridLayoutTree = new GridLayout(1, false);
		gridLayoutTree.numColumns = 1;
		gridLayoutTree.marginHeight = 0;
		// *compositeQueryTreeTop.setLayout(gridLayoutTree);
		GridData fromTreeGridData = new GridData(GridData.FILL_BOTH);
		fromTreeGridData.widthHint = 300;
		// *compositeQueryTreeTop.setLayoutData(fromTreeGridData);
		compositeQueryTree.setLayoutData(fromTreeGridData);

		// TreeComposite dragTree = new TreeComposite(compositeQueryTree, 1,
		// slm);
		// TreeComposite dragTree = new TreeComposite(compositeQueryTree,
		// 1,slm);
		// dragTree.setLayoutData(new GridData (GridData.FILL_BOTH));
		// dragTree.setLayout(gridLayout);

		return compositeQueryTree;
	}
	
	private void processPanels(PanelType panelType) {
		//PanelType panelType = queryDefinitionType
		//.getPanel().get(i);

		for (int j = 0; j < panelType.getItem().size(); j++) {
			ItemType itemType = panelType.getItem()
					.get(j);
			
			///////////////////////////////////////
			QueryModel nodedata = null;
			if(itemType.getConstrainByModifier() != null) {
				nodedata = new ModifierData();
				//itemType.getConstrainByModifier().
			}
			else {
				nodedata = new QueryModel();
			}
				
			nodedata.name(itemType.getItemName());
			nodedata.visualAttribute("FA");
			nodedata.tooltip(itemType.getTooltip());
			nodedata.fullname(itemType.getItemKey());
			nodedata.hlevel(new Integer(itemType
					.getHlevel()).toString());
		
			if (itemType.getItemShape() != null) {
				nodedata.tableRow().height = new String(
						itemType.getItemShape());
				nodedata.tableRow().color = ((ConceptTableModel) table
						.getModel()).getColor(itemType
						.getItemColor());
				// nodedata.tableRow().rowNumber =
				// Integer
				// .parseInt(itemType
				// .getItemRowNumber());
				//nodedata.tableRow().rowNumber = conceptCount + 1;
			} else {
				nodedata.tableRow().height = "Medium";
				nodedata.tableRow().color = new RGB(0,
						0, 128);
				//nodedata.tableRow().rowNumber = conceptCount + 1;
			}
		
			nodedata.constrainByValue(itemType
					.getConstrainByValue());
			if (itemType.getConstrainByValue().size() > 0) {
				nodedata.setValueConstrains(itemType
						.getConstrainByValue());
		
				if (itemType.getConstrainByValue()
						.size() > 0) {
					nodedata
							.setValueConstrains(itemType
									.getConstrainByValue());
					if (nodedata.valueModel()
							.hasEnumValue()) {
						if (nodedata.valueModel()
								.useTextValue()) {
							ArrayList<String> results = new ArrayList<String>();
							results
									.toArray(nodedata
											.valueModel()
											.value()
											.split(","));
							nodedata.valueModel().selectedValues = results;
						}
					}
				}
			}
		
			// Handle Constrain By Dates
			for (int u = 0; u < itemType
					.getConstrainByDate().size(); u++) {
				nodedata.setTimeConstrain(itemType
						.getConstrainByDate().get(u)
						.getDateFrom(), itemType
						.getConstrainByDate().get(u)
						.getDateTo());
			}
		
			String status = nodedata.setXmlContent();
			if (status.equalsIgnoreCase("error")) {
				// MessageBox mBox = new
				// MessageBox(table
				// .getShell(),
				// SWT.ICON_INFORMATION | SWT.OK);
				// mBox.setText("Please Note ...");
				// mBox.setMessage("Response delivered from the remote server could not be understood,\n"
				// +
				// "you may wish to retry your last action.");
				// mBox.open();
				// event.detail = DND.DROP_NONE;
		
				continue;
			} else {
				//nodeXmls.add(nodedata);
				//conceptCount++;
			}
		}
	}

	public static void main(String[] args) {
		final String ssFakeApplicationConfigurationXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<contents>\r\n"
				+ "    <table>\r\n"
				+ "        <name>Demographics</name>\r\n"
				+ "        <tableName>Demographics</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n"
				+ "    <table>\r\n"
				+ "        <name>Diagnoses</name>\r\n"
				+ "        <tableName>Diagnoses</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n"
				+ "    <table>\r\n"
				+ "        <name>Medications</name>\r\n"
				+ "        <tableName>Medications</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n"
				+ "    <table>\r\n"
				+ "        <name>I2B2</name>\r\n"
				+ "        <tableName>i2b2</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n" + "</contents>";
		System.setProperty("ApplicationConfigurationXML",
				ssFakeApplicationConfigurationXML);
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.setText("ExplorerC Test");
		shell.setSize(1000, 800);
		// ExplorerC oExplorerC = new ExplorerC(shell);
		// shell.pack();
		shell.open();
		// oExplorerC.run();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	boolean bStillPerformingVisualizationQuery;
	boolean bNoError;

	private void removelldFile() {
		try {
			String datafile = "i2b2xml.lld";
			String appDirectory = System.getProperty("user.dir").toString();
			datafile = appDirectory + File.separator + datafile;
			File f = new File(datafile);
			if (f.exists()) {
				f.delete();
			}
			log.debug(datafile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processQueryData(QueryMasterData ndata) {
		ArrayList<QueryModel> nodeXmls = new ArrayList<QueryModel>();
		try {
			JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
			String xmlcontent = null;
			String xmlrequest = null;

			xmlrequest = ndata.writeDefinitionQueryXML();
			lastRequestMessage(xmlrequest);

			if (System.getProperty("webServiceMethod").equals("SOAP")) {
				xmlcontent = PatientMappingQueryClient.sendPDQQueryRequestSOAP(xmlrequest);
			} else {
				xmlcontent = PatientMappingQueryClient.sendPDQQueryRequestREST(xmlrequest);
			}
			lastResponseMessage(xmlcontent);

			if (xmlcontent == null) {

				return;
			} else {
				log.debug("Query content response: " + xmlcontent);
				ndata.xmlContent(xmlcontent);
			}

			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(ndata
					.xmlContent());
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();

			BodyType bt = messageType.getMessageBody();
			MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bt.getAny(), MasterResponseType.class);
			RequestXmlType requestXmlType = masterResponseType.getQueryMaster()
					.get(0).getRequestXml();

			org.w3c.dom.Element element = (org.w3c.dom.Element) requestXmlType
					.getContent().get(0);
			if (element != null) {
				log.debug("query definition not null");
			} else {
				log.error("query definition is null");
			}

			String domString = edu.harvard.i2b2.common.util.xml.XMLUtil
					.convertDOMElementToString(element);
			log.debug("string output" + domString);

			JAXBContext jc1 = JAXBContext
					.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
			Unmarshaller unMarshaller = jc1.createUnmarshaller();
			JAXBElement queryDefinitionJaxbElement = (JAXBElement) unMarshaller
					.unmarshal(new StringReader(domString));

			QueryDefinitionType queryDefinitionType = (QueryDefinitionType) queryDefinitionJaxbElement
			.getValue();
	
			int numOfPanels = queryDefinitionType.getPanel().size();
			int conceptCount = 0;
			for (int i = 0; i < numOfPanels; i++) {
				PanelType panelType = queryDefinitionType
						.getPanel().get(i);
		
				for (int j = 0; j < panelType.getItem().size(); j++) {
					ItemType itemType = panelType.getItem()
							.get(j);
					
					///////////////////////////////////////////////
					QueryModel nodedata = null;
					ConstrainByModifier md = itemType.getConstrainByModifier();
					if(md != null) {
						nodedata = new ModifierData();
						nodedata.isModifier(true);
						((ModifierData)nodedata).modifier_key(md.getModifierKey());
						((ModifierData)nodedata).applied_path(md.getAppliedPath());
						((ModifierData)nodedata).modifier_name(md.getModifierName());
						((ModifierData)nodedata).setModifierValueConstraint(md.getConstrainByValue());
					}
					else {
						nodedata = new QueryModel();
					}
		
					nodedata.name(itemType.getItemName());
					nodedata.visualAttribute("FA");
					nodedata.tooltip(itemType.getTooltip());
					nodedata.fullname(itemType.getItemKey());
					nodedata.hlevel(new Integer(itemType
							.getHlevel()).toString());
		
					if (itemType.getItemShape() != null) {
						nodedata.tableRow().height = new String(
								itemType.getItemShape());
						nodedata.tableRow().color = ((ConceptTableModel) table
								.getModel()).getColor(itemType
								.getItemColor());
						// nodedata.tableRow().rowNumber =
						// Integer
						// .parseInt(itemType
						// .getItemRowNumber());
						nodedata.tableRow().rowNumber = conceptCount + 1;
					} else {
						nodedata.tableRow().height = "Medium";
						nodedata.tableRow().color = new RGB(0,
								0, 128);
						nodedata.tableRow().rowNumber = conceptCount + 1;
					}
		
					nodedata.constrainByValue(itemType
							.getConstrainByValue());
					if (itemType.getConstrainByValue().size() > 0) {
						nodedata.setValueConstrains(itemType
								.getConstrainByValue());
		
						if (itemType.getConstrainByValue()
								.size() > 0) {
							nodedata
									.setValueConstrains(itemType
											.getConstrainByValue());
							if (nodedata.valueModel()
									.hasEnumValue()) {
								if (nodedata.valueModel()
										.useTextValue()) {
									ArrayList<String> results = new ArrayList<String>();
									results
											.toArray(nodedata
													.valueModel()
													.value()
													.split(","));
									nodedata.valueModel().selectedValues = results;
								}
							}
						}
					}
		
					// Handle Constrain By Dates
					for (int u = 0; u < itemType
							.getConstrainByDate().size(); u++) {
						nodedata.setTimeConstrain(itemType
								.getConstrainByDate().get(u)
								.getDateFrom(), itemType
								.getConstrainByDate().get(u)
								.getDateTo());
					}
		
					nodedata.updateNodeMetaDataXML();
					//if (status.equalsIgnoreCase("error")) {
						// MessageBox mBox = new
						// MessageBox(table
						// .getShell(),
						// SWT.ICON_INFORMATION | SWT.OK);
						// mBox.setText("Please Note ...");
						// mBox.setMessage("Response delivered from the remote server could not be understood,\n"
						// +
						// "you may wish to retry your last action.");
						// mBox.open();
						// event.detail = DND.DROP_NONE;
		
						//continue;
					//} else {
						nodeXmls.add(nodedata);
						conceptCount++;
					//}
				}
			}
			if (nodeXmls.size() == 0) {
				
				return;
			}
			populateTable(nodeXmls);

			// get query instance
			String xmlRequest = ndata.writeContentQueryXML();
			lastRequestMessage(xmlRequest);
			String xmlResponse = PatientMappingQueryClient
					.sendPDQQueryRequestREST(xmlRequest);
			lastResponseMessage(xmlResponse);

			jaxbElement = jaxbUtil.unMashallFromString(xmlResponse);
			messageType = (ResponseMessageType) jaxbElement.getValue();
			bt = messageType.getMessageBody();
			InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bt.getAny(), InstanceResponseType.class);

			QueryInstanceData instanceData = null;
			XMLGregorianCalendar startDate = null;
			for (QueryInstanceType queryInstanceType : instanceResponseType
					.getQueryInstance()) {
				QueryInstanceData runData = new QueryInstanceData();

				runData.visualAttribute("FA");
				runData.tooltip("The results of the query run");
				runData.id(new Integer(queryInstanceType.getQueryInstanceId())
						.toString());
				XMLGregorianCalendar cldr = queryInstanceType.getStartDate();
				runData.name("Results of " + "[" + cldr.getMonth() + "-"
						+ cldr.getDay() + "-" + cldr.getYear() + " "
						+ cldr.getHour() + ":" + cldr.getMinute() + ":"
						+ cldr.getSecond() + "]");

				if (instanceData == null) {
					startDate = cldr;
					instanceData = runData;
				} else {
					if (cldr.toGregorianCalendar().compareTo(
							startDate.toGregorianCalendar()) > 0) {
						startDate = cldr;
						instanceData = runData;
					}
				}
			}
			// get patient set
			if (instanceData == null) {
				// event.detail = DND.DROP_NONE;
				return;
			}
			log.debug("Got query instance: " + instanceData.name());

			xmlRequest = instanceData.writeContentQueryXML();
			lastRequestMessage(xmlRequest);

			xmlResponse = PatientMappingQueryClient.sendPDQQueryRequestREST(xmlRequest);
			lastResponseMessage(xmlResponse);

			jaxbElement = jaxbUtil.unMashallFromString(xmlResponse);
			messageType = (ResponseMessageType) jaxbElement.getValue();
			bt = messageType.getMessageBody();
			ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bt.getAny(), ResultResponseType.class);

			for (QueryResultInstanceType queryResultInstanceType : resultResponseType
					.getQueryResultInstance()) {
				if (!(queryResultInstanceType.getQueryResultType().getName()
						.equalsIgnoreCase("PATIENTSET"))) {
					continue;
				}

				String status = queryResultInstanceType.getQueryStatusType()
						.getName();

				if (status.equalsIgnoreCase("FINISHED")) {

					String setId = new Integer(queryResultInstanceType
							.getResultInstanceId()).toString();
					String setSize = new Integer(queryResultInstanceType
							.getSetSize()).toString();
					String description = queryResultInstanceType
							.getDescription();
					if (description != null) {
						patientSetText.setText(description);
					} else {
						patientSetText.setText("Patient Set: " + setSize
								+ " patients");
					}
					patientRefId = new String(setId);
					patientMinNumText.setText("1");
					leftArrowButton.setEnabled(false);

					int maxPatientNum = new Integer(patientMaxNumText.getText())
							.intValue();
					patientSetSize = queryResultInstanceType.getSetSize();
					if (patientSetSize > maxPatientNum) {
						rightArrowButton.setEnabled(true);
						patientMaxNumText.setText("10");
					} else {
						rightArrowButton.setEnabled(false);
						if (patientSetSize > 0) {
							patientMaxNumText.setText(setSize);
						}
					}

					log.debug("Dropped set of: " + setSize + " patients"/*
																		 * strs[0
																		 * ]
																		 */
							+ " with refId: " + setId/*
													 * strs[ 1 ]
													 */);
				} else {
					// message
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private int getRowNumber() {
		int n = 1;
		for (int i = 0; i < rowData.size(); i++) {
			ArrayList list = (ArrayList) rowData.get(i);
			for (int j = 0; j < list.size(); j++) {
				n++;
			}
		}
		return n;
	}

	public void setIncrementNumber(int number) {
		if(number == -1) {
			String num = patientMinNumText.getText();
			int inc = new Integer(patientMaxNumText.getText()).intValue();
			int origin = Integer.parseInt(num) + inc;// - inc;
			patientMinNumText.setText("" + origin);
		}
		else {
			String num = patientMinNumText.getText();
			//int inc = new Integer(patientMaxNumText.getText()).intValue();
			int origin = Integer.parseInt(num) + number;// - inc;
			patientMinNumText.setText("" + origin);
		}
	}

	public void setDecreaseNumber(int number) {
		if(number == -1) {
			String num = patientMinNumText.getText();
			int inc = new Integer(patientMaxNumText.getText()).intValue();
			int origin = Integer.parseInt(num) - inc;// + inc;
			patientMinNumText.setText(""+origin);
		}
		else {
			String num = patientMinNumText.getText();
			//int inc = new Integer(patientMaxNumText.getText()).intValue();
			int origin = Integer.parseInt(num) - number;// + inc;
			patientMinNumText.setText(""+origin);
		}
	}
}