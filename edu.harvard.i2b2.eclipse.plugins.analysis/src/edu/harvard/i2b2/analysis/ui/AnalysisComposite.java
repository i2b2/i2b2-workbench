/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.analysis.ui;

import java.awt.Color;
import java.awt.Font;

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import edu.harvard.i2b2.analysis.data.QueryMasterData;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crc.datavo.i2b2result.ResultEnvelopeType;
import edu.harvard.i2b2.crc.datavo.i2b2result.ResultType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.CrcXmlResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.InstanceResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultTypeResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.StatusType.Condition;
import edu.harvard.i2b2.common.datavo.pdo.EventSet;
import edu.harvard.i2b2.common.datavo.pdo.EventType;
import edu.harvard.i2b2.common.datavo.pdo.ObservationType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.analysis.data.QueryInstanceData;
import edu.harvard.i2b2.analysis.data.QueryResultData;
import edu.harvard.i2b2.analysis.datavo.AnalysisJAXBUtil;
import edu.harvard.i2b2.analysis.queryClient.QueryClient;

public class AnalysisComposite extends Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AnalysisComposite.class);
	private Tree tree1;
	private Label label1;
	private Button clearButton;
	private FramedComposite composite3;
	private Label label2;
	private TreeItem treeItem;
	private FramedComposite composite2;
	private Composite composite1;
	private String queryName;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public AnalysisComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		this.setSize(557, 224);

		// Create the types
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		final Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayout(new FillLayout());
		final GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_composite_2.heightHint = 263;
		gd_composite_2.widthHint = 540;
		composite_2.setLayoutData(gd_composite_2);

		final SashForm sashForm = new SashForm(composite_2, SWT.HORIZONTAL);
		sashForm.setOrientation(SWT.HORIZONTAL);
		{
			composite2 = new FramedComposite(sashForm, SWT.SHADOW_NONE);
			GridLayout composite2Layout = new GridLayout();
			composite2Layout.horizontalSpacing = 1;
			composite2.setLayout(composite2Layout);
			{
				composite3 = new FramedComposite(composite2, SWT.SHADOW_NONE);
				GridLayout composite3Layout = new GridLayout();
				composite3Layout.numColumns = 2;
				composite3Layout.marginHeight = 3;
				GridData composite3LData = new GridData();
				composite3LData.grabExcessHorizontalSpace = true;
				composite3LData.horizontalAlignment = GridData.FILL;
				composite3LData.heightHint = 25;
				composite3.setLayoutData(composite3LData);
				composite3.setLayout(composite3Layout);
				{
					label2 = new Label(composite3, SWT.NONE);
					label2.setText("Graphic Analyses");
					GridData label2LData = new GridData();
					label2LData.horizontalAlignment = GridData.FILL;
					label2LData.heightHint = 14;
					label2LData.grabExcessHorizontalSpace = true;
					label2.setLayoutData(label2LData);
					label2.setAlignment(SWT.CENTER);
				}
				{
					clearButton = new Button(composite3, SWT.PUSH | SWT.CENTER);
					GridData button1LData = new GridData();
					button1LData.horizontalAlignment = GridData.CENTER;
					button1LData.widthHint = 19;
					button1LData.verticalAlignment = GridData.BEGINNING;
					button1LData.heightHint = 18;
					clearButton.setLayoutData(button1LData);
					clearButton.setText("x");
					clearButton
							.setToolTipText("Remove all nodes in analysis tree panel below");
					clearButton.setFont(SWTResourceManager.getFont("Tahoma",
							10, 1, false, false));
					clearButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							clearButtonWidgetSelected(evt);
						}
					});
				}
			}
			{
				tree1 = new Tree(composite2, SWT.BORDER);
				tree1.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						final TreeItem item = tree1.getSelection()[0];
						if (item.getText().equalsIgnoreCase(
								"No results to display")) {
							return;
						}
						getDisplay().syncExec(new Runnable() {
							public void run() {
								queryName = item.getParentItem().getText();
								label1.setText(item.getParentItem().getText());
							}
						});
						setSelection((QueryResultData) item.getData());
					}
				});
				GridData tree1LData = new GridData();
				tree1LData.verticalAlignment = GridData.FILL;
				tree1LData.horizontalAlignment = GridData.FILL;
				tree1LData.grabExcessHorizontalSpace = true;
				tree1LData.grabExcessVerticalSpace = true;
				tree1.setLayoutData(tree1LData);
				{
					/*
					 * analyses = new TreeItem(tree1, SWT.NONE);
					 * analyses.setText("Analyses"); analyses
					 * .setImage(SWTResourceManager
					 * .getImage("edu/harvard/i2b2/analysis/ui/openFolder.jpg"
					 * )); analyses.setExpanded(true);
					 */
				}
			}
		}
		{
			final FramedComposite right_composite = new FramedComposite(
					sashForm, SWT.SHADOW_NONE);
			GridLayout right_compositeLayout = new GridLayout();
			right_composite.setLayout(right_compositeLayout);
			{
				final FramedComposite top_composite = new FramedComposite(
						right_composite, SWT.SHADOW_NONE);
				GridLayout top_compositeLayout = new GridLayout();
				top_compositeLayout.makeColumnsEqualWidth = true;
				top_composite.setLayout(top_compositeLayout);
				GridData top_compositeLData = new GridData();
				top_compositeLData.horizontalAlignment = GridData.FILL;
				top_compositeLData.grabExcessHorizontalSpace = true;
				top_composite.setLayoutData(top_compositeLData);
				{
					label1 = new Label(top_composite, SWT.NO_TRIM);
					GridData gd_top_composite = new GridData();
					gd_top_composite.grabExcessHorizontalSpace = true;
					gd_top_composite.horizontalAlignment = GridData.FILL;
					label1.setLayoutData(gd_top_composite);
					queryName = "Query Name: ";
					label1.setText("Query Name: ");
					label1.addListener(SWT.Resize, new Listener() {
						public void handleEvent(Event event) {
							int width = label1.getBounds().width;
							GC gc = new GC(Display.getCurrent()
									.getActiveShell());

							if (gc != null) {

								gc.setFont(label1.getFont());
								Point pt = gc.stringExtent(queryName);

								if (pt.x <= width) {
									label1.setText(queryName);
									gc.dispose();
									return;
								}

								int charWidth = pt.x / queryName.length();
								int charNum = width / charWidth;
								label1.setText(queryName.substring(0,
										charNum - 6)
										+ "...");
								// System.out.println("size: "+label1.getSize()
								// + "; width"+width+
								// " font width: "+pt.x+"char width: "+pt.x/
								// queryName.length());

								gc.dispose();
							}
						}
					});
					label1.addMouseTrackListener(new MouseTrackListener() {

						public void mouseEnter(MouseEvent arg0) {
							top_composite.setForeground(getDisplay()
									.getSystemColor(SWT.COLOR_YELLOW));
						}

						public void mouseExit(MouseEvent arg0) {
							top_composite.setForeground(getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
						}

						public void mouseHover(MouseEvent arg0) {
							top_composite.setForeground(getDisplay()
									.getSystemColor(SWT.COLOR_YELLOW));
						}

					});
				}
				{
					DropTarget target1 = new DropTarget(top_composite,
							DND.DROP_COPY);
					// RowData target1LData = new RowData();
					// target1.setLayoutData(target1LData);
					target1.setTransfer(types);
					target1.addDropListener(new DropTargetAdapter() {
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
								org.jdom.Document tableDoc = parser
										.build(xmlStringReader);
								org.jdom.Element tableXml = tableDoc
										.getRootElement()
										.getChild(
												"query_master",
												Namespace
														.getNamespace("http://www.i2b2.org/xsd/cell/crc/psm/1.1/"));

								if (tableXml == null) {
									tableXml = tableDoc
											.getRootElement()
											.getChild(
													"query_instance",
													Namespace
															.getNamespace("http://www.i2b2.org/xsd/cell/crc/psm/1.1/"));
									if (tableXml == null) {

										MessageBox mBox = new MessageBox(
												top_composite.getShell(),
												SWT.ICON_INFORMATION | SWT.OK);
										mBox.setText("Please Note ...");
										mBox
												.setMessage("You can not drop this item here.");
										mBox.open();
										event.detail = DND.DROP_NONE;
										return;
									} else {
										try {
											QueryInstanceData ndata = new QueryInstanceData();
											// ndata.name(tableXml.getChildText(
											// "name"));
											// label1.setText("Query Name: " +
											// ndata.name());
											ndata.xmlContent(null);
											ndata
													.id(tableXml
															.getChildTextTrim("query_instance_id"));
											ndata
													.userId(tableXml
															.getChildTextTrim("user_id"));
											ndata.name(tableXml
													.getChildTextTrim("name"));

											insertNodes(ndata);
											setSelection(tree1.getItemCount() - 1);

										} catch (Exception e) {
											e.printStackTrace();
											return;
										}

										event.detail = DND.DROP_NONE;
										return;
									}
								}
								try {
									JAXBUtil jaxbUtil = AnalysisJAXBUtil
											.getJAXBUtil();
									QueryMasterData ndata = new QueryMasterData();
									ndata.name(tableXml.getChildText("name"));
									// label1.setText("Query Name: " +
									// ndata.name());
									ndata.xmlContent(null);
									ndata
											.id(tableXml
													.getChildTextTrim("query_master_id"));
									ndata.userId(tableXml
											.getChildTextTrim("user_id"));

									// get query instance
									String xmlRequest = ndata
											.writeContentQueryXML();
									// lastRequestMessage(xmlRequest);
									String xmlResponse = QueryClient
											.sendQueryRequestREST(xmlRequest);
									// lastResponseMessage(xmlResponse);

									JAXBElement jaxbElement = jaxbUtil
											.unMashallFromString(xmlResponse);
									ResponseMessageType messageType = (ResponseMessageType) jaxbElement
											.getValue();
									BodyType bt = messageType.getMessageBody();
									InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
											.getObjectByClass(bt.getAny(),
													InstanceResponseType.class);

									QueryInstanceData instanceData = null;
									XMLGregorianCalendar startDate = null;
									for (QueryInstanceType queryInstanceType : instanceResponseType
											.getQueryInstance()) {
										QueryInstanceData runData = new QueryInstanceData();

										runData.visualAttribute("FA");
										runData
												.tooltip("The results of the query run");
										runData.id(new Integer(
												queryInstanceType
														.getQueryInstanceId())
												.toString());
										XMLGregorianCalendar cldr = queryInstanceType
												.getStartDate();
										runData.name(ndata.name());

										if (instanceData == null) {
											startDate = cldr;
											instanceData = runData;
										} else {
											if (cldr
													.toGregorianCalendar()
													.compareTo(
															startDate
																	.toGregorianCalendar()) > 0) {
												startDate = cldr;
												instanceData = runData;
											}
										}
									}

									insertNodes(instanceData);
									if (treeItem.getItemCount() == 0) {
										getDisplay().syncExec(new Runnable() {
											public void run() {
												TreeItem treeItem1 = new TreeItem(
														treeItem, SWT.NONE);
												treeItem1
														.setText("No results to display");
												treeItem1
														.setForeground(getDisplay()
																.getSystemColor(
																		SWT.COLOR_RED));
												treeItem1.setExpanded(true);
												treeItem1
														.setImage(SWTResourceManager
																.getImage("edu/harvard/i2b2/analysis/ui/leaf.jpg"));

												JFreeChart chart = createNoDataChart(createEmptyDataset());
												composite1.getChildren()[0]
														.dispose();
												ChartComposite frame = new ChartComposite(
														composite1, SWT.NONE,
														chart, true, true,
														false, true, true);
												frame.pack();
												composite1.layout();

												tree1.select(treeItem1);
												return;
											}
										});

									} else {
										setSelection(tree1.getItemCount() - 1);
									}

								} catch (Exception e) {
									e.printStackTrace();
									return;
								}

								event.detail = DND.DROP_NONE;
							} catch (Exception e) {
								e.printStackTrace();
								event.detail = DND.DROP_NONE;
								return;
							}
						}

						@Override
						public void dragLeave(DropTargetEvent event) {
							super.dragLeave(event);
							top_composite.setForeground(getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
						}

						public void dragEnter(DropTargetEvent event) {
							event.detail = DND.DROP_COPY;
							top_composite.setForeground(getDisplay()
									.getSystemColor(SWT.COLOR_YELLOW));
						}
					});
				}
				top_composite.addMouseTrackListener(new MouseTrackListener() {

					public void mouseEnter(MouseEvent arg0) {
						top_composite.setForeground(getDisplay()
								.getSystemColor(SWT.COLOR_YELLOW));
					}

					public void mouseExit(MouseEvent arg0) {
						top_composite.setForeground(getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
					}

					public void mouseHover(MouseEvent arg0) {
						top_composite.setForeground(getDisplay()
								.getSystemColor(SWT.COLOR_YELLOW));
					}

				});
			}
			{
				composite1 = new Composite(right_composite, SWT.BORDER);
				FillLayout composite1Layout = new FillLayout(
						org.eclipse.swt.SWT.HORIZONTAL);
				GridData composite1LData = new GridData();
				composite1LData.grabExcessHorizontalSpace = true;
				composite1LData.grabExcessVerticalSpace = true;
				composite1LData.horizontalAlignment = GridData.FILL;
				composite1LData.verticalAlignment = GridData.FILL;
				composite1.setLayoutData(composite1LData);
				composite1.setLayout(composite1Layout);
				// composite1.setBackground(SWTResourceManager.getColor(255,
				// 255,
				// 0));

				getDisplay().syncExec(new Runnable() {
					public void run() {
						JFreeChart chart = createEmptyChart(createEmptyDataset());
						/* final ChartComposite frame = */new ChartComposite(
								composite1, SWT.NONE, chart, true, true, false,
								true, true);
					}
				});
			}

		}

		{
			// label2 = new Label(top_composite, SWT.NONE);
			// label2.setText("2512+3 patients");
			// label2.setBounds(254, 7, 108, 19);
		}
		{
			DropTarget target2 = new DropTarget(tree1, DND.DROP_COPY);
			target2.setTransfer(types);
			target2.addDropListener(new DropTargetAdapter() {
				public void dragEnter(DropTargetEvent event) {
					event.detail = DND.DROP_COPY;
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
						org.jdom.Document tableDoc = parser
								.build(xmlStringReader);
						org.jdom.Element tableXml = tableDoc
								.getRootElement()
								.getChild(
										"query_master",
										Namespace
												.getNamespace("http://www.i2b2.org/xsd/cell/crc/psm/1.1/"));

						if (tableXml == null) {
							tableXml = tableDoc
									.getRootElement()
									.getChild(
											"query_instance",
											Namespace
													.getNamespace("http://www.i2b2.org/xsd/cell/crc/psm/1.1/"));
							if (tableXml == null) {

								MessageBox mBox = new MessageBox(tree1
										.getShell(), SWT.ICON_INFORMATION
										| SWT.OK);
								mBox.setText("Please Note ...");
								mBox
										.setMessage("You can not drop this item here.");
								mBox.open();
								event.detail = DND.DROP_NONE;
								return;
							} else {
								try {
									// JAXBUtil jaxbUtil =
									// AnalysisJAXBUtil.getJAXBUtil();
									QueryInstanceData ndata = new QueryInstanceData();
									// ndata.name(tableXml.getChildText("name"));
									// label1.setText("Query Name: " +
									// ndata.name());
									ndata.xmlContent(null);
									ndata
											.id(tableXml
													.getChildTextTrim("query_instance_id"));
									ndata.userId(tableXml
											.getChildTextTrim("user_id"));
									ndata.name(tableXml
											.getChildTextTrim("name"));

									// clearTree();
									insertNodes(ndata);
									setSelection(tree1.getItemCount() - 1);

								} catch (Exception e) {
									e.printStackTrace();
									return;
								}

								event.detail = DND.DROP_NONE;
								return;
							}
						}
						try {
							JAXBUtil jaxbUtil = AnalysisJAXBUtil.getJAXBUtil();
							QueryMasterData ndata = new QueryMasterData();
							ndata.name(tableXml.getChildText("name"));
							// label1.setText("Query Name: " + ndata.name());
							ndata.xmlContent(null);
							ndata.id(tableXml
									.getChildTextTrim("query_master_id"));
							ndata.userId(tableXml.getChildTextTrim("user_id"));

							// get query instance
							String xmlRequest = ndata.writeContentQueryXML();
							// lastRequestMessage(xmlRequest);
							String xmlResponse = QueryClient
									.sendQueryRequestREST(xmlRequest);
							// lastResponseMessage(xmlResponse);

							JAXBElement jaxbElement = jaxbUtil
									.unMashallFromString(xmlResponse);
							ResponseMessageType messageType = (ResponseMessageType) jaxbElement
									.getValue();
							BodyType bt = messageType.getMessageBody();
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
								runData.name(ndata.name());

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
							// clearTree();
							insertNodes(instanceData);
							setSelection(tree1.getItemCount() - 1);

						} catch (Exception e) {
							e.printStackTrace();
							return;
						}

						event.detail = DND.DROP_NONE;
					} catch (Exception e) {
						e.printStackTrace();
						event.detail = DND.DROP_NONE;
						return;
					}
				}
			});
		}

		sashForm.setWeights(new int[] { 30, 70 });
		sashForm.setSashWidth(1);
	}

	public void setupEventTree(ArrayList<ObservationType> oset,
			EventSet eventSet) {
		tree1.removeAll();

		for (int k = 0; k < eventSet.getEvent().size(); k++) {
			EventType event = eventSet.getEvent().get(k);

			TreeItem eventItem = new TreeItem(tree1, SWT.NONE);
			eventItem.setText(event.getEventId().getValue());
			eventItem
					.setImage(new Image(
							Display.getDefault(),
							this
									.getClass()
									.getClassLoader()
									.getResourceAsStream(
											"edu/harvard/i2b2/imageExplorer/ui/core-cell.gif")));

			// for(int i=0; i<oset.size(); i++) {
			// ObservationSet set = oset.get(i);
			for (int j = 0; j < oset.size(); j++) {
				ObservationType obs = oset.get(j);
				if (obs.getEventId().getValue().equalsIgnoreCase(
						event.getEventId().getValue())) {
					TreeItem item = new TreeItem(eventItem, SWT.NONE);
					item.setText(obs.getConceptCd().getValue());
					// obs.getTvalChar().substring(obs.getTvalChar().lastIndexOf(
					// "/")+1));
					item
							.setImage(new Image(
									Display.getDefault(),
									this
											.getClass()
											.getClassLoader()
											.getResourceAsStream(
													"edu/harvard/i2b2/imageExplorer/ui/core-cell.gif")));
					item.setData(new Integer(j + 1).toString());
					item.setData("Observation", obs);

					tree1.showItem(item);
				}
			}
			// }
		}
	}

	private CategoryDataset createDataset(ResultType umResultType,
			String description) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// row keys...
		String series1 = description;// umResultType.getName();//"age";
		// String series2 = "Second";

		for (int i = 0; i < umResultType.getData().size(); i++) {
			String category = umResultType.getData().get(i).getColumn();
			// if (UserInfoBean.getInstance().isRoleInProject("DATA_OBFSC"))
			// {
			// category+="±";
			// }
			String value = umResultType.getData().get(i).getValue();
			double num = Double.parseDouble(value);
			log.debug("category: " + category + "--- value: " + num);
			dataset.addValue(num, series1, category);
		}
		// dataset.addValue(0, series1, "test-0");

		return dataset;
	}

	private CategoryDataset createEmptyDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		return dataset;
	}

	@SuppressWarnings("unused")
	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// row keys...
		String series1 = "age";
		// String series2 = "Second";

		// column keys...
		String category1 = "0";
		String category2 = "10";
		String category3 = "20";
		String category4 = "30";
		String category5 = "40";
		String category6 = "50";
		String category7 = "60";
		String category8 = "70";
		String category9 = "80";

		dataset.addValue(10, series1, category1);
		dataset.addValue(120, series1, category2);
		dataset.addValue(250, series1, category3);
		dataset.addValue(300, series1, category4);
		dataset.addValue(478, series1, category5);
		dataset.addValue(90, series1, category6);
		dataset.addValue(150, series1, category7);
		dataset.addValue(70, series1, category8);
		dataset.addValue(60, series1, category9);

		return dataset;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            dataset.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(CategoryDataset dataset, String title) {

		JFreeChart chart = ChartFactory.createBarChart(title, // chart
				// title
				"", // domain axis label
				"", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		if (plot.getCategories().size() > 10) {
			plot.getDomainAxis().setCategoryLabelPositions(
					CategoryLabelPositions.DOWN_45);
		}
		Font f = chart.getTitle().getFont();
		chart.getTitle().setFont(new java.awt.Font(f.getFamily(), 1, 12));
		return chart;

	}

	private JFreeChart createEmptyChart(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("", // chart
				// title
				"", // domain axis label
				"", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		plot.getRangeAxis().setRangeWithMargins(0.0, 100.0);
		Font f = chart.getTitle().getFont();
		chart.getTitle().setFont(new java.awt.Font(f.getFamily(), 1, 12));
		return chart;

	}

	private JFreeChart createWaitingChart(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("Working .............", // chart
				// title
				"", // domain axis label
				"", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		Font f = chart.getTitle().getFont();
		chart.getTitle().setFont(new java.awt.Font(f.getFamily(), 1, 12));
		return chart;

	}

	private JFreeChart createNoDataChart(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("No results to display", // chart
				// title
				"", // domain axis label
				"", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		Font f = chart.getTitle().getFont();
		chart.getTitle().setFont(new java.awt.Font(f.getFamily(), 1, 12));

		return chart;

	}

	@SuppressWarnings("unchecked")
	public void insertNodes(final QueryInstanceData data) {
		// QueryInstanceData data = (QueryInstanceData) node.getUserObject();
		getDisplay().syncExec(new Runnable() {
			public void run() {
				// tree1.select(tree1.getItem(index).getItem(index));
				queryName = "Query Name: " + data.name();
				label1.setText("Query Name: " + data.name());

				JFreeChart chart = createWaitingChart(createEmptyDataset());
				ChartComposite frame = new ChartComposite(composite1, SWT.NONE,
						chart, true, true, false, true, true);
				composite1.getChildren()[0].dispose();
				frame.pack();
				composite1.layout();
				getDisplay().update();
			}
		});

		try {
			addParentNode(data);
			String xmlRequest = data.writeContentQueryXML();

			String xmlResponse = null;
			if (System.getProperty("webServiceMethod").equals("SOAP")) {
				xmlResponse = QueryClient.sendQueryRequestSOAP(xmlRequest);
			} else {
				xmlResponse = QueryClient.sendQueryRequestREST(xmlRequest);
			}
			if (xmlResponse.equalsIgnoreCase("CellDown")) {

				return;
			}

			JAXBUtil jaxbUtil = AnalysisJAXBUtil.getJAXBUtil();

			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(xmlResponse);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bt = messageType.getMessageBody();
			ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bt.getAny(), ResultResponseType.class);

			for (QueryResultInstanceType queryResultInstanceType : resultResponseType
					.getQueryResultInstance()) {
				@SuppressWarnings("unused")
				String status = queryResultInstanceType.getQueryStatusType()
						.getName();

				QueryResultData resultData = new QueryResultData();
				if (queryResultInstanceType.getQueryResultType().getName()
						.equalsIgnoreCase("PATIENTSET")) {
					resultData.visualAttribute("FA");
				} else {
					resultData.visualAttribute("LAO");
				}
				// resultData.queryId(data.queryId());
				resultData.patientRefId(queryResultInstanceType
						.getResultInstanceId());// data.patientRefId());
				resultData.patientCount(new Integer(queryResultInstanceType
						.getSetSize()).toString());// data.patientCount());
				String resultname = "";
				if ((resultname = queryResultInstanceType.getDescription()) == null) {
					resultname = queryResultInstanceType.getQueryResultType()
							.getDescription();
				}
				// if (status.equalsIgnoreCase("FINISHED")) {
				if ((!queryResultInstanceType.getQueryResultType()
						.getDisplayType().equalsIgnoreCase("CATNUM")) /*
																	 * ||
																	 * queryResultInstanceType
																	 * .
																	 * getQueryResultType
																	 * ().
																	 * getResultTypeId
																	 * ().
																	 * equalsIgnoreCase
																	 * ("4")
																	 */) {
					continue;
				}

				resultData.name(resultname);// + " - "
				// + resultData.patientCount() + " Patients");
				resultData.tooltip(resultData.patientCount() + " Patients");

				// } else {
				// resultData.name(resultname);// + " - " + status);
				// resultData.tooltip(status);

				// }
				resultData.xmlContent(xmlResponse);
				resultData.queryName(data.queryName());
				resultData.type(queryResultInstanceType.getQueryResultType()
						.getName());
				resultData.queryId(queryResultInstanceType
						.getResultInstanceId());
				addNode(resultData);
			}

			// jTree1.scrollPathToVisible(new TreePath(node.getPath()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addNode(final QueryResultData data) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				TreeItem treeItem1 = new TreeItem(treeItem, SWT.NONE);
				treeItem1.setText(data.name());
				treeItem1.setExpanded(true);
				treeItem1.setImage(SWTResourceManager
						.getImage("edu/harvard/i2b2/analysis/ui/leaf.jpg"));
				treeItem1.setData(data);
			}
		});

		// tree1.select(treeItem2);
	}

	private void addParentNode(final QueryInstanceData data) {

		getDisplay().syncExec(new Runnable() {
			public void run() {
				treeItem = new TreeItem(tree1, SWT.NONE);
				treeItem.setText(data.name());
				treeItem.setExpanded(true);
				treeItem
						.setImage(SWTResourceManager
								.getImage("edu/harvard/i2b2/analysis/ui/openFolder.jpg"));
				treeItem.setData(data);
			}
		});

		// tree1.select(treeItem2);
	}

	public void setSelection(final int index) {
		getDisplay().syncExec(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				tree1.select(tree1.getItem(index).getItem(0));

				JFreeChart chart = createWaitingChart(createEmptyDataset());
				ChartComposite frame = new ChartComposite(composite1, SWT.NONE,
						chart, true, true, false, true, true);
				composite1.getChildren()[0].dispose();
				frame.pack();
				composite1.layout();
				getDisplay().update();

				QueryResultData resultData = (QueryResultData) tree1.getItem(
						index).getItem(0).getData();
				String xmlDocumentRequestStr = resultData
						.writeXMLDocumentQueryXML();
				log.debug("Generated Age XML document request: "
						+ xmlDocumentRequestStr);

				String response = QueryClient
						.sendQueryRequestREST(xmlDocumentRequestStr);
				log.debug("Age XML document response: " + response);
				boolean celldown = false;
				if (response.equalsIgnoreCase("CellDown")) {
					// cellStatus = new String("CellDown");
					celldown = true;
					chart = createNoDataChart(createEmptyDataset());
					composite1.getChildren()[0].dispose();
					frame = new ChartComposite(composite1, SWT.NONE, chart,
							true, true, false, true, true);
					frame.pack();
					composite1.layout();
					return;
				}

				try {

					JAXBUtil jaxbUtil = AnalysisJAXBUtil.getJAXBUtil();

					JAXBElement jaxbElement = jaxbUtil
							.unMashallFromString(response);
					ResponseMessageType messageType = (ResponseMessageType) jaxbElement
							.getValue();
					BodyType bt = messageType.getMessageBody();
					CrcXmlResultResponseType resultResponseType = (CrcXmlResultResponseType) new JAXBUnWrapHelper()
							.getObjectByClass(bt.getAny(),
									CrcXmlResultResponseType.class);

					for (Condition status : resultResponseType.getStatus()
							.getCondition()) {
						if (status.getType().equals("ERROR")) {
							// cellStatus = new String("CellDown");
							chart = createNoDataChart(createEmptyDataset());
							composite1.getChildren()[0].dispose();
							frame = new ChartComposite(composite1, SWT.NONE,
									chart, true, true, false, true, true);
							frame.pack();
							composite1.layout();
							return;
						}
					}

					String xmlString = (String) resultResponseType
							.getCrcXmlResult().getXmlValue().getContent()
							.get(0);
					jaxbElement = jaxbUtil.unMashallFromString(xmlString);
					ResultEnvelopeType resultEnvelopeType1 = (ResultEnvelopeType) jaxbElement
							.getValue();
					JAXBUnWrapHelper helper = new JAXBUnWrapHelper();
					ResultType umResultType = (ResultType) helper
							.getObjectByClass(resultEnvelopeType1.getBody()
									.getAny(), ResultType.class);

					String description = "";
					if ((description = resultResponseType
							.getQueryResultInstance().getDescription()) == null) {
						description = resultResponseType
								.getQueryResultInstance().getQueryResultType()
								.getDescription();
					}
					// if
					// (UserInfoBean.getInstance().isRoleInProject("DATA_OBFSC"
					// ))
					// {
					// description+="±";
					// }
					chart = createChart(
							createDataset(umResultType, description),
							description);
					// final ChartPanel chartPanel = new ChartPanel(chart);

					composite1.getChildren()[0].dispose();
					frame = new ChartComposite(composite1, SWT.NONE, chart,
							true, true, false, true, true);
					frame.pack();
					composite1.layout();
				} catch (Exception e) {
					e.printStackTrace();
					chart = createNoDataChart(createEmptyDataset());
					composite1.getChildren()[0].dispose();
					frame = new ChartComposite(composite1, SWT.NONE, chart,
							true, true, false, true, true);
					frame.pack();
					composite1.layout();
					return;
				}
			}
		});
	}

	public void setSelection(final QueryResultData resultData) {
		getDisplay().syncExec(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				JFreeChart chart = createWaitingChart(createEmptyDataset());
				ChartComposite frame = new ChartComposite(composite1, SWT.NONE,
						chart, true, true, false, true, true);
				composite1.getChildren()[0].dispose();
				frame.pack();
				composite1.layout();
				getDisplay().update();

				// tree1.select(tree1.getItem(index).getItem(index));
				// QueryResultData resultData =
				// (QueryResultData)tree1.getItem(0).getItem(0).getData();
				String xmlDocumentRequestStr = resultData
						.writeXMLDocumentQueryXML();
				log.debug("Generated Age XML document request: "
						+ xmlDocumentRequestStr);
				// parentPanel.lastRequestMessage(
				// xmlDocumentRequestStr);
				String response = QueryClient
						.sendQueryRequestREST(xmlDocumentRequestStr);
				log.debug("Age XML document response: " + response);
				boolean celldown = false;
				if (response.equalsIgnoreCase("CellDown")) {
					// cellStatus = new String("CellDown");
					celldown = true;
					chart = createNoDataChart(createEmptyDataset());
					composite1.getChildren()[0].dispose();
					frame = new ChartComposite(composite1, SWT.NONE, chart,
							true, true, false, true, true);
					frame.pack();
					composite1.layout();
					return;
				}

				try {

					JAXBUtil jaxbUtil = AnalysisJAXBUtil.getJAXBUtil();

					JAXBElement jaxbElement = jaxbUtil
							.unMashallFromString(response);
					ResponseMessageType messageType = (ResponseMessageType) jaxbElement
							.getValue();
					BodyType bt = messageType.getMessageBody();

					CrcXmlResultResponseType resultResponseType = (CrcXmlResultResponseType) new JAXBUnWrapHelper()
							.getObjectByClass(bt.getAny(),
									CrcXmlResultResponseType.class);

					for (Condition status : resultResponseType.getStatus()
							.getCondition()) {
						if (status.getType().equals("ERROR")) {
							// cellStatus = new String("CellDown");
							chart = createNoDataChart(createEmptyDataset());
							composite1.getChildren()[0].dispose();
							frame = new ChartComposite(composite1, SWT.NONE,
									chart, true, true, false, true, true);
							frame.pack();
							composite1.layout();
							return;
						}
					}

					String xmlString = (String) resultResponseType
							.getCrcXmlResult().getXmlValue().getContent()
							.get(0);
					jaxbElement = jaxbUtil.unMashallFromString(xmlString);
					ResultEnvelopeType resultEnvelopeType1 = (ResultEnvelopeType) jaxbElement
							.getValue();
					JAXBUnWrapHelper helper = new JAXBUnWrapHelper();
					ResultType umResultType = (ResultType) helper
							.getObjectByClass(resultEnvelopeType1.getBody()
									.getAny(), ResultType.class);

					String description = "";
					if ((description = resultResponseType
							.getQueryResultInstance().getDescription()) == null) {
						description = resultResponseType
								.getQueryResultInstance().getQueryResultType()
								.getDescription();
					}
					// if
					// (UserInfoBean.getInstance().isRoleInProject("DATA_OBFSC"
					// ))
					// {
					// description+="±";
					// }
					chart = createChart(
							createDataset(umResultType, description),
							description);
					composite1.getChildren()[0].dispose();
					frame = new ChartComposite(composite1, SWT.NONE, chart,
							true, true, false, true, true);
					frame.pack();
					composite1.layout();
				} catch (Exception e) {
					e.printStackTrace();
					chart = createNoDataChart(createEmptyDataset());
					composite1.getChildren()[0].dispose();
					frame = new ChartComposite(composite1, SWT.NONE, chart,
							true, true, false, true, true);
					frame.pack();
					composite1.layout();
					return;
				}
			}
		});
	}

	public void clearTree() {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				tree1.removeAll();
				queryName = "Query Name: ";
				label1.setText("Query Name: ");

				JFreeChart chart = createEmptyChart(createEmptyDataset());
				composite1.getChildren()[0].dispose();
				ChartComposite frame = new ChartComposite(composite1, SWT.NONE,
						chart, true, true, false, true, true);
				frame.pack();
				composite1.layout();
			}
		});
	}

	public void addNode(QueryInstanceData node) {
		clearTree();
		insertNodes(node);

		getDisplay().syncExec(new Runnable() {
			public void run() {
				if (treeItem.getItemCount() == 0) {
					TreeItem treeItem1 = new TreeItem(treeItem, SWT.NONE);
					treeItem1.setText("No results to display");
					treeItem1.setForeground(getDisplay().getSystemColor(
							SWT.COLOR_RED));
					treeItem1.setExpanded(true);
					treeItem1.setImage(SWTResourceManager
							.getImage("edu/harvard/i2b2/analysis/ui/leaf.jpg"));

					JFreeChart chart = createNoDataChart(createEmptyDataset());
					composite1.getChildren()[0].dispose();
					ChartComposite frame = new ChartComposite(composite1,
							SWT.NONE, chart, true, true, false, true, true);
					frame.pack();
					composite1.layout();

					tree1.select(treeItem1);
					return;
				} else {
					setSelection(0);
				}
			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private void clearButtonWidgetSelected(SelectionEvent evt) {
		clearTree();
	}

}
