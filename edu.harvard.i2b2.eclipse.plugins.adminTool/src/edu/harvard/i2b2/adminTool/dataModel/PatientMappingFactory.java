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

/**
 * Class: TimelineFactory
 * 
 * A utility class for generating timeline ".lld" files based on the PDO; some methods here were 
 * ported from DBLib class.
 * 
 */

package edu.harvard.i2b2.adminTool.dataModel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import edu.harvard.i2b2.adminTool.data.PatientDemographics;
import edu.harvard.i2b2.adminTool.data.PatientMappingData;
import edu.harvard.i2b2.adminTool.data.SiteColumnData;
import edu.harvard.i2b2.adminTool.data.SiteData;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PatientDataResponseType;
import edu.harvard.i2b2.common.datavo.pdo.ObservationSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientType;
import edu.harvard.i2b2.common.datavo.pdo.ObservationType;
import edu.harvard.i2b2.common.datavo.pdo.PidSet;
import edu.harvard.i2b2.common.datavo.pdo.PidType;
import edu.harvard.i2b2.common.datavo.pdo.PidType.PatientMapId;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;
import edu.harvard.i2b2.patientMapping.ui.MainComposite;
import edu.harvard.i2b2.smlib.Lib;

public class PatientMappingFactory {

	public String newline = System.getProperty("line.separator");

	private static final Log log = LogFactory.getLog(PatientMappingFactory.class);
	
	
	/**
	 * 
	 */
	public PatientMappingFactory() {
	}

	@SuppressWarnings("unchecked")
	public String generateTableData(String result, ArrayList<PatientMappingData> list) {

		try {
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
			StatusType statusType = pdoresponsefactory
					.getStatusFromResponseXML(result);
			if (!statusType.getType().equalsIgnoreCase("DONE")) {
				return "error";
			}

			JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(result);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bodyType = messageType.getMessageBody();
			PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bodyType.getAny(),
							PatientDataResponseType.class);
			PageType pageType = responseType.getPage();
			if (pageType != null) {
				final int returnLastIndex = pageType.getPagingByPatients()
						.getPatientsReturned().getLastIndex().intValue();
				final int returnFirstIndex = pageType.getPagingByPatients()
						.getPatientsReturned().getFirstIndex().intValue();
				final int requestLastIndex = pageType.getPagingByPatients()
						.getPatientsRequested().getLastIndex().intValue();
				/*if (returnLastIndex < requestLastIndex) {
					// System.out.println("Can't return all the requested "+
					// requestIndex+" patients, only "+returnIndex+" patients returned");
					explorer.getDisplay().syncExec(new Runnable() {
						public void run() {
							// MessageBox mBox = new MessageBox(explorer
							// .getShell(), SWT.ICON_INFORMATION
							// | SWT.OK );
							// mBox.setText("Please Note ...");
							// mBox.setMessage("Only "+(returnLastIndex-returnFirstIndex+1)+" patients returned");
							// mBox.open();
							if (explorer.runMode() >= 0) {
								explorer.setIncrementNumber(returnLastIndex
										- returnFirstIndex + 1);
							} else if (explorer.runMode() == -1) {
								explorer.setDecreaseNumber(returnLastIndex
										- returnFirstIndex + 1);
							}
						}
					});
					explorer.returnedNumber(returnLastIndex - returnFirstIndex
							+ 1);
				} else {
					explorer.returnedNumber(-1);
				}*/
			}
			else {
				/*explorer.getDisplay().syncExec(new Runnable() {
					public void run() {
						// MessageBox mBox = new MessageBox(explorer
						// .getShell(), SWT.ICON_INFORMATION
						// | SWT.OK );
						// mBox.setText("Please Note ...");
						// mBox.setMessage("Only "+(returnLastIndex-returnFirstIndex+1)+" patients returned");
						// mBox.open();
						if (explorer.runMode() >= 0) {
							explorer.setIncrementNumber(-1);
						} else if (explorer.runMode() == -1) {
							explorer.setDecreaseNumber(-1);
						}
					}
				});*/
			}

			StringBuilder resultFile = new StringBuilder();
			//resultFile.append(GetTimelineHeader());

			PidSet patientMappingSet = pdoresponsefactory.getPidSetFromResponseXML(result);
			if (patientMappingSet != null) {
				log.debug("Total patient: "
						+ patientMappingSet.getPid().size());
				for(int i=0; i<patientMappingSet.getPid().size(); i++) {
					PidType pidType = patientMappingSet.getPid().get(i);
					PatientMappingData pmData = new PatientMappingData();
					pmData.hiveID(pidType.getPatientId().getValue());
					for(int j=0; j<pidType.getPatientMapId().size(); j++) {
						 PatientMapId pmId = pidType.getPatientMapId().get(j);
						 SiteData site = new SiteData();
						 site.siteName(pmId.getSource());
						 site.siteId(pmId.getValue());
						 pmData.sites.add(site);
					}
					list.add(pmData);
				}
			} else {
				return "error";
			}

			// / testing the visit set
			// PatientDataType.VisitDimensionSet visitSet =
			// pdoresponsefactory.getVisitSetFromResponseXML(result);
			// System.out.println("Total visits: "+visitSet.getVisitDimension().
			// size());

			log.debug("\nGenerate lld:");
			
			// log.debug("\nThe lld file: \n"+resultFile.toString());
			return resultFile.toString();
		} catch (org.apache.axis2.AxisFault e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return "error";
		}
	}

	@SuppressWarnings("unchecked")
	public String generateTimelineData(String result,
			ArrayList<TimelineRow> rows, boolean writeFile, boolean displayAll,
			boolean displayDemographics, final MainComposite explorer) {

		try {
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
			StatusType statusType = pdoresponsefactory
					.getStatusFromResponseXML(result);
			if (!statusType.getType().equalsIgnoreCase("DONE")) {
				return "error";
			}

			JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(result);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bodyType = messageType.getMessageBody();
			PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bodyType.getAny(),
							PatientDataResponseType.class);
			PageType pageType = responseType.getPage();
			if (pageType != null) {
				final int returnLastIndex = pageType.getPagingByPatients()
						.getPatientsReturned().getLastIndex().intValue();
				final int returnFirstIndex = pageType.getPagingByPatients()
						.getPatientsReturned().getFirstIndex().intValue();
				final int requestLastIndex = pageType.getPagingByPatients()
						.getPatientsRequested().getLastIndex().intValue();
				if (returnLastIndex < requestLastIndex) {
					// System.out.println("Can't return all the requested "+
					// requestIndex+" patients, only "+returnIndex+" patients returned");
					explorer.getDisplay().syncExec(new Runnable() {
						public void run() {
							// MessageBox mBox = new MessageBox(explorer
							// .getShell(), SWT.ICON_INFORMATION
							// | SWT.OK );
							// mBox.setText("Please Note ...");
							// mBox.setMessage("Only "+(returnLastIndex-returnFirstIndex+1)+" patients returned");
							// mBox.open();
							if (explorer.runMode() >= 0) {
								explorer.setIncrementNumber(returnLastIndex
										- returnFirstIndex + 1);
							} else if (explorer.runMode() == -1) {
								explorer.setDecreaseNumber(returnLastIndex
										- returnFirstIndex + 1);
							}
						}
					});
					explorer.returnedNumber(returnLastIndex - returnFirstIndex
							+ 1);
				} else {
					explorer.returnedNumber(-1);
				}
			}
			else {
				explorer.getDisplay().syncExec(new Runnable() {
					public void run() {
						// MessageBox mBox = new MessageBox(explorer
						// .getShell(), SWT.ICON_INFORMATION
						// | SWT.OK );
						// mBox.setText("Please Note ...");
						// mBox.setMessage("Only "+(returnLastIndex-returnFirstIndex+1)+" patients returned");
						// mBox.open();
						if (explorer.runMode() >= 0) {
							explorer.setIncrementNumber(-1);
						} else if (explorer.runMode() == -1) {
							explorer.setDecreaseNumber(-1);
						}
					}
				});
			}

			StringBuilder resultFile = new StringBuilder();
			resultFile.append(GetTimelineHeader());

			PatientSet patientDimensionSet = pdoresponsefactory
					.getPatientSetFromResponseXML(result);
			if (patientDimensionSet != null) {
				log.debug("Total patient: "
						+ patientDimensionSet.getPatient().size());
				// for(int i=0;
				// i<patientDimensionSet.getPatientDimension().size();i++) {
				// PatientDimensionType patientType =
				// patientDimensionSet.getPatientDimension().get(i);
				// System.out.println("PatientNum: " +
				// patientType.getPatientNum());
				// }
			} else {
				return "error";
			}

			// / testing the visit set
			// PatientDataType.VisitDimensionSet visitSet =
			// pdoresponsefactory.getVisitSetFromResponseXML(result);
			// System.out.println("Total visits: "+visitSet.getVisitDimension().
			// size());

			List<ObservationSet> factSets = pdoresponsefactory
					.getFactSetsFromResponseXML(result);

			log.debug("\nGenerate lld:");
			
			String curPNum = "-1";
			for (int i = 0; i < patientDimensionSet.getPatient().size(); i++) {
				PatientType patientType = patientDimensionSet.getPatient().get(
						i);
				String pnum = patientType.getPatientId().getValue();
				// Integer pnum = new Integer(snum);
				// log.debug("PatientNum: " + snum);
				//if(curPNum.equals("-1")) {
					curPNum = new String(pnum);
				//}

				if (displayDemographics) {
					resultFile.append(getTimelinePatientString(pnum.toString(),
							patientType));
				} else {
					resultFile
							.append(getTimelinePatientString(pnum.toString()));
				}

				String path = null;
				TimelineRow currentRow = null;
				ObservationSet observationFactSet = null;

				for (int j = 0; j < rows.size(); j++) {
					TimelineRow row = rows.get(j);
					int total = 0;
					StringBuilder resultString = new StringBuilder();
					
					XMLGregorianCalendar curStartDate = null;
					//int currentInstanceNum = 0;
					//String currentConcept = "";
					ArrayList<ObservationType> facts = new ArrayList<ObservationType>();
					ObservationType curFact = null;
					boolean recorded = false;
					String sStart_date = null;
					String sEnd_date = null;

					// loop thru all the pdo sets for this row here
					for (int s = 0; s < row.pdoItems.size(); s++) {
						PDOItem pset = row.pdoItems.get(s);
						observationFactSet = null;

						for (int m = 0; m < factSets.size(); m++) {
							ObservationSet tmpFactSet = factSets.get(m);
							if (tmpFactSet.getPanelName().equalsIgnoreCase(
									pset.panelName())) {
								observationFactSet = tmpFactSet;
								path = observationFactSet.getPanelName();
								currentRow = row;
								break;
							}
						}

						if (observationFactSet == null) {
							continue;
						}
						
						for (int k = 0; k < observationFactSet.getObservation()
								.size(); k++) {
							ObservationType obsFactType = observationFactSet
									.getObservation().get(k);
							//if(curFact == null) {
								//curFact = obsFactType;
							//}
							
								
							//facts.add(obsFactType);

							if (pnum.equals(obsFactType.getPatientId().getValue())) {
								/*
								 * if ((curStartDate != null) &&
								 * (obsFactType.getStartDate().compare(
								 * curStartDate) == DatatypeConstants.EQUAL)) {
								 * continue; }
								 */
								if(curFact == null) {
									curFact = obsFactType;
								}
								
								if(curStartDate == null) {
									curStartDate = obsFactType.getStartDate();
								}

								sStart_date = obsFactType.getStartDate()
										.getMonth()
										+ "-"
										+ obsFactType.getStartDate().getDay()
										+ "-"
										+ obsFactType.getStartDate().getYear()
										+ " 12:00";
								
								if (obsFactType.getEndDate() == null) {
									sEnd_date = sStart_date;
								} else {
									sEnd_date = obsFactType.getEndDate()
											.getMonth()
											+ "-"
											+ obsFactType.getEndDate().getDay()
											+ "-"
											+ obsFactType.getEndDate()
													.getYear() + " 12:00";
								}

								double nval = -1;
								if (obsFactType.getNvalNum().getValue() != null) {
									nval = obsFactType.getNvalNum().getValue()
											.doubleValue();
								}
								PDOValueModel valdp = null;

								if (false){//pset.hasValueDisplayProperty) {
									for (int n = 0; n < pset.valDisplayProperties
											.size(); n++) {
										PDOValueModel tmpvaldp = pset.valDisplayProperties
												.get(n);
										// if (tmpvaldp.inRange(nval)) {
										valdp = tmpvaldp;
										// break;
										// }
									}

									String sValue = getSValue(obsFactType.getConceptCd().getName(),
											obsFactType, true);
									if ((curStartDate != null)
											&& (obsFactType.getStartDate()
													.compare(curStartDate) == DatatypeConstants.EQUAL)) {
										resultString
												.append(getTimelineDateStringHeight(
														sStart_date, sEnd_date,
														"slateblue",
														valdp.height, sValue));
									} else {
										resultString
												.append(getTimelineDateStringHeight(
														sStart_date, sEnd_date,
														valdp.color,
														valdp.height, sValue));
									}
								} else {
									//////////////////////
									String sValue = "";
									if(sameInstance(curFact, obsFactType)) {
										//if(recorded) {
											facts.add(obsFactType);
											//recorded = false;
											continue;
										//}
										/*sValue = getSValueSame(obsFactType.getConceptCd().getName(),
												obsFactType, false);
										if ((curStartDate != null)
												&& (obsFactType.getStartDate()
														.compare(curStartDate) == DatatypeConstants.EQUAL)) {
											resultString
													.append(getTimelineDateStringHeightSame(
															sStart_date, sEnd_date,
															"slateblue",
															pset.height, sValue));
										} else {
											resultString
													.append(getTimelineDateStringHeightSame(
															sStart_date, sEnd_date,
															pset.color,
															pset.height, sValue));
										}*/
									}								
									//else {
										sValue = getSValue(curFact.getConceptCd().getName(),
												facts, false);
									//}
										
										sStart_date = curFact.getStartDate()
										.getMonth()
										+ "-"
										+ curFact.getStartDate().getDay()
										+ "-"
										+ curFact.getStartDate().getYear()
										+ " 12:00";
								
								if (curFact.getEndDate() == null) {
									sEnd_date = sStart_date;
								} else {
									sEnd_date = curFact.getEndDate()
											.getMonth()
											+ "-"
											+ curFact.getEndDate().getDay()
											+ "-"
											+ curFact.getEndDate()
													.getYear() + " 12:00";
								}
									
									//if ((curStartDate != null)
									//		&& (obsFactType.getStartDate()
											//		.compare(curStartDate) == DatatypeConstants.EQUAL)) {
										//resultString
												//.append(getTimelineDateStringHeight(
														//sStart_date, sEnd_date,
														//"slateblue",
														//pset.height, sValue));
									//} else {
										resultString
												.append(getTimelineDateStringHeight(
														sStart_date, sEnd_date,
														pset.color,
														pset.height, sValue));
									//}
								}

								total++;
								recorded = false;
								facts.clear();
								facts.add(obsFactType);
								curStartDate = obsFactType.getStartDate();
								curFact = obsFactType;
							}
							else {
								if(recorded || facts.size() == 0) {
									curPNum = new String(pnum);
									continue;
								}
								sStart_date = curStartDate.getMonth()
								+ "-"
								+ curStartDate.getDay()
								+ "-"
								+ curStartDate.getYear()
								+ " 12:00";
						
								//if (obsFactType.getEndDate() == null) {
									//sEnd_date = sStart_date;
								//} else {
									sEnd_date = curStartDate
											.getMonth()
											+ "-"
											+ curStartDate.getDay()
											+ "-"
											+ curStartDate
													.getYear() + " 12:00";
								//}
								//curFact = null;
								String sValue = getSValue(curFact.getConceptCd().getName(),
										facts, false);
								//if ((curStartDate != null)
										//&& (obsFactType.getStartDate()
												//.compare(curStartDate) == DatatypeConstants.EQUAL)) {
									//resultString
											//.append(getTimelineDateStringHeight(
													//sStart_date, sEnd_date,
													//"slateblue",
													//pset.height, sValue));
								//} else {
									resultString
											.append(getTimelineDateStringHeight(
													sStart_date, sEnd_date,
													pset.color,
													pset.height, sValue));
								//}
								total++;
								recorded = true;
								facts.clear();
								curStartDate = null;//obsFactType.getStartDate();
								curFact = null;//obsFactType;
								curPNum = new String(pnum);
							}
						}
					}
					
					if (total > 0) {
						// log.debug("-- "+path+" has "+total+" events");
						resultFile.append(getTimelineConceptString(
								row.displayName, total));

						// log.debug(resultString.toString());
						resultFile.append(resultString);
						total = 0;
						////rdtr
					} else {
						// display all
						if (displayAll) {
							// log.debug("-- "+path+" has "+total+" events");
							resultFile.append(getTimelineConceptString(
									row.displayName, 1));

							// log.debug(getTimelineEmptyDateString());
							resultFile.append(getTimelineEmptyDateString());
						}
					}
					
					if(recorded || facts.size() == 0) {
						curPNum = new String(pnum);
						continue;
					}
					sStart_date = curStartDate.getMonth()
					+ "-"
					+ curStartDate.getDay()
					+ "-"
					+ curStartDate.getYear()
					+ " 12:00";
			
					//if (obsFactType.getEndDate() == null) {
						//sEnd_date = sStart_date;
					//} else {
						sEnd_date = curStartDate
								.getMonth()
								+ "-"
								+ curStartDate.getDay()
								+ "-"
								+ curStartDate
										.getYear() + " 12:00";
					//}
					//curFact = null;
					String sValue = getSValue(curFact.getConceptCd().getName(),
							facts, false);
					
						resultString
								.append(getTimelineDateStringHeight(
										sStart_date, sEnd_date,
										"navyblue",
										"p10", sValue));
			
					total++;
					recorded = true;
					facts.clear();
					//curStartDate = obsFactType.getStartDate();
					//curFact = obsFactType;
					curPNum = new String(pnum);
					if (total > 0) {
						// log.debug("-- "+path+" has "+total+" events");
						resultFile.append(getTimelineConceptString(
								row.displayName, total));

						// log.debug(resultString.toString());
						resultFile.append(resultString);
					}
				}
			}

			resultFile.append(GetTimelineFooter());

			if (writeFile) {
				String i2b2File = System.getProperty("user.dir") + "/temp/"
						+ "i2b2xml.lld";
				File oDelete = new File(i2b2File);
				if (oDelete != null)
					oDelete.delete();
				RandomAccessFile f = new RandomAccessFile(i2b2File, "rw");
				append(f, resultFile.toString());
				f.close();
			}

			// log.debug("\nThe lld file: \n"+resultFile.toString());
			return resultFile.toString();
		} catch (org.apache.axis2.AxisFault e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return "error";
		}
	}

	public static void append(RandomAccessFile f, String outString)
			throws IOException {
		try {
			f.seek(f.length());
			f.writeBytes(outString);
		} catch (IOException e) {
			throw new IOException("trouble writing to random access file.");
		}
		return;
	}

	public String createlld(int minPatientNum, int maxPatientNum,
			boolean bDisplayAll, boolean writeFile, boolean displayDemographics) {

		ArrayList conceptOrder = new ArrayList();
		int maxLineCount = 0; // zero turns off check for maximum count of lines
		StringBuilder resultFile = new StringBuilder();
		ArrayList<PatientDemographics> demographicsArray = new ArrayList<PatientDemographics>();

		try {
			// get the root
			Element root = null;// doc.getRootElement();
			// get the children from the i2b2 document
			java.util.List allChildren = root.getChildren();
			int iNumberOfChildren = allChildren.size();
			// set up the variables for the loop
			String sPatient_num = null;
			String sConcept_cd = null;
			String sOldPatient_num = "start";
			String sOldConcept_cd = null;
			String sStart_date = null;
			String sOldStart_date = null;
			String sEnd_date = null;
			String sInout_cd = null;
			String sDeath_date = null;
			String sColor = null;
			String sHeight = null;
			String sValue = null;
			String sTablename = null;
			int patientNum = 0;
			Date oDate;

			resultFile.append(GetTimelineHeader());
			boolean bOverMax = false;
			int conceptCount = 0;
			int patientCount = minPatientNum;
			StringBuilder patientRecord = new StringBuilder();

			String currentPatientNum = null;
			int indexPos = 0;

			for (int p = 0; p < demographicsArray.size(); p++) {
				PatientDemographics record = demographicsArray.get(p);
				currentPatientNum = record.patientNumber();

				if (displayDemographics) {
					patientRecord.append(getTimelinePatientString(
							currentPatientNum, record));
				} else {
					patientRecord
							.append(getTimelinePatientString(currentPatientNum));
				}

				resultFile.append(patientRecord.toString());
				patientRecord = new StringBuilder();
				patientCount++;

				conceptCount = 0;
				sOldConcept_cd = null;
				sOldStart_date = null;
				sOldPatient_num = "";

				if ((indexPos == iNumberOfChildren) && bDisplayAll) {
					conceptCount = 0;
					while ((conceptOrder != null)
							&& (conceptCount < conceptOrder.size())) {
						patientRecord.append(getTimelineConceptString(
								(String) conceptOrder.get(conceptCount), 1));
						patientRecord.append(getTimelineEmptyDateString());
						conceptCount++;
					}

					resultFile.append(patientRecord.toString());
					patientRecord = new StringBuilder();
				}

				for (int i = indexPos; i < iNumberOfChildren; i++) {
					if ((maxLineCount > 0) && (i > maxLineCount)) {
						bOverMax = true;
						break;
					}

					Element oChild = (Element) allChildren.get(i);
					sPatient_num = "";// oChild.getChild(ss_patient_num).getText(
					// );

					if (!sPatient_num.equals(currentPatientNum)
							&& (sOldPatient_num.equals("start")) /*
																 * &&
																 * !sOldPatient_num
																 * .equals(
																 * sPatient_num
																 * )
																 */) {
						if (bDisplayAll) {
							try {
								patientNum = Integer.parseInt(sPatient_num);
								conceptCount = 0;
								while ((conceptOrder != null)
										&& (conceptCount < conceptOrder.size())) {
									patientRecord
											.append(getTimelineConceptString(
													(String) conceptOrder
															.get(conceptCount),
													1));
									patientRecord
											.append(getTimelineEmptyDateString());
									conceptCount++;
								}

								resultFile.append(patientRecord.toString());
								patientRecord = new StringBuilder();
								// patientCount++;
							} catch (java.lang.OutOfMemoryError e) {
								log.error("In resultset builder 5: "
										+ e.getMessage());
								// closeConnection(oConnection);
								return "memory error";
							} catch (Exception e) {
								log.error(e.getMessage());
								// closeConnection(oConnection);
								return "error";
							}

							conceptCount = 0;
							sOldConcept_cd = null;
							sOldStart_date = null;
						}
						break;
					} else if (!sPatient_num.equals(currentPatientNum)
							&& !(sOldPatient_num.equals("start")) /*
																 * &&!
																 * sOldPatient_num
																 * .equals(
																 * sPatient_num
																 * )
																 */) {
						if ((bDisplayAll)
								&& (conceptCount < (conceptOrder.size()))) {
							while ((conceptOrder != null)
									&& (conceptCount < conceptOrder.size())) {
								patientRecord
										.append(getTimelineConceptString(
												(String) conceptOrder
														.get(conceptCount), 1));
								patientRecord
										.append(getTimelineEmptyDateString());
								conceptCount++;
							}
						}

						resultFile.append(patientRecord.toString());
						patientRecord = new StringBuilder();
						patientCount++;

						conceptCount = 0;
						sOldConcept_cd = null;
						sOldStart_date = null;
						break;
					} else if (sPatient_num.equals(currentPatientNum)) {
						indexPos = i + 1;
						sOldPatient_num = sPatient_num;
						// if (bUseConcept) {
						// sConcept_cd =
						// oChild.getChild(ss_concept_cd).getText();
						// }
						// else {
						// sConcept_cd =
						// oChild.getAttributeValue(ss_q_name_char);
						// }

						if (!sConcept_cd.equals(sOldConcept_cd)) {
							// conceptCount++;
							if (bDisplayAll) {
								for (int j = conceptCount; j < conceptOrder
										.size(); j++) {
									if (sConcept_cd.equals(conceptOrder.get(j))) {
										break;
									} else {
										patientRecord
												.append(getTimelineConceptString(
														(String) conceptOrder
																.get(j), 1));
										patientRecord
												.append(getTimelineEmptyDateString());
										conceptCount++;
									}
								}
							}

							// int iNumConceptObservations =
							// getNumConceptObservationsRollingupStartDate
							// (allChildren,i);

							// patientRecord.append(getTimelineConceptString(
							// sConcept_cd,iNumConceptObservations));
							conceptCount++;
							sOldStart_date = null;
						}

						sOldConcept_cd = sConcept_cd;
						// sStart_date =
						// oChild.getChild(ss_start_date).getText();
						// if (!sStart_date.equals(sOldStart_date)) {
						// if (!sStart_date.equals(null)) {
						if ((!sStart_date.equals(null))
								&& ((sOldStart_date == null) || (!sStart_date
										.equals(sOldStart_date)))) {
							// sEnd_date =
							// oChild.getChild(ss_end_date).getText();
							if ((sEnd_date == null)
									|| (sEnd_date.trim().length() == 0))
								sEnd_date = sStart_date;
							// sInout_cd =
							// oChild.getChild(ss_inout_cd).getText();
							sInout_cd = "";
							// sColor = oChild.getChild(ss_color_cd).getText();
							// sHeight =
							// oChild.getChild(ss_height_cd).getText();
							// sValue = oChild.getChild(ss_value_cd).getText();
							// sTablename =
							// oChild.getChild(ss_table_name).getText();
							String prefix = "C";
							if (sTablename.equalsIgnoreCase("visit_dimension")) {
								prefix = "E";
							} else if (sTablename
									.equalsIgnoreCase("provider_dimension")) {
								prefix = "P";
							}

							// if ((sValue==null)||(sValue.length()==0)) {
							// sValue = prefix+" = ::"+sConcept_cd+"::"+
							// "$$"+oChild.getChild(ss_patient_num).getText()+
							// "$$"+oChild.getChild(ss_concept_cd).getText() +
							// "$$"+ChangeRsDateFull(sStart_date) ;//+"::";
						} else {
							// sValue = prefix+" Value = " +
							// "::"+sConcept_cd+": "+sValue+"::"+
							// "$$"+oChild.getChild(ss_patient_num).getText()+
							// "$$"+oChild.getChild(ss_concept_cd).getText() +
							// "$$"+ChangeRsDateFull(sStart_date) ;//+"::";
						}

						// log.debug("   "+ ChangeRsDate(sStart_date) + " -> " +
						// ChangeRsDate(sEnd_date));
						// System.out.print(getTimelineDateString(ChangeRsDate(
						// sStart_date),ChangeRsDate(sEnd_date)));
						// System.out.print(getTimelineDateString(ChangeRsDate(
						// sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));
						if (sInout_cd.equalsIgnoreCase("I")) {
							if (sColor != null)
								patientRecord
										.append(getTimelineDateStringSpecial(
												ChangeRsDate(sStart_date),
												ChangeRsDate(sEnd_date), sColor));
							else
								patientRecord
										.append(getTimelineDateStringSpecial(
												ChangeRsDate(sStart_date),
												ChangeRsDate(sEnd_date)));
						} else if (sInout_cd.equalsIgnoreCase("E")) {
							if (sColor != null)
								patientRecord
										.append(getTimelineDateStringEncounter(
												ChangeRsDate(sStart_date),
												ChangeRsDate(sEnd_date), sColor));
							else
								patientRecord
										.append(getTimelineDateStringEncounter(
												ChangeRsDate(sStart_date),
												ChangeRsDate(sEnd_date)));
						} else if (sInout_cd.equalsIgnoreCase("D")) {
							if (sStart_date.length() == 0) {
								if (sColor != null)
									patientRecord
											.append(getTimelineDateStringEncounter(
													"today", "today", sColor));
								else
									patientRecord
											.append(getTimelineDateStringEncounter(
													"today", "today"));
							} else {
								if (sColor != null)
									patientRecord
											.append(getTimelineDateStringDeath(
													ChangeRsDate(sStart_date),
													"today", sColor));
								else
									patientRecord
											.append(getTimelineDateStringDeath(
													ChangeRsDate(sStart_date),
													"today", sColor));
							}
						} else {
							if (sConcept_cd.equals("Death")) {
								if (sStart_date.length() == 0) {
									sStart_date = "today";
									sColor = "lightbrown";
								}
								sEnd_date = "today";
							}
							if (sColor != null) {
								if (sConcept_cd.equalsIgnoreCase("EGFR"))
									patientRecord
											.append(getTimelineDateString(
													ChangeRsDate(sStart_date),
													ChangeRsDate(sEnd_date),
													sColor,
													"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=1956"));
								else
									patientRecord
											.append(getTimelineDateStringHeight(
													ChangeRsDate(sStart_date),
													ChangeRsDate(sEnd_date),
													sColor, sHeight, sValue));
							} else
								patientRecord
										.append(getTimelineDateStringHeight(
												ChangeRsDate(sStart_date),
												ChangeRsDate(sEnd_date),
												sHeight));
						}
					}
					sOldStart_date = sStart_date;

					if (!bOverMax) {
						if (bDisplayAll && (indexPos == iNumberOfChildren)) {
							while ((conceptOrder != null)
									&& (conceptCount < conceptOrder.size())) {
								patientRecord
										.append(getTimelineConceptString(
												(String) conceptOrder
														.get(conceptCount), 1));
								patientRecord
										.append(getTimelineEmptyDateString());
								conceptCount++;
							}
						}
						resultFile.append(patientRecord.toString());
						patientRecord = new StringBuilder();
						patientCount++;
					}
				}
			}

			if ((!bOverMax) && bDisplayAll) {
				while ((conceptOrder != null)
						&& (conceptCount < conceptOrder.size())) {
					patientRecord.append(getTimelineConceptString(
							(String) conceptOrder.get(conceptCount), 1));
					patientRecord.append(getTimelineEmptyDateString());
					conceptCount++;
				}
				resultFile.append(patientRecord.toString());
			}

			resultFile.append(GetTimelineFooter());
			log.debug(" Total Count " + iNumberOfChildren);

			if (writeFile) {
				String i2b2File = System.getProperty("user.dir") + '/'
						+ "i2b2xml.lld";
				File oDelete = new File(i2b2File);
				if (oDelete != null)
					oDelete.delete();
				RandomAccessFile f = new RandomAccessFile(i2b2File, "rw");
				Lib.append(f, resultFile.toString());
				f.close();
			}

			if (bOverMax) {
				log.debug("reached maximum at " + new Date());
				return "overmaximum";
			}
		} catch (java.lang.OutOfMemoryError e) {
			log.error("In resultset builder 6: " + e.getMessage());
			// closeConnection(oConnection);
			return "memory error";
		} catch (Exception e) {
			log.error(e.getMessage());
			// closeConnection(oConnection);
			return "error";
		}

		log.debug("done at " + new Date());
		return resultFile.toString();

	}

	public static String ChangeRsDateFull(String sInputDate) {
		try {
			/*
			 * sInputDate = Lib.StrFindAndReplace("Sunday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace("Monday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace("Tuesday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace("Wednesday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace("Thursday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace("Friday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace("Saturday, ","",sInputDate);
			 * sInputDate = Lib.StrFindAndReplace(" EDT","",sInputDate); Date
			 * oDate = java.text.DateFormat.getDateInstance().parse(sInputDate);
			 */

			SimpleDateFormat iFormat = new SimpleDateFormat(
					"d-MMM-yyyy hh:mm:ss a");
			Date oDate = iFormat.parse(sInputDate);

			// sInputDate =
			// DateFormat.getDateInstance(DateFormat.MEDIUM).format(oDate);
			SimpleDateFormat oFormat = new SimpleDateFormat(
					"yyyy-M-d hh:mm:ss a");
			sInputDate = oFormat.format(oDate);
			return sInputDate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * returns %facet,PERSON_#1...............,white,yes %c,comment %agg,
	 * normal,1, no %-,2-15-1999,today,white,p1,.,chiempty.html,""
	 */
	public String getTimelinePatientString(String sPatient_num) {
		String sFinished = newline + "%facet,Person_#" + sPatient_num
				+ "................,white,yes" + newline + " %c,comment"
				+ newline + " %agg, normal,1, no" + newline
				+ " %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""
				+ newline;
		return sFinished;
	}

	public String getTimelinePatientString(String sPatient_num,
			PatientDemographics record, String startDate) {
		String sFinished;

		sFinished = newline + "%facet,";

		if (!System.getProperty("applicationName").equals("BIRN")) {
			sFinished += "Person_#";
		}

		sFinished += sPatient_num + ",white,yes" + newline + " %c,comment"
				+ newline + " %agg, normal,1, no" + newline + " %-,"
				+ ChangeRsDate(startDate)
				+ ",today,white,p1,.,chiempty.html,\"\"" + newline;
		return sFinished;
	}

	/**
	 * returns %facet,PERSON_#1 gender: age: race: vital-status: ,white,yes
	 * %c,comment %agg, normal,1, no
	 * %-,2-15-1999,today,white,p1,.,chiempty.html,""
	 */
	public String getTimelinePatientString(String sPatient_num,
			PatientType record) {
		String sFinished = newline + "%facet,Person_#" + sPatient_num
				+ ",white,yes" + newline + " %c,comment" + newline
				+ " %agg, normal,1, no" + newline
				+ " %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""
				+ newline;

		PatientDemographics patientDemographics = new PatientDemographics();
		patientDemographics.setParamData(record.getParam());

		String age = patientDemographics.age() + "yrold";
		String gender = "";
		String race = "";

		if (patientDemographics.vitalStatus().equalsIgnoreCase("Y")) {
			age = "Dead";
		}

		if (patientDemographics.gender().toUpperCase().startsWith("M")) {
			gender = "Male";
		} else if (patientDemographics.gender().toUpperCase().startsWith("F")) {
			gender = "Female";
		}

		if (patientDemographics.race().equalsIgnoreCase("")) {
			race = "Unknown";
		} else {
			if (patientDemographics.race().toUpperCase().startsWith("W")) {
				race = "White";
			} else if (patientDemographics.race().toUpperCase().startsWith("B")) {
				race = "Black";
			} else if (patientDemographics.race().toUpperCase().startsWith("A")) {
				race = "Asian";
			} else if (patientDemographics.race().toUpperCase().startsWith("H")) {
				race = "Hispanic";
			} else if (patientDemographics.race().toUpperCase().startsWith("O")) {
				if (patientDemographics.race().toUpperCase().indexOf("OR") >= 0) {
					race = "Oriental";
				} else {
					race = "Other";
				}
			} else {
				race = "Unknown";
			}
		}

		sFinished = newline + "%facet,Person_#" + sPatient_num + "__" + gender
				+ "__" + age + "__" + race + ",white,yes" + newline
				+ " %c,comment" + newline + " %agg, normal,1, no" + newline
				+ " %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""
				+ newline;

		return sFinished;
	}

	/**
	 * returns %facet,PERSON_#1 gender: age: race: vital-status: ,white,yes
	 * %c,comment %agg, normal,1, no
	 * %-,2-15-1999,today,white,p1,.,chiempty.html,""
	 */
	public String getTimelinePatientString(String sPatient_num,
			PatientDemographics record) {
		String sFinished;

		if (record.age().equals("")) {
			sFinished = newline + "%facet,Person_#" + sPatient_num
					+ ",white,yes" + newline + " %c,comment" + newline
					+ " %agg, normal,1, no" + newline
					+ " %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""
					+ newline;
			return sFinished;
		}

		String age = record.age() + "yrold";
		String gender = null;
		String race = null;

		if (record.vitalStatus().equalsIgnoreCase("Y")) {
			age = "Dead";
		}

		if (record.gender().toUpperCase().startsWith("M")) {
			gender = "Male";
		} else if (record.gender().toUpperCase().startsWith("F")) {
			gender = "Female";
		}

		if (record.race().toUpperCase().startsWith("W")) {
			race = "White";
		} else if (record.race().toUpperCase().startsWith("B")) {
			race = "Black";
		} else if (record.race().toUpperCase().startsWith("A")) {
			race = "Asian";
		} else if (record.race().toUpperCase().startsWith("H")) {
			race = "Hispanic";
		} else if (record.race().toUpperCase().startsWith("O")) {
			if (record.race().toUpperCase().indexOf("OR") >= 0) {
				race = "Oriental";
			} else {
				race = "Other";
			}
		} else {
			race = "Unknown";
		}

		sFinished = newline + "%facet,Person_#" + sPatient_num + "__" + gender
				+ "__" + age + "__" + race + ",white,yes" + newline
				+ " %c,comment" + newline + " %agg, normal,1, no" + newline
				+ " %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""
				+ newline;

		return sFinished;
	}

	/**
	 * returns %facet,Diagnosis,lightbrown,yes %c,comment %agg, normal,6, no
	 * %-,6-27-1999,today,slateblue,p5,ICH,blank.htm,""
	 * %-,6-26-1999,6-30-1999,slateblue,p10, ,blank.htm,""
	 */
	public String getTimelineConceptString(String sConcept_cd,
			int iNumConceptObservations) {
		String sNewConcept = sConcept_cd.replaceAll(" ", "_").replaceAll(",",
				"_");
		sNewConcept = sNewConcept.replaceAll("__", "_");
		sNewConcept = sNewConcept.replaceAll(">", "_");
		sNewConcept = sNewConcept.replaceAll("<", "_");
		sNewConcept = sNewConcept.replaceAll("zz", "");

		sNewConcept = sNewConcept.trim();
		if (sNewConcept.length() > 15) {
			sNewConcept = sNewConcept.substring(0, 15) + "...";
		}

		return newline + "%facet," + sNewConcept + ",lightbrown," + "yes"
				+ newline + " %c,comment" + newline + " %agg, normal,"
				+ Integer.toString(iNumConceptObservations) + ", no" + newline;

	}

	public String getTimelineDateString(String sStart_date,
			String sEnd_date) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date
				+ ",slateblue,p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public String getTimelineDateStringHeight(String sStart_date,
			String sEnd_date, String height) {
		String pixel = "p10";
		if (height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} else if (height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		} else if (height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		} else if (height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}

		String sFinished = " %-," + sStart_date + "," + sEnd_date
				+ ",slateblue," + pixel + ", ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public String getTimelineEmptyDateString() {
		String sFinished = " %-,2-15-1999 12:00,2-15-1999 12:00"
				+ ",lightbrown,p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public String getTimelineDateString(String sStart_date,
			String sEnd_date, String colorName) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + ",p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public String getTimelineDateStringHeight(String sStart_date,
			String sEnd_date, String colorName, String height, String sValue) {

		String pixel = "p10";
		if (height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} else if (height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		} else if (height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		} else if (height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}

		/*
		 * if ((sValue==null)||(sValue.trim().length()==0)) sValue = ""; else
		 * sValue = "Value = " + sValue;
		 */

		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + "," + pixel + ", ,blank.htm,\""
				+ sValue.replaceAll(",", "-") + "\"" + newline;
		return sFinished;
	}
	
	public String getTimelineDateStringHeightSame(String sStart_date,
			String sEnd_date, String colorName, String height, String sValue) {

		String pixel = "p10";
		if (height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} else if (height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		} else if (height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		} else if (height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}

		/*
		 * if ((sValue==null)||(sValue.trim().length()==0)) sValue = ""; else
		 * sValue = "Value = " + sValue;
		 */

		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + "," + pixel + ", ,blank.htm,\""
				+ sValue.replaceAll(",", "-") + "\"" + newline;
		return sFinished;
	}

	public String getTimelineDateStringHeight(String sStart_date,
			String sEnd_date, String colorName, String height) {

		String pixel = "p10";
		if (height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} else if (height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		} else if (height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		} else if (height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}

		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + "," + pixel + ", ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public  String getTimelineDateString(String sStart_date,
			String sEnd_date, String colorName, String url) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + ",p5, ," + url + ",\"\"" + newline;
		return sFinished;
	}

	public  String getTimelineDateStringSpecial(String sStart_date,
			String sEnd_date) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date
				+ ",tomato,p10, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public  String getTimelineDateStringSpecial(String sStart_date,
			String sEnd_date, String colorName) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + ",p10, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public  String getTimelineDateStringEncounter(String sStart_date,
			String sEnd_date) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date
				+ ",yellowgreen,p2, ,blank.htm,\"\"" + newline;

		return sFinished;
	}

	public  String getTimelineDateStringEncounter(String sStart_date,
			String sEnd_date, String colorName) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + ",p2, ,blank.htm,\"\"" + newline;

		return sFinished;
	}

	public  String getTimelineDateStringDeath(String sStart_date,
			String sEnd_date) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date
				+ ",black,p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	public  String getTimelineDateStringDeath(String sStart_date,
			String sEnd_date, String colorName) {
		String sFinished = " %-," + sStart_date + "," + sEnd_date + ","
				+ colorName + ",p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}

	/*
	 * public  String getTimelineDateString(String sStart_date,String
	 * sEnd_date, String concept_cd){ String sFinished = " %-," + sStart_date +
	 * "," + sEnd_date + ",slateblue,p5," + concept_cd + ",blank.htm,\"\"" +
	 * newline; return sFinished; }
	 */

	public String GetTimelineHeader() {
		Calendar cldr = Calendar.getInstance(TimeZone.getDefault());
		cldr.setTimeInMillis(System.currentTimeMillis());
		String todayStr = (cldr.get(Calendar.MONTH) + 1) + "-"
				+ cldr.get(Calendar.DAY_OF_MONTH) + "-"
				+ cldr.get(Calendar.YEAR);
		return "%beforeSeptember1997" + newline + "%today,"
				+ /* 5-02-2006 */todayStr + " 12:00" + newline + newline +

				"%c, Available colors:" + newline + "%c, " + newline
				+ "%c, (\"seagreen\",          \"2e8b57\");" + newline
				+ "%c, (\"seashell\",          \"fff5ee\");" + newline
				+ "%c, (\"sienna\",            \"a0522d\");" + newline
				+ "%c, (\"skyblue\",           \"87ceeb\");" + newline
				+ "%c, (\"slateblue\",         \"6a5acd\");" + newline
				+ "%c, (\"slategray\",         \"708090\");" + newline
				+ "%c, (\"slategrey\",         \"708090\");" + newline
				+ "%c, (\"snow\",              \"fffafa\");" + newline
				+ "%c, (\"springgreen\",       \"00ff7f\");" + newline
				+ "%c, (\"steelblue\",         \"4682b4\");" + newline
				+ "%c, (\"tan\",               \"d2b48c\");" + newline
				+ "%c, (\"thistle\",           \"d8bfd8\");" + newline
				+ "%c, (\"tomato\",            \"ff6347\");" + newline
				+ "%c, (\"turquoise\",         \"40e0d0\");" + newline
				+ "%c, (\"violet\",            \"ee82ee\");" + newline
				+ "%c, (\"violetred\",         \"d02090\");" + newline
				+ "%c, (\"wheat\",             \"f5deb3\");" + newline
				+ "%c, (\"white\",             \"ffffff\");" + newline
				+ "%c, (\"whitesmoke\",        \"f5f5f5\");" + newline
				+ "%c, (\"yellow\",            \"ffff00\");" + newline
				+ "%c, (\"yellowgreen\",       \"9acd32\");" + newline
				+ "%c, (\"lightbrown\",        \"fff5c8\");" + newline
				+ "%c, (\"darkbrown\",         \"ffecaf\");" + newline
				+ newline +

				"%person,i2b2 Timeline Application,.,"
				+ cldr.get(Calendar.YEAR)/* 2007 */+ ",.,images/cath.gif"
				+ newline + newline +

				"%c,PERSON 1" + newline;
	}

	public  String GetTimelineFooter() {
		return newline + "%end" + newline;
	}

	public  String ChangeRsDate(String sInputDate) {
		try {
			SimpleDateFormat iFormat = new SimpleDateFormat(
					"d-MMM-yyyy hh:mm:ss a");
			Date oDate = iFormat.parse(sInputDate);

			SimpleDateFormat oFormat = new SimpleDateFormat("M-d-yyyy HH:mm");
			sInputDate = oFormat.format(oDate);
			return sInputDate;
		} catch (Exception e) {
			if (System.getProperty("applicationName").equals("BIRN")) {
				try {

					SimpleDateFormat iFormat = new SimpleDateFormat(
							"EEEEEEEE, MMMMMM dd, yyyy hh:mm:ss a z");
					Date oDate = iFormat.parse(sInputDate);

					SimpleDateFormat oFormat = new SimpleDateFormat(
							"M-d-yyyy HH:mm");
					sInputDate = oFormat.format(oDate);
					return sInputDate;
				} catch (Exception eee) {
					return sInputDate;
				}
			} else {
				try {

					SimpleDateFormat iFormat = new SimpleDateFormat(
							"EEEEEEEE, MMMMMM dd, yyyy");
					Date oDate = iFormat.parse(sInputDate);

					SimpleDateFormat oFormat = new SimpleDateFormat(
							"M-d-yyyy 12:00");
					sInputDate = oFormat.format(oDate);
					return sInputDate;
				} catch (Exception eee) {
					return sInputDate;
				}
			}
		}
	}
	
	private boolean sameInstance(ObservationType curFact, ObservationType obsFactType) {
		boolean same = false;
		//if(obsFactType.getInstanceNum() == null) {
			//return same;
		//}
		
		if(obsFactType.getStartDate().compare(curFact.getStartDate()) == DatatypeConstants.EQUAL
				&& obsFactType.getConceptCd().getValue().equals(curFact.getConceptCd().getValue())
				&& ((obsFactType.getInstanceNum() == null) || obsFactType.getInstanceNum().getValue().equals(curFact.getInstanceNum().getValue()))) {
			same = true;
		}
		
		return same;
	}
	
	private String getSValue(String sConcept_cd,
			ObservationType obsFactType, boolean hasValue) {
		String prefix = "C";
		String sValue = null;
		if (hasValue && obsFactType.getNvalNum().getValue() != null) {
			sValue = obsFactType.getNvalNum().getValue().toString();
		}
		// if(sTablename.equalsIgnoreCase("visit_dimension")) {
		// prefix = "E";
		// }
		// else if(sTablename.equalsIgnoreCase("provider_dimension")) {
		// prefix = "P";
		// }

		if (!hasValue) {
			sValue = prefix + " = ::" + sConcept_cd + "::" + "$$"
					+ obsFactType.getPatientId().getValue() + "$$"
					+ obsFactType.getConceptCd().getValue() + "$$"
					+ obsFactType.getEventId().getValue() + "$$"
					+ obsFactType.getObserverCd().getValue() + "$$"
					+ obsFactType.getModifierCd().getValue() + " "
					+ (obsFactType.getValuetypeCd().equals("T")?obsFactType.getTvalChar()
							:(obsFactType.getNvalNum().getValue()+obsFactType.getUnitsCd()))+"$$";
		} else {
			sValue = prefix + " Value = " + "::" + sConcept_cd + ": " + sValue
					+ "::" + "$$" + obsFactType.getPatientId().getValue()
					+ "$$" + obsFactType.getConceptCd().getValue() + "$$"
					+ obsFactType.getEventId().getValue() + "$$"
					+ obsFactType.getModifierCd().getValue() + " "
					+ (obsFactType.getValuetypeCd().equals("T")?obsFactType.getTvalChar()
							:(obsFactType.getNvalNum().getValue()+obsFactType.getUnitsCd()))+"$$";
		}
		return sValue;
	}
	
	private String getSValueSame(String sConcept_cd,
			ObservationType obsFactType, boolean hasValue) {
		String prefix = "C";
		String sValue = null;
		if (hasValue && obsFactType.getNvalNum().getValue() != null) {
			sValue = obsFactType.getNvalNum().getValue().toString();
		}
		// if(sTablename.equalsIgnoreCase("visit_dimension")) {
		// prefix = "E";
		// }
		// else if(sTablename.equalsIgnoreCase("provider_dimension")) {
		// prefix = "P";
		// }

		if (!hasValue) {
			sValue = prefix + " = ::" + sConcept_cd + "::" + "$$"
					+ obsFactType.getPatientId().getValue() + "$$"
					+ obsFactType.getConceptCd().getValue() + "$$"
					+ obsFactType.getEventId().getValue() + "$$"
					+ obsFactType.getObserverCd().getValue() + "$$"
					+ obsFactType.getModifierCd().getValue() + " "
					+ (obsFactType.getValuetypeCd().equals("T")?obsFactType.getTvalChar()
							:(obsFactType.getNvalNum().getValue()+obsFactType.getUnitsCd()))+"$$";
		} else {
			sValue = prefix + " Value = " + "::" + sConcept_cd + ": " + sValue
					+ "::" + "$$" + obsFactType.getPatientId().getValue()
					+ "$$" + obsFactType.getConceptCd().getValue() + "$$"
					+ obsFactType.getEventId().getValue() + "$$"
					+ obsFactType.getModifierCd().getValue() + " "
					+ (obsFactType.getValuetypeCd().equals("T")?obsFactType.getTvalChar()
							:(obsFactType.getNvalNum().getValue()+obsFactType.getUnitsCd()))+"$$";
		}
		return sValue;
	}
	
	
	private String getSValue(String sConcept_cd,
			ArrayList<ObservationType> obsFactTypes, boolean hasValue) {
		String prefix = "C";
		String sValue = null;
		//if (hasValue && obsFactType.getNvalNum().getValue() != null) {
			//sValue = obsFactType.getNvalNum().getValue().toString();
		//}
		// if(sTablename.equalsIgnoreCase("visit_dimension")) {
		// prefix = "E";
		// }
		// else if(sTablename.equalsIgnoreCase("provider_dimension")) {
		// prefix = "P";
		// }
		String mds = new String("$$");
		for(int i=0; i<obsFactTypes.size(); i++) {
			if(obsFactTypes.get(i).getValuetypeCd() != null
					&& !obsFactTypes.get(i).getValuetypeCd().equalsIgnoreCase("B")) {
				mds += obsFactTypes.get(i).getModifierCd().getValue() + " "
				+ (obsFactTypes.get(i).getValuetypeCd().equals("T")?obsFactTypes.get(i).getTvalChar()
						:(obsFactTypes.get(i).getNvalNum().getValue()+obsFactTypes.get(i).getUnitsCd()))+"@2@";
			}
			else {
				mds += obsFactTypes.get(i).getModifierCd().getValue()+"@2@";
			}
		}

		if (!hasValue) {
			sValue = prefix + " = ::" + sConcept_cd + "::" + "$$"
					+ obsFactTypes.get(0).getPatientId().getValue() + "$$"
					+ obsFactTypes.get(0).getConceptCd().getValue() + "$$"
					+ obsFactTypes.get(0).getEventId().getValue() + "$$"
					+ obsFactTypes.get(0).getObserverCd().getValue() //+ "$$"
					/*+ obsFactTypes.get(0).getModifierCd().getValue() + " "
					+ (obsFactTypes.get(0).getValuetypeCd().equals("T")?obsFactTypes.get(0).getTvalChar()
							:(obsFactTypes.get(0).getNvalNum().getValue()+obsFactTypes.get(0).getUnitsCd()))*/
					+mds+"$$";
		} else {
			sValue = prefix + " Value = " + "::" + sConcept_cd + ": " + sValue
					+ "::" + "$$" + obsFactTypes.get(0).getPatientId().getValue()
					+ "$$" + obsFactTypes.get(0).getConceptCd().getValue() + "$$"
					+ obsFactTypes.get(0).getEventId().getValue() //+ "$$"
					/*+ obsFactTypes.get(0).getModifierCd().getValue() + " "
					+ (obsFactTypes.get(0).getValuetypeCd().equals("T")?obsFactTypes.get(0).getTvalChar()
							:(obsFactTypes.get(0).getNvalNum().getValue()+obsFactTypes.get(0).getUnitsCd()))*/
					+mds+"$$";
		}
		return sValue;
	}
}
