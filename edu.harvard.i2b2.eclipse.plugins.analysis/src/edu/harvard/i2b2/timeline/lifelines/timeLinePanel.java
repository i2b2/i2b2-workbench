/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		Shawn Murphy, MD, PH.D (MGH)
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.io.*;

import javax.swing.*;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.timeline.excentric.*;
import edu.harvard.i2b2.timeline.external.*;
import edu.harvard.i2b2.analysis.dataModel.PDOResponseMessageModel;
import edu.harvard.i2b2.analysis.datavo.AnalysisJAXBUtil;
import edu.harvard.i2b2.analysis.ui.ExplorerC;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.navigator.Application;

//this is the timeline panel that gets displayed
public class timeLinePanel extends ScrollingPanel
implements ActionListener, MouseListener, MouseMotionListener    {
	
	public final int ATTRNUM = 3;
	
	boolean noRects = true;
	boolean relabeling = true;  
	public boolean slide = false;   // make it public to be accessed by facet.java
	public boolean search = false;   
	private boolean stream = false, rubber = false;
	private int inClick = 0;
	private int outClick = 0;
	private int lastInX;    /*Added 11/01/ - Partha*/
	private int lastOutX;   /*Added 11/01/ - Partha*/
	private int lastInY;    /*Added 11/01/ - Partha*/
	private int lastOutY;   /*Added 11/01/ - Partha*/
	private int mouseRel;   /*Added 10/17/ - Partha*/
	private int zoom_ratio; /*Added 11/17/ - Partha*/
	private int zoom_style; /*Added 11/17/ - Partha*/
	private int zoom_steps; /*Added 11/17/ - Partha*/
	private int zoomCounter;/*Added 11/22/ - Partha*/
	private MyDate[] lastPosnMax = new MyDate[500];/*Added 11/22/ - Partha*/
	private MyDate[] lastPosnMin = new MyDate[500];/*Added 11/22/ - Partha*/
	//private UserInfoBean uib = null;
	Image offScreenImage;
	boolean threadTest = false;
	private int rubber_startX, rubber_startY, rubber_endX, rubber_endY;
	public MyDate selectedStartDate;
	boolean selectedAgg = false;
	boolean choiceIsUp = false;
	private Choice testChoice;
	private Menu testMenu;
	private LiteLabel infoTipLabel = null;
	private MenuItem linkMenuItem[] = new MenuItem[20];
	private MenuItem attrMenuItem[] = new MenuItem[3];
	private int item=0;
	private genRecord selected = null;
	private String note = null;
	private String key = null;
	
	// for excentric
	/**
	 * Idle object triggering the timeout when the user doesn't move.
	 */
	Idle idleO;
	
	/**
	 * The displayed scene.  Don't use Components since they are not
	 * liteweight and it's impossible to display things on top of them.
	 */
	LiteGroup items = new LiteGroup();
	
	/**
	 * Contains the circular cursor when displaying the excentric labels.
	 */
	Lite cursor;
	
	/**
	 * Containts the excentric labels, as a <code>LiteGroup</code> of
	 * <code>LiteDisplacedLabels</code>.
	 */
	LiteGroup labels = new LiteGroup();
	
	/**
	 * Radius of the focus cursor.
	 */
	int cursor_radius = 30;
	
	/**
	 * Radius of the circle where labels are projected (we add a margin).
	 */
	int projection_radius = cursor_radius * 2;
	
	/**
	 * The actual algorithm to layout the labels.
	 */
	LabelLayout layout;
	
	/**
	 * Last position of the focus cursor.  When we move the mouse quickly,
	 * the cursor disappears so we need to maintain those coordinates to
	 * compute the distance between two moves.
	 */
	int last_x, last_y;
	
	
	public MyDate getDateMin() {
		return dateMin.copy();
	}
	
	public MyDate getDateMax() {
		return dateMax.copy();
	}
	
	public void init() {
		if(!threadTest)
			repaint(); // threadtest
		else
			record.changed = true;
	}
	
	public void start() {
		if(!threadTest)
			repaint(); // threadtest
		else
			record.changed = true;
	}
	
	public int width, height, dataheight; // 3/28/98  width is used when scale is initialized. 12/17/97 made public
	private Hashtable recordTable; // this contains the data;
	private String keyLabels[]; // keys are strings in facet list in left window
	private int n_key;
	private int n_rect = 0;
	private int lwinWidth;
	private int rwinWidth;
	private int rwinOffset;
	
	private static Font font = new Font("TimesRoman", Font.PLAIN, 12);
	private FontMetrics fontMetrics = getFontMetrics(font); // used for keylabels
	// change to using bold, see below
	public Font font1;    
	public FontMetrics fontMetrics1;    
	// changed to public so that storyRecord can access it, really should probably
	// be defined in record....? maybe not.. maybe use accessor function (tried
	// that already?
	
	private MyDate dateMin, dateMax, validDateMin, validDateMax;
	private scale aScale;
	private MyRectangle rects[];
	public static int MOUSE_MOVE = 1;
	public static int DEFAULT = 0;
	public static int START = 2;
	public static newApplet theApplet;
	// added by dan to stop multiple yellow tags from coming up:
	int oldi = -1;
	private aggregate aggregates[];
	private int aggregateNumber = 0;
	public static int selectedIndex = -1; // made public and static temporarily for illustration purposes
	private int font1TextHeight;  
	// the height of just the text of the font... removes the blank(?) space underneath. So that
	// labels are right on top of the lines... or does it have an effect? tried removing second term...
	boolean sameline = true; // determines whether nodes that can be on same line are indeed on same line...
	boolean tags = false; // determines if moving over or clicking on an object
	// changes its color and puts up a tag
	
	Color ly = new Color(230,237,210);
	Color lly = new Color(201, 199, 205);
	Color lp = new Color(255,200,255); // stands for lightpurple
	Color lb = new Color(255,245,200); // stands for lightbrown was 255,236,200
	Color db = new Color(255,236,175); // stands for darkbrown
	facet afacetRecord; // list of facets
	// what the data hashtable gets loaded into, a list of facets (i.e. genrecords)
	storyRecord aStoryRecord; // descendant of genrecord
	hourstoryRecord anhourStoryRecord; // descendant of genrecord
	Hashtable afacetList; // comes from a facetrecord, not list of facets but rather: list of whats in facet
	genRecord aGenRecord; // basically contains only info on the type of record
	
	record thisApplet;
	Image image1;
	
	MyDate today;
	
	public timeLinePanel (int width, int height,record thisApplet,MyDate today) {
		setLabelFont(12);  // default font size is 12
		testChoice = new Choice();
		testMenu = new Menu("test");
		this.today = today;
		this.thisApplet = thisApplet;
		
		int i = 0;
		int strWidth;
		this.width = width;
		this.height = height;
		
		//Default values for the zooming options when starting up
		ResourceTable.put(new String("zoom_ratio"),new Integer(1));/* Added 11/23 - Partha*/
		ResourceTable.put(new String("zoom_style"),new Integer(1));/* Added 11/23 - Partha*/
		ResourceTable.put(new String("zoom_steps"),new Integer(3));/* Added 11/23 - Partha*/
		
		//int rectWidth = 10; // this determines how big the rectange is.. penwidth is now the largest
		// rectangle width on the present line (if sameline is true)
		dateMin = new MyDate(1,1,record.theData.getMinDate().getYear(),0,0);
		// remember: theData is a loadrecord
		dateMax = new MyDate(1,1,record.theData.getMaxDate().getYear()+1,0,0);
		validDateMin = new MyDate(dateMin); // "valid" will change with scale see "listen" function below
		validDateMax = new MyDate(dateMax);
		recordTable = record.theData.getRecordTable(); // theData is a loadrecord.... table = hash
		// i.e. above is the main data in a hashtable, each entry is actually a facet 1/13/98 dan
		n_key = recordTable.size();
		keyLabels = new String[n_key];
		facet afacetRecord;
		//MyDate lastEndDate = new MyDate(1,1,2010);
		
		idleO = new Idle() {
			public void idle(int x, int y) { timeLinePanel.this.idle(x, y, true); }
			public void active(int x, int y) { }
		};
		idleO.register(this);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		for(i = 0, lwinWidth = 0; i < n_key; i++){
			afacetRecord = (facet)(recordTable.get(new Integer(i)));
			keyLabels[i] = (String)(afacetRecord.getKey());
			
			strWidth = (int)(fontMetrics.stringWidth(keyLabels[i]));
			if(lwinWidth < strWidth) lwinWidth = strWidth;
		}
		
		lwinWidth = lwinWidth +21; // add 15 (already was adding 6) for open/close box
		// 1/13/98 dan
		rwinWidth = width - lwinWidth - 4; // width of right window?
		rwinOffset = lwinWidth + 2; // = maximum with of a key + 2 + 6
		//rwinOffset = 20; // making this zero removed the facet titles for some reason...
		aScale = new scale(rwinWidth, dateMin, dateMax,today);
	}
	
	public int getWidth(){
		return rwinWidth; // leaves out left window (with keylabels)
		//return width; // 12/17/97, messed up slider and datebar above
	}
	
	public int getFullWidth() {
		return width;
	}
	
	public int getOffset(){
		return rwinOffset + 1;
	}
	
	public void mouseMoved(MouseEvent e) {
		hide_labels();
		
		/*int x=e.getX();
		 int y=e.getY();
		 
		 genRecord selectedRecord = inRegion(x,y,true,true); 
		 if(selectedRecord != null) {
		 infoTipLabel = new LiteLabel(selectedRecord.getInputLine(),
		 new Point(x+10, y+10),
		 1,
		 fontMetrics1.getFont(),
		 Color.black,
		 Color.yellow);
		 infoTipLabel.setAlignment(LiteLabel.LEFT);
		 repaint_rect(infoTipLabel.getBounds()); 
		 }*/
	}
	
	public void mousePressed(MouseEvent e){
		int i = 0; 
		int x = e.getX();
		int y = e.getY();
		
		if(noRects) {
			n_key = recordTable.size();
			facet tempFacet;
			for(int a=0;a<n_key;a++) {
				tempFacet = (facet)(recordTable.get(new Integer(a)));
				if(tempFacet.checkBoxContains(x,y)) {
					tempFacet.checkBoxClick();
					repaint();
				}
				if(tempFacet.contains(x,y)) 
					tempFacet.select(x,y); // doesn't do anything without changes in storyRecord (really should be aggregate) select, also need unselect call
			}
		}
		if(e.isMetaDown()) {
			
			if(e.getClickCount() == 1) { // zoom out, single right click in empty area
				zoomOut(i,x,y);
				if( !nothingSelected(x,y) && (e.getClickCount() == 2) && !e.isShiftDown() && !e.isControlDown() ){  // double click to open URL
					decStatus(i,x,y);
				}
				
				lastOutX = x;
				lastOutY = y;
			}
		}
		else if(e.isShiftDown() ) { 
			if (!nothingSelected(x,y))  {
				relabeling = true;      
				MyDate begin = fullSelectedStartDate(x,y); 
				MyDate ending = fullSelectedEndDate(x,y); 
				
				((mainPanel)getParent()).theYearSlider.adjust(10,begin,ending,rwinOffset);// 10 for event type, change this!
				
				if(aScale.getMode().equals("month"))
					((mainPanel)getParent()).theMonthSlider.adjust(10,begin,ending,rwinOffset);
				if(aScale.getMode().equals("day"))
					((mainPanel)getParent()).theWeekSlider.adjust(10,begin,ending,rwinOffset);
			}
			else    {
				rubber = true;
				rubber_startX = x;
				rubber_startY = y;
			}
			return;
		}
		else if(e.isControlDown() ) {  // control click on aggregate to search on storyRecord label (in other words.. find the storyRecord you are above too); at first try, this zoomed on controlclick so this was fixed below
			genRecord selectedRecord = inRegion(x,y,true,false);
			if (selectedRecord != null)   {
				relabeling = true; 
				grep(getLabel(x,y));
			}
			return;
		}
		else {
			if ( e.getClickCount() == 2 )	{  
				for(int a=0;a<n_key;a++) {
					facet tempFacet = (facet)(recordTable.get(new Integer(a)));
					if(tempFacet.checkTitleContains(x,y)) {
						if(System.getProperty("identityService").equals("")) {
							return;
						}
						
						String title = tempFacet.title();
						if(title.indexOf("Person_#") < 0 ) {
							return;
						}
						
						int end  = 0;
						if(title.indexOf("__") < 0) {
							end = title.length();
						}
						else {
							end = title.indexOf("__");
						}
						
						final String id = title.substring(title.indexOf("#")+1, end);
						System.out.println(id);
						
						String username = UserInfoBean.getInstance().getUserName();
						String password = UserInfoBean.getInstance().getUserPassword();
						
						String queryStr = "<?xml version=\"1.0\" standalone=\"yes\"?>\n"						
							+ "<search_by_master id=\""+id+"\">"
							+ "</search_by_master>";
				
						String resultStr = QueryClient.query(queryStr, username, password);
						System.out.println(resultStr);
						
						final ArrayList<String> mrnArr = new ArrayList<String>();
						String firstname = "N/A";
						String lastname = "N/A";
						
						SAXBuilder parser = new SAXBuilder();
						java.io.StringReader xmlStringReader = new java.io.StringReader(resultStr);
						try {
							org.jdom.Document tableDoc = parser.build(xmlStringReader);
							org.jdom.Element tableXml = tableDoc.getRootElement();
							Element responseXml = (Element) tableXml.getChild("person_list");
							java.util.List listChildren = responseXml.getChildren();
							if(listChildren.isEmpty()) {
								JOptionPane.showMessageDialog(this, "No record found");
								return;
							}
							
							Element masterXml = (Element) responseXml.getChild("master_record");
							java.util.List responseChildren = masterXml.getChildren();
							Iterator itr = responseChildren.iterator();
							
							while(itr.hasNext()){
								Element element = (org.jdom.Element) itr.next();
								if(element.getName().equalsIgnoreCase("string")) {
									String str = element.getAttribute("concept_cd").getValue();
									if(str.equalsIgnoreCase("FIRSTNAME")) {
										firstname = element.getValue();	
									}
									else if(str.equalsIgnoreCase("LASTNAME")) {
										lastname = element.getValue();	
									}
								}
								else if(element.getName().equalsIgnoreCase("local_record")) {
									String idstr = element.getAttribute("id").getValue();
									String sitestr = element.getAttribute("site").getValue();
									mrnArr.add(new String(sitestr+":"+idstr));
								}
							}
						} 
						catch(Exception e1) {
							e1.printStackTrace();
						}
						
						final String fullname = firstname+" "+lastname;
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								 new PatientNameFrame(id, fullname, mrnArr).setVisible(true);
							}
						 });
						 
						return;
					}  	           
				}
				
				genRecord selectedRecord = inRegion(x,y,true,false); 
				if(selectedRecord != null) {
					String msg = selectedRecord.getInputLine();
					String [] msgs = msg.split(",");
					String [] xtras = msgs[7].split("\\$\\$");
					
					
					String concept_cd = xtras[2];
					
					//String concept_cd = msgs[7].substring(msgs[7].lastIndexOf("$$")+2, msgs[7].lastIndexOf("\""));
				
					
					// changing to valueTypeCd == 'B' and blob not equal null below instead of concept code checks
			//		if (!System.getProperty("applicationName").equals("BIRN") && (!(("LCS-I2B2:c1009c".indexOf(concept_cd)>=0) || ("LCS-I2B2:c1010c".indexOf(concept_cd)>=0)
			//				|| ("LCS-I2B2:c1011c".indexOf(concept_cd)>=0) || ("LCS-I2B2:pul".indexOf(concept_cd)>=0)))) {
			//			return;
			//		}
					

					
					String patientNumber = xtras[1]; //msgs[7].substring(msgs[7].indexOf("$$")+2, msgs[7].lastIndexOf("$$"));            	  
					String start_date = msgs[1];
					String encounterNumber = xtras[3];
					String providerId = xtras[4];
					String modifier_cd = xtras[5];

					//	Clean the last char in xtra6
					xtras[6] = xtras[6].substring(0,xtras[6].length()-1);
					
					if (xtras[6] != null && xtras[6].length() > 0)
						start_date = xtras[6];
					
					
					//if (uib == null) {
					//	LoginDHelper ldh = new LoginDHelper();
					//	uib = ldh.getUserInfo(System.getProperty("user"),
					//			System.getProperty("pass"));
					//}
										
					String[] blobdata = getNotes(patientNumber, concept_cd, start_date, 
							encounterNumber, providerId, modifier_cd);
					if(blobdata == null) {
						//note = "No notes found";
						return;
					}
					//blobdata string array contents:
					// [0] is note
					// [1] is valueTypeCd
					// [2] is valueFlagCd
					// [3] is PDO 
					note = blobdata[0];
					
					
					if (!System.getProperty("applicationName").equals("BIRN") && (!blobdata[1].equals("B"))){
						return;
					}
					
					
					
					Application app;
					app = new Application();
					app.setClassName("edu.harvard.i2b2.timeline.external.NotesViewer");
					if(	(blobdata[1].equals("B")) && ((blobdata[2].trim()).equals("X")) )
							app.setEncrypted(true);
					else 
						app.setEncrypted(false);
					app.setName("Display Note");
					
					if (app.getEncrypted())
						note = decryptBlob(note);
					
					if(note!=null && note.equalsIgnoreCase("[I2B2-Error] Invalid key")) {
						JOptionPane.showMessageDialog(this, "Not a valid decryption key.");
						return;
					}
					
					/*try {						
						System.gc();
						
						Class appClass = Class.forName(app.getClassName());
						Object lObject = appClass.newInstance();
						
						Executor exe = (Executor) lObject;
						
						exe.init(app, note );
						
						exe.execute();
					} catch (Exception ee)
					{
						System.out.println(ee.getMessage());
					}*/
					
					String xmlPdoData = blobdata[3];
					StringWriter strWriter=null;
					
					try {
						PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
						PatientDataType patientDataType = 
							pdoresponsefactory.getPatientDataTypeFromResponseXML(xmlPdoData);
						//following line was appending decrypted note to encrypted one....   enc should be replaced with decrypted one.
				//		patientDataType.getObservationSet().get(0).getObservation().get(0).getObservationBlob().getContent().add(note.replaceAll("/n", "\n"));
						patientDataType.getObservationSet().get(0).getObservation().get(0).getObservationBlob().getContent().set(0,note.replaceAll("/n", "\n"));

						strWriter = new StringWriter();
						DndType dnd = new DndType();
						
						edu.harvard.i2b2.common.datavo.pdo.ObjectFactory pdoOf = new edu.harvard.i2b2.common.datavo.pdo.ObjectFactory();
						dnd.getAny().add(pdoOf.createPatientData(patientDataType));

						edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
						AnalysisJAXBUtil.getJAXBUtil().marshaller(of.createPluginDragDrop(dnd), strWriter);

					} catch (Exception ex) {
						System.out.println("Error marshalling Explorer drag text");
					} 
					
					System.out.println("DND content: "+strWriter.toString());
					final String dndXmlData = strWriter.toString();
					java.awt.EventQueue.invokeLater(new Runnable() {
						  public void run() {
							  new TextViewerFrame(note, dndXmlData).setVisible(true);
						  }
					 });
					}
					return;
				}   	  
			
			if ( e.getClickCount() == 1 ) {   
				genRecord selectedRecord = inRegion(x,y,true,false); 
				if(selectedRecord != null) {
					/* String msg = selectedRecord.getInputLine();
					 String [] msgs = msg.split(",");
					 if(msgs[4].equalsIgnoreCase("p2")) {
					 msgs[4] = "Very Low";
					 }
					 else if(msgs[4].equalsIgnoreCase("p8")) {
					 msgs[4] = "Low";
					 }
					 else if(msgs[4].equalsIgnoreCase("p10")) {
					 msgs[4] = "Normal";
					 }
					 else if(msgs[4].equalsIgnoreCase("p12")) {
					 msgs[4] = "High";
					 }
					 else if(msgs[4].equalsIgnoreCase("p20")){
					 msgs[4] = "Very High";
					 }
					 String val = msgs[7].substring(msgs[7].indexOf("=")+1, msgs[7].length()-1);
					 String infotip = msgs[1]+" to "+msgs[2]+":"+val+"("+msgs[4]+")";
					 infoTipLabel = new LiteLabel(infotip,
					 new Point(x+10, y+10),
					 1,
					 fontMetrics1.getFont(),
					 Color.black,
					 Color.yellow);
					 infoTipLabel.setAlignment(LiteLabel.LEFT);
					 repaint_rect(infoTipLabel.getBounds());*/  
				}
				else {
					if(x>102) {  // single click on the backgroup to zoom in
						relabeling = true;
						//if( !e.isMetaDown() && !e.isShiftDown() && !e.isControlDown()) { // zoom in
						lastInX = x;
						zoomIn(i,x,y);
						if( !nothingSelected(x,y) && (e.getClickCount() == 2) && !e.isShiftDown() && !e.isControlDown() ){  // double click to open URL
							decStatus(i,x,y);
						}
						//lastInX = x;
						lastInY = y;
					}         
				}
				return;
			}			
		}
	}
	
	public void mouseDragged(MouseEvent e)  {
		System.out.println("mouse Dragged");
		
		if (rubber) {
			rubber_endX = e.getX();
			rubber_endY = e.getY();
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		System.out.println("mouse Released");
		
		if (rubber) {
			rubber = false;
			stream = true;
			repaint();
		}
	}
	
	public void mouseClicked(MouseEvent e)  {;}   
	public void mouseEntered(MouseEvent e)  {;}
	public void mouseExited(MouseEvent e)   {;}
	
	public void actionPerformed(ActionEvent e) {
		
		for (int i=0; i<item; i++)  {
			if (e.getSource() == linkMenuItem[i])   {
				search = true;
				String command = e.getActionCommand();
				grep(command);
				return;
			}
		}
		
		if (e.getSource() == attrMenuItem[0])  {
			search = true;
			myColor mycolor = new myColor("dummy");
			if (mycolor.getColorString(selected.getRectColor()).equals("red"))
				grep(mycolor.getColorString(selected.getRectColor()) + "|severe");
			else
				grep(mycolor.getColorString(selected.getRectColor()));
			return;
		}
		else if (e.getSource() == attrMenuItem[1])  {
			search = true;  
			grep(new String("p" + new Integer(selected.getRectWidth()).toString()));
			return;
		}
		else if (e.getSource() == attrMenuItem[2])  {
			search = true;
			grep(selected.getCause());
			return;
		}
	}
	
	public void paint(Graphics g)   {
		
		facet aFacet = null;
		noRects = true;
		int scrollBarY = 0;
		
		if (noRects) {
			boolean offScreen = true;
			offScreenImage = createImage(width,height);
			
			this.ofg = offScreenImage.getGraphics();
			int currentY = 0, descent=0;
			Vector streamlist = new Vector(), substreamlist;
			
			for(int i = 0; i < n_key; i++) {
				aFacet = (facet)(recordTable.get(new Integer(i)));
				
				// checkbox, for opening and closing facets... really should be drawn in
				// facet class, but unless that becomes a component class the mouseclick
				// will have to be caught here (or in some component). dan 1/9/97
				ofg.setColor(Color.gray);
				//ofg.fillRect(1,currentY,10,10);
				
				//if(record.theTabPanel.tlpScroll.getValue() == 100 ) {
				//	  System.out.println("scroll position: " + record.theTabPanel.tlpScroll.getValue() +
				//		  " cuurent Y: "+currentY+" scrollBarY: "+scrollBarY);
				//}
				// 1/9/97, dan.. right pointing arrow if closed, down pointing arrow if open
				
				//System.out.println("scrollbar value: " + record.theTabPanel.tlpScroll.getValue());
				if(scrollBarY >= record.theTabPanel.tlpScroll.getValue())  {
					
					if(!aFacet.enabled) {
						int[] xCoordinates = {1,10,1};
						int[] yCoordinates = {currentY+2,currentY+7,currentY+12};
						
						ofg.fillPolygon(xCoordinates,yCoordinates,3);
					}
					
					else{ // now redrawn in facet class 1/12/98 dan
						int[] xCoordinates = {1,5,10};
						int[] yCoordinates = {currentY+2,currentY+12,currentY+2};
						
						ofg.fillPolygon(xCoordinates,yCoordinates,3);
					}
					
					if (stream)     {
						substreamlist = aFacet.rubber_band(rubber_startX, rubber_startY, rubber_endX, rubber_endY);
						for (int j=0; j<substreamlist.size(); j++) 
							streamlist.addElement(substreamlist.elementAt(j));
						
						aFacet.draw(currentY, this, relabeling, slide, stream, true, true);
						
						if (!streamlist.isEmpty())    {
							// animation of text stream
							// clear off the original labels
							//descent = record.theTabPanel.theTimeLinePanel.fontMetrics1.getMaxDescent();
							descent = 2;
							
							for (int j=0; j<streamlist.size(); j++) {
								storyRecord textstream;
								
								textstream = (storyRecord)streamlist.elementAt(j);
								
								g.setColor(aFacet.backgroundColor);
								g.fillRect(textstream.getLabelX(), textstream.getLabelY()+textstream.currentY-getFontTextHeight()-2, textstream.getLabelWidth(), getFontTextHeight()); 
								//g.drawString(textstream.getCause(), textstream.getLabelX(), textstream.getLabelY()+textstream.currentY-2);
							}
							//strange, pending, why the band disappears
							g.drawRect(rubber_startX, rubber_startY, rubber_endX-rubber_startX, rubber_endY-rubber_startY);
							
							// animation starts
							for (int j=0; j<streamlist.size(); j++) {
								storyRecord textstream;
								
								textstream = (storyRecord)streamlist.elementAt(j);
								//textstream.draw(currentY, this, false, true, true, false, false);
								g.setColor(Color.black);
								g.setFont(record.theTabPanel.theTimeLinePanel.fontMetrics1.getFont());
								g.drawString(textstream.getCause(), textstream.streamX, textstream.streamY+textstream.currentY-descent);
								
								try {
									Thread.sleep(4000);
								}
								catch (InterruptedException e) {
									System.out.println("thread exception");
								}                     
								g.setColor(aFacet.backgroundColor);
								g.drawString(textstream.getCause(), textstream.streamX, textstream.streamY+textstream.currentY-descent);
								
								//g.fillRect(textstream.streamX, textstream.streamY+textstream.currentY-getFontTextHeight(), textstream.getLabelWidth(), getFontTextHeight()); 
								// strange, pending
								g.drawRect(rubber_startX, rubber_startY, rubber_endX-rubber_startX, rubber_endY-rubber_startY);
								//g.setColor(Color.gray);
							}
							
							stream = false;
							// really needs more intelligence to detect
							aFacet.draw(currentY, this, relabeling, slide, stream, false, true);                                       
						}
					}
					
					else {  
						aFacet.draw(currentY, this, relabeling, slide, stream, true, true);
					}
					
					currentY += aFacet.getHeight() + 5;
					scrollBarY += aFacet.getHeight() + 5;
				}
				
				else {
					aFacet.redraw();
					scrollBarY += aFacet.getHeight() + 5;
				}// end scrollBar check
			}  // for loop
			
			// snm - looks like the height of the pane is here
			dataheight = currentY + 50; 
			//
			//System.out.println("dataheight = " + dataheight);
			//
			relabeling = false;
			slide = false;  
			search = false;  
			
			ofg.setColor(new Color(138,138,138)); 
			if(dateMin.before(today) && aScale.getTodayPosition() > 0 ) 
				ofg.drawLine(aScale.getTodayPosition() + rwinOffset,3,aScale.getTodayPosition() + rwinOffset, height - 3);
			
			if (rubber)    // only needs to draw the rubber band and clipping is much more efficient than the redraw
				ofg.drawRect(rubber_startX, rubber_startY, rubber_endX-rubber_startX, rubber_endY-rubber_startY);
			
			if (record.excentric)   {
				if (cursor != null) {
					cursor.paint(ofg);
				}
				labels.paint(ofg);
			}
			
			if (record.infotip && infoTipLabel != null) {
				infoTipLabel.paint(ofg);
			}
			
			if(offScreen)  {
				g.drawImage(offScreenImage,0,0,this); // no other option than offScreen at this point
				// (double buffering, this is what makes the applet need netscape/ie 4)
				
				ofg.dispose(); // trying to solve memory problems... didn't seem to make much of a difference
			}
		}     // end if noRects
		// snm - scrollbar changed here?
		if (scrollBarY > height)
			record.theTabPanel.tlpScroll.setVisible(true);
		else {
			record.theTabPanel.tlpScroll.setVisible(false);
			record.theTabPanel.tlpScroll.setValue(0);
		}  
		loop: if (inClick > 0) {/* this portion till end of paint modified 11/20*/
			while (inClick != zoom_steps) {
				int i = 0; 
				//int x = lastInX;
				//int y = lastInY;
				zoomIn(i,lastInX,lastInY);
				decStatus(i,lastInX,lastInY);
				if (mouseRel == 0){
					System.out.println("Broken loop");
					break loop;
				}
			}
			inClick = 0;/*modified 11/22*/
		}
		//inClick = 0;
		
		if (outClick > 0) {
			while (outClick != zoom_steps) {
				int i = 0; 
				int x = lastOutX;
				int y = lastOutY;
				zoomOut(i,x,y);
				decStatus(i,x,y);
				if (mouseRel == 0)
					break;
			}
			outClick = 0;
		}
	}
	
	public static void setApplet(newApplet inApplet) {
		theApplet = inApplet;
	}
	
	/**
	 * Create the visible circular cursor, then
	 * look for items under the cursor circle, add them into the labels
	 * list as <code>LiteDisplacedLabel</code> and call the layout
	 * algorithm.
	 */
	public void show_labels(int x, int y, boolean layoutY) {
		Point p = new Point(x, y);
		//Vector streamlist = new Vector();
		Vector substreamlist;
		facet aFacet = null;
		
		cursor = new LiteRect(new Rectangle(x-cursor_radius, y-cursor_radius, 
				2*cursor_radius, 2*cursor_radius),1, Color.red, null);
		if (record.excentric)   {
			// Julia, 10/12/98, move the following three lines here so that the excentric layout can be changed via control panel
			int column=1;
			if (record.column[1])   column = 2;
			System.out.println(getSize().height);
			layout = new StableLayout(getSize().width, getSize().height, column);
			
			for(int i = 0; i < n_key; i++) {
				aFacet = (facet)(recordTable.get(new Integer(i)));
				substreamlist = aFacet.rubber_band(x, y, cursor_radius);
				for (int j=0; j<substreamlist.size(); j++)  {
					//streamlist.addElement(substreamlist.elementAt(j));
					storyRecord thisRecord = (storyRecord)(substreamlist.elementAt(j));
					int centerX = thisRecord.startX + (thisRecord.getBarArea().intersection(cursor.getBounds())).width/2;
					int centerY = thisRecord.startY + thisRecord.getBarArea().height/2;
					if (!thisRecord.getCause().equals(" "))   {
						if (record.column[1]) {
							labels.addElement(new LiteDisplacedLabel(thisRecord.getCause(), 
									new Point(centerX, centerY),
									new Point(centerX, centerY),
									1,
									font1,
									thisRecord.getRectColor(),
									Color.yellow));
						}
						else {
							labels.addElement(new LiteDisplacedLabel(thisRecord.getCause(), 
									new Point(centerX, centerY),
									1,
									font1,
									thisRecord.getRectColor(),
									Color.yellow));
						}
					}
				}                      
			}
			if (layoutY)
				layout_labels(p);
			repaint_rect(labels.getBounds().union(cursor.getBounds()));
		}
		else if (record.infotip)    {  	
			genRecord selectedRecord = inRegion(x,y,true,false);
			if (selectedRecord != null)   {
				String msg = selectedRecord.getInputLine();
				String [] msgs = msg.split(",");
				if(msgs[4].equalsIgnoreCase("p4")) {
					msgs[4] = "Very Low";
				}
				else if(msgs[4].equalsIgnoreCase("p8")) {
					msgs[4] = "Low";
				}
				else if(msgs[4].equalsIgnoreCase("p10")) {
					msgs[4] = "Normal";
				}
				else if(msgs[4].equalsIgnoreCase("p12")) {
					msgs[4] = "High";
				}
				else if(msgs[4].equalsIgnoreCase("p18")){
					msgs[4] = "Very High";
				}
				else if(msgs[4].equalsIgnoreCase("p1")){
					return;
				}
				
				String val = " ";
				//String concept = msgs[7].substring(msgs[7].indexOf("::")+2, msgs[7].lastIndexOf("::"));
				String infotip = null;
				String [] xtras = msgs[7].split("\\$\\$");
				String name = xtras[0];
				String conceptName = "";
				String concept_cd = xtras[2];
				if(name.startsWith("\"E")) {
					conceptName = name.substring(name.indexOf("::")+2, name.lastIndexOf("::"));
					infotip = conceptName+", "+msgs[1];
				}
				else if(name.startsWith("\"C")) {
					conceptName = PDOQueryClient.getCodeInfo(concept_cd);
					if(msgs[7].indexOf("Value")>=0) {
						val = xtras[0].substring(xtras[0].lastIndexOf(": ")+2, xtras[0].length()-2);
						infotip = conceptName+" ("+makeReadableCodeString(concept_cd)+"), "+val+"("+msgs[4]+")"+", "+msgs[1];//+": "+val+" ("+msgs[4]+")";
					}
					else {
						infotip = conceptName+" ("+makeReadableCodeString(concept_cd)+"), "+msgs[1];
					}
					System.out.println("concept_cd: "+concept_cd+" code: "
							+makeReadableCodeString(concept_cd));
				}
				//else if(name.startsWith("\"P")) {					
				//	conceptName = getProviderDetails(concept_cd);
				//	infotip = conceptName+" ("+concept_cd+"), "+msgs[1];
				//}
				
				/*if(msgs[7].indexOf("Value")>=0) {
					val = xtras[0].substring(xtras[0].lastIndexOf(": ")+2, xtras[0].length()-2);
					infotip = conceptName+" ("+makeReadableCodeString(concept_cd)+"), "+val+"("+msgs[4]+")"+", "+msgs[1];//+": "+val+" ("+msgs[4]+")";
				}
				else {
					infotip = conceptName+" ("+makeReadableCodeString(concept_cd)+"), "+msgs[1];
				}*/
								
				infoTipLabel = new LiteLabel(infotip,
						new Point(x+10, y+30),
						1,
						fontMetrics1.getFont(),
						Color.black,
						Color.yellow);
				
				Rectangle r = infoTipLabel.getBounds();
				if(y > getSize().getHeight()-40) {
					infoTipLabel = new LiteLabel(infotip,
							new Point(x+10, y-5),
							1,
							fontMetrics1.getFont(),
							Color.black,
							Color.yellow);
				}
				
				if((x+r.width) < (getSize().width-10)) {
					infoTipLabel.setAlignment(LiteLabel.LEFT);
				} 
				else if(x > r.width) {
					infoTipLabel.setAlignment(LiteLabel.RIGHT);
				}
				else {
					infoTipLabel.setAlignment(LiteLabel.RIGHT);//CENTER);
				}
				
				repaint_rect(infoTipLabel.getBounds());  
			}
		}
	}
	
	/**
	 * Only repaint the right rectangle.
	 */
	public void repaint_rect(Rectangle r) {
		repaint(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Remove all the labels as well as the cursor for excentric.
	 * Remove the infotip label.
	 */
	public void hide_labels() {
		if (record.excentric && cursor != null) {
			//System.out.println("hide labels");
			Rectangle r = labels.getBounds().union(cursor.getBounds());
			labels.removeAllElements();
			cursor = null;
			repaint_rect(r);
		}
		else if (record.infotip && infoTipLabel != null)  {
			Rectangle r = infoTipLabel.getBounds();
			infoTipLabel = null;
			repaint_rect(r);
		}
	}
	
	/**
	 * Conditionnaly show the excentric labels at the next mouse move.
	 * If the mouse moved too much, just hide the labels.
	 */
	public void track_focus(int x, int y, boolean layout) {
		boolean shown = (cursor != null);
		hide_labels();
		if (record.excentric)   {
			if (shown &&
					dist2(x - last_x, y - last_y) <	(cursor_radius * cursor_radius))
				idle(x, y, layout);
		}
		else if (record.infotip)
			idle(x, y, layout);
	}
	
	/**
	 * Compute the layout of excentric labels.
	 */
	public void layout_labels(Point center) {
		if (labels.isEmpty())
			return;
		layout.setSize(getSize());
		layout.layout(labels.getVector(), center, projection_radius);
	}
	
	/**
	 * Display labels if they are not displayed yet.  Use <code>cursor</code>
	 * to figure that out.
	 */
	void idle(int x, int y, boolean layout) {
		if (record.excentric && cursor == null) {
			show_labels(x, y, layout);
			last_x = x;
			last_y = y;
		}
		else if (record.infotip)   
			show_labels(x, y, layout);
	}
	
	static boolean inside(int x, int y, int radius, int x2, int y2) {
		int dx = x2 -x, dy = y2 - y;
		return (dx * dx + dy * dy) <= (radius * radius);
	}
	static boolean intersects(int x, int y, int radius, Rectangle r) {
		return inside(x, y, radius, r.x, r.y) ||
		inside(x, y, radius, r.x + r.width, r.y) ||
		inside(x, y, radius, r.x + r.width, r.y + r.height) ||
		inside(x, y, radius, r.x, r.y + r.height);
	}
	static int dist2(int dx, int dy) { return dx*dy + dy*dy; }
	
	//called from talk method in slider class (called from adjust in year(or other)slider)
	public void listen(MyDate validDateMin, MyDate validDateMax){
		this.validDateMin = validDateMin;
		this.validDateMax = validDateMax;
		aScale.setScale(validDateMin, validDateMax,today);
		relabeling = true;   
		slide = true;     
		
		for(int k=0;k<n_rect;k++) 
			rects[k].setInitFlag();
		
		if(selectedIndex != -1)
			rects[selectedIndex].unSetInitFlag();
		
		for(int j=0;j < aggregateNumber;j++) {
			if( aggregates[j] != null && selectedIndex <= aggregates[j].getGenList().size()) {
				aggregates[j].unSetInitFlags(rects);
				//shouldRepaint true is redundant here... also not in scope
			}
		}
		
		if(!threadTest)
			repaint();// threadtest
		record.changed = true;
	}
	
	public MyDate selectedStartDate(int x,int y) {
		
		facet tempFacet;
		genRecord selectedRecord = null;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				selectedRecord = tempFacet.getSelected(x,y);
			}
		}
		return (selectedRecord.getStartdate());
	}
	
	public MyDate selectedEndDate(int x,int y) {
		
		facet tempFacet;
		genRecord selectedRecord = null;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				selectedRecord = tempFacet.getSelected(x,y); 
			}
		}
		return (selectedRecord.getEnddate());
	}
	
	// 1/6/98 for double click to zoom on full aggregate (see mouseDown)
	
	public MyDate fullSelectedStartDate(int x,int y) {
		
		facet tempFacet;
		genRecord selectedRecord = null;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				selectedRecord = tempFacet.getSelected(x,y);
			}
		}
		return (selectedRecord.getStartdate());
	}
	
	
	public MyDate fullSelectedEndDate(int x,int y) {
		
		facet tempFacet;
		genRecord selectedRecord = null;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				selectedRecord = tempFacet.getSelected(x,y); 
			}
		}
		return (selectedRecord.getEnddate());
	}
	
	// 1/7/98 for shift-click to url.
	
	public genRecord getSelection(int x,int y) { // should be: get most specific, i.e. storyRecord?
		
		// probably also need to check if anything IS selected
		facet tempFacet;
		genRecord selectedRecord = null;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				selectedRecord = tempFacet.getSelected(x,y); 
			}
		}
		return (selectedRecord);
	}
	
	// dan, 1/9/98 to fix single click zoom on mouse pointer: need function to
	// to tell if pointer is not over anything
	
	public boolean nothingSelected(int x,int y) { 
		
		// probably also need to check if anything IS selected in  functions too... (like selected
		// EndDate...
		
		facet tempFacet;
		genRecord selectedRecord = null;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				return false;
			}
		}
		return true;
	}
	
	// 10/30/98
	public genRecord inRegion(int x, int y, boolean data, boolean label) { 
		facet tempFacet;
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			genRecord selectedRecord = tempFacet.inRegion(x,y,data,label,10);
			if(selectedRecord != null) { 
				//System.out.println("current x, y: "+x+","+y);
				//Rectangle r = ((storyRecord) selectedRecord).getBarArea();
				//System.out.println("matched x, y: "+r.x+","+r.y);
				return selectedRecord;
			}
		}
		return null;
	}
	
	public boolean noSelection() {
		
		if(selectedIndex == -1)
			return true;
		else
			return false;
		
	}
	
	public scale getScale() {
		
		return aScale;
		
	}
	
	public boolean existsPreviousEvent(Hashtable afacetList,int j) {
		
		for(int k=j;k>0;k--) {
			genRecord agenRecord = (genRecord)afacetList.get(new Integer(k));
			if( !(agenRecord.getCause().equals("nolabel")) )
				return true;
			else
				return false;
		}
		
		return true;
		
	}
	
	public int previousEventWidth( Hashtable afacetList,int j) {
		
		for(int k=j;k>0;k--) {
			genRecord aGenRecord = (genRecord)afacetList.get(new Integer(k));
			if( !(aGenRecord.getCause().equals("nolabel")) )
				return aGenRecord.getRectWidth();
		}
		return -1;
	}
	
	public MyDate coordToDate(int start){
		
		//long diff = validDateMin.MinDiff(validDateMax);
		
		double scaleFactor = (double) ( (double)rwinWidth/ (double) (aScale.getDateMin()).MinDiff( aScale.getDateMax() ));
		
		MyDate dateMinTemp = record.theData.getMinDate();
		MyDate dateMaxTemp = record.theData.getMaxDate();
		
		long diff = validDateMin.MinDiff(validDateMax);
		
		return(validDateMin.DateAfterMins((long)Math.round((double) diff * (start-lwinWidth) /(double)(rwinWidth) )));
	}
	
	public int dateToCoord(MyDate thisDate) {/*Added 11/21/98 - Partha*/
		long diff = validDateMin.MinDiff(thisDate);
		long x = validDateMin.MinDiff(validDateMax);
		if (x == 0)
		{
			return 0;
		}
		else {
			return((int)(diff*rwinWidth/x + lwinWidth));
		}
	}
	
	public int getRwinOffset() {
		
		return rwinOffset;
		
	}
	
	public int getRwinWidth() {
		
		return rwinWidth;
		
	}
	
	public int getFontTextHeight() {
		
		return font1TextHeight;
		
	}
	
	public void setLabelFont(int fontsize) {
		font1 = new Font("TimesRoman", Font.PLAIN, fontsize);
		fontMetrics1 = getFontMetrics(font1); // used for datalabels
		font1TextHeight = fontMetrics1.getHeight() - fontMetrics1.getLeading() ;
	}
	
	private Graphics ofg;
	
	public Graphics getOfg() {
		
		return ofg;
		
	}
	
	// returns true if the aggregate is not in the present scale
	
	public boolean offScale(genRecord checkThis) {
		
		return (aScale.offScale(checkThis.getStartdate(),checkThis.getEnddate()));   
		
	}
	public MyDate scaleMin(genRecord checkThis)   {
		return (aScale.scaleMin(checkThis.getStartdate()));
	}
	
	public MyDate scaleMax(genRecord checkThis)   {
		return (aScale.scaleMax(checkThis.getEnddate()));
	}
	
	public void grep(String searchString) {
		n_key = recordTable.size();
		facet tempFacet;
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			tempFacet.select(searchString); 
		}     
		repaint();
	}
	
	// 3/11/98 dan added function fof grep on control-click
	
	public String getLabel(int x,int y) {
		genRecord selectedRecord = null;
		facet tempFacet; 
		
		for(int a=0;a<n_key;a++) {
			tempFacet = (facet)(recordTable.get(new Integer(a)));
			if(tempFacet.contains(x,y)) {
				selectedRecord = tempFacet.getSelected(x,y); 
			}
		}
		String label = new String(selectedRecord.getCause());
		return label;
		
	}
	
	public void zoomIn(int i, int x, int y) {/* Changed 11-22-98 - Partha*/
		
		//int dValue = 3;
		/* START MODIFICATION - Partha 11/22*/
		zoom_ratio = ((Integer)ResourceTable.get("zoom_ratio")).intValue();
		zoom_steps = ((Integer)ResourceTable.get("zoom_steps")).intValue();
		
		MyDate temp = coordToDate(x);
		
		MyDate newMin = temp.copy();
		MyDate newMax = temp.copy();
		
		try {
			lastPosnMax[0] = new MyDate(dateMax.getMonth(),dateMax.getDay(),dateMax.getYear(),dateMax.getHour(),dateMax.getMin());
			lastPosnMin[0] = new MyDate(dateMin.getMonth(),dateMin.getDay(),dateMin.getYear(),dateMin.getHour(),dateMin.getMin());
		}
		catch(Exception aae){System.out.println("Big Exception");}
		
		
		int xmin = dateToCoord(validDateMin);
		System.out.println("ValidDateMin : "+xmin);
		int xmax = dateToCoord(validDateMax);
		System.out.println("dateMax ;"+xmax);
		
		newMin = coordToDate(xmin + (int)(zoom_ratio*0.08*(x - xmin)));
		newMax = coordToDate(xmax - (int)(zoom_ratio*0.08*(xmax - x)));
		lastPosnMax[zoomCounter+1] = new MyDate(newMax.getMonth(),newMax.getDay(),newMax.getYear(),newMax.getHour(),newMax.getMin());
		lastPosnMin[zoomCounter+1] = new MyDate(newMin.getMonth(),newMin.getDay(),newMin.getYear(),newMin.getHour(),newMin.getMin());
		
		
		/*END MODIFICATION - Partha 11/22*/
		
		
		if(!(newMin.after(newMax))) {
			for (int slideValue = 1; slideValue < 11; slideValue++)
			{
				((mainPanel)getParent()).theYearSlider.adjust(slideValue,newMin,newMax,rwinOffset);// 10 for event type, change this!
				
				if(aScale.getMode().equals("month"))
					((mainPanel)getParent()).theMonthSlider.adjust(slideValue,newMin,newMax,rwinOffset);
				if(aScale.getMode().equals("day"))
					((mainPanel)getParent()).theWeekSlider.adjust(slideValue,newMin,newMax,rwinOffset);
				
				validDateMin = new MyDate(newMin.getMonth(),newMin.getDay(),newMin.getYear(),newMin.getHour(),newMin.getMin());
				validDateMax = new MyDate(newMax.getMonth(),newMax.getDay(),newMax.getYear(),newMax.getHour(),newMin.getMin());
				aScale.setScale(validDateMin, validDateMax,today);
				
				MyDate shouldBeMin = new MyDate(coordToDate(lwinWidth).getMonth(),
						coordToDate(lwinWidth).getDay(),
						coordToDate(lwinWidth).getYear(),
						coordToDate(lwinWidth).getHour(),
						coordToDate(lwinWidth).getMin());
				record.theTabPanel.upBar.listen(validDateMin, validDateMax);
				//System.out.println("Before Paint");
			}
			
			if(!threadTest){
				// System.out.println("Waiting5");
				repaint();
				// threadtest
			}else{
				// System.out.println("Waiting6");
				record.changed = true;
			}
						
			/*try {
			 Thread.sleep(250);
			 }
			 catch(InterruptedException ex){};*/						
		}
		inClick++;
		zoomCounter++;/*added Partha -11/23 to keep track of the positions that
		the newMin and the newMax take in zoomIn so that they can be reversed in zoomOut*/
		
	}
	
	public void zoomOut(int i, int x, int y) {/*Changed 11-22-98 - Partha*/
		
		/* START MODIFICATION - Partha 11/22*/
		zoom_style = ((Integer)ResourceTable.get("zoom_style")).intValue();
		/* for the time being, can also simply put zoom_style = 1*/
		
		
		MyDate newMin = new MyDate(validDateMin.getMonth(),validDateMin.getDay(),validDateMin.getYear(),validDateMin.getHour(),validDateMin.getMin());
		MyDate newMax = new MyDate(validDateMax.getMonth(),validDateMax.getDay(),validDateMax.getYear(),validDateMax.getHour(),validDateMax.getMin());
		if (zoom_style == 0) {
			MyDate temp = new MyDate(coordToDate(x).getMonth(),coordToDate(x).getDay(),coordToDate(x).getYear(),coordToDate(x).getHour(),coordToDate(x).getMin());
			
			long diff = validDateMin.DateDiff(validDateMax)*3/2;
			
			while( diff > 0 ) {
				if((int)diff > 30) {
					newMax.addDays(30);
					newMin.subtractDays(30);
					diff -= 30;
					
				}
				else {
					newMax.addDays((int)diff);
					newMin.subtractDays((int)diff);
					diff = 0;
				}
			}
		}
		//int xmin = dateToCoord(newMin);
		//int xmax = dateToCoord(newMax);
		//rDiff = dateToCoord(dateMax) - dateToCoord(validDateMax);
		//lDiff = dateToCoord(validDateMin) - dateToCoord(dateMin);
		//newMin = coordToDate(xmin - (int)(0.5*lDiff/2));
		//newMax = coordToDate(xmax + (int)(0.5*rDiff/2));
		// inClick--;
		else {
			zoomCounter--;
			if(zoomCounter<0)
				zoomCounter = 0;
			//{
			newMin = lastPosnMin[zoomCounter].copy();
			newMax = lastPosnMax[zoomCounter].copy();
			//}
		}
		/* END MODIFICATION -Partha 11/22 */
		
		if(newMax.after(dateMax))
			newMax = new MyDate(dateMax.getMonth(),dateMax.getDay(),dateMax.getYear(),dateMax.getHour(),dateMin.getMin());
		
		if( newMin.after(dateMin) && dateMax.after(newMax) ) {
			((mainPanel)getParent()).theYearSlider.adjust(10,newMin,newMax,rwinOffset);// 10 for event type, change this!
			
			if(aScale.getMode().equals("month"))
				((mainPanel)getParent()).theMonthSlider.adjust(10,newMin,newMax,rwinOffset);
			if(aScale.getMode().equals("day"))
				((mainPanel)getParent()).theWeekSlider.adjust(10,newMin,newMax,rwinOffset);
		}
		else {
			((mainPanel)getParent()).theYearSlider.adjust(10,dateMin,dateMax,rwinOffset);// 10 for event type, change this!
			
			if(aScale.getMode().equals("month"))
				((mainPanel)getParent()).theMonthSlider.adjust(10,dateMin,dateMax,rwinOffset);
			if(aScale.getMode().equals("day"))
				((mainPanel)getParent()).theWeekSlider.adjust(10,dateMin,dateMax,rwinOffset);
		}
		outClick++;
	}     
	
	public void decStatus(int i, int x, int y) {
		
		if (theApplet != null) {
			// String theURL = new String("http://www.cs.umd.edu/projects/hcil/Research/1997/yidemo/DOCS/cathrepframe.html");
			// trying out single click for jump 1/13/98 ( dan ) at same time as trying double click
			// zoom
			System.out.println("Before ShowStatus 0");
			genRecord selected = getSelection(x,y);
			
			if(selected == null) {
				return;
			}
			
			System.out.println("Before ShowStatus 1");
			System.out.println(x);
			System.out.println(y);
			String theURL = selected.getUrl();
			System.out.println("Before ShowStatus 1.5");
			
			theApplet.showStatus(theURL);
			System.out.println("Before ShowStatus 2");
			if( theURL.substring(0,4).equals("http"))
				theApplet.showDocument(theURL); // removing removes the thread errors? nope...
			else
				theApplet.showDocument(thisApplet.getCodeBase()   + theURL); // removing removes the thread errors? nope..
			System.out.println("Before ShowStatus 2.5");
		}
		else 
			theApplet.showStatus("");
		
		System.out.println("Before ShowStatus 3");
		
		if(!threadTest)
			repaint(); 
		else
			record.changed = true;
	}
	
	public int getTimeLinePanelDataheight() {
		return dataheight;
	}
	
	private String getKey()
	{
		key = ExplorerC.noteKey;
		String path = null;
		
		if(key == null) {
			if((path = getNoteKeyDrive()) == null) {
				Object[] possibleValues = { "Type in the key", "Browse to find the file containing the key" };
				String selectedValue = (String) JOptionPane.showInputDialog(this, 
						"You have selected an item associated with a report\n"+
						"which contains protected health information.\n"+
						"You need a decryption key to perform this operation.\n"+
						"How would you like to enter the key?\n" +
						"(If the key is on a floppy disk, insert the disk then\n select " +
						"\"Browse to find the file containing the key\")", "Notes Viewer",
						JOptionPane.QUESTION_MESSAGE, null,
						possibleValues, possibleValues[0]);
				if(selectedValue == null) {
					return "Not a valid key";
				}
				if(selectedValue.equalsIgnoreCase("Type in the key")) {
					key = JOptionPane.showInputDialog(this, "Please input the decryption key"); 
					if(key == null) {
						return "Not a valid key";
					}
				}
				else {
					JFileChooser chooser = new JFileChooser();       
					int returnVal = chooser.showOpenDialog(this);
					if(returnVal == JFileChooser.CANCEL_OPTION) {
						return "Not a valid key";
					}
					
					File f = null;
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						f = chooser.getSelectedFile();
						System.out.println("Open this file: "+f.getAbsolutePath());	
						
						BufferedReader in = null;
						try {	    			 
							in = new BufferedReader(new FileReader(f.getAbsolutePath()));
							String line = null;
							while((line = in.readLine()) != null) {
								if(line.length() > 0) {
									key = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
								}
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						finally {
							if(in!=null) {
								try {
									in.close();
								}
								catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			else {
				System.out.println("Found key file: "+path);
				BufferedReader in = null;
				try {	    			 
					in = new BufferedReader(new FileReader(path));
					String line = null;
					while((line = in.readLine()) != null) {
						if(line.length() > 0) {
							key = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					if(in!=null) {
						try {
							in.close();
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		if(key == null) {
			return null;
		}
		
		return key;
	}
	
	private String[] getNotes(String patientNumber, String concept_cd, 
			String start_date, String encounterNumber, String providerId, String modifier_cd) {
		
		/*Connection oConnection = null;
		String sSQL = null;
		ResultSet oRs = null;
		String notes = null;
		String valtype = null;
		
		//call pdo service here!!!!!
		oConnection = DBLib.openJDBCConnection(
				System.getProperty("datamartURL"),
				System.getProperty("datamartDriver"),
				System.getProperty("datamartUser"),
				System.getProperty("datamartPassword"));
		
		
		if (System.getProperty("applicationName").equals("BIRN"))
		{
	    	 
	    	 sSQL = "select OBSERVATION_BLOB, valtype_cd from "
	    		 	+ System.getProperty("datamartDatabase")
	    	 		+ ".observation_fact"
	     		 	+ " where event_ide = '"+patientNumber+"' and"
	     		 	+ " concept_cd = '"+concept_cd+"'"
	     		 	+ " and date_trunc('second', start_date) = '" + start_date + "'"; 
	    	 
	    	 System.out.println("before query: " + new Date());
			 System.out.println("Query: " + sSQL);
				
			 try {
				 oRs = DBLib.doQuery(oConnection, sSQL);
			 }
			 catch (Exception e) {
				 System.out.println(e.getMessage());
				 return null;
			}
			
			System.out.println("after query: " + new Date());
			
			try { 
				while(oRs.next()) { //!oRs.isAfterLast()) {
					notes = oRs.getString(1);
					valtype = oRs.getString(2);

					if(notes != null) {
						break;
					}
				}
				
				if(notes == null) {
					return null;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}	
			
		}
		else {
			sSQL = "select OBSERVATION_BLOB, valtype_cd from "
				+ System.getProperty("datamartDatabase")
				+ ".observation_fact"
				+ " where patient_num = "+patientNumber+" and"
				+ " concept_cd = '"+concept_cd+"'"
				+ " and start_date = to_date('"+start_date+" AM', 'mm-dd-yyyy hh:mi am')";
			
			System.out.println("before query: " + new Date());
			System.out.println("Query: " + sSQL);
			
			try {
				oRs = DBLib.doQuery(oConnection, sSQL);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			System.out.println("after query: " + new Date());
			
			Clob data = null;
			try { 
				oRs.next();
				while(!oRs.isAfterLast()) {
					data = oRs.getClob(1);
					valtype = oRs.getString(2);
					if(data != null) {
						break;
					}
					oRs.next();
				}
				
				if(data == null) {
					return null;
				}
				notes = data.getSubString(1, (int)(data.length()));
				System.out.println("notes: " + notes);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		DBLib.closeConnection(oConnection);		*/
		
		try {
			PDORequestMessageFactory pdoFactory = new PDORequestMessageFactory();
			
			String xmlStr = pdoFactory.requestXmlMessage(patientNumber, 
					encounterNumber, concept_cd, providerId, modifier_cd, start_date);
			String result = PDOQueryClient.sendQueryRequestREST(xmlStr);
			
			//FileWriter fwr = new FileWriter("c:\\testdir\\response.txt");
			//fwr.write(result);
			System.out.println(result);
			return PDOQueryClient.getNotes(result);
			//return new String[] {notes, valtype};
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryptBlob(String blob)
	{		
		if (key == null || key.length() == 0)
			getKey();
		noteCryptUtil util = new noteCryptUtil(key);
		if(util == null) {
			JOptionPane.showMessageDialog(this, "Not a valid key");
			return "Not a valid key";
		}
		String deNote = util.decryptNotes(blob);
		System.out.println("notes: " + deNote);
		
		if(!deNote.equalsIgnoreCase("[I2B2-Error] Invalid key")) {
			ExplorerC.noteKey = new String(key);
		}
		
		return deNote;
	}
	
	private String getNoteKeyDrive() {
		File[] drives = File.listRoots();
		String filename = "i2b2notekey.txt";
		for(int i=drives.length-1; i>=0; i--){
			if(drives[i].getPath().startsWith("A") || 
					drives[i].getPath().startsWith("B")) {
				continue;
			}
			
			File tmp = new File(drives[i]/*+File.separator*/+filename);
			if(tmp.exists()) {
				return drives[i]/*+File.separator*/+filename;
			}
			//else {
			//	 return null;
			//}
		}
		
		File testFile = new File("i2b2notekey.txt");
		System.out.println("file dir: "+testFile.getAbsolutePath());
		if(testFile.exists()) {
			return testFile.getAbsolutePath();
		}
		
		return null;
	}
	
	/*private String getConceptDetails(String concept_cd) {		
		Connection oConnection = null;
		String sSQL = null;
		ResultSet oRs = null;
		String data = null;
		
		oConnection = DBLib.openJDBCConnection(
				System.getProperty("datamartURL"),
				System.getProperty("datamartDriver"),
				System.getProperty("datamartUser"),
				System.getProperty("datamartPassword"));
		
		//((OracleConnection)oConnection).setDefaultExecuteBatch(20);

		if (System.getProperty("applicationName").equals("BIRN"))
		{
			
		}
		else {
			sSQL = "select NAME_CHAR from "
				+ System.getProperty("datamartDatabase")
				+ ".CONCEPT_DIMENSION"
				+ " where concept_cd = '"+concept_cd+"'";
				//+ " and start_date = to_date('"+start_date+" 12:00 AM', 'mm-dd-yyyy hh:mi am')";
			
			//System.out.println("before query: " + new Date());
			//System.out.println("Query: " + sSQL);
			
			try {
				oRs = DBLib.doQuery(oConnection, sSQL);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			//System.out.println("after query: " + new Date());

			try { 
				oRs.next();
				while(!oRs.isAfterLast()) {
					data = oRs.getString(1);
					if(data != null) {
						break;
					}
					oRs.next();
				}
			}
			catch(Exception e) {
				//e.printStackTrace();
			}
		}
		
		DBLib.closeConnection(oConnection);	
		
		if(data == null) {
			return concept_cd;
		}
		else {
			return data;
		}
	}*/
	
	/*private String getProviderDetails(String provider_id) {		
		Connection oConnection = null;
		String sSQL = null;
		ResultSet oRs = null;
		String data = null;
		
		oConnection = DBLib.openJDBCConnection(
				System.getProperty("datamartURL"),
				System.getProperty("datamartDriver"),
				System.getProperty("datamartUser"),
				System.getProperty("datamartPassword"));
		
		//((OracleConnection)oConnection).setDefaultExecuteBatch(20);

		if (System.getProperty("applicationName").equals("BIRN"))
		{
			
		}
		else {
			sSQL = "select NAME_CHAR from "
				+ System.getProperty("datamartDatabase")
				+ ".PROVIDER_DIMENSION"
				+ " where PROVIDER_ID = '"+provider_id+"'";
				//+ " and start_date = to_date('"+start_date+" 12:00 AM', 'mm-dd-yyyy hh:mi am')";
			
			//System.out.println("before query: " + new Date());
			//System.out.println("Query: " + sSQL);
			
			try {
				oRs = DBLib.doQuery(oConnection, sSQL);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			//System.out.println("after query: " + new Date());

			try { 
				oRs.next();
				while(!oRs.isAfterLast()) {
					data = oRs.getString(1);
					if(data != null) {
						break;
					}
					oRs.next();
				}
			}
			catch(Exception e) {
				//e.printStackTrace();
			}
		}
		
		DBLib.closeConnection(oConnection);	
		
		if(data == null) {
			return provider_id;
		}
		else {
			return data;
		}
	}*/
	
	private String makeReadableCodeString(String sCode) {
		
		String sFirstChar;
		String sSecondChar;
		String result = null;
    
		if(sCode.length() == 0) {
			return result;
		}
				
		if(sCode.indexOf(":") >= 0) {
			return sCode;
		}
		else if(sCode.equalsIgnoreCase("Death") || 
				sCode.equalsIgnoreCase("Encounter_range")) {
			return "";
		}
		
		sCode = sCode.toUpperCase();
		sFirstChar = sCode.substring(0, 1);
		sSecondChar = sCode.substring(1, 2);
		
		if(Character.isDigit(sFirstChar.charAt(0))) {
			result = "ICD:"+insertIcd9Period(sCode);
		}
		else if(sFirstChar.equals("E")) {
			result = "ICD:"+insertIcd9Period(sCode);
		}
		else if(sFirstChar.equals("V")) {
			result = "ICD:"+insertIcd9Period(sCode);
		}
		else if(sFirstChar.equals("P")) {
			result = "ICD:"+insertIcd9Period(sCode);
		}
		else if(sFirstChar.equals("C") && Character.isDigit(sSecondChar.charAt(0))) {
			result = "CPT:"+sCode.substring(sCode.length()-5, sCode.length());
		}
		else if(sFirstChar.equals("C") && !Character.isDigit(sSecondChar.charAt(0))) {
			result = "HCPCS:"+sCode.substring(sCode.length()-5, sCode.length());
		}
		else if(sFirstChar.equals("L")) {
			result = "";
		}
		else {
			result = sCode;
		}
		return result;
	}
	
	private String insertIcd9Period(String icd9) {
		String sFirstChar;
		
		if(icd9.length() == 0) {
			return icd9;
		}
		else if(icd9.indexOf(".")>=0) {
			return icd9;
		}
		
		icd9 = icd9.toUpperCase();
		sFirstChar = icd9.substring(0, 1);
		if(sFirstChar.equals("E")) {
			if(icd9.length() <= 4) {
				return icd9;
			}
			else if(icd9.length() == 5 || icd9.length() == 6) {
				return icd9.substring(0, 4)+"."+icd9.substring(4);
			}
			//else if(icd9.length() == 6) {
			//	return icd9.substring(0, 3)+"."+icd9.substring(4);
			//}
 		}
		else if(sFirstChar.equals("P")) {
			if(icd9.length() <= 3) {
				return icd9.substring(1);
			}
			else if(icd9.length() == 4 || icd9.length() == 5) {
				return icd9.substring(1, 3)+"."+icd9.substring(3);
			}
			//else if(icd9.length() == 5) {
			//	return icd9.substring(1, 2)+"."+icd9.substring(3);
			//}
 		}
		else if(sFirstChar.equals("V")) {
			if(icd9.length() <= 3) {
				return icd9;
			}
			else if(icd9.length() == 4 || icd9.length() == 5) {
				return icd9.substring(0, 3)+"."+icd9.substring(3);
			}
			//else if(icd9.length() == 5) {
			//	return icd9.substring(0, 2)+"."+icd9.substring(3);
			//}
 		}
		else {
			if(icd9.length() <= 3) {
				return icd9;
			}
			else if(icd9.length() == 4 || icd9.length() == 5) {
				return icd9.substring(0, 3)+"."+icd9.substring(3);
			}
			//else if(icd9.length() == 5) {
			//	return icd9.substring(0, 2)+"."+icd9.substring(3);
			//}
		}
		
		return icd9;
	}
}
