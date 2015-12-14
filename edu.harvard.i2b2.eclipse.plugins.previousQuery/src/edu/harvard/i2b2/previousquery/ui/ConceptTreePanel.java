/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.previousquery.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.previousquery.data.ConceptTreeData;
import edu.harvard.i2b2.previousquery.data.QueryConceptTreeNodeData;

/*
 * QueryConceptTreePanel.java
 *
 * Created on August 22, 2006, 12:45 PM
 */

public class ConceptTreePanel extends javax.swing.JPanel implements
		TreeExpansionListener, TreeWillExpandListener, ActionListener {
	private DefaultMutableTreeNode top = null;
	private DefaultTreeModel treeModel = null;
	private TopPanel parentPanel = null;
	private ConceptTreeData panelData = null;

	public ConceptTreeData data() {
		return panelData;
	}

	private String groupname_ = null;

	public String getGroupName() {
		return groupname_;
	}

	/** Creates new form QueryConceptTreePanel */
	public ConceptTreePanel(String groupname, TopPanel parent) {
		initComponents();
		jNameLabel.setText(groupname);
		createPopupMenu();
		parentPanel = parent;
		groupname_ = groupname;
		panelData = new ConceptTreeData();
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
				// this.setForeground(Color.GRAY);
				setBackgroundNonSelectionColor(Color.LIGHT_GRAY);
			} else {
				// this.setForeground(Color.BLACK);
				setBackgroundNonSelectionColor(Color.WHITE);
			}

			return this;
		}

		private ImageIcon getImageIcon(QueryConceptTreeNodeData data) {
			String key = null;
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
				key = "leaf";// "multi";
			}

			if (key.equals("multi")) {
				return createImageIcon(key + ".bmp");
			} else {
				return createImageIcon(key + ".jpg");
			}
		}
	}

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = TopPanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, "");
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
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

		public boolean importData(JComponent comp, Transferable t) {
			try {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);

				try {
					SAXBuilder parser = new SAXBuilder();
					String xmlContent = text;
					java.io.StringReader xmlStringReader = new java.io.StringReader(
							xmlContent);
					org.jdom.Document tableDoc = parser.build(xmlStringReader);
					org.jdom.Element tableXml = tableDoc.getRootElement();
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
											.showMessageDialog(
													jTree1,
													"You can not use this item in a query, "
															+ "it is only used for organizing the lists.");
								}
							});
							return true;
						}

						Element lookupdbXml = (Element) conceptXml
								.getChild("lookupdb");
						String lookupdb = lookupdbXml.getText().trim();
						Element lookuptableXml = (Element) conceptXml
								.getChild("lookuptable");
						String lookuptable = lookuptableXml.getText().trim();
						Element selectserviceXml = (Element) conceptXml
								.getChild("selectservice");
						String selectservice = selectserviceXml.getText()
								.trim();

						Element conTableXml = (Element) conceptXml
								.getChildren().get(0);
						Element visualAttribs = conTableXml
								.getChild("c_visualattributes");
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
						org.jdom.Element nameXml = conTableXml
								.getChild("c_name");
						String c_name = nameXml.getText();
						nameXml = conTableXml.getChild("c_dimcode");
						String c_dimcode = nameXml.getText();
						nameXml = conTableXml.getChild("c_operator");
						nameXml = conTableXml.getChild("c_columndatatype");
						nameXml = conTableXml.getChild("c_columnname");
						nameXml = conTableXml.getChild("c_tablename");
						nameXml = conTableXml.getChild("c_tooltip");
						String c_tooltip = nameXml.getText();
						nameXml = conTableXml.getChild("c_visualattributes");
						String c_visual = nameXml.getText();
						nameXml = conTableXml.getChild("c_hlevel");
						String hlevel = nameXml.getText();
						nameXml = conTableXml.getChild("c_fullname");
						String fullname = nameXml.getText();

						if (nameXml == null) {
							nameXml = conTableXml.getChild("c_facttablecolumn");
						}

						QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
						node.name(c_name);
						node.visualAttribute(c_visual);
						node.tooltip(c_tooltip);
						node.hlevel(hlevel);
						node.fullname(fullname);
						node.dimcode(c_dimcode);
						node.lookupdb(lookupdb);
						node.lookuptable(lookuptable);
						node.selectservice(selectservice);
						node.xmlContent(text);
						System.out.println("nodes xml content: "
								+ node.xmlContent());

						addNode(node);
						panelData.getItems().add(node);
						parentPanel.getRunQueryButton().requestFocus();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println(text);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}
	}

	public void redraw(ConceptTreeData data) {
		panelData = data;

		String str = "Occurs " + data.getOccurrenceTimes() + "x";
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
		setOccurrenceText("Occurs 1x");

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
		jScrollPane1.setBounds(0, 40, 190, 200);

		jClearButton.setFont(new java.awt.Font("Tahoma", 1, 11));
		jClearButton.setText("X");
		jClearButton.setToolTipText("Clear all items from panel");
		jClearButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		jClearButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		jClearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jClearButtonActionPerformed(evt);
			}
		});

		add(jClearButton);
		jClearButton.setBounds(170, 0, 18, 20);

		jConstrainButton.setText("Dates");
		jConstrainButton.setToolTipText("Constrain group by dates");
		jConstrainButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jConstrainButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		jConstrainButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jConstrainButtonActionPerformed(evt);
			}
		});

		add(jConstrainButton);
		jConstrainButton.setBounds(0, 20, 40, 21);

		// jExcludeButton.setMnemonic('E');
		jExcludeButton.setText("Exclude");
		jExcludeButton.setToolTipText("Exclude all items in group");
		jExcludeButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jExcludeButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		jExcludeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jExcludeButtonActionPerformed(evt);
			}
		});

		add(jExcludeButton);
		jExcludeButton.setBounds(140, 20, 48, 21);

		jOccurrenceButton.setText("Occurs 1x");
		jOccurrenceButton.setToolTipText("Set occurrence times");
		jOccurrenceButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jOccurrenceButton.setIconTextGap(0);
		jOccurrenceButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jOccurrenceButtonActionPerformed(evt);
					}
				});
		add(jOccurrenceButton);
		jOccurrenceButton.setBounds(40, 20, 100, 21);

		jNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jNameLabel.setText("Group 1");
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		jNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		add(jNameLabel);
		jNameLabel.setBounds(0, 0, 170, 20);

		jTree1.addTreeExpansionListener(this);
		jTree1.setTransferHandler(new TextHandler());
		add(jScrollPane1);
		jScrollPane1.setViewportView(jTree1);
		// jTree1.setToolTipText(
		// "Double click on a folder to view the items inside");
		// jScrollPane1.getViewport().setToolTipText(
		// "Double click on a folder to view the items inside");
		jScrollPane1.setBounds(0, 40, 190, 160);
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

		data().getItems().clear();
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) node
				.getUserObject();

		System.out.println("Node collapsed: " + data.dimcode());

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

		System.out.println("Node expanded: " + data.dimcode());

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
			if (((QueryConceptTreeNodeData) node1.getUserObject())
					.visualAttribute().equals("LAO")) {
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
					adata.visualAttribute("FA");
				} else if (adata.visualAttribute().equals("CAO")) {
					adata.visualAttribute("CA");
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

		System.out.println("Node will collapse: " + data.dimcode());

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

		System.out.println("Node will expand: " + data.dimcode());

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

	private void populateChildNodes(DefaultMutableTreeNode node) {
		try {
			org.w3c.dom.Document queryResultDoc = null;// QuerySelectServiceClient
			// .getXMLResult(data,
			// 0);
			if (queryResultDoc != null) {
				addNodesFromXML(queryResultDoc, node);
				DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode) node
						.getChildAt(0);
				treeModel.removeNodeFromParent(tmpnode);
				jTree1.scrollPathToVisible(new TreePath(node.getPath()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addNodesFromXML(org.w3c.dom.Document resultDoc,
			DefaultMutableTreeNode pnode) {
		QueryConceptTreeNodeData data = (QueryConceptTreeNodeData) pnode
				.getUserObject();
		String c_xml = "";
		try {
			org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
			org.jdom.Document jresultDoc = builder.build(resultDoc);
			org.jdom.Namespace ns = jresultDoc.getRootElement().getNamespace();
			System.out.println((new XMLOutputter()).outputString(jresultDoc));

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
				nameXml = lookup.getChild("c_tablename");
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
			System.out.println(resultString);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

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
		menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.CTRL_MASK));
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
		// final QueryConstrainFrame cframe = new QueryConstrainFrame(this);
		// cframe.setTitle("Constrain Dates for: "+groupname_);
		// java.awt.EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// cframe.setVisible(true);
		// }
		// });
	}

	private void jOccurrenceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		final OccurrenceFrame cframe = new OccurrenceFrame(this);
		cframe.setTitle("Constrain Occurence Times for: " + groupname_);
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

	public void setOccurrenceTimes(int i) {
		panelData.setOccurrenceTimes(i);
	}

	public int getOccurrenceTimes() {
		return panelData.getOccurrenceTimes();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jClearButton;
	private javax.swing.JButton jConstrainButton;
	private javax.swing.JButton jExcludeButton;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel jNameLabel;
	private javax.swing.JButton jOccurrenceButton;
	// End of variables declaration//GEN-END:variables

	public javax.swing.JTree jTree1;
}
