/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.patientSet.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.NodeList;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean; //import edu.harvard.i2b2.previousquery.data.StackData;
import edu.harvard.i2b2.eclipse.plugins.patientSet.workplaceMessaging.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.patientSet.workplaceMessaging.WorkplaceServiceDriver;
import edu.harvard.i2b2.patientSet.data.Messages;
import edu.harvard.i2b2.patientSet.data.PatientData;
import edu.harvard.i2b2.patientSet.data.QueryData;
import edu.harvard.i2b2.patientSet.data.QueryInstanceData;
import edu.harvard.i2b2.patientSet.data.QueryMasterData;
import edu.harvard.i2b2.patientSet.data.QueryResultData;
import edu.harvard.i2b2.patientSet.dataModel.PDOResponseMessageFactory;
import edu.harvard.i2b2.patientSet.dataModel.QueryConceptTreeNodeModel;
import edu.harvard.i2b2.patientSet.datavo.PatientSetJAXBUtil;
import edu.harvard.i2b2.patientSet.serviceClient.QueryListNamesClient;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.XmlValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FolderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FoldersType;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ApplicationType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.FacilityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageControlIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ProcessingIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.SecurityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainDateType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.FindByChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.InstanceResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MatchStrType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryMasterType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.StatusType.Condition;

/*
 * QueryPreviousRunsPanel.java
 * 
 * Created on September 19, 2006, 1:55 PM
 */

@SuppressWarnings("serial")
public class PatientSetJPanel extends javax.swing.JPanel implements
		ActionListener, TreeExpansionListener, TreeWillExpandListener {

	private static final Log log = LogFactory.getLog(PatientSetJPanel.class);
	// private static final int MAX_STACK_SIZE = 28;
	private long lEventTime = 0;
	private DefaultMutableTreeNode top = null;
	private DefaultTreeModel treeModel = null;
	private ArrayList<QueryMasterData> previousQueries = null;
	private ArrayList<QueryResultData> patientSets = null;
	private ViewPart parentView = null;
	private String cellStatus = "";
	private int result;

	private boolean ascending = false;
	private XMLGregorianCalendar curCreationDate;
	
	public void ascending(boolean b) {
		ascending = b;
	}

	public boolean ascending() {
		return ascending;
	}

	private boolean showName = false;

	public void showName(boolean b) {
		showName = b;
	}

	public boolean showName() {
		return showName;
	}

	private String lastRequestMessage = "";

	public String lastRequestMessage() {
		return lastRequestMessage;
	}

	private String lastResponseMessage = "";

	public String lastResponseMessage() {
		return lastResponseMessage;
	}

	private boolean isManager = false;
	private boolean hasProtectedAccess = false;
	
	public ArrayList<String> users = null;

	/** Creates new form QueryPreviousRunsPanel */
	public PatientSetJPanel(QueryC parentC) {// , ExplorerC explorerC) {
		ArrayList<String> roles = (ArrayList<String>) UserInfoBean
				.getInstance().getProjectRoles();

		for (String param : roles) {
			if (param.equalsIgnoreCase("manager")) {
				isManager = true;
				break;
			}
		}

		// for (String param : roles) {
		// if (param.equalsIgnoreCase("protected_access")) {
		if (UserInfoBean.getInstance().isRoleInProject("DATA_PROT")) {
			hasProtectedAccess = true;
		}
		// break;
		// }
		// }

		if (hasProtectedAccess) {
			showName = true;
		} else {
			showName = false;
		}

		if (isManager) {
			loadPreviousQueries(true);
		} else {
			loadPreviousQueries(false);
		}
		loadPatientSets();
		initComponents();
		createPopupMenu();
	}

	public PatientSetJPanel(ViewPart parent) {
		log.info("Patient Sets plugin version 1.7.0");

		parentView = parent;
		ArrayList<String> roles = (ArrayList<String>) UserInfoBean
				.getInstance().getProjectRoles();

		for (String param : roles) {
			if (param.equalsIgnoreCase("manager")) {
				isManager = true;
				break;
			}
		}

		// for (String param : roles) {
		// if (param.equalsIgnoreCase("protected_access")) {
		if (UserInfoBean.getInstance().isRoleInProject("DATA_PROT")) {
			hasProtectedAccess = true;
		}
		// break;
		// }
		// }

		if (hasProtectedAccess) {
			showName = true;
		} else {
			showName = false;
		}

		if (isManager) {
			loadPreviousQueries(true);
		} else {
			loadPreviousQueries(false);
		}

		loadPatientSets();
		initComponents();
		createPopupMenu();

		if (cellStatus.equalsIgnoreCase("")) {
			reset(200, false, false);
		}
	}

	public DefaultMutableTreeNode addNode(QueryConceptTreeNodeModel node,
			DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryConceptTreeNodeModel tmpData = new QueryConceptTreeNodeModel();
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

	public DefaultMutableTreeNode addNode(QueryMasterData node,
			DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryMasterData tmpData = new QueryMasterData();
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

	public DefaultMutableTreeNode addNode(QueryInstanceData node,
			DefaultMutableTreeNode parent) {
		QueryMasterData logicdata = (QueryMasterData) parent.getUserObject();
		logicdata.runs.add(node);

		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryInstanceData tmpData = new QueryInstanceData();
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

		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode) parent
				.getChildAt(0);
		QueryData tmpdata = (QueryData) tmpnode.getUserObject();
		if (tmpdata.name().equalsIgnoreCase("working ......")) {
			treeModel.removeNodeFromParent(tmpnode);
		}

		return childNode;
	}

	public DefaultMutableTreeNode addNode(QueryResultData node,
			DefaultMutableTreeNode parent) {
		// QueryInstanceData rundata = (QueryInstanceData)
		// parent.getUserObject();

		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryInstanceData tmpData = new QueryInstanceData();
		tmpData.name("working ......");
		tmpData.tooltip("A tmp node");
		tmpData.visualAttribute("LAO");
		DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
		if (!(node.visualAttribute().startsWith("L")
				|| node.visualAttribute().equalsIgnoreCase("MA") || node
				.patientCount().equalsIgnoreCase("0"))) {
			treeModel.insertNodeInto(tmpNode, childNode, childNode
					.getChildCount());
		}

		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode) parent
				.getChildAt(0);
		QueryData tmpdata = (QueryData) tmpnode.getUserObject();
		if (tmpdata.name().equalsIgnoreCase("working ......")) {
			treeModel.removeNodeFromParent(tmpnode);
		}

		return childNode;
	}

	public DefaultMutableTreeNode addNode(PatientData node,
			DefaultMutableTreeNode parent) {
		// QueryInstanceData rundata = (QueryInstanceData)
		// parent.getUserObject();

		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		// QueryInstanceData tmpData = new QueryInstanceData();
		// tmpData.name("working ......");
		// tmpData.tooltip("A tmp node");
		// tmpData.visualAttribute("LAO");
		// DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
		// if(!(node.visualAttribute().startsWith("L") ||
		// node.visualAttribute().equalsIgnoreCase("MA"))) {
		// treeModel.insertNodeInto(tmpNode, childNode,
		// childNode.getChildCount());
		// }
		// Make sure the user can see the lovely new node.

		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode) parent
				.getChildAt(0);
		QueryData tmpdata = (QueryData) tmpnode.getUserObject();
		if (tmpdata.name().equalsIgnoreCase("working ......")) {
			treeModel.removeNodeFromParent(tmpnode);
		}

		return childNode;
	}

	/*public DefaultMutableTreeNode addNode(QueryData node) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryResultData tmpData = new QueryResultData();
		tmpData.name("working ......");
		tmpData.tooltip("A tmp node");
		tmpData.visualAttribute("LAO");
		DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		treeModel.insertNodeInto(childNode, top, top.getChildCount());
		if (!(node.visualAttribute().startsWith("L") || node.visualAttribute()
				.equalsIgnoreCase("MA"))) {
			treeModel.insertNodeInto(tmpNode, childNode, childNode
					.getChildCount());
		}

		jTree1.expandPath(new TreePath(top.getPath()));

		return childNode;
	}*/
	
	public DefaultMutableTreeNode addNode(QueryResultData node) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryResultData tmpData = new QueryResultData();
		tmpData.name("working ......");
		tmpData.tooltip("A tmp node");
		tmpData.visualAttribute("LAO");
		tmpData.type("PATIENTSET");
		DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		treeModel.insertNodeInto(childNode, top, top.getChildCount());
		if (!(node.visualAttribute().startsWith("L") || node.visualAttribute()
				.equalsIgnoreCase("MA"))) {
			treeModel.insertNodeInto(tmpNode, childNode, childNode
					.getChildCount());
		}

		jTree1.expandPath(new TreePath(top.getPath()));

		return childNode;
	}

	public DefaultMutableTreeNode insertNode(QueryMasterData node) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);

		QueryMasterData tmpData = new QueryMasterData();
		tmpData.name("working ......");
		tmpData.tooltip("A tmp node");
		tmpData.visualAttribute("LAO");
		DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpData);

		if (ascending) {
			treeModel.insertNodeInto(childNode, top, top.getChildCount());
		} else {
			treeModel.insertNodeInto(childNode, top, 0);
		}

		if (!(node.visualAttribute().startsWith("L") || node.visualAttribute()
				.equalsIgnoreCase("MA"))) {
			treeModel.insertNodeInto(tmpNode, childNode, childNode
					.getChildCount());
		}

		jTree1.expandPath(new TreePath(top.getPath()));
		previousQueries.add(node);

		return childNode;
	}

	protected MessageHeaderType getMessageHeader() {
		MessageHeaderType messageHeader = new MessageHeaderType();

		messageHeader.setI2B2VersionCompatible(new BigDecimal(Messages
				.getString("QueryData.i2b2VersionCompatible"))); //$NON-NLS-1$

		ApplicationType appType = new ApplicationType();
		appType.setApplicationName(Messages
				.getString("QueryData.SendingApplicationName")); //$NON-NLS-1$
		appType.setApplicationVersion(Messages
				.getString("QueryData.SendingApplicationVersion")); //$NON-NLS-1$
		messageHeader.setSendingApplication(appType);

		messageHeader.setAcceptAcknowledgementType(new String("messageId"));

		MessageTypeType messageTypeType = new MessageTypeType();
		messageTypeType.setEventType(Messages.getString("QueryData.EventType"));
		messageTypeType.setMessageCode(Messages
				.getString("QueryData.MessageCode"));
		messageHeader.setMessageType(messageTypeType);

		FacilityType facility = new FacilityType();
		facility.setFacilityName(Messages
				.getString("QueryData.SendingFacilityName")); //$NON-NLS-1$
		messageHeader.setSendingFacility(facility);

		ApplicationType appType2 = new ApplicationType();
		appType2.setApplicationVersion(Messages
				.getString("QueryData.ReceivingApplicationVersion")); //$NON-NLS-1$
		appType2.setApplicationName(Messages
				.getString("QueryData.ReceivingApplicationName")); //$NON-NLS-1$
		messageHeader.setReceivingApplication(appType2);

		FacilityType facility2 = new FacilityType();
		facility2.setFacilityName(Messages
				.getString("QueryData.ReceivingFacilityName")); //$NON-NLS-1$
		messageHeader.setReceivingFacility(facility2);

		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		messageHeader.setDatetimeOfMessage(factory
				.getXMLGregorianCalendar(currentDate.getTime()));

		SecurityType secType = new SecurityType();
		secType.setDomain(UserInfoBean.getInstance().getUserDomain());
		secType.setUsername(UserInfoBean.getInstance().getUserName());
		PasswordType ptype = new PasswordType();
		ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
		ptype.setTokenMsTimeout(UserInfoBean.getInstance()
				.getUserPasswordTimeout());
		ptype.setValue(UserInfoBean.getInstance().getUserPassword());

		secType.setPassword(ptype);
		messageHeader.setSecurity(secType);

		MessageControlIdType mcIdType = new MessageControlIdType();
		mcIdType.setInstanceNum(0);
		mcIdType.setMessageNum(generateMessageId());
		messageHeader.setMessageControlId(mcIdType);

		ProcessingIdType proc = new ProcessingIdType();
		proc.setProcessingId(Messages.getString("QueryData.ProcessingId")); //$NON-NLS-1$
		proc.setProcessingMode(Messages.getString("QueryData.ProcessingMode")); //$NON-NLS-1$
		messageHeader.setProcessingId(proc);

		messageHeader.setAcceptAcknowledgementType(Messages
				.getString("QueryData.AcceptAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setApplicationAcknowledgementType(Messages
				.getString("QueryData.ApplicationAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setCountryCode(Messages
				.getString("QueryData.CountryCode")); //$NON-NLS-1$
		messageHeader.setProjectId(UserInfoBean.getInstance().getProjectId());
		return messageHeader;
	}

	protected String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for (int i = 0; i < 20; i++) {
			int num = getValidAcsiiValue();
			// System.out.println("Generated number: " + num + " char:
			// "+(char)num);
			strWriter.append((char) num);
		}
		return strWriter.toString();
	}

	private int getValidAcsiiValue() {
		int number = 48;
		while (true) {
			number = 48 + (int) Math.round(Math.random() * 74);
			if ((number > 47 && number < 58) || (number > 64 && number < 91)
					|| (number > 96 && number < 123)) {
				break;
			}
		}
		return number;
	}

	private String writeContentQueryXML(boolean getAllInGroup) {

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		String userId = UserInfoBean.getInstance().getUserName();
		userType.setLogin(userId);
		userType.setValue(userId);

		headerType.setUser(userType);
		if (getAllInGroup) {
			headerType
					.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_GROUP_ID);
		} else {
			headerType
					.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_USER_ID);
		}

		UserRequestType userRequestType = new UserRequestType();
		userRequestType.setGroupId(UserInfoBean.selectedProjectID());

		userRequestType.setUserId(userId);

		String maxNum = System.getProperty("QueryToolMaxQueryNumber");
		if (maxNum == null || maxNum.equals("")) {
			userRequestType.setFetchSize(20);
		} else {
			userRequestType.setFetchSize(Integer.parseInt(maxNum));
		}

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(userRequestType));
		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Generated content XML request: " +
		// strWriter.toString());
		return strWriter.toString();
	}
	
	public String loadPreviousQueries(String user) {
		System.out.println("Loading previous queries for: "
				+ System.getProperty("user"));
		String xmlStr = writeFindQueryXML(user, "Patient Set", 1, 1);//writeFindQueryXML(user);
		// System.out.println(xmlStr);

		String responseStr = null;
		if (System.getProperty("webServiceMethod").equals("SOAP")) {
			responseStr = QueryListNamesClient.sendQueryRequestSOAP(xmlStr);
		} else {
			responseStr = QueryListNamesClient.sendFindQueryRequestREST(xmlStr);
		}

		if (responseStr.equalsIgnoreCase("CellDown")) {
			cellStatus = new String("CellDown");
			return "CellDown";
		}

		try {
			JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseStr);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bt = messageType.getMessageBody();
			MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(
							bt.getAny(),
							edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType.class);
			for (Condition status : masterResponseType.getStatus()
					.getCondition()) {
				if (status.getType().equals("ERROR"))
					cellStatus = new String("CellDown");
			}
			previousQueries = new ArrayList<QueryMasterData>();
			for (QueryMasterType queryMasterType : masterResponseType
					.getQueryMaster()) {
				QueryMasterData tmpData;
				tmpData = new QueryMasterData();
				XMLGregorianCalendar cldr = queryMasterType.getCreateDate();
				tmpData.name(queryMasterType.getName() + " ["
						+ addZero(cldr.getMonth()) + "-"
						+ addZero(cldr.getDay()) + "-"
						+ addZero(cldr.getYear()) + " ]" + " ["
						+ queryMasterType.getUserId() + "]");
				tmpData.creationTime(cldr);//.clone());
				tmpData.creationTimeStr(addZero(cldr.getMonth()) + "-"
						+ addZero(cldr.getDay()) + "-"
						+ addZero(cldr.getYear())+ " "+cldr.getHour()+":"
						+cldr.getMinute()+":"+cldr.getSecond());
				tmpData.tooltip("A query run by "
								+ queryMasterType.getUserId());// System.
				// getProperty
				// ("user"));
				tmpData.visualAttribute("CA");
				tmpData.xmlContent(null);
				tmpData.id(queryMasterType.getQueryMasterId());
				tmpData.userId(queryMasterType.getUserId()); // System.getProperty
				// ("user"));
				if(queryMasterType.getMasterTypeCd() != null) {
					tmpData.queryType(queryMasterType.getMasterTypeCd());
				}
				previousQueries.add(tmpData);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@SuppressWarnings("unchecked")
	public String loadPreviousQueries(boolean getAllInGroup) {
		System.out.println("Loading previous queries for: "
				+ System.getProperty("user"));
		//String xmlStr = writeContentQueryXML(getAllInGroup);
		String xmlStr = writeFindQueryXML("all users", "Patient Set", 1, 1);
		// System.out.println(xmlStr);

		String responseStr = null;
		if (System.getProperty("webServiceMethod").equals("SOAP")) {
			responseStr = QueryListNamesClient.sendQueryRequestSOAP(xmlStr);
		} else {
			responseStr = QueryListNamesClient.sendFindQueryRequestREST(xmlStr);
		}

		if (responseStr.equalsIgnoreCase("CellDown")) {
			cellStatus = new String("CellDown");
			return "CellDown";
		}

		try {
			JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseStr);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bt = messageType.getMessageBody();
			MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(
							bt.getAny(),
							edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType.class);
			for (Condition status : masterResponseType.getStatus()
					.getCondition()) {
				if (status.getType().equals("ERROR"))
					cellStatus = new String("CellDown");
			}
			previousQueries = new ArrayList<QueryMasterData>();
			for (QueryMasterType queryMasterType : masterResponseType
					.getQueryMaster()) {
				QueryMasterData tmpData;
				tmpData = new QueryMasterData();
				XMLGregorianCalendar cldr = queryMasterType.getCreateDate();
				tmpData.name(queryMasterType.getName() + " ["
						+ addZero(cldr.getMonth()) + "-"
						+ addZero(cldr.getDay()) + "-"
						+ addZero(cldr.getYear()) + " ]" + " ["
						+ queryMasterType.getUserId() + "]");
				tmpData.creationTime(cldr);//.clone());
				tmpData.creationTimeStr(addZero(cldr.getMonth()) + "-"
						+ addZero(cldr.getDay()) + "-"
						+ addZero(cldr.getYear())+ " "+cldr.getHour()+":"
						+cldr.getMinute()+":"+cldr.getSecond());
				tmpData.tooltip("A query run by "
								+ queryMasterType.getUserId());// System.
				// getProperty
				// ("user"));
				tmpData.visualAttribute("CA");
				tmpData.xmlContent(null);
				tmpData.id(queryMasterType.getQueryMasterId());
				tmpData.userId(queryMasterType.getUserId()); // System.getProperty
				// ("user"));
				previousQueries.add(tmpData);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public String loadPatientSets() {
		System.out.println("Loading patient sets for: "
				+ System.getProperty("user"));
		this.patientSets = new ArrayList<QueryResultData>();
		
		for(int i=0; i<this.previousQueries.size(); i++) {
			QueryMasterData qdata = previousQueries.get(i);
			try {
				String xmlRequest = qdata.writeContentQueryXML();

				String xmlResponse = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					xmlResponse = QueryListNamesClient
							.sendQueryRequestSOAP(xmlRequest);
				} else {
					xmlResponse = QueryListNamesClient
							.sendQueryRequestREST(xmlRequest, false);
				}
				if (xmlResponse.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
					return null;
				}

				try {
					JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
					JAXBElement jaxbElement = jaxbUtil
							.unMashallFromString(xmlResponse);
					ResponseMessageType messageType = (ResponseMessageType) jaxbElement
							.getValue();

					BodyType bt = messageType.getMessageBody();
					InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
							.getObjectByClass(bt.getAny(),
									InstanceResponseType.class);

					for (QueryInstanceType queryInstanceType : instanceResponseType
							.getQueryInstance()) {
						// change later for working with new xml schema
						// RunQuery runQuery =

						QueryInstanceData runData = new QueryInstanceData();

						runData.visualAttribute("FA");
						if (queryInstanceType.getQueryStatusType().getName()
								.equalsIgnoreCase("completed")) {
							XMLGregorianCalendar sCldr = queryInstanceType
									.getStartDate();
							XMLGregorianCalendar eCldr = queryInstanceType
									.getEndDate();
							long diff = eCldr.toGregorianCalendar()
									.getTimeInMillis()
									- sCldr.toGregorianCalendar()
											.getTimeInMillis();
							runData.tooltip("All results are available, run "
									+ (diff / 1000) + " seconds");
						}
						runData.id(queryInstanceType.getQueryInstanceId());
						// runData.patientRefId(new
						// Integer(queryInstanceType.getRefId()).toString());
						// runData.patientCount(new
						// Long(queryInstanceType.getCount()).toString());
						// XMLGregorianCalendar cldr =
						// queryInstanceType.getStartDate();
						/*
						 * runData.name("Results of "+
						 * "["+addZero(cldr.getMonth(
						 * ))+"-"+addZero(cldr.getDay())+"-"
						 * +addZero(cldr.getYear())+"
						 * "+addZero(cldr.getHour())+":"
						 * +addZero(cldr.getMinute())
						 * +":"+addZero(cldr.getSecond())+"]");
						 */
						runData.name("Results of "
								+ qdata.name().substring(0,
										qdata.name().indexOf("[")));
						runData.queryName(qdata.name());
						qdata.runs.add(runData);
						if (!queryInstanceType.getQueryStatusType().getName()
								.equalsIgnoreCase("completed")) {
							runData.name(runData.name()
									+ " --- "
									+ queryInstanceType.getQueryStatusType()
											.getName());
							runData.tooltip("The results of the query run");
						}
						//addNode(runData, node);
						/////////////get patient sets//////////////////////
						try {
							xmlRequest = runData.writeContentQueryXML();

							xmlResponse = null;
							if (System.getProperty("webServiceMethod").equals("SOAP")) {
								xmlResponse = QueryListNamesClient
										.sendQueryRequestSOAP(xmlRequest);
							} else {
								xmlResponse = QueryListNamesClient
										.sendQueryRequestREST(xmlRequest, false);
							}
							if (xmlResponse.equalsIgnoreCase("CellDown")) {
								final JPanel parent = this;
								java.awt.EventQueue.invokeLater(new Runnable() {
									public void run() {
										JOptionPane
												.showMessageDialog(
														parent,
														"Trouble with connection to the remote server, "
																+ "this is often a network error, please try again",
														"Network Error",
														JOptionPane.INFORMATION_MESSAGE);
									}
								});
								return null;
							}

							//JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

							jaxbElement = jaxbUtil
									.unMashallFromString(xmlResponse);
							messageType = (ResponseMessageType) jaxbElement
									.getValue();
							bt = messageType.getMessageBody();
							ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
									.getObjectByClass(bt.getAny(), ResultResponseType.class);

							for (QueryResultInstanceType queryResultInstanceType : resultResponseType
									.getQueryResultInstance()) {
								String status = queryResultInstanceType
										.getQueryStatusType().getName();

								QueryResultData resultData = new QueryResultData();
								if (queryResultInstanceType.getQueryResultType().getName()
										.equalsIgnoreCase("PATIENTSET")
										&& UserInfoBean.getInstance().isRoleInProject(
												"DATA_LDS")) {
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
								if ((resultname = queryResultInstanceType
										.getQueryResultType().getDescription()) == null) {
									resultname = queryResultInstanceType
											.getQueryResultType().getName();
								}
								// if (status.equalsIgnoreCase("FINISHED")) {
								if (queryResultInstanceType.getQueryResultType().getName()
										.equals("PATIENTSET") 
										/*|| queryResultInstanceType.getQueryResultType().getName()
										.equals("PATIENT_COUNT_XML")*/) {
									// if (UserInfoBean.getInstance().isRoleInProject(
									// "DATA_OBFSC")) {
									// resultData.name(resultname + " - "
									// + resultData.patientCount() + "� Patients");
									// resultData.tooltip(resultData.patientCount()
									// + "� Patients");
									// } else {
									if (queryResultInstanceType.getDescription() != null) {
										resultname = queryResultInstanceType
												.getDescription();
										resultData.name(queryResultInstanceType
												.getDescription());
									} else {
										resultData.name(resultname + " - "
												+ resultData.patientCount() + " Patients");
									}
									resultData.tooltip(resultData.patientCount()
											+ " Patients");
									// }
								

									resultData.xmlContent(xmlResponse);
									resultData.queryName(runData.queryName());
									resultData.type(queryResultInstanceType
										.getQueryResultType().getName());
									if (!status.equalsIgnoreCase("FINISHED")) {
										resultData.name(resultData.name() + " --- " + status);
									}
									//addNode(resultData, node);
									this.patientSets.add(resultData);
								}
							}
							// }
							// jTree1.scrollPathToVisible(new TreePath(node.getPath()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						/////////////get patient sets//////////////////////
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// jTree1.scrollPathToVisible(new TreePath(node.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "Done";
	}
		

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		java.awt.GridBagConstraints gridBagConstraints;
		System.setProperty("PQSortByTimeCheckBox", "true");
		System.setProperty("PatientSetMaxQueryNumber", "20");
		System.setProperty("PatientSetMaxPatientsNumber", "200");
		System.setProperty("PQSortByNameCheckBox", "false");

		jPanel1 = new javax.swing.JPanel();
        jContainComboBox = new javax.swing.JComboBox();
        jCategoryComboBox = new javax.swing.JComboBox();
        jSearchStringTextField = new javax.swing.JTextField();
        jFindButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTree1 = new javax.swing.JTree();
		jPanel2 = new javax.swing.JPanel();
	    jStartTimeTextField = new javax.swing.JTextField();
	    jBackwardButton = new javax.swing.JButton();
	    jForwardButton = new javax.swing.JButton();
	    jLabel2 = new javax.swing.JLabel();

		setLayout(new java.awt.BorderLayout(20, 10));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Search By Name"));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 50));
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 80));
        jContainComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing", "Start With", "End With", "Exact" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        jPanel1.add(jContainComboBox, gridBagConstraints);

        jCategoryComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Category", "Patient Set", "Patient" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel1.add(jCategoryComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 11;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(jSearchStringTextField, gridBagConstraints);

        jFindButton.setText("Find");
        jFindButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFindButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 2);
        jPanel1.add(jFindButton, gridBagConstraints);
        
        add(jPanel1, java.awt.BorderLayout.NORTH);

		QueryResultData tmpData = new QueryResultData();
		tmpData.name("Pateint Sets by " + UserInfoBean.getInstance().getUserName());
		tmpData.tooltip("Patient Sets");
		tmpData.visualAttribute("CA");
		tmpData.type("PATIENTSET");
		top = new DefaultMutableTreeNode(tmpData);
		// top = new DefaultMutableTreeNode("Root Node");
		treeModel = new DefaultTreeModel(top);
		// treeModel.addTreeModelListener(new MyTreeModelListener());

		jTree1 = new JTree(treeModel);
		jTree1.setEditable(false);

		// jTree1.getSelectionModel().setSelectionMode
		// (TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree1.setShowsRootHandles(true);
		// JScrollPane treeView = new JScrollPane(jTree1);
		jTree1.setRootVisible(false);
		jTree1.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree1.setCellRenderer(new MyRenderer());
		ToolTipManager.sharedInstance().registerComponent(jTree1);

		if (cellStatus.equalsIgnoreCase("CellDown")) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
					"Data Repository Cell is unavailable");
			treeModel.insertNodeInto(childNode, top, top.getChildCount());
			// Make sure the user can see the lovely new node.
			jTree1.expandPath(new TreePath(top.getPath()));
		}

		ArrayList<QueryResultData> queries = patientSets;
		if (queries != null) {
			for (int i = 0; i < queries.size(); i++) {
				addNode(patientSets.get(i));
			}
		}

		jScrollPane1.setViewportView(jTree1);
		add(jScrollPane1, java.awt.BorderLayout.CENTER);

		jTree1.setTransferHandler(new NodeCopyTransferHandler());
		jTree1.addTreeExpansionListener(this);
		jTree1.addTreeWillExpandListener(this);
		
		jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setMinimumSize(new java.awt.Dimension(92, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 244;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jStartTimeTextField, gridBagConstraints);

        jBackwardButton.setText("<");
        jBackwardButton.setMaximumSize(new java.awt.Dimension(43, 22));
        jBackwardButton.setMinimumSize(new java.awt.Dimension(43, 22));
        jBackwardButton.setPreferredSize(new java.awt.Dimension(43, 22));
        jBackwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBackwardButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanel2.add(jBackwardButton, gridBagConstraints);

        jForwardButton.setText(">");
        jForwardButton.setMaximumSize(new java.awt.Dimension(43, 22));
        jForwardButton.setMinimumSize(new java.awt.Dimension(43, 22));
        jForwardButton.setPreferredSize(new java.awt.Dimension(43, 22));
        jForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jForwardButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(0, 43, 0, 3);
        jPanel2.add(jForwardButton, gridBagConstraints);

        jLabel2.setText("Begin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 2);
        jPanel2.add(jLabel2, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.SOUTH);
	}
	
	@SuppressWarnings("rawtypes")
	private void jBackwardButtonActionPerformed(java.awt.event.ActionEvent evt) {
		/*LoginHelper pms = new LoginHelper();
		try {
			PasswordType ptype = new PasswordType();
			ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
			ptype.setTokenMsTimeout(UserInfoBean.getInstance()
					.getUserPasswordTimeout());
			ptype.setValue(UserInfoBean.getInstance().getUserPassword());
			String response = pms.getUserInfo(UserInfoBean.getInstance().getUserName(), ptype, UserInfoBean.getInstance().getSelectedProjectUrl(), 
					UserInfoBean.getInstance().getUserDomain(), false, UserInfoBean.getInstance().getProjectId());
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		System.out.println("Loading previous queries for: "
				+ System.getProperty("user"));
	 
		cellStatus = "";
		//String searchStr = jSearchStringTextField.getText();
		int category = jCategoryComboBox.getSelectedIndex();
		int strategy = jContainComboBox.getSelectedIndex();
	 
		curCreationDate = previousQueries.get(0).creationTime();
		////////////////////////////////////////////////
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");//.getDateInstance();
		Date date = null;
		try {
			date = df.parse(this.jStartTimeTextField.getText());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		DTOFactory dtoFactory = new DTOFactory();

		TimeZone tz = Calendar.getInstance().getTimeZone();
		GregorianCalendar cal = new GregorianCalendar(tz);
		cal.setTime(date);
		//cal.get(Calendar.ZONE_OFFSET);
		int zt_offset = (cal.get(Calendar.ZONE_OFFSET)+cal.get(Calendar.DST_OFFSET))/60000;
		//log.info("Timezone: "+tz.getID()+" : "+zt_offset);
		
		//if (startTime() != -1) {
			ConstrainDateType constraindateType = new ConstrainDateType();
			XMLGregorianCalendar xmlC = dtoFactory.getXMLGregorianCalendarDate(
					cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH)+1, 
					cal.get(GregorianCalendar.DAY_OF_MONTH));
			xmlC.setTimezone(zt_offset);//0);//-5*60);
			xmlC.setHour(cal.get(GregorianCalendar.HOUR_OF_DAY));
			xmlC.setMinute(cal.get(GregorianCalendar.MINUTE));
			xmlC.setSecond(cal.get(GregorianCalendar.SECOND));
			constraindateType.setValue(xmlC);
			//timeConstrain.setDateFrom(constraindateType);
		//}
		////////////////////////////////////////////////
		String xmlStr = writePagingQueryXML("", category, strategy, true, xmlC);//curCreationDate);
		// System.out.println(xmlStr);

		String responseStr = null;
		if (System.getProperty("webServiceMethod").equals("SOAP")) {
			responseStr = QueryListNamesClient.sendQueryRequestSOAP(xmlStr);
		} else {
			responseStr = QueryListNamesClient.sendFindQueryRequestREST(xmlStr);
		}

		if (responseStr.equalsIgnoreCase("CellDown")) {
			cellStatus = new String("CellDown");
			return; //"CellDown";
		}

		try {
			JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseStr);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bt = messageType.getMessageBody();
			MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(
							bt.getAny(),
							edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType.class);
			for (Condition status : masterResponseType.getStatus()
					.getCondition()) {
				if (status.getType().equals("ERROR"))
					cellStatus = new String("CellDown");
			}
			previousQueries = new ArrayList<QueryMasterData>();
			for (QueryMasterType queryMasterType : masterResponseType
					.getQueryMaster()) {
				QueryMasterData tmpData;
				tmpData = new QueryMasterData();
				XMLGregorianCalendar cldr = queryMasterType.getCreateDate();
				tmpData.name(queryMasterType.getName() 
						+ " ["
						+ addZero(cldr.getMonth()) 
						+ "-"
						+ addZero(cldr.getDay()) 
						+ "-"
						+ addZero(cldr.getYear()) 
						+ " ]" 
						+ " ["
						+ queryMasterType.getUserId() 
						+ "]");
				tmpData.creationTime(cldr);//.clone());
				tmpData.creationTimeStr(addZero(cldr.getMonth()) 
						+ "-"
						+ addZero(cldr.getDay()) 
						+ "-"
						+ addZero(cldr.getYear())
						+ " "
						+cldr.getHour()
						+":"
						+cldr.getMinute()
						+":"
						+cldr.getSecond());
				tmpData.tooltip("A query run by "+ queryMasterType.getUserId());
				// System.
				// getProperty
				// ("user"));
				tmpData.visualAttribute("CA");
				tmpData.xmlContent(null);
				tmpData.id(queryMasterType.getQueryMasterId());
				tmpData.userId(queryMasterType.getUserId()); // System.getProperty
				// ("user"));
				previousQueries.add(tmpData);
			}
			
			if (previousQueries.size() == 0) {
				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
										parent,
										"No results were found.",
										"Not Found",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
				return;
			}
			loadPatientSets();
			if (cellStatus.equalsIgnoreCase("")) {
				reset(200, false, true);
			} else if (cellStatus.equalsIgnoreCase("CellDown")) {
				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										parent,
										"Trouble with connection to the remote server, "
												+ "this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	private String writePagingQueryXML(String searchStr, int category, 
			int strategy, boolean ascending, XMLGregorianCalendar cldr) {

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		String userId = UserInfoBean.getInstance().getUserName();
		userType.setLogin(userId);
		userType.setValue(userId);

		headerType.setUser(userType);
		//if (getAllInGroup) {
			//headerType
				//	.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_GROUP_ID);
		//} else {
			//headerType
				//	.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_USER_ID);
		//}

		FindByChildType userRequestType = new FindByChildType();
		String categoryStr = "top";//"@";
		/*if(category == 1) {
			categoryStr = "top";
		}
		else if(category == 2) {
			categoryStr = "results";
		}
		else if(category == 3) {
			categoryStr = "PDO";
		}*/
		
		String strategyStr = "contains";
		/*if(strategy == 1) {
			strategyStr = "left";
		}
		else if(strategy == 2) {
			strategyStr = "right";
		}
		else if(strategy == 3) {
			strategyStr = "exact";
		}*/
		
		userRequestType.setCategory(categoryStr);
		MatchStrType mStr = new MatchStrType();
		mStr.setValue(searchStr);
		mStr.setStrategy(strategyStr);
		userRequestType.setMatchStr(mStr);

		String maxNum = System.getProperty("QueryToolMaxQueryNumber");
		if (maxNum == null || maxNum.equals("")) {
			userRequestType.setMax(20);
		} else {
			userRequestType.setMax(Integer.parseInt(maxNum));
		}
		
		userRequestType.setAscending(ascending);
		userRequestType.setCreateDate(cldr);//.toGregorianCalendar().getTime().getTime());
		userRequestType.setUserId("demo");
		
		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createGetNameInfo(userRequestType));
		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Generated content XML request: " +
		// strWriter.toString());
		return strWriter.toString();
	}

	@SuppressWarnings("rawtypes")
	private void jForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("Loading previous queries for: "
				+ System.getProperty("user"));
	 
		cellStatus = "";
		//String searchStr = jSearchStringTextField.getText();
		int category = jCategoryComboBox.getSelectedIndex();
		int strategy = jContainComboBox.getSelectedIndex();
	 
		curCreationDate = previousQueries.get(previousQueries.size()-1).creationTime();
		////////////////////////////////////////////////
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");//.getDateInstance();
		Date date = null;
		try {
			date = df.parse(this.jStartTimeTextField.getText());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		DTOFactory dtoFactory = new DTOFactory();

		TimeZone tz = Calendar.getInstance().getTimeZone();
		GregorianCalendar cal = new GregorianCalendar(tz);
		cal.setTime(date);
		//cal.get(Calendar.ZONE_OFFSET);
		int zt_offset = (cal.get(Calendar.ZONE_OFFSET)+cal.get(Calendar.DST_OFFSET))/60000;
		//log.info("Timezone: "+tz.getID()+" : "+zt_offset);
		
		//if (startTime() != -1) {
			ConstrainDateType constraindateType = new ConstrainDateType();
			XMLGregorianCalendar xmlC = dtoFactory.getXMLGregorianCalendarDate(
					cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH)+1, 
					cal.get(GregorianCalendar.DAY_OF_MONTH));
			xmlC.setTimezone(zt_offset);//0);//-5*60);
			xmlC.setHour(cal.get(GregorianCalendar.HOUR_OF_DAY));
			xmlC.setMinute(cal.get(GregorianCalendar.MINUTE));
			xmlC.setSecond(cal.get(GregorianCalendar.SECOND));
			constraindateType.setValue(xmlC);
			//timeConstrain.setDateFrom(constraindateType);
		//}
		////////////////////////////////////////////////
		String xmlStr = writePagingQueryXML("", category, strategy, false, xmlC);//curCreationDate);
		// System.out.println(xmlStr);

		String responseStr = null;
		if (System.getProperty("webServiceMethod").equals("SOAP")) {
			responseStr = QueryListNamesClient.sendQueryRequestSOAP(xmlStr);
		} else {
			responseStr = QueryListNamesClient.sendFindQueryRequestREST(xmlStr);
		}

		if (responseStr.equalsIgnoreCase("CellDown")) {
			cellStatus = new String("CellDown");
			return; //"CellDown";
		}

		try {
			JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseStr);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bt = messageType.getMessageBody();
			MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(
							bt.getAny(),
							edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType.class);
			for (Condition status : masterResponseType.getStatus()
					.getCondition()) {
				if (status.getType().equals("ERROR"))
					cellStatus = new String("CellDown");
			}
			previousQueries = new ArrayList<QueryMasterData>();
			for (QueryMasterType queryMasterType : masterResponseType
					.getQueryMaster()) {
				QueryMasterData tmpData;
				tmpData = new QueryMasterData();
				XMLGregorianCalendar cldr = queryMasterType.getCreateDate();
				tmpData.name(queryMasterType.getName() 
						+ " ["
						+ addZero(cldr.getMonth()) + "-"
						+ addZero(cldr.getDay()) + "-"
						+ addZero(cldr.getYear()) + " ]" 
						+ " ["
						+ queryMasterType.getUserId() + "]");
				tmpData.creationTime(cldr);//.clone());
				tmpData.creationTimeStr(addZero(cldr.getMonth()) 
						+ "-"
						+ addZero(cldr.getDay()) 
						+ "-"
						+ addZero(cldr.getYear())
						+ " "
						+cldr.getHour()
						+":"
						+cldr.getMinute()
						+":"
						+cldr.getSecond());
				tmpData.tooltip("A query run by " + queryMasterType.getUserId());
				// System.
				// getProperty
				// ("user"));
				tmpData.visualAttribute("CA");
				tmpData.xmlContent(null);
				tmpData.id(queryMasterType.getQueryMasterId());
				tmpData.userId(queryMasterType.getUserId()); 
				// System.getProperty
				// ("user"));
				previousQueries.add(tmpData);
			}
			
			if (previousQueries.size() == 0) {
				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
										parent,
										"No results were found.",
										"Not Found",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
				return;
			}
			loadPatientSets();
			if (cellStatus.equalsIgnoreCase("")) {
				reset(200, false, false);
			} else if (cellStatus.equalsIgnoreCase("CellDown")) {
				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										parent,
										"Trouble with connection to the remote server, "
												+ "this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}


	public void reset(int number, boolean byName, boolean left) {
		while (top.getChildCount() > 0) {
			for (int i = 0; i < top.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) top
						.getChildAt(i);
				// System.out.println("Remove node: "+
				// ((QueryTreeNodeData)node.getUserObject()).tooltip());
				treeModel.removeNodeFromParent(node);
			}
		}

		ArrayList<QueryResultData> queries = null;

		if (byName) {
			queries = new ArrayList<QueryResultData>(patientSets);
			Collections.sort(queries, new Comparator<QueryResultData>() {
				public int compare(QueryResultData d1, QueryResultData d2) {
					return java.text.Collator.getInstance().compare(d1.name(),
							d2.name());
				}
			});
		} else {
			queries = patientSets;
		}

		if (queries != null) {
			if (number > queries.size()) {
				number = queries.size();
			}

			if (!ascending) {
				for (int i = queries.size() - number; i < queries.size(); i++) {
					addNode(queries.get(i));
				}
			} else {
				for (int i = queries.size() - 1; i >= queries.size() - number; i--) {
					addNode(queries.get(i));
				}
			}
		}
		if(previousQueries.size()>0) {
			if(left) {
				jStartTimeTextField.setText(previousQueries.get(0).creationTimeStr());
			}
			else {
				jStartTimeTextField.setText(previousQueries.get(previousQueries.size()-1).creationTimeStr());
			}
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

	class NodeCopyTransferHandler extends TransferHandler {
		protected NodeCopyTransferHandler() {
			super("text");
		}

		@SuppressWarnings("rawtypes")
		protected Transferable createTransferable(JComponent c) {

			Transferable t = null;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();

			String str = null;
			if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryMasterData")) {
				StringWriter strWriter = new StringWriter();
				try {
					// JAXBUtil jaxbUtil = PreviousQueryJAXBUtil.getJAXBUtil();

					QueryMasterData ndata = (QueryMasterData) node
							.getUserObject();
					// if(ndata.xmlContent() == null) {
					// setCursor(new Cursor(Cursor.WAIT_CURSOR));
					QueryMasterType queryMasterType = new QueryMasterType();
					queryMasterType.setName(ndata.name().substring(0,
							ndata.name().indexOf("[")));
					queryMasterType.setQueryMasterId(ndata.id());
					queryMasterType.setUserId(ndata.userId());
					queryMasterType.setGroupId(UserInfoBean.getInstance()
							.getProjectId());
					// strWriter = new StringWriter();
					DndType dnd = new DndType();
					edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
					dnd.getAny().add(psmOf.createQueryMaster(queryMasterType));
					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
					PatientSetJAXBUtil.getJAXBUtil().marshaller(
							of.createPluginDragDrop(dnd), strWriter);
				} catch (JAXBUtilException e1) {
					// log.error("Error marshalling Ont drag text");
					// throw e;
					e1.printStackTrace();
				}

				str = strWriter.toString();
				System.out.println("Node xml set to: " + strWriter.toString());
			} else if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryInstanceData")) {
				StringWriter strWriter = new StringWriter();
				try {
					// JAXBUtil jaxbUtil = PreviousQueryJAXBUtil.getJAXBUtil();

					QueryInstanceData ndata = (QueryInstanceData) node
							.getUserObject();
					QueryMasterData mdata = (QueryMasterData) ((DefaultMutableTreeNode) node
							.getParent()).getUserObject();
					QueryInstanceType queryInstanceType = new QueryInstanceType();
					queryInstanceType.setQueryInstanceId(ndata.id());
					queryInstanceType.setUserId(ndata.userId());
					queryInstanceType.setGroupId(UserInfoBean.getInstance()
							.getProjectId());
					queryInstanceType.setName(mdata.name());

					DndType dnd = new DndType();
					edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
					dnd.getAny().add(
							psmOf.createQueryInstance(queryInstanceType));
					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
					PatientSetJAXBUtil.getJAXBUtil().marshaller(
							of.createPluginDragDrop(dnd), strWriter);
				} catch (JAXBUtilException e1) {
					// log.error("Error marshalling Ont drag text");
					// throw e;
					e1.printStackTrace();
				}

				str = strWriter.toString();
				System.out.println("Node xml set to: " + strWriter.toString());
			} else if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryResultData")) {
				QueryData nodedata = (QueryData) node.getUserObject();
				str = nodedata.name() + ":"
						+ ((QueryResultData) nodedata).patientRefId();
				if (str.equalsIgnoreCase("working ......")) {
					str = "logicquery";
				}

				StringWriter strWriter = new StringWriter();
				try {
					JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

					JAXBElement jaxbElement = jaxbUtil
							.unMashallFromString(nodedata.xmlContent());
					ResponseMessageType messageType = (ResponseMessageType) jaxbElement
							.getValue();
					BodyType bt = messageType.getMessageBody();
					ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
							.getObjectByClass(bt.getAny(), ResultResponseType.class);
					//int index = node.getParent().getIndex(node);
					QueryResultInstanceType queryResultInstanceType = resultResponseType
							.getQueryResultInstance().get(0);					
					for(int i=0; i<resultResponseType.getQueryResultInstance().size(); i++) {
						queryResultInstanceType = resultResponseType.getQueryResultInstance().get(i);
						if(queryResultInstanceType.getQueryResultType().getName().equalsIgnoreCase("patientset")) {
							break;
						}
					}
					queryResultInstanceType.setQueryInstanceId(((QueryResultData) nodedata).patientRefId());
					queryResultInstanceType.setDescription(((QueryResultData) nodedata).name());
					DndType dnd = new DndType();
					edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
					dnd.getAny().add(psmOf.createQueryResultInstance(queryResultInstanceType));
					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
					PatientSetJAXBUtil.getJAXBUtil().marshaller(
							of.createPluginDragDrop(dnd), strWriter);
				} catch (JAXBUtilException e) {
					// log.error("Error marshalling Ont drag text");
					// throw e;
					e.printStackTrace();
				}

				// log.info("Ont Client dragged "+ strWriter.toString());
				str = strWriter.toString();
				System.out.println("Node xml set to: " + strWriter.toString());
			} else if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("PatientData")) {
				PatientData nodedata = (PatientData) node.getUserObject();
				str = nodedata.name();
				if (str.equalsIgnoreCase("working ......")) {
					str = "logicquery";
				}

				StringWriter strWriter = new StringWriter();
				try {
					//JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

					// JAXBElement jaxbElement =
					// jaxbUtil.unMashallFromString(nodedata.xmlContent());
					// ResponseMessageType messageType =
					// (ResponseMessageType)jaxbElement.getValue();
					// BodyType bt = messageType.getMessageBody();
					// ResultResponseType resultResponseType =
					// (ResultResponseType) new
					// JAXBUnWrapHelper().getObjectByClass(bt.getAny(),
					// ResultResponseType.class);
					// QueryResultInstanceType queryResultInstanceType =
					// resultResponseType.getQueryResultInstance().get(0);
					// strWriter = new StringWriter();

					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientType patientType = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientType();
					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientSet patientSet = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientSet();
					patientType.setPatientId(nodedata.patientID());
					// patientType.setUploadId(nodedata.patientSetID());
					patientSet.setPatientSetId(nodedata.patientSetID());
					patientSet.setPatientSetName(nodedata.queryName());
					patientSet.getPatient().add(patientType);

					DndType dnd = new DndType();
					// edu.harvard.i2b2.crcxmljaxb.datavo.pdo.ObjectFactory
					// pdoOf = new
					// edu.harvard.i2b2.crcxmljaxb.datavo.pdo.ObjectFactory();
					// dnd.getAny().add(patientType);
					dnd.getAny().add(patientSet);
					edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
					PatientSetJAXBUtil.getJAXBUtil().marshaller(
							of.createPluginDragDrop(dnd), strWriter);
				} catch (JAXBUtilException e) {
					// log.error("Error marshalling Ont drag text");
					// throw e;
					e.printStackTrace();
				}

				// log.info("Ont Client dragged "+ strWriter.toString());
				str = strWriter.toString();
				System.out.println("Node xml set to: " + strWriter.toString());
			}

			t = new QueryDataTransferable(str);
			return t;
			// return new StringSelection(str);
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
			// return TransferHandler.COPY_OR_MOVE;
		}
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
					.equalsIgnoreCase("String")) {
				String nodeInfo = (String) (node.getUserObject());
				setText(nodeInfo);
				setToolTipText(nodeInfo);
				setIcon(null);
				setForeground(Color.RED);
			} else {
				QueryData nodeInfo = (QueryData) (node.getUserObject());
				setToolTipText(nodeInfo.tooltip());
				setIcon(getImageIcon(nodeInfo));
			}
			// else
			// if(node.getUserObject().getClass().getSimpleName().equalsIgnoreCase
			// ("QueryInstanceData"))
			// {
			// QueryInstanceData nodeInfo =
			// (QueryInstanceData)(node.getUserObject());
			// setToolTipText(nodeInfo.tooltip());
			// setIcon(getImageIcon(nodeInfo));
			// }
			// else {
			// setToolTipText(null);
			// }

			return this;
		}

		private ImageIcon getImageIcon(QueryData data) {
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
				if (data.name().equalsIgnoreCase("working ......")) {
					key = "leaf";
				} else {
					key = "plainpeople";
				}
			} else if (data.visualAttribute().substring(0, 1).equals("M")) {
				key = "leaf";
			}

			if (data.getClass().getSimpleName().equalsIgnoreCase(
					"QueryResultData")) {

				QueryResultData node = (QueryResultData) data;
				if (node.type().equalsIgnoreCase("PATIENTSET"))
					key = "patient_coll";
				else if (node.type().equalsIgnoreCase("PATIENT_COUNT_XML"))
					key = "patient_count_xml";
				else
					key = "morepeople";
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
	
	 @SuppressWarnings("rawtypes")
	private void jFindButtonActionPerformed(java.awt.event.ActionEvent evt) { 
		 System.out.println("Loading previous queries for: "
					+ System.getProperty("user"));
		 cellStatus = "";
		 String searchStr = this.jSearchStringTextField.getText();
		 int category = this.jCategoryComboBox.getSelectedIndex();
		 int strategy = this.jContainComboBox.getSelectedIndex();
		 
			String xmlStr = writeFindQueryXML("all users", searchStr, category, strategy);
			// System.out.println(xmlStr);

			String responseStr = null;
			if (System.getProperty("webServiceMethod").equals("SOAP")) {
				responseStr = QueryListNamesClient.sendQueryRequestSOAP(xmlStr);
			} else {
				responseStr = QueryListNamesClient.sendFindQueryRequestREST(xmlStr);
			}

			if (responseStr.equalsIgnoreCase("CellDown")) {
				cellStatus = new String("CellDown");
				return; //"CellDown";
			}

			try {
				JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
				JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseStr);
				ResponseMessageType messageType = (ResponseMessageType) jaxbElement
						.getValue();
				BodyType bt = messageType.getMessageBody();
				MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
						.getObjectByClass(
								bt.getAny(),
								edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterResponseType.class);
				for (Condition status : masterResponseType.getStatus()
						.getCondition()) {
					if (status.getType().equals("ERROR"))
						cellStatus = new String("CellDown");
				}
				previousQueries = new ArrayList<QueryMasterData>();
				for (QueryMasterType queryMasterType : masterResponseType
						.getQueryMaster()) {
					QueryMasterData tmpData;
					tmpData = new QueryMasterData();
					XMLGregorianCalendar cldr = queryMasterType.getCreateDate();
					tmpData.name(queryMasterType.getName() + " ["
							+ addZero(cldr.getMonth()) + "-"
							+ addZero(cldr.getDay()) + "-"
							+ addZero(cldr.getYear()) + " ]" + " ["
							+ queryMasterType.getUserId() + "]");
					tmpData
							.tooltip("A query run by "
									+ queryMasterType.getUserId());// System.
					// getProperty
					// ("user"));
					tmpData.visualAttribute("CA");
					tmpData.xmlContent(null);
					tmpData.id(queryMasterType.getQueryMasterId());
					tmpData.userId(queryMasterType.getUserId()); // System.getProperty
					// ("user"));
					previousQueries.add(tmpData);
				}
				
				if (previousQueries.size() == 0) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(
											parent,
											"No results were found.",
											"Not Found",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
					return;
				}
				
				loadPatientSets();
				if (cellStatus.equalsIgnoreCase("")) {
					reset(200, false, false);
				} else if (cellStatus.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	 }

	private void createPopupMenu() {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		menuItem = new JMenuItem("Rename ...");
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
		// java.awt.event.KeyEvent.VK_R,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);

		/* popup.add(new javax.swing.JSeparator()); */

		menuItem = new JMenuItem("Delete");
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
		// java.awt.event.KeyEvent.VK_D,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);

		popup.add(new javax.swing.JSeparator());

		menuItem = new JMenuItem("Refresh All");
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
		// java.awt.event.KeyEvent.VK_A,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);

		menuItem = new JMenuItem("Cancel");
		// menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
		// java.awt.event.KeyEvent.VK_C,
		// java.awt.event.InputEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		popup.add(menuItem);

		// Add listener to the tree
		MouseListener popupListener = new PreviousRunsTreePopupListener(popup);
		jTree1.addMouseListener(popupListener);
		jTree1.addMouseMotionListener(new PreviousRunsTreeMouseMoveListener());
	}

	@SuppressWarnings("rawtypes")
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Rename ...")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryMasterData")) {

				QueryMasterData ndata = (QueryMasterData) node.getUserObject();
				Object inputValue = JOptionPane.showInputDialog(this,
						"Rename this query to: ", "Rename Query Dialog",
						JOptionPane.PLAIN_MESSAGE, null, null, ndata.name()
								.substring(0, ndata.name().indexOf("[") - 1));

				if (inputValue != null) {
					String newQueryName = (String) inputValue;
					String requestXml = ndata.writeRenameQueryXML(newQueryName);

					setCursor(new Cursor(Cursor.WAIT_CURSOR));

					String response = null;
					if (System.getProperty("webServiceMethod").equals("SOAP")) {
						// TO DO
						// response =
						// QueryListNamesClient.sendQueryRequestSOAP(requestXml);
					} else {
						response = QueryListNamesClient
								.sendQueryRequestREST(requestXml, true);
					}

					if (response.equalsIgnoreCase("CellDown")) {
						final JPanel parent = this;
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								JOptionPane
										.showMessageDialog(
												parent,
												"Trouble with connection to the remote server, "
														+ "this is often a network error, please try again",
												"Network Error",
												JOptionPane.INFORMATION_MESSAGE);
							}
						});
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return;
					}

					if (response != null) {
						JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

						try {
							JAXBElement jaxbElement = jaxbUtil
									.unMashallFromString(response);
							ResponseMessageType messageType = (ResponseMessageType) jaxbElement
									.getValue();
							StatusType statusType = messageType
									.getResponseHeader().getResultStatus()
									.getStatus();
							String status = statusType.getType();

							if (status.equalsIgnoreCase("DONE")) {
								// XMLGregorianCalendar cldr =
								// //queryMasterType.getCreateDate();
								Calendar cldr = Calendar.getInstance(Locale
										.getDefault());
								ndata.name(newQueryName
										+ " ["
										+ addZero(cldr.get(Calendar.MONTH) + 1)
										+ "-"
										+ addZero(cldr
												.get(Calendar.DAY_OF_MONTH))
										+ "-"
										+ addZero(cldr.get(Calendar.YEAR))
										+ " ]" + " [" + ndata.userId() + "]");
								// ndata.name(newQueryName + " [" +
								// ndata.userId()
								// + "]");
								node.setUserObject(ndata);
								// DefaultMutableTreeNode parent =
								// (DefaultMutableTreeNode) node.getParent();

								jTree1.repaint();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} else if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryResultData")) {

				QueryResultData rdata = (QueryResultData) node.getUserObject();
				// if(!rdata.type().equalsIgnoreCase("PatientSet")) {
				// return;
				// }

				Object inputValue1 = JOptionPane.showInputDialog(this,
						"Rename this to: ", "Renaming Dialog",
						JOptionPane.PLAIN_MESSAGE, null, null, rdata.name());
				// .substring(0, rdata.name().lastIndexOf("[") - 1));

				if (inputValue1 != null) {
					String newQueryName = (String) inputValue1;
					String requestXml = rdata.writeRenameQueryXML(newQueryName);

					setCursor(new Cursor(Cursor.WAIT_CURSOR));

					String response = null;
					if (System.getProperty("webServiceMethod").equals("SOAP")) {
						// TO DO
						// response =
						// QueryListNamesClient.sendQueryRequestSOAP(requestXml);
					} else {
						response = QueryListNamesClient
								.sendQueryRequestREST(requestXml, true);
					}

					if (response.equalsIgnoreCase("CellDown")) {
						final JPanel parent = this;
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								JOptionPane
										.showMessageDialog(
												parent,
												"Trouble with connection to the remote server, "
														+ "this is often a network error, please try again",
												"Network Error",
												JOptionPane.INFORMATION_MESSAGE);

							}
						});
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return;
					}

					if (response != null) {
						JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

						try {
							JAXBElement jaxbElement = jaxbUtil
									.unMashallFromString(response);
							ResponseMessageType messageType = (ResponseMessageType) jaxbElement
									.getValue();
							StatusType statusType = messageType
									.getResponseHeader().getResultStatus()
									.getStatus();
							String status = statusType.getType();

							if (status.equalsIgnoreCase("DONE")) {
								rdata.name(newQueryName);// + " [" +
								// rdata.userId()
								// + "]");
								node.setUserObject(rdata);
								// DefaultMutableTreeNode parent =
								// (DefaultMutableTreeNode) node.getParent();

								jTree1.repaint();
							} else {
								final String tmp = response;
								final JPanel parent = this;
								java.awt.EventQueue.invokeLater(new Runnable() {
									public void run() {
										JOptionPane
												.showMessageDialog(
														parent,
														"Error message delivered from the remote server, "
																+ "you may wish to retry your last action",
														"Server Error",
														JOptionPane.INFORMATION_MESSAGE);
										log.error(tmp);
										setCursor(new Cursor(
												Cursor.DEFAULT_CURSOR));
									}

								});

								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else if (e.getActionCommand().equalsIgnoreCase("Cancel")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryMasterData")) {
				DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) node
						.getFirstChild();
				if (node1.getUserObject().getClass().getSimpleName()
						.equalsIgnoreCase("QueryMasterData")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Please expand this node then try to cancel it.",
											"Message",
											JOptionPane.INFORMATION_MESSAGE);

						}
					});
					return;
				}
				QueryInstanceData rdata = (QueryInstanceData) node1
						.getUserObject();

				String requestXml = rdata.writeCancelQueryXML();

				setCursor(new Cursor(Cursor.WAIT_CURSOR));

				String response = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					// TO DO
					// response =
					// QueryListNamesClient.sendQueryRequestSOAP(requestXml);
				} else {
					response = QueryListNamesClient
							.sendQueryRequestREST(requestXml, true);
				}

				if (response.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);

						}
					});
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				}

				if (response != null) {
					JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

					try {
						JAXBElement jaxbElement = jaxbUtil
								.unMashallFromString(response);
						ResponseMessageType messageType = (ResponseMessageType) jaxbElement
								.getValue();
						StatusType statusType = messageType.getResponseHeader()
								.getResultStatus().getStatus();
						String status = statusType.getType();
						final JPanel parent = this;
						if (status.equalsIgnoreCase("DONE")) {
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(parent,
											"The query is finished.",
											"Message",
											JOptionPane.INFORMATION_MESSAGE);
									// log.error(tmp);
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}

							});
							rdata.name(rdata.name().substring(0,
									rdata.name().indexOf("-")));// + " [" +
							// rdata
							// .userId()
							// + "]");
							node.setUserObject(rdata);
							// DefaultMutableTreeNode parent =
							// (DefaultMutableTreeNode) node.getParent();

							jTree1.repaint();
						} else {
							final String tmp = response;
							// final JPanel parent = this;
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													parent,
													"Error message delivered from the remote server, "
															+ "you may wish to retry your last action",
													"Server Error",
													JOptionPane.INFORMATION_MESSAGE);
									log.error(tmp);
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}

							});

							return;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// }
				}
			} else if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryInstanceData")) {

				QueryInstanceData rdata = (QueryInstanceData) node
						.getUserObject();

				String requestXml = rdata.writeCancelQueryXML();

				setCursor(new Cursor(Cursor.WAIT_CURSOR));

				String response = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					// TO DO
					// response =
					// QueryListNamesClient.sendQueryRequestSOAP(requestXml);
				} else {
					response = QueryListNamesClient
							.sendQueryRequestREST(requestXml, true);
				}

				if (response.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);

						}
					});
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				}

				if (response != null) {
					JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

					try {
						JAXBElement jaxbElement = jaxbUtil
								.unMashallFromString(response);
						ResponseMessageType messageType = (ResponseMessageType) jaxbElement
								.getValue();
						StatusType statusType = messageType.getResponseHeader()
								.getResultStatus().getStatus();
						String status = statusType.getType();
						final JPanel parent = this;
						if (status.equalsIgnoreCase("DONE")) {
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(parent,
											"The query is finished.",
											"Message",
											JOptionPane.INFORMATION_MESSAGE);
									// log.error(tmp);
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}

							});
							rdata.name(rdata.name().substring(0,
									rdata.name().indexOf("-")));// + " [" +
							// rdata
							// .userId()
							// + "]");
							node.setUserObject(rdata);
							// DefaultMutableTreeNode parent =
							// (DefaultMutableTreeNode) node.getParent();

							jTree1.repaint();
						} else {
							final String tmp = response;
							// final JPanel parent = this;
							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													parent,
													"Error message delivered from the remote server, "
															+ "you may wish to retry your last action",
													"Server Error",
													JOptionPane.INFORMATION_MESSAGE);
									log.error(tmp);
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}

							});

							return;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// }
				}
			} else {
				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {

					public void run() {
						JOptionPane.showMessageDialog(parent,
								"Cancel action is not supported on this level",
								"Message", JOptionPane.INFORMATION_MESSAGE);
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}

				});
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else if (e.getActionCommand().equalsIgnoreCase("Delete")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			QueryMasterData ndata = (QueryMasterData) node.getUserObject();
			Object selectedValue = JOptionPane.showConfirmDialog(this,
					"Delete Query \"" + ndata.name() + "\"?",
					"Delete Query Dialog", JOptionPane.YES_NO_OPTION);
			if (selectedValue.equals(JOptionPane.YES_OPTION)) {
				System.out.println("delete " + ndata.name());
				String requestXml = ndata.writeDeleteQueryXML();
				// System.out.println(requestXml);

				setCursor(new Cursor(Cursor.WAIT_CURSOR));

				String response = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					// TO DO
					// response =
					// QueryListNamesClient.sendQueryRequestSOAP(requestXml);
				} else {
					response = QueryListNamesClient
							.sendQueryRequestREST(requestXml, true);
				}

				if (response.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				}

				if (response != null) {
					JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

					try {
						JAXBElement jaxbElement = jaxbUtil
								.unMashallFromString(response);
						ResponseMessageType messageType = (ResponseMessageType) jaxbElement
								.getValue();
						StatusType statusType = messageType.getResponseHeader()
								.getResultStatus().getStatus();
						String status = statusType.getType();

						if (status.equalsIgnoreCase("DONE")) {
							treeModel.removeNodeFromParent(node);

							// jTree1.repaint();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		} else if (e.getActionCommand().equalsIgnoreCase("Refresh All")) {
			String status = "";
			if (isManager) {
				status = loadPreviousQueries(true);
			} else {
				status = loadPreviousQueries(false);
			}
			loadPatientSets();
			if (status.equalsIgnoreCase("")) {
				reset(200, false, false);
			} else if (status.equalsIgnoreCase("CellDown")) {
				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										parent,
										"Trouble with connection to the remote server, "
												+ "this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
	}

	public void refresh() {
		String status = "";
		if (isManager) {
			status = loadPreviousQueries(true);
		} else {
			status = loadPreviousQueries(false);
		}
		loadPatientSets();
		if (status.equalsIgnoreCase("")) {
			reset(200, false, false);
		} else if (status.equalsIgnoreCase("CellDown")) {
			final JPanel parent = this;
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					JOptionPane
							.showMessageDialog(
									parent,
									"Trouble with connection to the remote server, "
											+ "this is often a network error, please try again",
									"Network Error",
									JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	class PreviousRunsTreeMouseMoveListener extends MouseMotionAdapter {

		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
					.getSelectionPath().getLastPathComponent();
			if (node.getUserObject().getClass().getSimpleName()
					.equalsIgnoreCase("QueryResultData")) {
				QueryResultData data = (QueryResultData) node.getUserObject();
				if (!(data.type().equalsIgnoreCase("PATIENTSET"))
						&& !(data.type().equalsIgnoreCase("PATIENT_COUNT_XML"))
						&& !(data.type()
								.equalsIgnoreCase("PATIENT_ENCOUNTER_SET"))) {
					return;
				}
			}

			// if (node.getUserObject().getClass().getSimpleName()
			// .equalsIgnoreCase("QueryInstanceData")) {
			// return;
			// }

			JComponent c = (JComponent) e.getSource();
			TransferHandler th = c.getTransferHandler();
			th.exportAsDrag(c, e, TransferHandler.COPY);
		}
	}

	class PreviousRunsTreePopupListener extends MouseAdapter {

		JPopupMenu popup;

		PreviousRunsTreePopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			if (!(e.isMetaDown() || e.isPopupTrigger())
					&& (jTree1.getSelectionPath() != null)
					&& e.getClickCount() == 1 && e.getX() > 15000) {

				TreePath path = jTree1.getPathForLocation(e.getX(), e.getY());
				DefaultMutableTreeNode clickednode = null;
				if (path != null) {
					clickednode = (DefaultMutableTreeNode) path
							.getLastPathComponent();
				}

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1
						.getSelectionPath().getLastPathComponent();
				if (node != null
						&& node.getUserObject().getClass().getSimpleName()
								.equalsIgnoreCase("QueryMasterData")
						&& clickednode != null
						&& clickednode.getUserObject().getClass()
								.getSimpleName().equalsIgnoreCase(
										"QueryMasterData")) {

					setCursor(new Cursor(Cursor.WAIT_CURSOR));
					StringWriter strWriter = new StringWriter();
					try {
						//JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

						QueryMasterData ndata = (QueryMasterData) node
								.getUserObject();
						// if(ndata.xmlContent() == null) {
						setCursor(new Cursor(Cursor.WAIT_CURSOR));
						QueryMasterType queryMasterType = new QueryMasterType();
						queryMasterType.setName(ndata.name());
						queryMasterType.setQueryMasterId(ndata.id());
						queryMasterType.setUserId(ndata.userId());
						queryMasterType.setGroupId(UserInfoBean.getInstance()
								.getProjectId());

						// strWriter = new StringWriter();
						DndType dnd = new DndType();
						edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
						dnd.getAny().add(
								psmOf.createQueryMaster(queryMasterType));
						edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
						PatientSetJAXBUtil.getJAXBUtil().marshaller(
								of.createPluginDragDrop(dnd), strWriter);
					} catch (JAXBUtilException e1) {
						// log.error("Error marshalling Ont drag text");
						// throw e;
						e1.printStackTrace();
					}
					/*
					 * String xmlcontent = null; String xmlrequest = null;
					 * 
					 * xmlrequest = ndata.writeDefinitionQueryXML();
					 * lastRequestMessage = xmlrequest;
					 * 
					 * xmlcontent =
					 * QueryListNamesClient.sendQueryRequest(xmlrequest);
					 * lastResponseMessage = xmlcontent;
					 * 
					 * if(xmlcontent == null) { setCursor(new
					 * Cursor(Cursor.DEFAULT_CURSOR)); return; } else {
					 * System.out.println("Query content response:
					 * "+xmlcontent); ndata.xmlContent(xmlcontent); }
					 */

					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					// }

					// if(parent != null) {
					// parent.queryPanel().getTopPanel().reset();
					// parent.queryPanel().dataModel().redrawPanelFromXml(ndata.
					// xmlContent());
					// }

					IWorkbenchPage page = parentView.getViewSite().getPage();
					ViewPart queryview = (ViewPart) page
							.findView("edu.harvard.i2b2.eclipse.plugins.query.views.QueryView");
					// if(queryview == null) {
					// try {
					// queryview = (ViewPart)
					// page.showView(
					// "edu.harvard.i2b2.eclipse.plugins.query.views.QueryView"
					// );
					// }
					// catch(Exception ex) {
					// ex.printStackTrace();
					// }
					// }
					System.out.println("Sending Node xml to: "
							+ queryview.getTitle() + "\n"
							+ strWriter.toString());
					// System.out.println("First view title:
					// "+queryview.getTitle());
					// queryview.setInitializationData(null, null,
					// ndata.xmlContent());

					((ICommonMethod) queryview).doSomething(strWriter
							.toString());// ndata.xmlContent());
				}
			}

			maybeShowPopup(e);
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);

			// //remove this after the mac 10.5 (leopard) fixed its mouse
			// dragging event bug////
			/*if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
				JComponent c = (JComponent) e.getSource();
				TransferHandler th = c.getTransferHandler();
				try {
					th.exportAsDrag(c, e, TransferHandler.COPY);
				} catch (Exception e2) {
					log.error(e2.getMessage());
				}
			}*/

		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		@SuppressWarnings("unused")
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				TreePath path = jTree1.getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					// DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
					// node.getParent();
					// if (node.isLeaf()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
					jTree1.setSelectionPath(path);
					// }
				}
			}
		}
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryData data = (QueryData) node.getUserObject();

		System.out.println("Node collapsed: " + data.name());

		if (data.visualAttribute().equals("FAO")) {
			data.visualAttribute("FA");
		} else if (data.visualAttribute().equals("CAO")) {
			data.visualAttribute("CA");
		}
	}

	public void treeExpanded(TreeExpansionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();
		QueryData data = (QueryData) node.getUserObject();

		/*if (node.getUserObject().getClass().getSimpleName().equalsIgnoreCase(
				"QueryResultData")) {
			QueryResultData rdata = (QueryResultData) node.getUserObject();
			if (rdata.patientCount().equalsIgnoreCase("0")) {
				return;
				/*
				 * final JPanel parent = this;
				 * java.awt.EventQueue.invokeLater(new Runnable() { public void
				 * run() { JOptionPane.showMessageDialog(parent, "There's no
				 * patient in this set.", "Message",
				 * JOptionPane.INFORMATION_MESSAGE); } });
				 */

			//}
		//}*/

		jTree1.scrollPathToVisible(new TreePath(node));

		System.out.println("Node expanded: " + data.name());

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
			if (((QueryData) node1.getUserObject()).visualAttribute().equals(
					"LAO")
					&& ((QueryData) node1.getUserObject()).name().equals(
							"working ......")) {
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
				QueryData adata = (QueryData) anode.getUserObject();
				if (adata.visualAttribute().equals("FAO")) {
					adata.visualAttribute("FA");
				} else if (adata.visualAttribute().equals("CAO")) {
					adata.visualAttribute("CA");
				}
			}
		}
	}

	private String addZero(int number) {
		String result = new Integer(number).toString();
		if (number < 10 && number >= 0) {
			result = "0" + result;
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private void populateChildNodes(DefaultMutableTreeNode node) {
		if (node.getUserObject().getClass().getSimpleName().equalsIgnoreCase(
				"QueryMasterData")) {
			QueryMasterData data = (QueryMasterData) node.getUserObject();
			try {
				String xmlRequest = data.writeContentQueryXML();

				String xmlResponse = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					xmlResponse = QueryListNamesClient
							.sendQueryRequestSOAP(xmlRequest);
				} else {
					xmlResponse = QueryListNamesClient
							.sendQueryRequestREST(xmlRequest, true);
				}
				if (xmlResponse.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
					return;
				}

				try {
					JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
					JAXBElement jaxbElement = jaxbUtil
							.unMashallFromString(xmlResponse);
					ResponseMessageType messageType = (ResponseMessageType) jaxbElement
							.getValue();

					BodyType bt = messageType.getMessageBody();
					InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
							.getObjectByClass(bt.getAny(),
									InstanceResponseType.class);

					for (QueryInstanceType queryInstanceType : instanceResponseType
							.getQueryInstance()) {
						// change later for working with new xml schema
						// RunQuery runQuery =

						QueryInstanceData runData = new QueryInstanceData();

						runData.visualAttribute("FA");
						if (queryInstanceType.getQueryStatusType().getName()
								.equalsIgnoreCase("completed")) {
							XMLGregorianCalendar sCldr = queryInstanceType
									.getStartDate();
							XMLGregorianCalendar eCldr = queryInstanceType
									.getEndDate();
							long diff = eCldr.toGregorianCalendar()
									.getTimeInMillis()
									- sCldr.toGregorianCalendar()
											.getTimeInMillis();
							runData.tooltip("All results are available, run "
									+ (diff / 1000) + " seconds");
						}
						runData.id(queryInstanceType.getQueryInstanceId());
						// runData.patientRefId(new
						// Integer(queryInstanceType.getRefId()).toString());
						// runData.patientCount(new
						// Long(queryInstanceType.getCount()).toString());
						// XMLGregorianCalendar cldr =
						// queryInstanceType.getStartDate();
						/*
						 * runData.name("Results of "+
						 * "["+addZero(cldr.getMonth(
						 * ))+"-"+addZero(cldr.getDay())+"-"
						 * +addZero(cldr.getYear())+"
						 * "+addZero(cldr.getHour())+":"
						 * +addZero(cldr.getMinute())
						 * +":"+addZero(cldr.getSecond())+"]");
						 */
						runData.name("Results of "
								+ data.name().substring(0,
										data.name().indexOf("[")));
						runData.queryName(data.name());
						data.runs.add(runData);
						if (!queryInstanceType.getQueryStatusType().getName()
								.equalsIgnoreCase("completed")) {
							runData.name(runData.name()
									+ " --- "
									+ queryInstanceType.getQueryStatusType()
											.getName());
							runData.tooltip("The results of the query run");
						}
						addNode(runData, node);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// jTree1.scrollPathToVisible(new TreePath(node.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (node.getUserObject().getClass().getSimpleName()
				.equalsIgnoreCase("QueryInstanceData")) {
			QueryInstanceData data = (QueryInstanceData) node.getUserObject();

			try {
				String xmlRequest = data.writeContentQueryXML();

				String xmlResponse = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					xmlResponse = QueryListNamesClient
							.sendQueryRequestSOAP(xmlRequest);
				} else {
					xmlResponse = QueryListNamesClient
							.sendQueryRequestREST(xmlRequest, true);
				}
				if (xmlResponse.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
					return;
				}

				JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();

				JAXBElement jaxbElement = jaxbUtil
						.unMashallFromString(xmlResponse);
				ResponseMessageType messageType = (ResponseMessageType) jaxbElement
						.getValue();
				BodyType bt = messageType.getMessageBody();
				ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
						.getObjectByClass(bt.getAny(), ResultResponseType.class);

				for (QueryResultInstanceType queryResultInstanceType : resultResponseType
						.getQueryResultInstance()) {
					String status = queryResultInstanceType
							.getQueryStatusType().getName();

					QueryResultData resultData = new QueryResultData();
					if (queryResultInstanceType.getQueryResultType().getName()
							.equalsIgnoreCase("PATIENTSET")
							&& UserInfoBean.getInstance().isRoleInProject(
									"DATA_LDS")) {
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
					if ((resultname = queryResultInstanceType
							.getQueryResultType().getDescription()) == null) {
						resultname = queryResultInstanceType
								.getQueryResultType().getName();
					}
					// if (status.equalsIgnoreCase("FINISHED")) {
					if (queryResultInstanceType.getQueryResultType().getName()
							.equals("PATIENTSET") 
							/*|| queryResultInstanceType.getQueryResultType().getName()
							.equals("PATIENT_COUNT_XML")*/) {
						// if (UserInfoBean.getInstance().isRoleInProject(
						// "DATA_OBFSC")) {
						// resultData.name(resultname + " - "
						// + resultData.patientCount() + "� Patients");
						// resultData.tooltip(resultData.patientCount()
						// + "� Patients");
						// } else {
						if (queryResultInstanceType.getDescription() != null) {
							resultname = queryResultInstanceType
									.getDescription();
							resultData.name(queryResultInstanceType
									.getDescription());
						} else {
							resultData.name(resultname + " - "
									+ resultData.patientCount() + " Patients");
						}
						resultData.tooltip(resultData.patientCount()
								+ " Patients");
						// }
					} else {
						if (queryResultInstanceType.getDescription() != null) {
							resultname = queryResultInstanceType
									.getDescription();
							resultData.name(queryResultInstanceType
									.getDescription());
						} else {
							resultData.name(resultname);// + " - " + status);
							resultData.tooltip(status);
						}

					}

					resultData.xmlContent(xmlResponse);
					resultData.queryName(data.queryName());
					resultData.type(queryResultInstanceType
							.getQueryResultType().getName());
					if (!status.equalsIgnoreCase("FINISHED")) {
						resultData.name(resultData.name() + " --- " + status);
					}
					addNode(resultData, node);
				}
				// }
				// jTree1.scrollPathToVisible(new TreePath(node.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (node.getUserObject().getClass().getSimpleName()
				.equalsIgnoreCase("QueryResultData")) {
			QueryResultData data = (QueryResultData) node.getUserObject();
			if (data.patientCount().equalsIgnoreCase("0")) {
				return;
			}
			int maxNumPatientsToDisplay = Integer.valueOf(System
					.getProperty("PQMaxPatientsNumber"));
			if (Integer.valueOf(data.patientCount()) > maxNumPatientsToDisplay) {
				final JPanel parent = this;
				result = JOptionPane
						.showConfirmDialog(
								parent,
								"The patient count is greater than maximum configured to be displayed.\n"
										+ "Populating the patient list may affect performance. \n"
										+ "Do you want to continue?",
								"Please Note ...", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode) node
							.getChildAt(0);
					QueryData tmpdata = (QueryData) tmpnode.getUserObject();
					if (tmpdata.name().equalsIgnoreCase("working ......")) {
						tmpdata.name("Over maximum number of patient nodes");
						treeModel.reload(tmpnode);
					}
					return;
				}
			}
			try {
				String xmlRequest = data.writeContentQueryXML();

				String xmlResponse = null;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					xmlResponse = QueryListNamesClient.sendPDORequestSOAP(
							xmlRequest, showName());
				} else {
					xmlResponse = QueryListNamesClient
							.sendPdoRequestREST(xmlRequest);
				}
				if (xmlResponse.equalsIgnoreCase("CellDown")) {
					final JPanel parent = this;
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
					return;
				}

				// check response status here ......

				// System.out.println("Response: "+xmlResponse);
				PatientSet patientSet = new PDOResponseMessageFactory()
						.getPatientSetFromResponseXML(xmlResponse);
				List<PatientType> patients = patientSet.getPatient();
				System.out.println("Patient set size: " + patients.size());

				for (int i = 0; i < patients.size(); i++) {
					PatientType patient = patients.get(i);

					PatientData pData = new PatientData();
					pData.patientID(patient.getPatientId().getValue());
					pData.setParamData(patient.getParam());
					pData.visualAttribute("LAO");
					pData.tooltip("Patient");
					pData.patientSetID(data.patientRefId());
					if (showName() && pData.lastName() != null
							&& pData.firstName() != null) {
						pData.name(pData.patientID()
								+ " ["
								+ pData.lastName().substring(0, 1)
										.toUpperCase()
								+ pData.lastName().substring(1,
										pData.lastName().length())
										.toLowerCase()
								+ ", "
								+ pData.firstName().substring(0, 1)
										.toUpperCase()
								+ pData.firstName().substring(1,
										pData.firstName().length())
										.toLowerCase() + "]");// ["+pData.age()+"
						// y/o
						// "+pData.gender()+"
						// "+pData.race()+"]");
					} else {
						pData.name(pData.patientID() + " [" + pData.age()
								+ " y/o " + pData.gender() + " " + pData.race()
								+ "]");
					}
					pData.queryName(data.queryName());
					addNode(pData, node);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// implement for other type of nodes later!!!
	}

	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {

	}

	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath()
				.getLastPathComponent();

		if (node.getUserObject().getClass().getSimpleName().equalsIgnoreCase(
				"QueryResultData")) {
			//QueryResultData rdata = (QueryResultData) node.getUserObject();
			/*if (rdata.patientCount().equalsIgnoreCase("0")) {

				final JPanel parent = this;
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(parent,
								"There are no patients in this set.",
								"Message", JOptionPane.INFORMATION_MESSAGE);
					}
				});

				return;
			}*/
		}
	}
	
	private void jNameLabelMouseExited(java.awt.event.MouseEvent evt) {
		jLabel1.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
	}

	private void jNameLabelMouseMoved(java.awt.event.MouseEvent evt) {
		jLabel1.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.YELLOW));
		jLabel1.paintImmediately(jLabel1.getVisibleRect());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		jLabel1.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.BLACK));
	}
    
	private class DragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);

			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			// reading the system time to a long
			lEventTime = System.currentTimeMillis();
		}
	}
	
	private class TableDragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			//super.mousePressed(e);
			
			
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);

			/*jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			// reading the system time to a long
			lEventTime = System.currentTimeMillis();*/
			//super.mousePressed(e);
		}
	}
	
	class NameLabelTextHandler extends TransferHandler {
		public NameLabelTextHandler() {
			super("text");
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavor) {
			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.YELLOW));
			jLabel1.paintImmediately(jLabel1.getVisibleRect());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.BLACK));

			if ((System.currentTimeMillis() - lEventTime) > 2000) {

				return true;
			}
			return false;
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		@SuppressWarnings("rawtypes")
		public boolean importData(JComponent comp, Transferable t) {
			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			

	try {    
		final String text = (String) t.getTransferData(DataFlavor.stringFlavor);
		System.out.println(text);
		//String description = "";
		//String id = null;
		
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
		if (tableXml1.getName().equalsIgnoreCase(
				"folders")) {
			JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
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
				GetChildrenType parentType = new GetChildrenType();
				parentType.setBlob(true);
				parentType.setParent(folder.getIndex());///*"\\\\" + this.getData().getTableCd() + "\\" + */this.getData().getIndex());	

				//		log.info(parentType.getParent());
				//		log.info(this.getData().getHierarchy());

				GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
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
					QueryResultData qData = new QueryResultData();
					qData.name("Patinet Set for \""+folder.getName()+"\"");
					qData.visualAttribute("CA");
					qData.id(folder.getIndex());
					qData.tooltip(folder.getTooltip());
					qData.type("PATINETSET");
					addNode(qData);
					//getChildren().clear();
					//getNodesFromXMLString(folders);
					//org.w3c.dom.Element dndElement = folders1.get(0).getWorkXml().getAny().get(0);
					//dndElement.getChildNodes();
					String site = getSiteId(folders1.get(0).getWorkXml());
					log.info("site: "+site);
				}	

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
			}
			/*List children = tableXml1.getChildren();
			//QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
			String resultTypeDescription = "";
			for (Iterator itr = children.iterator(); itr.hasNext();) {
				Element element = (org.jdom.Element) itr.next();

				if (element.getName().equalsIgnoreCase(
						"result_instance_id")) {
					id = element.getText().trim();
					//node.fullname("patient_set_coll_id:" + id);
					System.out.println("key: " + id);
				} else if (element.getName().equalsIgnoreCase(
						"description")) {
					description = element.getText().trim();
					//node.name(description);
					//node.tooltip(description);
					log.info("Description: "
							+ description);

				}
				else if (element.getName().equalsIgnoreCase(
				"query_result_type")) {
					resultTypeDescription = element.getChildTextTrim("name");
					if(!resultTypeDescription.equalsIgnoreCase("PATIENTSET")) {
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								JOptionPane
										.showMessageDialog(jLabel1,
												"Please note, You can not drop this item here.");
							}
						});
						return true;
					}
					//resultType = element;
					log.info("keep set id: " + id);
				}
			}
			//node.originalXml(text);
			//node.visualAttribute("L");
			//if (description!=null && description.indexOf("Encounter Set") >= 0) {
				//node.fullname("patient_set_enc_id:" + id);
			//}
			
			//if(description==null) {
				//node.name(resultTypeDescription);
			//}

			//addNode(node);
			//panelData.getItems().add(node);
			//parentPanel.getRunQueryButton().requestFocus();
			
			
		        	//@SuppressWarnings("unchecked")
					//Class[] types = new Class[] { java.lang.Boolean.class,
							//java.lang.String.class };

					//@SuppressWarnings("unchecked")
					//public Class getColumnClass(int columnIndex) {
						//if(columnIndex ==0) {
							//return java.lang.Boolean.class;
						//}
						//return java.lang.Object.class;
					//}
		       // };
	
			//jLabel1.setText("  Patient Set Identifier: "+description.substring(description.indexOf("\"")));*/
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return true;
		}
		else {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane
							.showMessageDialog(jLabel1,
									"Please note, You can not drop this item here.");
				}
			});
			return true;
		}
				
				
			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						JOptionPane.showMessageDialog(jLabel1,
										"Please note, You can not drop this item here.");
					}
				});
			}

			return true;
		}

		protected Transferable createTransferable(JComponent c) {

			Transferable t = null;
			//String str = jLabel1.getText();

			// t = new QueryDataTransferable(str);
			// return t;
			//return new StringSelection(str);

			//Transferable t = new Transferable();

			//QueryDefinitionType queryDefinitionType = new QueryDefinitionType();

			/*QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
			for (int i = 0; i < jPanel1.getComponentCount() - 1; i++) {
				if (jPanel1.getComponent(i) instanceof GroupPanel) {
					GroupPanel panel = (GroupPanel) jPanel1.getComponent(i);// getTreePanel(i);

					if ((panel != null) && (panel.data().getItems().size() > 0)) {
						queryDefinitionType.setQueryName(jNameLabel.getText()
								.replace(" Query Name: ", ""));
						// queryDefinitionType.setQueryName(panel.data()
						// .getItems().get(0).name());
						// + "_" + generateMessageId().substring(0, 4));
						ArrayList<QueryConceptTreeNodeData> nodelist = panel
								.data().getItems();
						if ((nodelist != null) && (nodelist.size() > 0)) {
							// System.out.println("Panel: "+panel.getGroupName()+
							// " Excluded:
							// "+((panel.data().exclude())?"yes":"no"));
							PanelType panelType = new PanelType();
							panelType.setInvert((panel.data().exclude()) ? 1
									: 0);
							PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
							totalOccurrences.setValue(panel
									.getOccurrenceTimes());
							panelType.setTotalItemOccurrences(totalOccurrences);
							panelType.setPanelNumber(i + 1);

							for (int j = 0; j < nodelist.size(); j++) {
								QueryConceptTreeNodeData node = nodelist.get(j);
								// System.out.println("\tItem: "+node.fullname())
								// ;

								// create item
								ItemType itemType = new ItemType();

								itemType.setItemKey(node.fullname());
								itemType.setItemName(node.name());
								// mm removed
								// itemType.setItemTable(node.lookuptable());
								itemType.setTooltip(node.tooltip());
								itemType.setHlevel(Integer.parseInt(node
										.hlevel()));
								itemType.setClazz("ENC");

								// handle time constrain
								if (panel.data().startTime() != -1
										|| panel.data().endTime() != -1) {
									ConstrainByDate timeConstrain = panel
											.data().writeTimeConstrain();
									itemType.getConstrainByDate().add(
											timeConstrain);
								}

								// handle value constrain
								if (!node.valuePropertyData().noValue()) {
									ConstrainByValue valueConstrain = node
											.valuePropertyData()
											.writeValueConstrain();
									itemType.getConstrainByValue().add(
											valueConstrain);
								}

								panelType.getItem().add(itemType);
							}
							queryDefinitionType.getPanel().add(panelType);

						}
					}
				}

			}
			StringWriter strWriter = new StringWriter();

			try {

				DndType dnd = new DndType();

				edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();

				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
				// psmOf = new
				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				dnd.getAny().add(
						psmOf.createQueryDefinition(queryDefinitionType));

				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				QueryJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);
				str = strWriter.toString();

			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										jNameLabel,
										"You can not use this item in a query, "
												+ "it is only used for organizing the lists.");
					}
				});
			}*/

			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new
			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
			/*
			 * //edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
			 * psmOf = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			 * dnd.getAny().add(vdoOf.c.c(queryDefinitionType));
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
			 * QueryJAXBUtil.getJAXBUtil().marshaller(
			 * of.createPluginDragDrop(dnd), strWriter);
			 */

			//t = new QueryDataTransferable(str);
			return t;
		}
	}
	
	public String getSiteId(XmlValueType xml)
	{    	
		org.w3c.dom.Element rootElement = xml.getAny().get(0);
		NodeList nameElements = rootElement.getElementsByTagName("patient_id");
		if (nameElements.getLength() != 0 && nameElements.item(0).getAttributes().getLength() != 0){
			return nameElements.item(0).getAttributes().item(0).getTextContent();
		}
		else
			return "";//MessageUtil.getInstance().getTimestamp();

	}
	
	@SuppressWarnings("unused")
	private String writeContentQueryXML(String user) {

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		String userId = UserInfoBean.getInstance().getUserName();
		userType.setLogin(userId);
		userType.setValue(userId);

		headerType.setUser(userType);
		if (user.equalsIgnoreCase("all users")) {
			headerType
					.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_GROUP_ID);
		} else {
			headerType
					.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_USER_ID);
		}

		UserRequestType userRequestType = new UserRequestType();
		userRequestType.setGroupId(UserInfoBean.selectedProjectID());

		userRequestType.setUserId(user);

		String maxNum = System.getProperty("QueryToolMaxQueryNumber");
		if (maxNum == null || maxNum.equals("")) {
			userRequestType.setFetchSize(20);
		} else {
			userRequestType.setFetchSize(Integer.parseInt(maxNum));
		}

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(userRequestType));
		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Generated content XML request: " +
		// strWriter.toString());
		return strWriter.toString();
	}
	
	private String writeFindQueryXML(String user, String searchStr, int category, int strategy) {

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		String userId = UserInfoBean.getInstance().getUserName();
		userType.setLogin(userId);
		userType.setValue(userId);

		headerType.setUser(userType);
		//if (getAllInGroup) {
			//headerType
				//	.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_GROUP_ID);
		//} else {
			//headerType
				//	.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_MASTER_LIST_FROM_USER_ID);
		//}

		FindByChildType userRequestType = new FindByChildType();
		String categoryStr = "@";
		/*if(category == 1) {
			categoryStr = "top";
		}
		else*/ if(category == 1) {
			categoryStr = "results";
		}
		else if(category == 2) {
			categoryStr = "PDO";
		}
		
		String strategyStr = "contains";
		if(strategy == 1) {
			strategyStr = "left";
		}
		else if(strategy == 2) {
			strategyStr = "right";
		}
		else if(strategy == 3) {
			strategyStr = "exact";
		}
		
		userRequestType.setCategory(categoryStr);
		
		if(!user.equalsIgnoreCase("all users")) {
			userRequestType.setUserId(user);
		}
		
		MatchStrType mStr = new MatchStrType();
		mStr.setValue(searchStr);
		mStr.setStrategy(strategyStr);
		userRequestType.setMatchStr(mStr);

		String maxNum = System.getProperty("QueryToolMaxQueryNumber");
		if (maxNum == null || maxNum.equals("")) {
			userRequestType.setMax(20);
		} else {
			userRequestType.setMax(Integer.parseInt(maxNum));
		}

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createGetNameInfo(userRequestType));
		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = PatientSetJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Generated content XML request: " +
		// strWriter.toString());
		return strWriter.toString();
	}

	// Variables declaration
	private javax.swing.JComboBox jCategoryComboBox;
    private javax.swing.JComboBox jContainComboBox;
    private javax.swing.JButton jFindButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jSearchStringTextField;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTree jTree1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton jBackwardButton;
	private javax.swing.JTextField jStartTimeTextField;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JButton jForwardButton;
	private javax.swing.JPanel jPanel2;
	// End of variables declaration
}
