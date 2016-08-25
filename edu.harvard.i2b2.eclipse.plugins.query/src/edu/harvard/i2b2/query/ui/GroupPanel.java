/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.query.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jdom.Element;

import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.query.utils.XmlUtil;
import edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging.WorkplaceServiceDriver;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue; 
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.XmlValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FolderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FoldersType;
import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreePanelData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryMasterData;
import edu.harvard.i2b2.query.data.UnitsData;
import edu.harvard.i2b2.query.datavo.QueryJAXBUtil;

/*
 * GroupPanel.java
 *
 * Created on August 22, 2006, 12:45 PM
 */

public class GroupPanel extends javax.swing.JPanel implements
		TreeExpansionListener, TreeWillExpandListener, ActionListener {
	private static final Log log = LogFactory.getLog(GroupPanel.class);

	private DefaultMutableTreeNode top = null;
	private DefaultTreeModel treeModel = null;
	private MainPanel parentPanel = null;
	private QueryConceptTreePanelData panelData = null;

	public QueryConceptTreePanelData data() {
		return panelData;
	}

	private String groupname_ = null;

	public String getGroupName() {
		return groupname_;
	}

	private QueryConceptTreeNodeData currentData;

	public QueryConceptTreeNodeData currentData() {
		return currentData;
	}

	private boolean firsttime = true;
	private Border defaultBorder;
	private long lEventTime = 0;
	private GroupPanel thisPanel = null;

	/** Creates new form QueryConceptTreePanel */
	public GroupPanel(String groupname, MainPanel parent) {
		initComponents();
		jNameLabel.setText(groupname);
		createPopupMenu();
		thisPanel = this;
		parentPanel = parent;
		groupname_ = groupname;
		panelData = new QueryConceptTreePanelData();

		defaultBorder = jScrollPane1.getBorder();

		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentMoved(java.awt.event.ComponentEvent evt) {
				// formComponentMoved(evt);
			}

			public void componentResized(java.awt.event.ComponentEvent evt) {
				// formComponentResized(evt);
				// System.out.println("waiting panel resizing ...");

				int width = (int) (parentPanel.getWidth());
				int height = (int) (parentPanel.getHeight());
				if (width < 5 || height < 5) {
					return;
				}

				resizePanels(width, height);
				// log.info("width: "+width+", height: "+height);

				if (firsttime) {
					firsttime = false;
					resizePanels(width, height + 3);
					// log.info("second width: "+width+", height: "+(height+3));
				}
			}
		});
	}

	private void resizePanels(int width, int height) {
		int h = height - 130;
		if (h < 1) {
			h = 10;
		}
		jScrollPane1.setBounds(0, 60, 180, h - 50);
		// System.out.println("height: "+height);

		// jScrollPane1.setViewportView(jTree1);
		jHintLabel.setBounds(0, h + 12, 180, 26);
	}

	public ArrayList<QueryConceptTreeNodeData> getItems() {
		/*
		 * if(top.getChildCount() > 0) { ArrayList alist = new
		 * ArrayList<QueryConceptTreeNodeData>(); for(int i=0;
		 * i<top.getChildCount(); i++) { DefaultMutableTreeNode childNode =
		 * (DefaultMutableTreeNode) top.getChildAt(i); QueryConceptTreeNodeData
		 * nodeData = (QueryConceptTreeNodeData) childNode.getUserObject();
		 * alist.add(nodeData); } return alist; } return null;
		 */

		return panelData.getItems();
	}

	public static String nodeToString(Node node) {
		DOMImplementation impl = node.getOwnerDocument().getImplementation();
		DOMImplementationLS factory = (DOMImplementationLS) impl.getFeature(
				"LS", "3.0");
		LSSerializer serializer = factory.createLSSerializer();
		return serializer.writeToString(node);
	}

	public DefaultMutableTreeNode addNode(QueryConceptTreeNodeData node) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryConceptTreeNodeData tmpData = new QueryConceptTreeNodeData();
		tmpData.name("working ......");
		tmpData.tooltip("A tmp node");
		tmpData.visualAttribute("LAO");
		DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		treeModel.insertNodeInto(childNode, top, top.getChildCount());
		treeModel.insertNodeInto(tmpNode, childNode, childNode.getChildCount());
		// Make sure the user can see the lovely new node.
		jTree1.scrollPathToVisible(new TreePath(childNode.getPath()));

		return childNode;
	}

	public void setTimingCombo(int index) {
		jTimingComboBox.setSelectedIndex(index);
	}

	public void enableTimingCombo(boolean b) {
		jTimingComboBox.setEnabled(b);
	}
	
	public void setTimingComboText(String text) {
		DefaultComboBoxModel model = ((DefaultComboBoxModel)jTimingComboBox.getModel());
		//model.removeElementAt(1);
		if(text.equalsIgnoreCase("Items instance will be same")) {
			if(model.getSize() == 2) {
				model.insertElementAt(text, 2);
			}
		}
		else {
			if(model.getSize() == 3) {
				model.removeElementAt(2);
			}
		}
	}

	private DefaultMutableTreeNode addNode(QueryConceptTreeNodeData node,
			DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryConceptTreeNodeData tmpData = new QueryConceptTreeNodeData();
		tmpData.name("working ......");
		tmpData.tooltip("A tmp node");
		tmpData.visualAttribute("LAO");
		DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
		if (!(node.visualAttribute().startsWith("L") || node.visualAttribute()
				.equalsIgnoreCase("MA"))) {
			treeModel.insertNodeInto(tmpNode, childNode, childNode
					.getChildCount());
		}
		// Make sure the user can see the lovely new node.
		// jTree1.scrollPathToVisible(new TreePath(childNode.getPath()));

		return childNode;
	}

	class MyRenderer extends DefaultTreeCellRenderer {

		public MyRenderer() {

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryConceptTreeNodeData")) {
				QueryConceptTreeNodeData nodeInfo = (QueryConceptTreeNodeData) (node
						.getUserObject());
				String tooltip = nodeInfo.tooltip();

				// String tooltip =
				// ((QueryTreeNodeData)(((DefaultMutableTreeNode
				// )value).getUserObject())).tooltip();
				setToolTipText(tooltip);
				setIcon(getImageIcon(nodeInfo));
			} else {
				setToolTipText(null);
			}
			if (panelData.exclude()) {
				setBackgroundNonSelectionColor(Color.LIGHT_GRAY);
			} else {
				setBackgroundNonSelectionColor(Color.WHITE);
			}

			return this;
		}

		private ImageIcon getImageIcon(QueryConceptTreeNodeData data) {
			String key = "leaf";
			// System.out.println(data.visualAttribute());
			if (data.visualAttribute().substring(0, 1).equals("F")) {
				if ((data.visualAttribute().substring(1).equals("A"))
						|| (data.visualAttribute().substring(1).equals("I")))
					key = "closedFolder";
				else if ((data.visualAttribute().substring(1).equals("AO"))
						|| (data.visualAttribute().substring(1).equals("IO")))
					key = "openFolder";
			} else if (data.visualAttribute().substring(0, 1).equals("C")) {
				if ((data.visualAttribute().substring(1).equals("A"))
						|| (data.visualAttribute().substring(1).equals("I")))
					key = "closedCase";
				else if ((data.visualAttribute().substring(1).equals("AO"))
						|| (data.visualAttribute().substring(1).equals("IO")))
					key = "openCase";
			} else if (data.visualAttribute().substring(0, 1).equals("L")) {
				key = "leaf";
			} else if (data.visualAttribute().substring(0, 1).equals("M")) {
				key = "leaf";
			}
			else if (data.visualAttribute().equals("PQ")) {
				key = "prevQuery";// "multi";
			}
			else if (data.visualAttribute().equals("PT")) {
				key = "plainpeople";// "multi";
			}

			if (key.equals("multi")) {
				return createImageIcon(key + ".bmp");
			} else {
				return createImageIcon(key + ".jpg");
			}
		}
	}

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = MainPanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, "");
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	class QueryDataTransferable implements Transferable {
		public QueryDataTransferable(Object data) {
			super();
			this.data = data;
			flavors[0] = DataFlavor.stringFlavor;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			return data;
		}

		private Object data;
		private final DataFlavor[] flavors = new DataFlavor[1];
	}

	class GroupLabelTextHandler extends TransferHandler {
		public GroupLabelTextHandler() {
			super("text");
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavor) {
			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.YELLOW));
			jNameLabel.paintImmediately(jNameLabel.getVisibleRect());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.BLACK));

			if ((System.currentTimeMillis() - lEventTime) > 2000) {

				return true;
			}
			return false;
		}

		public boolean importData(JComponent comp, Transferable t) {

			try {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);

				if (!text.startsWith("<"))
					return false;

				try {

					edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();

					JAXBContext jc1 = JAXBContext
							.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory.class);

					Unmarshaller unMarshaller = jc1.createUnmarshaller();
					JAXBElement jaxbElement = (JAXBElement) unMarshaller
							.unmarshal(new StringReader(text));
					// QueryDefinitionType qftype =
					// (QueryDefinitionType)jaxbElement.getValue();
					DndType dnd = (DndType) jaxbElement.getValue();

					if (dnd.getAny().size() > 0) {
						org.w3c.dom.Element rootElement = (org.w3c.dom.Element) dnd
								.getAny().get(0);

						String name = nodeToString(rootElement);

						jc1 = JAXBContext
								.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);

						unMarshaller = jc1.createUnmarshaller();
						jaxbElement = (JAXBElement) unMarshaller
								.unmarshal(new StringReader(name));

						PanelType panel = (PanelType) jaxbElement.getValue();
						clearTree();

						// panelData.getItems()..removeAll(null);
						parentPanel.getRunQueryButton().requestFocus();
						panelData.setOccurrenceTimes(panel
								.getTotalItemOccurrences().getValue());

						// totalOccurrences.setValue(panelData.getOccurrenceTimes
						// ());

						for (int j = 0; j < panel.getItem().size(); j++) {
							ItemType itemType = panel.getItem().get(j);
							
							QueryConceptTreeNodeData node = null;
							ConstrainByModifier md = itemType.getConstrainByModifier();
							if(md != null) {
								node = new ModifierData();
								node.isModifier(true);
								((ModifierData)node).modifier_key(md.getModifierKey());
								((ModifierData)node).applied_path(md.getAppliedPath());
								((ModifierData)node).modifier_name(md.getModifierName());
								((ModifierData)node).setModifierValueConstraint(md.getConstrainByValue());
							}
							else {
								node = new QueryConceptTreeNodeData();
							}
							
							node.name(itemType.getItemName());
							node.titleName(itemType.getItemName());

							if (itemType.getItemIcon() != null) {
								node.visualAttribute(itemType.getItemIcon());
							} else {
								node.visualAttribute("LA");
							}

							node.tooltip(itemType.getTooltip());
							node.fullname(itemType.getItemKey());
							node.hlevel(new Integer(itemType.getHlevel())
									.toString());
							node.setValueConstraints(itemType.getConstrainByValue());
							node.fullname(itemType.getItemKey());
							//node.name(itemType.getItemName());
							node.tooltip(itemType.getTooltip());
							//node.visualAttribute(itemType.getItemIcon());
							node.hlevel(Integer.toString(itemType.getHlevel()));
							for (int u = 0; u < itemType.getConstrainByDate()
									.size(); u++) {
								panelData.writeTimeConstrain(itemType
										.getConstrainByDate().get(u)
										.getDateFrom(), itemType
										.getConstrainByDate().get(u)
										.getDateTo());
								// s setDateConstrainText("<html><u>Dates</u>");

							}
							if (panel.getInvert() == 0) {
								panelData.exclude(false);
								node.inverted(false);
							} else {
								panelData.exclude(true);
								node.inverted(true);
							}

							/*List list = getConceptsFromTerm(node, null);

							boolean hasValue = false;

							for (int i = 0; i < list.size(); i++) {
								ConceptType concept = (ConceptType) list.get(i);
								// node.name(concept.getName());
								node.titleName(concept.getName());
								// node.visualAttribute(concept.
								// getVisualattributes().trim());

								if ((concept.getMetadataxml() != null)
										&& (concept.getVisualattributes() != null)) {
									System.out.println("Has value");
									hasValue = true;
									SAXBuilder parser = new SAXBuilder();

									XmlValueType a = concept.getMetadataxml();
									org.w3c.dom.Element b = a.getAny().get(0);
									// NodeList c =
									// b.getElementsByTagName("ValueMetadata");

									String xmlContent = nodeToString(b);
									// Element b = a.getAny().get(0);

									// String xmlContent = b.toString();
									java.io.StringReader xmlStringReader = new java.io.StringReader(
											xmlContent);
									org.jdom.Document tableDoc = parser
											.build(xmlStringReader);
									// org.jdom.Element tableXml =
									// tableDoc.getRootElement
									// ().getChild("concepts",
									// Namespace.getNamespace
									// ("http://www.i2b2.org/xsd/cell/ont/1.1/"
									// ));

									// Element metadataAttribs =
									// tableDoc.getRootElement
									// ().getChild("metadataxml");
									// Element valuedataAttribs
									// =tableDoc.getRootElement
									// ().getChild("ValueMetadata");
									Element dataTypeElement = tableDoc
											.getRootElement().getChild(
													"DataType");
									if (dataTypeElement != null
											&& dataTypeElement.getTextTrim()
													.equalsIgnoreCase("Enum")) {
										// add text values to node data
										node.valuePropertyData().hasEnumValue(
												true);
										Element enumElement = tableDoc
												.getRootElement().getChild(
														"EnumValues");
										for (int ii = 0; ii < enumElement
												.getChildren().size(); ii++) {
											Element valElement = (Element) enumElement
													.getChildren().get(ii);
											String valString = new String(
													valElement.getTextTrim());
											node.valuePropertyData().enumValues
													.add(valString);
										}
										System.out
												.println("Got vals: "
														+ node
																.valuePropertyData().enumValues
																.size());
									}

									if (tableDoc.getRootElement().getChild(
											"Oktousevalues") != null
											&& tableDoc.getRootElement()
													.getChild("Oktousevalues")
													.getText()
													.equalsIgnoreCase("Y")) {
										node.valuePropertyData().okToUseValue(
												true);
									}

									if (tableDoc.getRootElement().getChild(
											"Flagstouse") == null
											|| tableDoc.getRootElement()
													.getChild("Flagstouse")
													.getText()
													.equalsIgnoreCase("")) {
										node.valuePropertyData()
												.okToUseValueFlag(false);
									}

									Element unitElement = tableDoc
											.getRootElement().getChild(
													"UnitValues");
									if (unitElement != null) {
										for (int ii = 0; ii < unitElement
												.getChildren().size(); ii++) {
											Element element = (Element) unitElement
													.getChildren().get(ii);
											if (element.getName()
													.equalsIgnoreCase(
															"NormalUnits")) {
												String unitString = new String(
														element.getTextTrim());
												UnitsData u = new UnitsData(unitString, 1, false);
												node.valuePropertyData().units
														.add(new UnitsData(unitString, 1, false));
											}
										}
										System.out
												.println("Got vals: "
														+ node
																.valuePropertyData().enumValues
																.size());
									}
								}
							}
							//node.hasValue(hasValue);

							//if (itemType.getConstrainByValue().size() > 0) {
							//	node.setValueConstraints(itemType
							//			.getConstrainByValue());

								if (itemType.getConstrainByValue().size() > 0) {
									node.setValueConstraints(itemType
											.getConstrainByValue());
									if (node.valuePropertyData().hasEnumValue()) {
										if (node.valuePropertyData()
												.useTextValue()) {
											ArrayList<String> results = new ArrayList<String>();
											String[] valueData = node
													.valuePropertyData()
													.value().split(",");
											for (String data : valueData)
												results.add(data);
											node.valuePropertyData().selectedValues = results;
										}

									}
								}
							//}
							// addNode(node);*/
							panelData.getItems().add(node);
							parentPanel.getRunQueryButton().requestFocus();

						}

					}
					/*
					 * 
					 * addNode(node); panelData.getItems().add(node);
					 * parentPanel.getRunQueryButton().requestFocus(); }
					 */
					redraw(panelData);
				} catch (Exception e) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(jNameLabel,
											"Please note, You can not drop this item here.");
						}
					});
				}
				// System.out.println(text);
			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(jNameLabel,
										"Please note, You can not drop this item here.");
					}
				});
			}

			return false;

		}

		public int getSourceActions(JComponent c) {
			// return TransferHandler.COPY_OR_MOVE;
			return TransferHandler.COPY;
		}

		protected Transferable createTransferable(JComponent c) {

			// Transferable t = null;
			String str = jNameLabel.getText();

			// t = new QueryDataTransferable(str);
			// return t;
			// return new StringSelection(str);

			Transferable t = null;
			t = new QueryDataTransferable(str);

			int count = jTree1.getComponentCount();

			// TRY
			// panelData.

			// QueryConceptTreePanel panel = getTreePanel(0);
			ArrayList<QueryConceptTreeNodeData> nodelist = panelData.getItems();
			// panel.data()
			// .getItems();
			if ((nodelist != null) && (nodelist.size() > 0)) {
				// System.out.println("Panel: "+panel.getGroupName()+" Excluded:
				// "+((panel.data().exclude())?"yes":"no"));
				PanelType panelType = new PanelType();
				panelType.setInvert((panelData.exclude()) ? 1 : 0);
				PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
				totalOccurrences.setValue(panelData.getOccurrenceTimes());
				panelType.setTotalItemOccurrences(totalOccurrences);
				panelType.setPanelNumber(0 + 1);
				// panelType.setName(panelData.getItems().get(0).name() + "_"
				// + generateMessageId().substring(0, 4));
				panelType.setName(str.replace(" ", "")); // + "_"
				// + generateMessageId().substring(0, 4));

				for (int j = 0; j < nodelist.size(); j++) {
					QueryConceptTreeNodeData node = nodelist.get(j);
					// System.out.println("\tItem: "+node.fullname());

					// create item
					ItemType itemType = new ItemType();

					itemType.setItemKey(node.fullname());
					//if(node.isModifier()) {
						//ModifierData mnode = (ModifierData) node;
						//itemType.setItemName(node.name() + " ["+mnode.modifier_name()+node.valueName()+"]");
					//}
					//else{
						itemType.setItemName(node.name());// + " ["+mnode.modifier_name()+node.valueName()+"]");
					//}
					// itemType.setItemTable(node.lookuptable());
					itemType.setTooltip(node.tooltip());
					itemType.setHlevel(Integer.parseInt(node.hlevel()));
					itemType.setClazz("ENC");
					itemType.setItemIcon(node.visualAttribute());

					// handle time constrain
					if (panelData.startTime() != -1
							|| panelData.endTime() != -1) {
						ConstrainByDate timeConstrain = panelData
								.writeTimeConstrain();
						itemType.getConstrainByDate().add(timeConstrain);
					}

					// handle value constrain
					if (!node.valuePropertyData().noValue()) {
						ConstrainByValue valueConstrain = node
								.valuePropertyData().writeValueConstrain();
						itemType.getConstrainByValue().add(valueConstrain);
					}
					
					// handle modifier
					if (node.isModifier()) {
						ConstrainByModifier modifierConstraint = ((ModifierData)node).writeModifierConstraint();
						itemType.setConstrainByModifier(modifierConstraint);
					}

					panelType.getItem().add(itemType);
				}
				QueryDefinitionType queryDefinitionType = new QueryDefinitionType();
				queryDefinitionType.getPanel().add(panelType);

				JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
				StringWriter strWriter = new StringWriter();

				try {

					DndType dnd = new DndType();

					edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();

					// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
					// psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.
					// ObjectFactory();
					dnd.getAny().add(psmOf.createPanel(panelType));

					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
					QueryJAXBUtil.getJAXBUtil().marshaller(
							of.createPluginDragDrop(dnd), strWriter);

					str = strWriter.toString();
					// int first = str.indexOf("<panel name");
					// int last = str.indexOf("</panel>") + 8;
					// str = str.substring(first, last);
					// str =
					// "<ns5:plugin_drag_drop xmlns:ns4=\"http://www.i2b2.org/xsd/cell/crc/psm/1.1/\" xmlns:ns7=\"http://www.i2b2.org/xsd/cell/ont/1.1/\" xmlns:ns3=\"http://www.i2b2.org/xsd/cell/crc/pdo/1.1/\" xmlns:ns5=\"http://www.i2b2.org/xsd/hive/plugin/\" xmlns:ns2=\"http://www.i2b2.org/xsd/hive/pdo/1.1/\" xmlns:ns6=\"http://www.i2b2.org/xsd/hive/msg/1.1/\" xmlns:ns8=\"http://www.i2b2.org/xsd/cell/crc/psm/querydefinition/1.1/\">"
					// + str;
					// str += "</ns5:plugin_drag_drop>";
					log.debug("panel xml: " + str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// END TRY

			/*
			 * DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
			 * .getSelectionPath().getLastPathComponent();
			 * 
			 * //String str = null; if
			 * (node.getUserObject().getClass().getSimpleName()
			 * .equalsIgnoreCase("QueryConceptTreeNodeData")) { StringWriter
			 * strWriter = new StringWriter(); try { // JAXBUtil jaxbUtil =
			 * PreviousQueryJAXBUtil.getJAXBUtil();
			 * 
			 * QueryConceptTreeNodeData ndata = (QueryConceptTreeNodeData) node
			 * .getUserObject();
			 * 
			 * ConceptsType conceptsType = new ConceptsType(); ConceptType
			 * conceptType = new ConceptType();
			 * conceptType.setLevel(Integer.valueOf(ndata.hlevel()));
			 * conceptType.setKey(ndata.dimcode());
			 * conceptType.setName(ndata.name());
			 * //conceptType.setSynonymCd(value)
			 * conceptType.setVisualattributes(ndata.visualAttribute());
			 * //conceptType.setTotalnum(value)
			 * conceptType.setColumnname(ndata.columnName());
			 * conceptType.setFacttablecolumn(ndata.factTableColumn()); //NULL
			 * conceptType.setTablename(ndata.tableName());
			 * conceptType.setColumndatatype(ndata.columnDataType());
			 * conceptType.setOperator(ndata.operator());
			 * conceptType.setDimcode(ndata.dimcode());
			 * conceptType.setTooltip(ndata.tooltip());
			 * 
			 * 
			 * 
			 * conceptsType.getConcept().add(conceptType);
			 * 
			 * 
			 * DndType dnd = new DndType();
			 * 
			 * edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
			 * 
			 * //edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
			 * psmOf = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			 * dnd.getAny().add(vdoOf.createConcepts(conceptsType));
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
			 * QueryJAXBUtil.getJAXBUtil().marshaller(
			 * of.createPluginDragDrop(dnd), strWriter);
			 * 
			 * } catch (Exception e1) { //
			 * log.error("Error marshalling Ont drag text"); // throw e;
			 * e1.printStackTrace(); }
			 * 
			 * str = strWriter.toString();
			 * System.out.println("Node xml set to: " + strWriter.toString());
			 * 
			 * }
			 */
			t = new QueryDataTransferable(str);
			return t;

		}
	}

	class TextHandler extends TransferHandler {
		public TextHandler() {
			super("text");
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavor) {
			// Accepts all drops

			return true;
		}

		protected Transferable createTransferable(JComponent c) {

			Transferable t = null;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();

			String str = null;
			if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryConceptTreeNodeData") ||
					node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("ModifierData")) {
				// StringWriter strWriter = new StringWriter();

				QueryConceptTreeNodeData ndata = (QueryConceptTreeNodeData) node
						.getUserObject();

				str = ndata.originalXml;
				/*
				 * 
				 * try { // JAXBUtil jaxbUtil =
				 * PreviousQueryJAXBUtil.getJAXBUtil();
				 * 
				 * QueryConceptTreeNodeData ndata = (QueryConceptTreeNodeData)
				 * node .getUserObject();
				 * 
				 * str = ndata.originalXml; ConceptsType conceptsType = new
				 * ConceptsType(); ConceptType conceptType = new ConceptType();
				 * conceptType.setLevel(Integer.valueOf(ndata.hlevel()));
				 * conceptType.setKey(ndata.dimcode());
				 * conceptType.setName(ndata.name());
				 * //conceptType.setSynonymCd(value)
				 * conceptType.setVisualattributes(ndata.visualAttribute());
				 * //conceptType.setTotalnum(value)
				 * conceptType.setColumnname(ndata.columnName());
				 * conceptType.setFacttablecolumn(ndata.factTableColumn());
				 * //NULL conceptType.setTablename(ndata.tableName());
				 * conceptType.setColumndatatype(ndata.columnDataType());
				 * conceptType.setOperator(ndata.operator());
				 * conceptType.setDimcode(ndata.dimcode());
				 * conceptType.setTooltip(ndata.tooltip());
				 * 
				 * 
				 * 
				 * conceptsType.getConcept().add(conceptType);
				 * 
				 * DndType dnd = new DndType();
				 * 
				 * edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf =
				 * new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
				 * 
				 * //edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
				 * psmOf = new
				 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				 * dnd.getAny().add(vdoOf.createConcepts(conceptsType));
				 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new
				 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				 * QueryJAXBUtil.getJAXBUtil().marshaller(
				 * of.createPluginDragDrop(dnd), strWriter);
				 * 
				 * } catch (Exception e1) { //
				 * log.error("Error marshalling Ont drag text"); // throw e;
				 * e1.printStackTrace(); }
				 * 
				 * str = strWriter.toString();
				 * System.out.println("Node xml set to: " +
				 * strWriter.toString());
				 */

			}
			t = new QueryDataTransferable(str);
			return t;
			// return new StringSelection(str);
		}

		public int getSourceActions(JComponent c) {
			// return TransferHandler.COPY_OR_MOVE;
			return TransferHandler.COPY;
		}

		public boolean importData(JComponent comp, Transferable t) {
			try {
				
					//}
				//});
				
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				//IWorkbench wb = PlatformUI.getWorkbench();
				//IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				//IWorkbenchPage page = win.getActivePage();
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						Display display = ((QueryToolInvestigatorPanel) parentPanel.parentPanel).parentview
						.getViewSite().getShell().getDisplay();
						display.syncExec(new Runnable(){
							public void run() {
								IWorkbenchPage page = ((QueryToolInvestigatorPanel) parentPanel.parentPanel).parentview
								.getViewSite().getPage();
								IWorkbenchPart part = page.getActivePart();
								IWorkbenchPartSite site = part.getSite();
								IViewSite vSite = (IViewSite) site;
								IActionBars actionBars = vSite.getActionBars();
								if(actionBars != null) {
									
									IStatusLineManager statuslineManager = actionBars.getStatusLineManager();
									//statuslineManager.removeAll();
									((StatusLineContributionItem)statuslineManager.find("Status")).setText("Retriving information on this item ...");
									//statuslineManager.setMessage("\t\t\t\tRetriving information on this item ...");
									statuslineManager.update(true);
									//statuslineManager.setErrorMessage("error Retriving information on this item ...");
								}
							}
						});
					}
				});
				
				
				final String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				String description = null;
				String id = null;

				try {    
					SAXBuilder parser = new SAXBuilder();
					String xmlContent = text;
					java.io.StringReader xmlStringReader = new java.io.StringReader(
							xmlContent);
					final org.jdom.Document tableDoc = parser.build(xmlStringReader);
					org.jdom.Element tableXml1 = null;
					for (int i = 0; i < tableDoc.getRootElement().getContent()
							.size(); i++) {
						if (tableDoc.getRootElement().getContent().get(i)
								.getClass().getSimpleName().equalsIgnoreCase(
										"Element")) {
							tableXml1 = (org.jdom.Element) tableDoc
									.getRootElement().getContent().get(i);
							break;
						}
					}
					
					/////////////////////////////////////////
					if (tableXml1.getName().equalsIgnoreCase(
					"folders")) {
				JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
				FoldersType folders = null;
				FolderType folder = null;
				try {
					JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(text);
					DndType dndType = (DndType)jaxbElement.getValue();     
					folders = (FoldersType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),
							FoldersType.class);
					folder = folders.getFolder().get(0);
				} catch (JAXBUtilException e) {
					log.error("Unwrap error: " + e.getMessage(), e);
					return true;
				} 
				
				try {
					edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType parentType = new edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType();
					parentType.setBlob(true);
					parentType.setParent(folder.getIndex());///*"\\\\" + this.getData().getTableCd() + "\\" + */this.getData().getIndex());	

					//		log.info(parentType.getParent());
					//		log.info(this.getData().getHierarchy());

					edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging.GetChildrenResponseMessage msg 
						= new edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging.GetChildrenResponseMessage();
					StatusType procStatus = null;	
					while(procStatus == null || !procStatus.getType().equals("DONE")){
						String response = WorkplaceServiceDriver.getChildren(parentType);

						procStatus = msg.processResult(response);
						if(procStatus.getValue().equals("MAX_EXCEEDED")) {
							/*theDisplay.syncExec(new Runnable() {
								public void run() {
									MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), 
											SWT.ICON_QUESTION | SWT.YES | SWT.NO);
									mBox.setText("Please Note ...");
									mBox.setMessage("The node has exceeded maximum number of children\n"
											+ "Populating the node will be slow\n"
											+"Do you want to continue?");
									result = mBox.open();
								}
							});
							if(result == SWT.NO) {
								TreeNode node = (TreeNode) this.getChildren().get(0);
								node.getData().setName("Over maximum number of child nodes");
								procStatus.setType("DONE");
							}
							else {
								parentType.setMax(null);
								response = WorkplaceServiceDriver.getChildren(parentType);
								procStatus = msg.processResult(response);
							}*/
						}
						//				else if  other error codes
						//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
						else if (procStatus.getType().equals("ERROR")){		
							System.setProperty("errorMessage",  procStatus.getValue());				
							/*theDisplay.syncExec(new Runnable() {
								public void run() {
									MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
									mBox.setText("Please Note ...");
									mBox.setMessage("Server reports: " +  System.getProperty("errorMessage"));
									int result = mBox.open();
								}
							});*/
							return true;
						}			
					}
					FoldersType allFolders = msg.doReadFolders();   	  
					if (allFolders != null){
						List<FolderType> folders1 = allFolders.getFolder();
						for(int i=0; i<folders1.size(); i++) {						
							//addNode(qData);
							//getChildren().clear();
							//getNodesFromXMLString(folders);
							//org.w3c.dom.Element dndElement = folders1.get(0).getWorkXml().getAny().get(0);
							//dndElement.getChildNodes();
							id = XmlUtil.getPatientId(folders1.get(i).getWorkXml());
							if(id == null) {
								continue;
							}
							QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
							String source = XmlUtil.getSiteId(folders1.get(i).getWorkXml());
							if(source == null) {
								source = new String("HIVE");
							}
							id = XmlUtil.getPatientId(folders1.get(i).getWorkXml());
							node.fullname("PATIENT:"+source+":" + id);
							node.name("PATIENT "+source+":" + id);
							log.info("PATIENT:"+source+":" + id);
							node.originalXml(folders1.get(i).getWorkXml().toString());
							node.visualAttribute("PT");

							addNode(node);
							panelData.getItems().add(node);							
						}
					}
					parentPanel.getRunQueryButton().requestFocus();
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return true;
				} catch (AxisFault e) {
					log.error(e.getMessage());
					/*theDisplay.syncExec(new Runnable() {
						public void run() {
							// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Unable to make a connection to the remote server\n" +  
									"This is often a network error, please try again");
							int result = mBox.open();
						}
					});*/
					return true;
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
					/*theDisplay.syncExec(new Runnable() {
						public void run() {
							// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Error message delivered from the remote server\n" +  
									"You may wish to retry your last action");
							int result = mBox.open();
						}
					});	*/		
					return true;
				}
					}
				
					if (tableXml1.getName().equalsIgnoreCase(
							"query_result_instance")) {
						List children = tableXml1.getChildren();
						QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
						String resultTypeDescription = "";
						for (Iterator itr = children.iterator(); itr.hasNext();) {
							Element element = (org.jdom.Element) itr.next();

							if (element.getName().equalsIgnoreCase(
									"result_instance_id")) {
								id = element.getText().trim();
								node.fullname("patient_set_coll_id:" + id);
								System.out.println("key: " + id);
							} else if (element.getName().equalsIgnoreCase(
									"description")) {
								description = element.getText().trim();
								node.name(description);
								node.tooltip(description);
								System.out.println("Description: "
										+ description);

							}
							else if (element.getName().equalsIgnoreCase(
							"query_result_type")) {
								resultTypeDescription = element.getChildTextTrim("description");
								//resultType = element;
							}
						}
						node.originalXml(text);
						node.visualAttribute("L");
						if (description!=null && description.indexOf("Encounter Set") >= 0) {
							node.fullname("patient_set_enc_id:" + id);
						}
						
						if(description==null) {
							node.name(resultTypeDescription);
						}

						addNode(node);
						panelData.getItems().add(node);
						parentPanel.getRunQueryButton().requestFocus();
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								Display display = ((QueryToolInvestigatorPanel) parentPanel.parentPanel).parentview
								.getViewSite().getShell().getDisplay();
								display.syncExec(new Runnable(){
									public void run() {
										IWorkbenchPage page = ((QueryToolInvestigatorPanel) parentPanel.parentPanel).parentview
										.getViewSite().getPage();
										IWorkbenchPart part = page.getActivePart();
										IWorkbenchPartSite site = part.getSite();
										IViewSite vSite = (IViewSite) site;
										IActionBars actionBars = vSite.getActionBars();
										if(actionBars != null) {
											
											IStatusLineManager statuslineManager = actionBars.getStatusLineManager();
											//statuslineManager.removeAll();
											((StatusLineContributionItem)statuslineManager.find("Status")).setText("..");
											//statuslineManager.setMessage("\t\t\t\tRetriving information on this item ...");
											statuslineManager.update(true);
											//statuslineManager.setErrorMessage("error Retriving information on this item ...");
										}
									}
								});
							}
						});
						return true;
					} else if (tableXml1.getName().equalsIgnoreCase(
							"patient_set")) {
						List children = tableXml1.getChildren();
						QueryConceptTreeNodeData node; //= new QueryConceptTreeNodeData();
						for (Iterator itr = children.iterator(); itr.hasNext();) {
							Element element = (org.jdom.Element) itr.next();

							if (element.getName().equalsIgnoreCase(
									"patient")) {
								children = element.getChildren();
								node = new QueryConceptTreeNodeData();
								for (Iterator itr1 = children.iterator(); itr1.hasNext();) {
									Element element1 = (org.jdom.Element) itr1.next();

									if (element1.getName().equalsIgnoreCase(
											"patient_id")) {
										id = element1.getText().trim();
										String source = element1.getAttributeValue("source");
										if(source == null) {
											source = "HIVE";
										}
										node.fullname("PATIENT:"+source+":" + id);
										node.name("PATIENT "+source+":" + id);
										System.out.println("key: " + source+":" + id);
									} 

								}
								node.originalXml(text);
								node.visualAttribute("PT");

								addNode(node);
								panelData.getItems().add(node);
							} 
						}
						
						parentPanel.getRunQueryButton().requestFocus();
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return true;
					}
					else if (tableXml1.getName().equalsIgnoreCase("query_master")) {
						List children = tableXml1.getChildren();
						QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
						for (Iterator itr = children.iterator(); itr.hasNext();) {
							Element element = (org.jdom.Element) itr.next();
		
							if (element.getName().equalsIgnoreCase(
									"query_master_id")) {
								id = element.getText().trim();
								node.fullname("masterid:" + id);
								System.out.println("key: " + id);
							} else if (element.getName().equalsIgnoreCase(
									"name")) {
								description = element.getText().trim();
								node.name("(PrevQuery)" + description);
								node.tooltip(description);
								System.out.println("name: " + description);		
							}
						}
						node.originalXml(text);
						node.visualAttribute("PQ");
		
						addNode(node);
						panelData.getItems().add(node);
						parentPanel.getRunQueryButton().requestFocus();
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return true;
					}

					org.jdom.Element tableXml = tableDoc
							.getRootElement()
							.getChild(
									"concepts",
									Namespace
											.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));
					
							
					List conceptChildren = tableXml.getChildren();
					for (Iterator itr = conceptChildren.iterator(); itr
							.hasNext();) {
						
						Element conceptXml = (org.jdom.Element) itr.next();
						String conceptText = conceptXml.getText().trim();
						if (conceptText.equals("null")) // this is root level
						// node
						{
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(jTree1,
													"Please note, You can not drop this item here.");
								}
							});
							return true;
						}
						

						final Element conTableXml = conceptXml;
						org.jdom.Element nameXml = conTableXml.getChild("modifier");
						
						QueryConceptTreeNodeData node = null;
						if(nameXml == null) {
						  node = new QueryConceptTreeNodeData();
						}
						else {
							node = new ModifierData();
							node.isModifier(true);
							
							org.jdom.Element modifierXml = nameXml.getChild("applied_path");
							String applied_path = modifierXml.getText();
							((ModifierData)node).applied_path(applied_path);
							
							modifierXml = nameXml.getChild("key");
							String key = modifierXml.getText();
							((ModifierData)node).modifier_key(key);
							
							modifierXml = nameXml.getChild("name");
							String name = modifierXml.getText();
							((ModifierData)node).modifier_name(name);
							
							//((ModifierData)node).updateModifierMetaDataXML();
							//parentPanel.enableSameVisit(true);
						}
						nameXml = conTableXml.getChild("name");
						String c_name = nameXml.getText();
						//nameXml = conTableXml.getChild("dimcode");
						//String c_dimcode = nameXml.getText();
						//nameXml = conTableXml.getChild("operator");
						//String c_operator = nameXml.getText();
						//nameXml = conTableXml.getChild("columndatatype");
						//String c_columndatatype = nameXml.getText();
						//nameXml = conTableXml.getChild("columnname");
						//String c_columnname = nameXml.getText();
						//nameXml = conTableXml.getChild("tablename");
						//String c_table = nameXml.getText();
						nameXml = conTableXml.getChild("tooltip");
						String c_tooltip = "";
						if(nameXml != null) {
							c_tooltip = nameXml.getText();
						}
						nameXml = conTableXml.getChild("visualattributes");
						String c_visual = nameXml.getText();
						nameXml = conTableXml.getChild("level");
						String hlevel = nameXml.getText();
						nameXml = conTableXml.getChild("key");
						String rawfullname = nameXml.getText();
						rawfullname.indexOf("\\\\");
						String fullname = rawfullname;// .substring(rawfullname.
						// indexOf("\\", 2));

						//nameXml = conTableXml.getChild("facttablecolumn");
						//String c_facttablecolumn = nameXml.getText();

						//node.factTableColumn(c_facttablecolumn);

						//if(node.isModifier()) {							
						//	node.name(c_name+" ["+((ModifierData)node).modifier_name()+"]");
						//}
						//else {
							node.name(c_name);
						//}
						node.titleName(c_name);
						node.visualAttribute(c_visual);
						node.tooltip(c_tooltip);
						node.hlevel(hlevel);
						node.fullname(fullname);
						//node.dimcode(c_dimcode);
						////node.hasValue(hasValue);
						// xml content is conceptText; not text
						// this accounts for drags of multiple concepts
						node.xmlContent(conceptText);
						
						//QueryConceptTreeNodeData fnode2 = new QueryConceptTreeNodeData();
						//fnode2.name("testing");
						//fnode2.visualAttribute("FA");
						//java.awt.EventQueue.invokeLater(new Runnable() {
							//public void run() {
								final DefaultMutableTreeNode node2 = addNode(node);
								revalidate();
						if(node.isModifier()) {
							((ModifierData)node).updateModifierMetaDataXML();
						}
							
						Element visualAttribs = conTableXml
								.getChild("visualattributes");
						String sVisualAttribs = visualAttribs.getText().trim();
						if (sVisualAttribs.toUpperCase().startsWith("C")) {
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													jTree1,
													"You can not use this item in a query, "
															+ "it is only used for organizing the lists.");
								}
							});
							return true;
						}

						Element metadataAttribs = conTableXml
								.getChild("metadataxml");
						Element valuedataAttribs = null;
						if (metadataAttribs != null) {
							valuedataAttribs = metadataAttribs
									.getChild("ValueMetadata");
						}
						
						
						boolean hasValue = false;
						if ((metadataAttribs != null)
								&& (valuedataAttribs != null)) {
							System.out.println("Has value");
							hasValue = true;
							Element dataTypeElement = valuedataAttribs
									.getChild("DataType");
							if (dataTypeElement != null
									&& dataTypeElement.getTextTrim()
											.equalsIgnoreCase("Enum")) {
								// add text values to node data
								node.valuePropertyData().hasEnumValue(true);
								Element enumElement = valuedataAttribs
										.getChild("EnumValues");
								for (int i = 0; i < enumElement.getChildren()
										.size(); i++) {
									Element valElement = (Element) enumElement
											.getChildren().get(i);
									String valString = new String(valElement
											.getTextTrim());
									node.valuePropertyData().enumValues
											.add(valString);
								}
								System.out.println("Got vals: "
										+ node.valuePropertyData().enumValues
												.size());
							}

							if (valuedataAttribs.getChild("Oktousevalues") != null
									&& valuedataAttribs.getChild(
											"Oktousevalues").getText()
											.equalsIgnoreCase("Y")) {
								node.valuePropertyData().okToUseValue(true);
							}

							if (valuedataAttribs.getChild("Flagstouse") == null
									|| valuedataAttribs.getChild("Flagstouse")
											.getText().equalsIgnoreCase("")) {
								node.valuePropertyData()
										.okToUseValueFlag(false);
							}

							Element unitElement = valuedataAttribs
									.getChild("UnitValues");
							if (unitElement != null) {
								for (int i = 0; i < unitElement.getChildren()
										.size(); i++) {
									Element element = (Element) unitElement
											.getChildren().get(i);
									if (element.getName().equalsIgnoreCase(
											"NormalUnits")) {
										String unitString = new String(element
												.getTextTrim());
										node.valuePropertyData().units
												.add(new UnitsData(unitString, 1, false));
									}
								}
								System.out.println("Got vals: "
										+ node.valuePropertyData().enumValues
												.size());
							}
						}
						//else {
							//node.updateNodeMetaDataXML();
						//}

						
						//node.columnName(c_columnname);
						//node.tableName(c_table);
						//node.columnDataType(c_columndatatype);
						//node.operator(c_operator);
						// System.out.println("nodes xml content: "+node.
						// xmlContent());
						node.originalXml(text);		
						final QueryConceptTreeNodeData fnode = node;
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								//DefaultMutableTreeNode node1 = addNode(fnode);
								//revalidate();
								fnode.updateNodeMetaDataXML();
								
								//node1.setUserObject(fnode);
								panelData.getItems().add(fnode);
								parentPanel.getRunQueryButton().requestFocus();
								currentData = fnode;
								Display display = ((QueryToolInvestigatorPanel) parentPanel.parentPanel).parentview
								.getViewSite().getShell().getDisplay();
								display.syncExec(new Runnable(){
									public void run() {
										IWorkbenchPage page = ((QueryToolInvestigatorPanel) parentPanel.parentPanel).parentview
										.getViewSite().getPage();
										IWorkbenchPart part = page.getActivePart();
										IWorkbenchPartSite site = part.getSite();
										IViewSite vSite = (IViewSite) site;
										IActionBars actionBars = vSite.getActionBars();
										if(actionBars != null) {
											
											IStatusLineManager statuslineManager = actionBars.getStatusLineManager();
											//statuslineManager.removeAll();
											statuslineManager.setMessage(null);
											((StatusLineContributionItem)statuslineManager.find("Status")).setText("");
											//statuslineManager.setMessage("\t\t\t\tRetriving information on this item ...");
											statuslineManager.update(true);
											//statuslineManager.setErrorMessage("error Retriving information on this item ...");
										}
									}
								});
								
								node2.setUserObject(fnode);	
								// Make sure the user can see the lovely new node.
								jTree1.scrollPathToVisible(new TreePath(node2.getPath()));
								parentPanel.repaint();
								
								if(fnode.isModifier()) {
									parentPanel.enableSameInstanceVisit(true);
									parentPanel.repaint();

									ModifierData ndata = (ModifierData) fnode;
									if(ndata.hasModifierValue()) {
										if (ndata.modifierValuePropertyData().hasEnumValue()) {
											ModifierEnumValueConstraintFrame vDialog = new ModifierEnumValueConstraintFrame(
													thisPanel);
											vDialog.setSize(410, 330);
											vDialog.setLocation(300, 300);
											vDialog
											.setTitle("Choose modifier value of "
													+ ndata.titleName());
											vDialog.setVisible(true);
										} else if (ndata.modifierValuePropertyData().hasStringValue()) {
											if(ndata.hasModifierHelp()){
												ModifierStringValueHelpConstraintFrame vDialog = new ModifierStringValueHelpConstraintFrame(
														thisPanel);
												vDialog.setSize(410, 250);
												vDialog.setLocation(300, 300);
												vDialog
												.setTitle("Choose modifier value of "
														+ ndata.titleName());
												vDialog.setVisible(true);

											}
											else {
												ModifierStringValueConstraintFrame vDialog = new ModifierStringValueConstraintFrame(
														thisPanel);

												vDialog.setSize(410, 250);
												vDialog.setLocation(300, 300);
												vDialog
												.setTitle("Choose modifier value of "
														+ ndata.titleName());
												vDialog.setVisible(true);
											}
										} else {
											if(ndata.hasModifierHelp()){
												ModifierNumericValueHelpConstraintFrame vDialog = new ModifierNumericValueHelpConstraintFrame(
														thisPanel);
												vDialog.setSize(450, 250);
												vDialog.setLocation(300, 300);
												vDialog
												.setTitle("Choose modifier value of "
														+ ndata.titleName());
												vDialog.setVisible(true);
											}
											else {
												ModifierNumericValueConstraintFrame vDialog = new ModifierNumericValueConstraintFrame(
														thisPanel);

												vDialog.setSize(410, 215);
												vDialog.setLocation(300, 300);
												vDialog
												.setTitle("Choose modifier value of "
														+ ndata.titleName());
												vDialog.setVisible(true);
											}
										}
									}
								}
						
						if(fnode.hasValue()) {
	 						if (fnode.valuePropertyData().hasEnumValue()) {
								EnumValueConstraintFrame vDialog = new EnumValueConstraintFrame(
										thisPanel);
								vDialog.setSize(410, 330);
								vDialog.setLocation(300, 300);
								vDialog
										.setTitle("Choose value of "
												+ fnode.titleName());
								vDialog.setVisible(true);
							} else if (fnode.valuePropertyData().hasStringValue()) {
								StringValueConstraintFrame vDialog = new StringValueConstraintFrame(
										thisPanel);
								vDialog.setSize(410, 250);
								vDialog.setLocation(300, 300);
								vDialog
										.setTitle("Search within the "
												+ fnode.titleName());
								vDialog.setVisible(true);
							}else {
								NumericValueConstraintFrame vDialog = new NumericValueConstraintFrame(
										thisPanel);
								vDialog.setSize(410, 215);
								vDialog.setLocation(300, 300);
								vDialog
										.setTitle("Choose value of "
												+ fnode.titleName());
								vDialog.setVisible(true);
							}
							}
						
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
							
						});
					}
					//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					jHintLabel
							.setText("<html><center>The terms of this group are joined <br>"
									+ "<left>then intersected with other groups");
				} catch (Exception e) {
					e.printStackTrace();
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(jNameLabel,
											"Please note, You can not drop this item here.");
						}
					});
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(jNameLabel,
										"Please note, You can not drop this item here.");
					}
				});
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				return true;
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return true;
		}
	}

	public void redraw(final QueryConceptTreePanelData data) {
		panelData = data;

		String str = "Occurs > " + (data.getOccurrenceTimes() - 1) + "x";
		if (data.getOccurrenceTimes() == 1) {
			setOccurrenceText(str);
		} else if (data.getOccurrenceTimes() > 1) {
			setOccurrenceText("<html><u>" + str + "</u>");
		}

		if (data.exclude()) {
			jExcludeButton.setText("<html><center><u>Exclude</u>");
			jTree1.setBackground(Color.LIGHT_GRAY);
			jExcludeButton.setToolTipText("Include all items in panel");
		} else {
			jExcludeButton.setText("Exclude");
			jTree1.setBackground(Color.WHITE);
			jExcludeButton.setToolTipText("Exclude all items in panel");
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (data.timing().equalsIgnoreCase("ANY")) {
					jTimingComboBox.setSelectedIndex(0);
				} else {
					jTimingComboBox.setSelectedIndex(1);
				}
				
				if(data.hasModifier) {
					parentPanel.enableSameInstanceVisit(true);
				}
			}
		});

		if (data.startDay() != -1 || data.endDay() != -1) {
			setDateConstrainText("<html><u>Dates</u>");
		} else {
			setDateConstrainText("Dates");
		}

		for (int i = 0; i < data.getItems().size(); i++) {
			QueryConceptTreeNodeData node = data.getItems().get(i);
			addNode(node);
		}
	}

	public void reset() {
		clearTree();
		jTree1.setBackground(Color.WHITE);
		panelData.exclude(false);
		jExcludeButton.setText("Exclude");
		jExcludeButton.setToolTipText("Exclude all items in panel");

		panelData.startTime(-1);
		panelData.startDay(-1);
		panelData.startMonth(-1);
		panelData.startYear(-1);

		panelData.endTime(-1);
		panelData.endDay(-1);
		panelData.endMonth(-1);
		panelData.endYear(-1);
		jConstrainButton.setText("Dates");

		panelData.setOccurrenceTimes(1);
		setOccurrenceText("Occurs > 0x");

		panelData.getItems().clear();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		jClearButton = new javax.swing.JButton();
		jConstrainButton = new javax.swing.JButton();
		jExcludeButton = new javax.swing.JButton();
		jOccurrenceButton = new javax.swing.JButton();
		jNameLabel = new javax.swing.JLabel();
		jHintLabel = new javax.swing.JLabel();
		jTimingComboBox = new javax.swing.JComboBox();
		// jAnyvisitToggleButton = new javax.swing.JToggleButton();
		// jSamevisitToggleButton = new javax.swing.JToggleButton();

		setLayout(null);

		QueryConceptTreeNodeData tmpData = new QueryConceptTreeNodeData();
		tmpData.name("working ......");
		tmpData.tooltip("A root node");
		tmpData.visualAttribute("FAO");
		top = new DefaultMutableTreeNode(tmpData);
		// top = new DefaultMutableTreeNode("Root Node");
		treeModel = new DefaultTreeModel(top);
		// treeModel.addTreeModelListener(new MyTreeModelListener());

		jTree1 = new JTree(treeModel);

		jTree1.setDragEnabled(true);
		jTree1.setEditable(true);
		// jTree1.getSelectionModel().setSelectionMode
		// (TreeSelectionModel.SINGLE_TREE_SELECTION);
		// jTree1.setShowsRootHandles(true);
		// JScrollPane treeView = new JScrollPane(jTree1);
		jTree1.setRootVisible(false);
		jTree1.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree1.setCellRenderer(new MyRenderer());
		ToolTipManager.sharedInstance().registerComponent(jTree1);

		setBorder(javax.swing.BorderFactory.createEtchedBorder());
		add(jScrollPane1);
		// jScrollPane1.setBounds(0, 40, 180, 200);

		jClearButton.setFont(new java.awt.Font("Tahoma", 1, 10));
		jClearButton.setText("X");
		jClearButton.setToolTipText("Clear all items from panel");
		jClearButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		jClearButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			jClearButton.setMargin(new java.awt.Insets(-10, -15, -10, -20));
		}
		jClearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jClearButtonActionPerformed(evt);
			}
		});

		add(jClearButton);
		jClearButton.setBounds(160, 0, 18, 20);

		jConstrainButton.setText("Dates");
		jConstrainButton.setToolTipText("Constrain group by dates");
		jConstrainButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		jConstrainButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		// jConstrainButton.setMargin(new java.awt.Insets(-10, -15, -10,-20));
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			jConstrainButton
					.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// jConstrainButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			jConstrainButton.setMargin(new java.awt.Insets(-10, -15, -10, -20));
		}

		jConstrainButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jConstrainButtonActionPerformed(evt);
			}
		});

		add(jConstrainButton);
		jConstrainButton.setBounds(0, 20, 40, 21);

		jOccurrenceButton.setText("Occurs > 0x");
		jOccurrenceButton.setToolTipText("Set occurrence times");
		jOccurrenceButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jOccurrenceButton
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jOccurrenceButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			jOccurrenceButton
					.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jOccurrenceButton
					.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jOccurrenceButton
					.setMargin(new java.awt.Insets(-10, -10, -10, -10));
		}
		jOccurrenceButton.setIconTextGap(0);
		jOccurrenceButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jOccurrenceButtonActionPerformed(evt);
					}
				});
		jOccurrenceButton.setBounds(40, 20, 90, 21);
		add(jOccurrenceButton);

		// jExcludeButton.setMnemonic('E');
		jExcludeButton.setText("Exclude");
		jExcludeButton.setToolTipText("Exclude all items in group");
		jExcludeButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jExcludeButton
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jExcludeButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			jExcludeButton.setMargin(new java.awt.Insets(-10, -15, -10, -20));
			jExcludeButton
					.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		}
		jExcludeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jExcludeButtonActionPerformed(evt);
			}
		});
		add(jExcludeButton);
		jExcludeButton.setBounds(130, 20, 48, 21);

		jNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jNameLabel.setText("Group 1");
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		jNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		add(jNameLabel);
		jNameLabel.setBounds(0, 0, 160, 20);
		jNameLabel.setTransferHandler(new GroupLabelTextHandler());
		jNameLabel.addMouseListener(new DragMouseAdapter());
		jNameLabel
				.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
					public void mouseMoved(java.awt.event.MouseEvent evt) {
						jNameLabelMouseMoved(evt);
						// System.out.println("mouse x: "+evt.getX()+" y: "+evt.
						// getY());
						// System.out.println("name label x: "+jNameLabel.getX()+
						// " width: "+
						// jNameLabel.getWidth()+" y: "
						// +jNameLabel.getY()+" height "+jNameLabel.getHeight());
					}

				});
		jNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseExited(java.awt.event.MouseEvent evt) {
				jNameLabelMouseExited(evt);
			}

		});

		/*
		 * jAnyvisitToggleButton.setText("Any Visit");
		 * add(jAnyvisitToggleButton); jAnyvisitToggleButton.setBounds(0, 40,
		 * 89, 20);
		 * 
		 * jSamevisitToggleButton.setText("Same Visit");
		 * add(jSamevisitToggleButton); jSamevisitToggleButton.setBounds(90, 40,
		 * 88, 20);
		 * 
		 * ButtonGroup group = new ButtonGroup();
		 * group.add(jAnyvisitToggleButton); group.add(jSamevisitToggleButton);
		 * jAnyvisitToggleButton.setSelected(true);
		 */

		jTree1.addTreeExpansionListener(this);
		jTree1.setTransferHandler(new TextHandler());
		add(jScrollPane1);
		jScrollPane1.setViewportView(jTree1);
		// jTree1.setToolTipText(
		// "Double click on a folder to view the items inside");
		// jScrollPane1.getViewport().setToolTipText(
		// "Double click on a folder to view the items inside");
		jScrollPane1.setBounds(0, 60, 180, 120);
		// jScrollPane1.setBounds(0, 60, 190, 270);
		// jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new
		// java.awt.Color(0, 0, 0)));
		// jTree1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
		// {
		// public void mouseMoved(java.awt.event.MouseEvent evt) {
		// jScrollPane1MouseMoved(evt);
		// }

		// @Override
		// public void mouseDragged(MouseEvent e) {
		// jScrollPane1MouseMoved(e);
		// }

		// });
		// jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
		// public void mouseExited(java.awt.event.MouseEvent evt) {
		// jScrollPane1MouseExited(evt);
		// }

		// @Override
		// public void mouseEntered(MouseEvent e) {

		// jScrollPane1MouseEntered(e);
		// }

		// });

		jHintLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jHintLabel.setText("<html><center>Drag terms from Navigate, <br>"
				+ "<left>Find and Workplace into this group");
		// jHintLabel.getFont();
		jHintLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
		// jHintLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jHintLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jHintLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		// jHintLabel.setBackground(Color.WHITE);
		// jHintLabel.setForeground(Color.WHITE);
		add(jHintLabel);
		jHintLabel.setBounds(0, 120, 180, 30);

		jTimingComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Treat Independently",
						"Occurs in Same Encounter",
						"Items instance will be same"}));
		jTimingComboBox.setAlignmentX(5.0F);
		ListCellRenderer renderer = new DefaultListCellRenderer();
		((JLabel)renderer).setHorizontalAlignment(SwingConstants.CENTER);
		jTimingComboBox.setRenderer(renderer);
		// jTimingComboBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		add(jTimingComboBox);
		jTimingComboBox.setBounds(1, 40, 177, 20);
		jTimingComboBox.setEnabled(false);
	}

	private void jClearButtonActionPerformed(java.awt.event.ActionEvent evt) {
		clearTree();
	}

	private void clearTree() {
		while (top.getChildCount() > 0) {
			for (int i = 0; i < top.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) top
						.getChildAt(i);
				// System.out.println("Remove node: "+
				// ((QueryTreeNodeData)node.getUserObject()).tooltip());
				treeModel.removeNodeFromParent(node);
			}
		}

		panelData.exclude(false);
		panelData.setOccurrenceTimes(1);
		panelData.startTime(-1);
		panelData.endTime(-1);

		panelData.startDay(-1);
		panelData.endDay(-1);
		panelData.timing("ANY");
		data().getItems().clear();
		jHintLabel.setText("<html><center>Drag terms from Navigate, <br>"
				+ "<left>Find and Workplace into this group");
		redraw(panelData);
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) node
				.getUserObject();

		// System.out.println("Node collapsed: "+data.dimcode());

		if (data.visualAttribute().equals("FAO")) {
			data.visualAttribute("FA");
		} else if (data.visualAttribute().equals("CAO")) {
			data.visualAttribute("CA");
		}
	}

	public void treeExpanded(TreeExpansionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) node
				.getUserObject();
		jTree1.scrollPathToVisible(new TreePath(node));

		// System.out.println("Node expanded: "+data.dimcode());
		if (data.visualAttribute().equals("L")) {
			// return;
		}

		if (data.visualAttribute().equals("FA")) {
			data.visualAttribute("FAO");
		} else if (data.visualAttribute().equals("CA")) {
			data.visualAttribute("CAO");
		}

		// check to see if child is a placeholder ('working...')
		// if so, make Web Service call to update children of node
		if (node.getChildCount() == 1) {
			final DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) node
					.getChildAt(0);
			if (((QueryConceptTreeNodeData) node1.getUserObject()).name()
					.equalsIgnoreCase("working ......")) {
				final DefaultMutableTreeNode anode = node;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						populateChildNodes(anode);
					}
				});
			}
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode anode = (DefaultMutableTreeNode) node
						.getChildAt(0);
				QueryConceptTreeNodeData adata = (QueryConceptTreeNodeData) anode
						.getUserObject();
				if (adata.visualAttribute().equals("FAO")) {
					// adata.visualAttribute("FA");
				} else if (adata.visualAttribute().equals("CAO")) {
					// adata.visualAttribute("CA");
				}
			}
		}
	}

	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) node
				.getUserObject();

		// System.out.println("Node will collapse: "+data.dimcode());

		if (data.visualAttribute().equals("FAO")) {
			data.visualAttribute("FA");
		} else if (data.visualAttribute().equals("CAO")) {
			data.visualAttribute("CA");
		}
	}

	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) node
				.getUserObject();

		// System.out.println("Node will expand: "+data.dimcode());

		if (data.visualAttribute().equals("FA")) {
			data.visualAttribute("FAO");
		} else if (data.visualAttribute().equals("CA")) {
			data.visualAttribute("CAO");
		}

		// check to see if child is a placeholder ('working...')
		// if so, make Web Service call to update children of node
		if (node.getChildCount() == 1
				&& !(node.getChildAt(0).toString()
						.equalsIgnoreCase("Over 300 child nodes"))) {
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) node
					.getChildAt(0);
			if (((QueryConceptTreeNodeData) node1.getUserObject())
					.visualAttribute().equals("LAO")) {
				populateChildNodes(node);
			}
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode anode = (DefaultMutableTreeNode) node
						.getChildAt(0);
				QueryConceptTreeNodeData adata = (QueryConceptTreeNodeData) anode
						.getUserObject();
				if (adata.visualAttribute().equals("FAO")) {
					adata.visualAttribute("FA");
				} else if (adata.visualAttribute().equals("CAO")) {
					adata.visualAttribute("CA");
				}
			}
		}
	}

	private List getConceptsFromTerm(QueryConceptTreeNodeData data,
			DefaultMutableTreeNode tmpnode) {

		// QueryConceptTreeNodeData data = (QueryConceptTreeNodeData)
		// node.getUserObject();
		try {
			GetTermInfoType parentType = new GetTermInfoType();

			parentType.setMax(null);// Integer.parseInt(System.getProperty(
			// "OntMax")));
			parentType.setHiddens(Boolean.parseBoolean(System
					.getProperty("OntHiddens")));
			parentType.setSynonyms(Boolean.parseBoolean(System
					.getProperty("OntSynonyms")));

			parentType.setMax(parentPanel.max_child());

			parentType.setBlob(true);

			parentType.setSelf(data.fullname());
			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;
			String response = OntServiceDriver.getTermInfo(parentType, "");
			procStatus = msg.processResult(response);
			int result;
			if (procStatus.getValue().equals("MAX_EXCEEDED") && tmpnode != null) {
				result = JOptionPane.showConfirmDialog(parentPanel,
						"The node has exceeded maximum number of children.\n"
								+ "Do you want to continue?",
						"Please note ...", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.NO_OPTION) {
					// DefaultMutableTreeNode tmpnode =
					// data;//(DefaultMutableTreeNode) node.getChildAt(0);
					QueryConceptTreeNodeData tmpdata = (QueryConceptTreeNodeData) tmpnode
							.getUserObject();
					tmpdata.name("Over maximum number of child nodes");
					// procStatus.setType("DONE");
					jTree1.repaint();
					jTree1.scrollPathToVisible(new TreePath(tmpnode.getPath()));
					return null;
				} else {
					parentType.setMax(null);
					response = OntServiceDriver.getTermInfo(parentType, "");
					procStatus = msg.processResult(response);
				}
			}

			if (!procStatus.getType().equals("DONE")) {
				JOptionPane.showMessageDialog(parentPanel,
						"Error message delivered from the remote server, "
								+ "you may wish to retry your last action");
				return null;
			}

			ConceptsType allConcepts = msg.doReadConcepts();
			return allConcepts.getConcept();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(parentPanel,
					"Response delivered from the remote server could not be understood,\n"
							+ "you may wish to retry your last action.");
			return null;
		}
	}

	private List getConceptsFromChildren(QueryConceptTreeNodeData data,
			DefaultMutableTreeNode tmpnode) {

		// QueryConceptTreeNodeData data = (QueryConceptTreeNodeData)
		// node.getUserObject();
		try {
			GetChildrenType parentType = new GetChildrenType();

			parentType.setMax(null);// Integer.parseInt(System.getProperty(
			// "OntMax")));
			parentType.setHiddens(Boolean.parseBoolean(System
					.getProperty("OntHiddens")));
			parentType.setSynonyms(Boolean.parseBoolean(System
					.getProperty("OntSynonyms")));

			parentType.setMax(parentPanel.max_child());

			parentType.setBlob(true);

			parentType.setParent(data.fullname());
			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;
			String response = OntServiceDriver.getChildren(parentType, "");
			procStatus = msg.processResult(response);
			int result;
			if (procStatus.getValue().equals("MAX_EXCEEDED") && tmpnode != null) {
				result = JOptionPane.showConfirmDialog(parentPanel,
						"The node has exceeded maximum number of children.\n"
								+ "Do you want to continue?",
						"Please note ...", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.NO_OPTION) {
					// DefaultMutableTreeNode tmpnode =
					// data;//(DefaultMutableTreeNode) node.getChildAt(0);
					QueryConceptTreeNodeData tmpdata = (QueryConceptTreeNodeData) tmpnode
							.getUserObject();
					tmpdata.name("Over maximum number of child nodes");
					// procStatus.setType("DONE");
					jTree1.repaint();
					jTree1.scrollPathToVisible(new TreePath(tmpnode.getPath()));
					return null;
				} else {
					parentType.setMax(null);
					response = OntServiceDriver.getChildren(parentType, "");
					procStatus = msg.processResult(response);
				}
			}

			if (!procStatus.getType().equals("DONE")) {
				JOptionPane.showMessageDialog(parentPanel,
						"Error message delivered from the remote server, "
								+ "you may wish to retry your last action");
				return null;
			}

			ConceptsType allConcepts = msg.doReadConcepts();
			return allConcepts.getConcept();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(parentPanel,
					"Response delivered from the remote server could not be understood,\n"
							+ "you may wish to retry your last action.");
			return null;
		}
	}

	private void populateChildNodes(DefaultMutableTreeNode node) {

		List concepts = getConceptsFromChildren((QueryConceptTreeNodeData) node
				.getUserObject(), (DefaultMutableTreeNode) node.getChildAt(0));
		if (concepts != null) {
			addNodesFromOntXML(concepts, node);
			DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode) node
					.getChildAt(0);
			treeModel.removeNodeFromParent(tmpnode);
			jTree1.scrollPathToVisible(new TreePath(node.getPath()));
		}

	}

	private void addNodesFromOntXML(List<ConceptType> list,
			DefaultMutableTreeNode pnode) {
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) pnode
				.getUserObject();
		String c_xml = "";

		if (list.size() > 0) {
			((QueryConceptTreeNodeData) pnode.getUserObject())
					.visualAttribute("FAO");
		}

		for (int i = 0; i < list.size(); i++) {
			QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
			ConceptType concept = list.get(i);
			node.name(concept.getName());
			node.titleName(concept.getName());
			node.visualAttribute(concept.getVisualattributes().trim());
			node.tooltip(concept.getTooltip());
			node.hlevel(new Integer(concept.getLevel()).toString());
			node.fullname(concept.getKey());
			node.dimcode(concept.getDimcode());
			node.columnName(concept.getColumnname());
			node.factTableColumn(concept.getFacttablecolumn());
			node.tableName(concept.getTablename());
			node.columnDataType(concept.getColumndatatype());
			node.operator(concept.getOperator());

			// node.lookupdb(data.lookupdb());
			// node.lookuptable(data.lookuptable());
			// node.selectservice(data.selectservice());

			// int size = concept.getMetadataxml().getAny().size();
			// System.out.println("Value xml tag item size: "+size);

			addNode(node, pnode);
		}
	}

	/*private void addNodesFromXML(org.w3c.dom.Document resultDoc,
			DefaultMutableTreeNode pnode) {
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) pnode
				.getUserObject();
		String c_xml = "";
		try {
			org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
			org.jdom.Document jresultDoc = builder.build(resultDoc);
			org.jdom.Namespace ns = jresultDoc.getRootElement().getNamespace();
			// System.out.println((new
			// XMLOutputter()).outputString(jresultDoc));

			Iterator iterator = jresultDoc.getRootElement().getChildren(
					"patientData", ns).iterator();
			while (iterator.hasNext()) {
				org.jdom.Element patientData = (org.jdom.Element) iterator
						.next();
				org.jdom.Element lookup = (org.jdom.Element) patientData
						.getChild(data.lookuptable().toLowerCase(), ns).clone();

				// modification of c_metadataxml tag to make it part of the xml
				// document
				try {
					org.jdom.Element metaDataXml = (org.jdom.Element) lookup
							.getChild("c_metadataxml");
					c_xml = metaDataXml.getText();
					if ((c_xml != null) && (c_xml.trim().length() > 0)
							&& (!c_xml.equals("(null)"))) {
						SAXBuilder parser = new SAXBuilder();
						String xmlContent = c_xml;
						java.io.StringReader xmlStringReader = new java.io.StringReader(
								xmlContent);
						org.jdom.Document tableDoc = parser
								.build(xmlStringReader);
						org.jdom.Element rootElement = (org.jdom.Element) tableDoc
								.getRootElement().clone();
						metaDataXml.setText("");
						metaDataXml.getChildren().add(rootElement);
					}
				} catch (Exception e) {
					System.out.println("getNodesFromXML: parsing XML:"
							+ e.getMessage());
				}

				org.jdom.Element nameXml = lookup.getChild("c_name");
				String c_name = nameXml.getText().trim();
				nameXml = lookup.getChild("c_dimcode");
				String c_dimcode = nameXml.getText().trim();
				nameXml = lookup.getChild("c_operator");
				String c_operator = nameXml.getText().trim();
				nameXml = lookup.getChild("c_columndatatype");
				String c_columndatatype = nameXml.getText().trim();
				nameXml = lookup.getChild("c_columnname");
				String c_columnname = nameXml.getText().trim();
				nameXml = lookup.getChild("c_tablename");
				String c_table = nameXml.getText().trim();
				nameXml = lookup.getChild("c_tooltip");
				String c_tooltip = nameXml.getText().trim();
				nameXml = lookup.getChild("c_visualattributes");
				String c_visual = nameXml.getText().trim();
				nameXml = lookup.getChild("c_hlevel");
				String hlevel = nameXml.getText().trim();
				nameXml = lookup.getChild("c_fullname");
				String fullname = nameXml.getText().trim();
				nameXml = lookup.getChild("c_synonym_cd");
				String synonym = nameXml.getText().trim();

				if (nameXml == null)
					nameXml = lookup.getChild("c_facttablecolumn");
				String sFactDimColumn = nameXml.getText();
				if (c_operator.toUpperCase().equals("LIKE")) {
					c_dimcode = "'" + c_dimcode + "\\%'";
				} else if (c_operator.toUpperCase().equals("IN")) {
					c_dimcode = "(" + c_dimcode + ")";
				} else if (c_operator.toUpperCase().equals("=")) {
					if (c_columndatatype.equals("T")) {
						c_dimcode = "'" + c_dimcode + "'";
					}
				}

				if (!(c_visual.substring(1, 2).equals("H"))
						&& !(synonym.equals("Y"))) {
					QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
					node.name(c_name);
					node.titleName(c_name);
					node.visualAttribute(c_visual);
					node.tooltip(c_tooltip);
					node.hlevel(hlevel);
					node.fullname(fullname);
					node.dimcode(c_dimcode);
					node.lookupdb(data.lookupdb());
					node.lookuptable(data.lookuptable());
					node.selectservice(data.selectservice());
					addNode(node, pnode);
				}
			}
			org.jdom.Element result = (org.jdom.Element) jresultDoc
					.getRootElement().getChild("result");
			String resultString = result.getChildTextTrim("resultString", ns);
			// System.out.println(resultString);
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e.getMessage());
		}
	}*/

	private void createPopupMenu() {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		/*
		 * menuItem = new JMenuItem("Constrain Item ...");
		 * menuItem.setAccelerator
		 * (javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
		 * java.awt.event.InputEvent.CTRL_MASK));
		 * menuItem.addActionListener(this); popup.add(menuItem);
		 */

		menuItem = new JMenuItem("Delete Item");
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.
		// event.KeyEvent.VK_X,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);

		/*
		 * popup.add(new javax.swing.JSeparator());
		 * 
		 * menuItem = new JMenuItem("Exclude All Items");
		 * menuItem.setAccelerator
		 * (javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
		 * java.awt.event.InputEvent.CTRL_MASK));
		 * menuItem.addActionListener(this); popup.add(menuItem);
		 */

		popup.add(new javax.swing.JSeparator());

		menuItem = new JMenuItem("Set Value ...");
		menuItem.setEnabled(false);
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.
		// event.KeyEvent.VK_S,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("Set Modifier Value ...");
		menuItem.setEnabled(false);
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.
		// event.KeyEvent.VK_S,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);

		// Add listener to the tree
		MouseListener popupListener = new ConceptTreePopupListener(popup);
		jTree1.addMouseListener(popupListener);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Constrain Item ...")) {
			// JOptionPane.showMessageDialog(this, "Constrain Item ...");
			// DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			// jTree1.getSelectionPath().getLastPathComponent();
			// QueryConceptTreeNodeData ndata = (QueryConceptTreeNodeData)
			// node.getUserObject();
			// final QueryConstrainFrame cframe = new
			// QueryConstrainFrame(ndata);
			// cframe.setTitle("Constrain Item: "+ndata.name());
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					// cframe.setVisible(true);
				}
			});
		} else if (e.getActionCommand().equalsIgnoreCase("Delete Item")) {
			// JOptionPane.showMessageDialog(this, "Delete Item");
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			treeModel.removeNodeFromParent(node);
			data().getItems().remove(node.getUserObject());
		} else if (e.getActionCommand().equalsIgnoreCase("Set Value ...")) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			final QueryConceptTreeNodeData ndata = (QueryConceptTreeNodeData) node
					.getUserObject();
			final GroupPanel parent = this;

			int index = data().getItems().indexOf(node.getUserObject());
			currentData = data().getItems().get(index);

			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					if (ndata.valuePropertyData().hasEnumValue()) {
						EnumValueConstraintFrame vDialog = new EnumValueConstraintFrame(
								parent);
						vDialog.setSize(410, 330);						
						//StringValueConstraintFrame vDialog = new StringValueConstraintFrame(
								//parent);
						//vDialog.setSize(380, 250);
						vDialog.setLocation(300, 300);
						vDialog
								.setTitle("Choose value of "
										+ ndata.titleName());
						vDialog.setVisible(true);
					} else if (ndata.valuePropertyData().hasStringValue()) {
						StringValueConstraintFrame vDialog = new StringValueConstraintFrame(
								parent);
						vDialog.setSize(380, 250);
						vDialog.setLocation(300, 300);
						vDialog
								.setTitle("Search within the "
										+ ndata.titleName());
						vDialog.setVisible(true);
					} else {
						NumericValueConstraintFrame vDialog = new NumericValueConstraintFrame(
								parent);
						vDialog.setSize(410, 215);
						vDialog.setLocation(300, 300);
						vDialog
								.setTitle("Choose value of "
										+ ndata.titleName());
						vDialog.setVisible(true);
					}
				}
			});
		}
		else if (e.getActionCommand().equalsIgnoreCase("Set Modifier Value ...")) {
			/*final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			final QueryConceptTreeNodeData ndata = (QueryConceptTreeNodeData) node
					.getUserObject();
			final GroupPanel parent = this;

			int index = data().getItems().indexOf(node.getUserObject());
			currentData = data().getItems().get(index);

			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					//if (ndata.valuePropertyData().hasEnumValue()) {
						ModifierFrame vDialog = new ModifierFrame();
						vDialog.setSize(200, 410);
						vDialog.setLocation(300, 300);
						//vDialog
								//.setTitle("Choose value of "
										//+ ndata.titleName());
						vDialog.setVisible(true);
					
				}
			});*/
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
							.getSelectionPath().getLastPathComponent();
			final ModifierData ndata = (ModifierData) node
					.getUserObject();
			final GroupPanel parent = this;
		
			int index = data().getItems().indexOf(node.getUserObject());
			currentData = data().getItems().get(index);
		
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					if (ndata.modifierValuePropertyData().hasEnumValue()) {
						ModifierEnumValueConstraintFrame vDialog = new ModifierEnumValueConstraintFrame(
								parent);
						vDialog.setSize(410, 330);
						//ModifierStringValueConstraintFrame vDialog = new ModifierStringValueConstraintFrame(
						//		parent);
						//vDialog.setSize(380, 250);  ////410, 330);
						vDialog.setLocation(300, 300);
						vDialog
								.setTitle("Choose modifier value of "
										+ ndata.titleName());
						vDialog.setVisible(true);
					}else if (ndata.modifierValuePropertyData().hasStringValue()) {
						ModifierStringValueConstraintFrame vDialog = new ModifierStringValueConstraintFrame(
								parent);
						vDialog.setSize(380, 250);
						vDialog.setLocation(300, 300);
						vDialog
								.setTitle("Choose modifier value of "
										+ ndata.titleName());
						vDialog.setVisible(true);
					} else {
						ModifierNumericValueConstraintFrame vDialog = new ModifierNumericValueConstraintFrame(
								parent);
						vDialog.setSize(410, 215);
						vDialog.setLocation(300, 300);
						vDialog
								.setTitle("Choose modifier value of "
										+ ndata.titleName());
						vDialog.setVisible(true);
					}
				}
			});
		}
	}

	public void setValueDisplay() {
		while (top.getChildCount() > 0) {
			for (int i = 0; i < top.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) top
						.getChildAt(i);
				// System.out.println("Remove node: "+
				// ((QueryTreeNodeData)node.getUserObject()).tooltip());
				treeModel.removeNodeFromParent(node);
			}
		}

		for (int i = 0; i < data().getItems().size(); i++) {
			QueryConceptTreeNodeData node = data().getItems().get(i);
			addNode(node);
		}
	}

	private class DragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			// reading the system time to a long
			lEventTime = System.currentTimeMillis();
		}
	}

	class ConceptTreePopupListener extends MouseAdapter {
		JPopupMenu popup;

		ConceptTreePopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				TreePath path = jTree1.getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node
							.getParent();
					if (parent.isRoot()) {
						QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) node
								.getUserObject();
						if(data.isModifier()) {
							ModifierData mdata = (ModifierData)data;
							if (mdata.hasModifierValue()) {
								for (int i = 0; i < popup.getSubElements().length; i++) {
									JMenuItem item = (JMenuItem) popup
											.getSubElements()[i];
									if (item.getText() != null
											&& item.getText().equalsIgnoreCase(
													"Set Modifier Value ...")) {
										item.setEnabled(true);
										break;
									}
								}
								
								for (int i = 0; i < popup.getSubElements().length; i++) {
								JMenuItem item = (JMenuItem) popup
										.getSubElements()[i];
								if (item.getText() != null
										&& item.getText().equalsIgnoreCase(
												"Set Value ...")) {
									item.setEnabled(false);
									break;
								}
							}
							} else {
								for (int i = 0; i < popup.getSubElements().length; i++) {
									JMenuItem item = (JMenuItem) popup
											.getSubElements()[i];
									if (item.getText() != null
											&& (item.getText().equalsIgnoreCase(
													"Set Modifier Value ..."))) {
										item.setEnabled(false);
										break;
									}
								}
							}
						}
						else {
							for (int i = 0; i < popup.getSubElements().length; i++) {
								JMenuItem item = (JMenuItem) popup
										.getSubElements()[i];
								if (item.getText() != null
										&& item.getText().equalsIgnoreCase(
												"Set Modifier Value ...")) {
									item.setEnabled(false);
									break;
								}
							}
							if (data.hasValue()) {
								for (int i = 0; i < popup.getSubElements().length; i++) {
									JMenuItem item = (JMenuItem) popup
											.getSubElements()[i];
									if (item.getText() != null
											&& item.getText().equalsIgnoreCase(
													"Set Value ...")) {
										item.setEnabled(true);
										break;
									}
								}
							} else {
								for (int i = 0; i < popup.getSubElements().length; i++) {
									JMenuItem item = (JMenuItem) popup
											.getSubElements()[i];
									if (item.getText() != null
											&& item.getText().equalsIgnoreCase(
													"Set Value ...")) {
										item.setEnabled(false);
										break;
									}
								}
							}
						}

						popup.show(e.getComponent(), e.getX(), e.getY());
						jTree1.setSelectionPath(path);
					}
				}
			}
		}
	}

	private void jExcludeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (!panelData.exclude()) {
			panelData.exclude(true);
			jExcludeButton.setText("<html><center><u>Exclude</u>");
			jTree1.setBackground(Color.LIGHT_GRAY);
			jExcludeButton.setToolTipText("Include all items in panel");
			jNameLabel.requestFocus();
		} else {
			panelData.exclude(false);
			jExcludeButton.setText("Exclude");
			jTree1.setBackground(Color.WHITE);
			jExcludeButton.setToolTipText("Exclude all items in panel");
			jNameLabel.requestFocus();
		}
	}

	private void jConstrainButtonActionPerformed(java.awt.event.ActionEvent evt) {
		final TimeConstraintFrame cframe = new TimeConstraintFrame(this);
		cframe.setTitle("Constrain Dates for: " + groupname_);
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				cframe.setVisible(true);
			}
		});
	}

	private void jOccurrenceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		final OccurrenceFrame cframe = new OccurrenceFrame(this);
		cframe.setTitle("Constrain Occurrence Times for: " + groupname_);
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				cframe.setVisible(true);
			}
		});
	}

	public void setDateConstrainText(String str) {
		jConstrainButton.setText(str);
	}

	public void setOccurrenceText(String str) {
		jOccurrenceButton.setText(str);
	}

	public boolean isAny() {
		return jTimingComboBox.getSelectedIndex() == 0;
	}
	
	public String getTimeComboText() {
		return (String) jTimingComboBox.getSelectedItem();
	}

	public void setOccurrenceTimes(int i) {
		panelData.setOccurrenceTimes(i);
	}
	
	public void setAccuracyScale(int i) {
		panelData.setAccuracyScale(i);
	}

	public int getOccurrenceTimes() {
		return panelData.getOccurrenceTimes();
	}
	
	public int getAccuracyScale() {
		return panelData.getAccuracyScale();
	}

	private void jNameLabelMouseExited(java.awt.event.MouseEvent evt) {
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
	}

	private void jNameLabelMouseMoved(java.awt.event.MouseEvent evt) {
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.YELLOW));
		jNameLabel.paintImmediately(jNameLabel.getVisibleRect());

		// for (int i=0;i<1000;i++) {
		// System.out.println("times: "+i);
		// }
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.BLACK));

	}

	private void jScrollPane1MouseExited(java.awt.event.MouseEvent evt) {
		jScrollPane1.setBorder(defaultBorder);
	}

	private void jScrollPane1MouseMoved(java.awt.event.MouseEvent evt) {
		jScrollPane1.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.YELLOW));
	}

	// private void jScrollPane1MouseEntered(java.awt.event.MouseEvent evt) {
	// jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(Color.
	// YELLOW));
	// }

	/*private String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for (int i = 0; i < 20; i++) {
			int num = getValidAcsiiValue();
			// System.out.println("Generated number: " + num + " char:
			// "+(char)num);
			strWriter.append((char) num);
		}
		return strWriter.toString();
	}*/

	/*private int getValidAcsiiValue() {
		int number = 48;
		while (true) {
			number = 48 + (int) Math.round(Math.random() * 74);
			if ((number > 47 && number < 58) || (number > 64 && number < 91)
					|| (number > 96 && number < 123)) {
				break;
			}
		}
		return number;
	}*/
	
	public String getSiteId(edu.harvard.i2b2.crcxmljaxb.datavo.wdo.XmlValueType xml)
	{    	
		org.w3c.dom.Element rootElement = xml.getAny().get(0);
		NodeList nameElements = rootElement.getElementsByTagName("patient_id");
		if (nameElements.getLength() != 0 && nameElements.item(0).getAttributes().getLength() != 0){
			return nameElements.item(0).getAttributes().item(0).getTextContent();
		}
		else
			return "";//MessageUtil.getInstance().getTimestamp();

	}

	// Variables declaration
	private javax.swing.JButton jClearButton;
	private javax.swing.JButton jConstrainButton;
	private javax.swing.JButton jExcludeButton;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel jNameLabel;
	private javax.swing.JButton jOccurrenceButton;
	private javax.swing.JLabel jHintLabel;
	// private javax.swing.JToggleButton jAnyvisitToggleButton;
	// private javax.swing.JToggleButton jSamevisitToggleButton;
	private javax.swing.JComboBox jTimingComboBox;
	// End of variables declaration

	public javax.swing.JTree jTree1;
}
