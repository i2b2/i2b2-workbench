/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 * 
 *     Wensong Pan
 */

package edu.harvard.i2b2.query.ui;

/*******************************************************************************
 * Class: QueryTopPanelModel
 * 
 * A data model class for the top panel of the Query Tool.     
 * 
 * 
 */

import java.awt.Cursor;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

// import javax.xml.datatype.DatatypeConfigurationException;
// import javax.xml.datatype.DatatypeConstants;
// import javax.xml.datatype.DatatypeFactory;
// import javax.xml.datatype.XMLGregorianCalendar;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreePanelData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryMasterData;
import edu.harvard.i2b2.query.datavo.QueryJAXBUtil;
import edu.harvard.i2b2.query.serviceClient.QueryListNamesClient;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.*;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.*;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.query.utils.Messages;

public class MainPanelModel {
	private ArrayList<GroupPanel> panelList;
	private ArrayList<JLabel> andOrLabelList;

	private int panelCount = 0;

	private int specificity = 0;

	public void specificity(int i) {
		specificity = i;
	}

	public int specificity() {
		return specificity;
	}

	private String timing = "ANY";

	public void timing(String str) {
		timing = str;
	}

	public String timing() {
		return timing;
	}

	private int lastLabelPosition;

	public void lastLabelPosition(int i) {
		lastLabelPosition = i;
	}

	public int lastLabelPosition() {
		return lastLabelPosition;
	}

	private String queryName = null;

	public String queryName() {
		return queryName;
	}

	public void queryName(String str) {
		queryName = str;
	}

	private MainPanel mainPanel = null;

	public void setTopPanel(MainPanel p) {
		mainPanel = p;
	}

	public MainPanelModel() {
		panelList = new ArrayList<GroupPanel>();
		andOrLabelList = new ArrayList<JLabel>();
	}

	public GroupPanel getTreePanel(int index) {
		return panelList.get(index);
	}

	public JLabel getAndOrLabel(int index) {
		return andOrLabelList.get(index);
	}

	public int getCurrentPanelCount() {
		return panelCount;
	}

	public void addPanel(GroupPanel panel, JLabel label, int position) {
		panelList.add(panel);
		if (label != null) {
			andOrLabelList.add(label);
		}
		lastLabelPosition = position;

		panelCount++;
	}

	private String generateMessageId() {
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

	public String writeContentQueryXML(String queryName, String userId) {

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();
		UserType userType = new UserType();
		userType.setLogin(userId);
		userType.setValue(userId);
		headerType.setUser(userType);
		headerType
				.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
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

	public String wirteQueryXML(ArrayList<String> resultOptions) {

		QueryDefinitionType queryDefinitionType = new QueryDefinitionType();
		ResultOutputOptionListType resultOutputOptionListType = new ResultOutputOptionListType();
		// /loop thru the options
		for (int i = 0; i < resultOptions.size(); i++) {
			ResultOutputOptionType resultOutputOptionType = new ResultOutputOptionType();
			resultOutputOptionType.setName(resultOptions.get(i));// "patient_count_xml"
			// );
			resultOutputOptionType.setPriorityIndex(new Integer(i + 1));
			resultOutputOptionListType.getResultOutput().add(
					resultOutputOptionType);
		}

		QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
		for (int i = 0; i < panelCount; i++) {
			GroupPanel panel = getTreePanel(i);
			ArrayList<QueryConceptTreeNodeData> nodelist = panel.data()
					.getItems();
			if ((nodelist != null) && (nodelist.size() > 0)) {
				// System.out.println("Panel: "+panel.getGroupName()+" Excluded:
				// "+((panel.data().exclude())?"yes":"no"));
				PanelType panelType = new PanelType();
				panelType.setInvert((panel.data().exclude()) ? 1 : 0);
				PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
				totalOccurrences.setValue(panel.getOccurrenceTimes());
				panelType.setTotalItemOccurrences(totalOccurrences);
				panelType.setPanelAccuracyScale(panel.getAccuracyScale());
				panelType.setPanelNumber(i + 1);
				if (panel.isAny()) {
					panelType.setPanelTiming("ANY");
				} else {
					if(panel.getTimeComboText().equalsIgnoreCase("Occurs in Same Encounter")) {
						panelType.setPanelTiming("SAMEVISIT");
					}
					else {
						panelType.setPanelTiming("SAMEINSTANCENUM");
					}
				}

				for (int j = 0; j < nodelist.size(); j++) {
					QueryConceptTreeNodeData node = nodelist.get(j);
					// System.out.println("\tItem: "+node.fullname());
					// create item
					ItemType itemType = new ItemType();

					// // checking for special items, patient, encounter set or
					// prevQuery
					if (node.name().indexOf("Patient Set") >= 0
							|| node.name().indexOf("Encounter Set") >= 0
							|| node.name().indexOf("PrevQuery") >= 0
							|| node.name().indexOf("PATIENT") >= 0) {
						itemType.setItemKey(node.fullname());
						itemType.setItemName(node.name());
						// itemType.setItemTable(node.lookuptable());
						itemType.setTooltip(node.tooltip());
					} else {
						itemType.setItemKey(node.fullname());
						itemType.setItemName(node.name());
						// itemType.setItemTable(node.lookuptable());
						itemType.setTooltip(node.tooltip());
						itemType.setHlevel(Integer.parseInt(node.hlevel()));
						itemType.setClazz("ENC");
						itemType.setItemIcon(node.visualAttribute());
					}

					// handle time constrain
					if (panel.data().startTime() != -1
							|| panel.data().endTime() != -1) {
						ConstrainByDate timeConstrain = panel.data()
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
				queryDefinitionType.getPanel().add(panelType);

				// JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
				// StringWriter strWriter = new StringWriter();
				// try {
				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
				// psmOf =
				// new
				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				// jaxbUtil.marshaller(psmOf.createPanelType(panelType),
				// strWriter);
				// System.out.println("panel xml: "+strWriter.toString());
				// }
				// catch(Exception e) {
				// e.printStackTrace();
				// }
			}
		}

		// create infotype
		InfoType infoType = new InfoType();
		infoType.setValue("INFO");
		infoType.setUrl("http://www.ibm.com");

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		userType.setLogin(UserInfoBean.getInstance().getUserName());
		userType.setGroup(System.getProperty("projectName"));
		userType.setValue(UserInfoBean.getInstance().getUserName());

		headerType.setUser(userType);
		headerType
				.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);
		if (queryName == null) {
			queryName = getTreePanel(0).data().getItems().get(0).name() + "_"
					+ generateMessageId().substring(0, 4);
		}
		headerType.setQueryMode(QueryModeType.OPTIMIZE_WITHOUT_TEMP_TABLE);

		queryDefinitionType.setQueryName(queryName);
		queryDefinitionType.setQueryTiming(timing);
		queryDefinitionRequestType.setQueryDefinition(queryDefinitionType);
		queryDefinitionRequestType
				.setResultOutputList(resultOutputOptionListType);

		RequestHeaderType requestHeader = new RequestHeaderType();

		if (System.getProperty("QueryToolMaxWaitingTime") != null) {
			requestHeader.setResultWaittimeMs((Integer.parseInt(System
					.getProperty("QueryToolMaxWaitingTime"))) * 1000);
		} else {
			requestHeader.setResultWaittimeMs(180000);
		}

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(queryDefinitionRequestType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshallerWithCDATA(of.createRequest(requestMessageType), strWriter, new String[] {"value_constraint"});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strWriter.toString();
	}

	public String wirteAllQueryXML(ArrayList<String> resultOptions) {

		QueryDefinitionType queryDefinitionType = new QueryDefinitionType();
		ResultOutputOptionListType resultOutputOptionListType = new ResultOutputOptionListType();
		// /loop thru the options
		for (int i = 0; i < resultOptions.size(); i++) {
			ResultOutputOptionType resultOutputOptionType = new ResultOutputOptionType();
			resultOutputOptionType.setName(resultOptions.get(i));// "patient_count_xml"
			// );
			resultOutputOptionType.setPriorityIndex(new Integer(i + 1));
			resultOutputOptionListType.getResultOutput().add(
					resultOutputOptionType);
		}

		QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
		// for (int i = 0; i < panelCount; i++) {
		// QueryConceptTreePanel panel = getTreePanel(i);
		// ArrayList<QueryConceptTreeNodeData> nodelist = panel.data()
		// .getItems();
		// if ((nodelist != null) && (nodelist.size() > 0)) {
		// System.out.println("Panel: "+panel.getGroupName()+" Excluded:
		// "+((panel.data().exclude())?"yes":"no"));
		PanelType panelType = new PanelType();
		panelType.setInvert(1);
		PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
		totalOccurrences.setValue(1);
		panelType.setTotalItemOccurrences(totalOccurrences);
		panelType.setPanelNumber(1);

		/*
		 * for (int j = 0; j < nodelist.size(); j++) { QueryConceptTreeNodeData
		 * node = nodelist.get(j); //
		 * System.out.println("\tItem: "+node.fullname());
		 * 
		 * // create item ItemType itemType = new ItemType();
		 * 
		 * itemType.setItemKey(node.fullname());
		 * itemType.setItemName(node.name()); // mm removed //
		 * itemType.setItemTable(node.lookuptable());
		 * itemType.setTooltip(node.tooltip());
		 * itemType.setHlevel(Integer.parseInt(node.hlevel()));
		 * itemType.setClazz("ENC");
		 * itemType.setItemIcon(node.visualAttribute());
		 * 
		 * // handle time constrain if (panel.data().startTime() != -1 ||
		 * panel.data().endTime() != -1) { ConstrainByDate timeConstrain =
		 * panel.data() .writeTimeConstrain();
		 * itemType.getConstrainByDate().add(timeConstrain); }
		 * 
		 * // handle value constrain if (!node.valuePropertyData().noValue()) {
		 * ConstrainByValue valueConstrain = node
		 * .valuePropertyData().writeValueConstrain();
		 * itemType.getConstrainByValue().add(valueConstrain); }
		 * 
		 * panelType.getItem().add(itemType); }
		 */
		queryDefinitionType.getPanel().add(panelType);

		// JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		// StringWriter strWriter = new StringWriter();
		// try {
		// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
		// psmOf =
		// new
		// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		// jaxbUtil.marshaller(psmOf.createPanelType(panelType),
		// strWriter);
		// System.out.println("panel xml: "+strWriter.toString());
		// }
		// catch(Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }

		// create infotype
		InfoType infoType = new InfoType();
		infoType.setValue("INFO");
		infoType.setUrl("http://www.ibm.com");

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		userType.setLogin(UserInfoBean.getInstance().getUserName());
		userType.setGroup(System.getProperty("projectName"));
		userType.setValue(UserInfoBean.getInstance().getUserName());

		headerType.setUser(userType);
		headerType
				.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);
		if (queryName == null) {
			queryName = getTreePanel(0).data().getItems().get(0).name() + "_"
					+ generateMessageId().substring(0, 4);
		}
		headerType.setQueryMode(QueryModeType.OPTIMIZE_WITHOUT_TEMP_TABLE);
		
		queryDefinitionType.setQueryName(queryName);
		queryDefinitionType.setQueryTiming(timing);
		queryDefinitionRequestType.setQueryDefinition(queryDefinitionType);
		queryDefinitionRequestType
				.setResultOutputList(resultOutputOptionListType);

		RequestHeaderType requestHeader = new RequestHeaderType();

		if (System.getProperty("QueryToolMaxWaitingTime") != null) {
			requestHeader.setResultWaittimeMs((Integer.parseInt(System
					.getProperty("QueryToolMaxWaitingTime"))) * 1000);
		} else {
			requestHeader.setResultWaittimeMs(180000);
		}

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(queryDefinitionRequestType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strWriter.toString();
	}

	public String getDayString() {
		// Calendar cldr = Calendar.getInstance(TimeZone
		// .getTimeZone("America/New_York"));
		Calendar cldr = Calendar.getInstance(Locale.getDefault());

		return addZero(cldr.get(Calendar.MONTH) + 1) + "-"
				+ addZero(cldr.get(Calendar.DAY_OF_MONTH)) + "-"
				+ addZero(cldr.get(Calendar.YEAR));
	}

	public String getTmpQueryName() {
		String name = new String("");
		int notEmptyPanels = notEmptyPanels();
		int numberOfPanels = (notEmptyPanels < 3) ? notEmptyPanels : 3;
		int numberOfChar = 15 / numberOfPanels;
		for (int i = 0; i < panelList.size(); i++) {
			if (i < 3) {
				GroupPanel panel = panelList.get(i);
				if ((panel.getItems() == null)
						|| (panel.getItems().size() == 0)) {
					continue;
				}
				if (name.equals("")) {
					if (panel.getItems().get(0).name().length() >= numberOfChar) {
						name += panel.getItems().get(0).name().substring(0,
								numberOfChar);
					} else {
						name += panel.getItems().get(0).name();
					}
				} else {
					if (panel.getItems().get(0).name().length() >= numberOfChar) {
						name += "-"
								+ panel.getItems().get(0).name().substring(0,
										numberOfChar);
					} else {
						name += "-" + panel.getItems().get(0).name();
					}
				}
			}
		}

		Calendar cldr = Calendar.getInstance(TimeZone
				.getTimeZone("America/New_York"));
		name += "@"/*
					 * +addZero(cldr.get(Calendar.MONTH)+1)+"-"+addZero(cldr.get(
					 * Calendar .DAY_OF_MONTH))+"-"
					 * +addZero(cldr.get(Calendar.YEAR))+" "
					 */
				+ addZero(cldr.get(Calendar.HOUR)) + ":"
				+ addZero(cldr.get(Calendar.MINUTE)) + ":"
				+ addZero(cldr.get(Calendar.SECOND));

		if (hasConstrain()) {
			name = "(+) " + name;
		}
		return name;
	}

	private String addZero(int number) {
		String result = new Integer(number).toString();
		if (number < 10 && number >= 0) {
			result = "0" + result;
		}
		return result;
	}

	private int notEmptyPanels() {
		int count = 0;
		for (int i = 0; i < panelList.size(); i++) {
			GroupPanel panel = panelList.get(i);
			if ((panel.getItems() != null) && (panel.getItems().size() > 0)) {
				count++;
			}
		}
		return count;
	}

	private boolean hasConstrain() {
		boolean hasConstrain = false;

		for (int i = 0; i < panelList.size(); i++) {
			GroupPanel panel = panelList.get(i);
			if ((panel.getItems() == null) || (panel.getItems().size() == 0)) {
				continue;
			}

			QueryConceptTreePanelData panelData = panel.data();
			if (panelData.getOccurrenceTimes() > 1 || panelData.exclude()
					|| panelData.startDay() != -1 || panelData.endDay() != -1) {
				hasConstrain = true;
				break;
			}
		}

		return hasConstrain;
	}

	public int getQueryResultCount(String fileURL) {
		return 0;
	}

	public boolean hasEmptyPanels() {
		for (int i = 0; i < panelList.size(); i++) {
			GroupPanel panel = panelList.get(i);
			if ((panel.getItems() == null) || (panel.getItems().size() == 0)) {
				return true;
			}
		}
		return false;
	}

	public void clearConceptTrees() {
		for (int i = 0; i < panelList.size(); i++) {
			GroupPanel panel = panelList.get(i);
			panel.reset();
		}
	}

	public void removeAdditionalPanels() {
		if (getCurrentPanelCount() > 3) {
			for (int i = 3; i < getCurrentPanelCount(); i++) {
				GroupPanel panel = panelList.get(3);
				JLabel label = andOrLabelList.get(2);
				panel.setVisible(false);
				label.setVisible(false);
				panelList.remove(3);
				andOrLabelList.remove(2);
			}
			panelCount = 3;
		}
	}

	public boolean isAllPanelEmpty() {
		for (int i = 0; i < panelList.size(); i++) {
			GroupPanel panel = panelList.get(i);
			if ((panel.getItems() != null) && (panel.getItems().size() > 0)) {
				return false;
			}
		}
		return true;
	}

	public void redrawPanelFromXml(final String xmlstr) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				redrawPanels(xmlstr);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void redrawPanels(String xmlstr) {
		mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(xmlstr);
			DndType dndType = (DndType) jaxbElement.getValue();

			QueryDefinitionType queryDefinitionType = null;
			Object xmlTest = new JAXBUnWrapHelper().getObjectByClass(dndType
					.getAny(), QueryMasterType.class);
			if (xmlTest != null
					&& xmlTest.getClass().getSimpleName().equalsIgnoreCase(
							"QueryMasterType")) {

				QueryMasterType queryMasterType = (QueryMasterType) new JAXBUnWrapHelper()
						.getObjectByClass(dndType.getAny(),
								QueryMasterType.class);
				QueryMasterData ndata = new QueryMasterData();
				ndata.name(queryMasterType.getName());
				ndata.xmlContent(null);
				ndata.id(queryMasterType.getQueryMasterId());
				ndata.userId(queryMasterType.getUserId());

				// final String name = queryDefinitionType.getQueryName();
				// java.awt.EventQueue.invokeLater(new Runnable() {
				// public void run() {
				mainPanel.setQueryName(" Query Name: " + ndata.name());// .
				// substring
				// (0,
				// ndata
				// .name
				// ().
				// indexOf
				// (
				// "[")));
				// panel.redraw(fpanelData);
				// }
				// });

				String xmlcontent = null;
				String xmlrequest = null;

				xmlrequest = ndata.writeDefinitionQueryXML();
				// mainPanel.parentPanel.lastRequestMessage(xmlrequest);

				// String xmlcontent;
				if (System.getProperty("webServiceMethod").equals("SOAP")) {
					xmlcontent = QueryListNamesClient
							.sendQueryRequestSOAP(xmlrequest);
				} else {
					xmlcontent = QueryListNamesClient
							.sendQueryRequestREST(xmlrequest);
				}
				// xmlcontent =
				// QueryListNamesClient.sendQueryRequestREST(xmlrequest);
				// mainPanel.parentPanel.lastResponseMessage(xmlcontent);

				if (xmlcontent == null) {
					mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				} else {
					// System.out.println("Query content response: "+xmlcontent);
					ndata.xmlContent(xmlcontent);
					mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

				jaxbElement = jaxbUtil.unMashallFromString(ndata.xmlContent());
				ResponseMessageType messageType = (ResponseMessageType) jaxbElement
						.getValue();

				BodyType bt = messageType.getMessageBody();
				MasterResponseType masterResponseType = (MasterResponseType) new JAXBUnWrapHelper()
						.getObjectByClass(bt.getAny(), MasterResponseType.class);
				RequestXmlType requestXmlType = masterResponseType
						.getQueryMaster().get(0).getRequestXml();
				// String strRequest = (String)
				// requestXmlType.getContent().get(0);

				// jaxbElement = jaxbUtil.unMashallFromString(strRequest);
				// RequestMessageType requestMessageType =
				// (RequestMessageType)jaxbElement.getValue();
				// bt = requestMessageType.getMessageBody();
				// QueryDefinitionRequestType queryDefinitionRequestType =
				// (QueryDefinitionRequestType) new
				// JAXBUnWrapHelper().getObjectByClass(bt.getAny(),
				// QueryDefinitionRequestType.class);
				// QueryDefinitionType queryDefinitionType =
				// queryDefinitionRequestType.getQueryDefinition();

				// QueryDefinitionType queryDefinitionType =
				// (QueryDefinitionType)jaxbElement.getValue();

				org.w3c.dom.Element element = (org.w3c.dom.Element) requestXmlType
						.getContent().get(0);

				String domString = edu.harvard.i2b2.common.util.xml.XMLUtil
						.convertDOMElementToString(element);
				JAXBContext jc1 = JAXBContext
						.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
				Unmarshaller unMarshaller = jc1.createUnmarshaller();
				JAXBElement queryDefinitionJaxbElement = (JAXBElement) unMarshaller
						.unmarshal(new StringReader(domString));

				queryDefinitionType = (QueryDefinitionType) queryDefinitionJaxbElement
						.getValue();

			} else {
				mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

				queryDefinitionType = (QueryDefinitionType) new JAXBUnWrapHelper()
						.getObjectByClass(dndType.getAny(),
								QueryDefinitionType.class);
				final String name = queryDefinitionType.getQueryName();
				// java.awt.EventQueue.invokeLater(new Runnable() {
				// public void run() {
				mainPanel.setQueryName(" Query Name: " + name);
				// panel.redraw(fpanelData);
				// }
				// });
			}

			final int numOfPanels = queryDefinitionType.getPanel().size();
			if(queryDefinitionType.getQueryTiming()==null) {
				mainPanel.setSame(0);
			}
			else if(queryDefinitionType.getQueryTiming().equalsIgnoreCase("SAMEVISIT")) {
				mainPanel.setSame(1);
			}
			else if(queryDefinitionType.getQueryTiming().equalsIgnoreCase("SAMEINSTANCENUM")) {
				mainPanel.setSame(2);
			}

			if (numOfPanels > 3) {
				for (int i = 0; i < (numOfPanels - 3); i++) {
					mainPanel.addPanel();
				}
			}

			for (int i = 0; i < numOfPanels; i++) {
				PanelType panelType = queryDefinitionType.getPanel().get(i);
				final GroupPanel panel = getTreePanel(i);
				QueryConceptTreePanelData panelData = new QueryConceptTreePanelData();
				panelData.setOccurrenceTimes(panelType
						.getTotalItemOccurrences().getValue());
				if (panelType.getInvert() == 0) {
					panelData.exclude(false);
				} else if (panelType.getInvert() == 1) {
					panelData.exclude(true);
				}

				// if (panelType.getPanelTiming().equalsIgnoreCase("ANY")) {
				panelData.timing(panelType.getPanelTiming());
				// } //else if (panelType.getInvert() == 1) {
				// panelData.exclude(true);
				// }

				// set time constrain at the panel level based on the constrains
				// on the items
				if (panelType.getItem().get(0).getConstrainByDate().size() > 0) {
					if (panelType.getItem().get(0).getConstrainByDate().get(0)
							.getDateFrom() != null) {
						GregorianCalendar fromCalendar = panelType.getItem()
								.get(0).getConstrainByDate().get(0)
								.getDateFrom().getValue().toGregorianCalendar();
						TimeZone pdt = TimeZone.getTimeZone("EST");
						fromCalendar.setTimeZone(pdt);
						panelData.startYear(fromCalendar.get(Calendar.YEAR));
						panelData.startMonth(fromCalendar.get(Calendar.MONTH));
						panelData.startDay(fromCalendar
								.get(Calendar.DAY_OF_MONTH));
						panelData.startTime(fromCalendar.getTimeInMillis());
					}

					if (panelType.getItem().get(0).getConstrainByDate().get(0)
							.getDateTo() != null) {
						GregorianCalendar toCalendar = panelType.getItem().get(
								0).getConstrainByDate().get(0).getDateTo()
								.getValue().toGregorianCalendar();
						TimeZone pdt = TimeZone.getTimeZone("EST");
						toCalendar.setTimeZone(pdt);
						panelData.endYear(toCalendar.get(Calendar.YEAR));
						panelData.endMonth(toCalendar.get(Calendar.MONTH));
						panelData.endDay(toCalendar.get(Calendar.DAY_OF_MONTH));
						panelData.endTime(toCalendar.getTimeInMillis());
					}
				}

				for (int j = 0; j < panelType.getItem().size(); j++) {
					ItemType itemType = panelType.getItem().get(j);
					
					QueryConceptTreeNodeData nodedata = null;
					ConstrainByModifier md = itemType.getConstrainByModifier();
					if(md != null) {
						panelData.hasModifier = true;
						nodedata = new ModifierData();
						nodedata.isModifier(true);
						((ModifierData)nodedata).modifier_key(md.getModifierKey());
						((ModifierData)nodedata).applied_path(md.getAppliedPath());
						((ModifierData)nodedata).modifier_name(md.getModifierName());
						((ModifierData)nodedata).setModifierValueConstraint(md.getConstrainByValue());
					}
					else {
						nodedata = new QueryConceptTreeNodeData();
					}
					nodedata.name(itemType.getItemName());
					nodedata.titleName(itemType.getItemName());

					if (itemType.getItemIcon() != null) {
						nodedata.visualAttribute(itemType.getItemIcon());
					} else {
						nodedata.visualAttribute("LA");
					}

					nodedata.tooltip(itemType.getTooltip());
					nodedata.fullname(itemType.getItemKey());
					nodedata.hlevel(new Integer(itemType.getHlevel())
							.toString());
					nodedata.setValueConstraints(itemType.getConstrainByValue());

					panelData.getItems().add(nodedata);
				}

				final QueryConceptTreePanelData fpanelData = panelData;
				// final String name =
				// masterResponseType.getQueryMaster().get(0).getName();

				// final String name = queryDefinitionType.getQueryName();
				// java.awt.EventQueue.invokeLater(new Runnable() {
				// public void run() {
				// mainPanel.setQueryName(" Query Name: " + name);
				panel.redraw(fpanelData);
				// }
				// });
			}
		} catch (Exception e) {
			e.printStackTrace();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(mainPanel,
							"Please note, You can not drop this item here.");
				}
			});
		}
	}
}
