/*
 * Copyright (c) 2006-2017 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Heekyong Park (hpark25)
 *     
 */

package edu.harvard.i2b2.explorer.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;

import edu.harvard.i2b2.common.datavo.pdo.ObservationType;
import edu.harvard.i2b2.timeline.lifelines.Aggregate;
import edu.harvard.i2b2.timeline.lifelines.GenRecord;
import edu.harvard.i2b2.timeline.lifelines.MyColor;
import edu.harvard.i2b2.timeline.lifelines.PDOQueryClient;

public class InfoJFrameOverlapRecs extends javax.swing.JFrame {
	
	private TimeLinePanel panel_;
	public int tHeight;
	private InfoJFrame infoFrame = null;
	private String obKeys[][] = new String[10][6];
	public String status[] = new String[10];
	public boolean pin = false;
	public GenRecord[] selRecs;
	private int numRetrieved;
	private int curIndx;
	private int lMostIndx, rMostIndx;
	private Aggregate cAggr;
	 

	public int getLeftMostIndx() {
		return lMostIndx;
	}
	
	public int getRightMostIndx() {
		return rMostIndx;
	}
	
	public int getCurIndx() {
		return curIndx;
	}
	
    /**
     * Creates new form InfoJFrame
     * 
     */	
	public InfoJFrameOverlapRecs(TimeLinePanel panel, Aggregate curntAggr) { 
    	panel_ = panel;    	
    	cAggr = curntAggr;
    	selRecs=cAggr.getOverlapRecords();
    	
    	initAggrVal();
    	
        initComponents(selRecs);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
           
        parseOverlapList();
        
        DefaultTableCellRenderer colAlignRenderer = new DefaultTableCellRenderer();
        colAlignRenderer.setHorizontalAlignment( JLabel.RIGHT );
        jTable1.getColumnModel().getColumn(4).setCellRenderer( colAlignRenderer );

        
        int total = jTable1.getColumnModel().getTotalColumnWidth();
        int w0 = 20, w1 = 10, w2, w3 = 70, w4 = 85;
        w2 = total - (w0+w1+w3+w4); 
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(w0);
		 jTable1.getColumnModel().getColumn(1).setPreferredWidth(w1);
		jTable1.getColumnModel().getColumn(2).setPreferredWidth(w2);
		jTable1.getColumnModel().getColumn(3).setPreferredWidth(w3);
		jTable1.getColumnModel().getColumn(4).setPreferredWidth(w4);
    }
	
	private void parseOverlapList() {
		Color temp = null;
		int crntTotalNumRow = dtm.getRowCount();
		
		if(crntTotalNumRow < numRetrieved)
    		for(int i = crntTotalNumRow; i<numRetrieved; i++)
    			dtm.addRow(new String [] {"", "", "", "", ""});
    	
    	if(crntTotalNumRow > numRetrieved)
    		for(int i = crntTotalNumRow - 1; i>=numRetrieved; i--)
    			dtm.removeRow(i);
    	
		for(int i=0; i< numRetrieved; i++)
        {
	        if (selRecs[i] != null) {
	        	
	        	String start_date;
	        	
				String msg = selRecs[i].getInputLine();
				String[] msgs = msg.split(",");			
				
				
				String val = " ";
				String infoStr = null;
				String [] xtras = msgs[7].split("\\$\\$");
				String name = xtras[0];
				String conceptName = "";
				String concept_cd = xtras[2];				
				
				
				// Tag ">>" to current record
				if(i == curIndx)
					jTable1.setValueAt(">>", i, 0);
				else
					jTable1.setValueAt("", i, 0);
				
				// show status column (original tick color / read(gray) / starred (yellow)
				MyColor tempColor = new MyColor(msgs[3]);
				temp = tempColor.getColor();
				status[i]=selRecs[i].mark_status;
				
				
				// show brief details 
				if(name.startsWith("\"E")) {
					conceptName = name.substring(name.indexOf("::")+2, name.lastIndexOf("::"));
					infoStr = conceptName;
				}
				else if(name.startsWith("\"C")) {
					conceptName = name.substring(name.indexOf("::") + 2, name
							.lastIndexOf("::"));
					if (conceptName == null || conceptName.equals("")) {
						conceptName = PDOQueryClient.getCodeInfo(concept_cd);
					}
					String cstr ="";
					if(concept_cd !=  null)
					{
						infoStr = conceptName+" ("+concept_cd+")";
					}
					else
						infoStr = conceptName+" ( - )";					
				}
				jTable1.setValueAt(infoStr, i, 2);
				
				start_date = msgs[1];
				if(start_date == null)
					jTable1.setValueAt(null, i, 3);
				else
				{
					String date="";
					String[] dateStr = start_date.split("-");	
					if(dateStr[0].length()<2)
						date=date+"0";
					date=date+dateStr[0]+"-";
					if(dateStr[1].length()<2)
						date=date+"0";
					date=date+dateStr[1]+"-"+dateStr[2];
					
					jTable1.setValueAt(date, i, 3);
				}
				
				String patientNumber = xtras[1]; 
				String encounterNumber = xtras[3];
				String providerId = xtras[4];
				String modifier_cd = xtras[5];
				// Clean the last char in xtra6
				if(xtras.length > 6) {
					xtras[6] = xtras[6].substring(0, xtras[6].length() - 1);
					if (xtras[6] != null && xtras[6].length() > 0)
							start_date = xtras[6];
				}
				
				// save keys for observational data search
				obKeys[i][0] = patientNumber;
				obKeys[i][1] = concept_cd;
				obKeys[i][2] = start_date;
				obKeys[i][3] = encounterNumber;
				obKeys[i][4] = providerId;
				obKeys[i][5] = modifier_cd;
				
				ObservationType ob = panel_.getObservation(patientNumber, concept_cd,
							start_date, encounterNumber, providerId,
							modifier_cd);
				String tVal = ob.getTvalChar();
				BigDecimal nVal=ob.getNvalNum().getValue();
				String unit = ob.getUnitsCd();
				
				
				
				
				if(nVal != null) //show numeric value
				{
					
					if(unit==null)
						jTable1.setValueAt(nVal.setScale(2,  BigDecimal.ROUND_HALF_UP), i, 4);
					else
						jTable1.setValueAt(nVal.setScale(2,  BigDecimal.ROUND_HALF_UP) + " " + unit, i, 4);
				}
				else if(!tVal.equals("")) // show text value
					jTable1.setValueAt(tVal, i, 4);
				else //if the data does not contain a result value
					jTable1.setValueAt("-", i, 4);
				
	        }
		}
		
		TableColumn statCol = jTable1.getColumnModel().getColumn(1);
		statCol.setCellRenderer(new ColorColumnRenderer(temp));
		markOverlapTag();

	}
	
	public void changeCurrentPos(int crntPos) {
		for(int i=0; i < cAggr.getNumRetrieved(); i++) {
			if(i == crntPos)
				jTable1.setValueAt(">>", crntPos, 0);
			else
				jTable1.setValueAt("", i, 0);			
		}
	}
    
	private void initAggrVal() {
		curIndx = cAggr.getCurIndx();
    	numRetrieved=cAggr.getNumRetrieved();
    	lMostIndx = cAggr.getLeftMostIndx();
    	rMostIndx = cAggr.getRightMostIndx();    	
	}
	
	private int calcTblHeight() {
		return tHeight =  jTable1.getRowHeight() * numRetrieved
        		+ jTable1.getIntercellSpacing().height * (numRetrieved +1)
        		+  jTable1.getTableHeader().getPreferredSize().height;
        
	}
	
	private void redrawOverlapPopup() {
		setBounds(panel_.overlapFramePos_x, panel_.overlapFramePos_y, 381, calcTblHeight()+32); // pop up window resize
		calcTblHeight();
		jPanel1.setBounds(7, 25, 366, tHeight); // table area resize
		
		if(cAggr.getLeftMostIndx() == 0) // if current list is the left end of the data stream 
    		jLeftButton.setEnabled(false);
		else
			jLeftButton.setEnabled(true);
		if(cAggr.getAllRecords().size() -1 == cAggr.getRightMostIndx()) // if current list is the right end of the data stream
    		jRightButton.setEnabled(false); // disable right arrow button
		else 
			jRightButton.setEnabled(true);
		
	}
    
    private void initComponents(final GenRecord[] selectedRecords) {
    	getContentPane().setBackground(Color.decode("0xfaf4ce"));
    	getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new JTable(){
        	public String getToolTipText(MouseEvent e) {
                String toolTip = null;
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                int col = columnAtPoint(p);

                try {
                	toolTip = getValueAt(row, col).toString();
                	
                	UIManager.put("ToolTip.background", new ColorUIResource(255, 250, 140)); 
                	Border border = BorderFactory.createLineBorder(new Color(76,79,83)); 
                	UIManager.put("ToolTip.border", border);
                	
                } catch (RuntimeException e1) {
                    //null pointer exception if mouse is over an empty line
                }             

                return toolTip;
            }
        	
        };
        jPinButton = new JButton();
        jCloseButton = new JButton();      
        jLeftButton = new JButton();
        jRightButton =  new JButton();

        setLayout(null);

        jPanel1.setLayout(new java.awt.BorderLayout());
        
        FrameDragListener frameDragListener = new FrameDragListener(this);
        addMouseListener(frameDragListener);
        addMouseMotionListener(frameDragListener);
        setLocationRelativeTo(null);

        
        dtm = new DefaultTableModel(
        		new String [] {
                        "", "", "Concept name (CD)", "Date", "Value"         				
                     },
        		numRetrieved
        ){
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false
                };

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            };
        jTable1.setModel(dtm);       
        ((JLabel) jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
  
        jScrollPane1.setViewportView(jTable1);        
        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        add(jPanel1);
        
        calcTblHeight();        
        jPanel1.setBounds(7, 25, 366, tHeight);
        
        Font thisFont = new Font("Helvetica", Font.BOLD, 10);    
        // close button
        // change to icon image?
        jCloseButton.setFont(thisFont);
        jCloseButton.setText("X");
        jCloseButton.setToolTipText("Close"); // tdw9: added tooltip
        jCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseButtonActionPerformed(evt);                
            }
        });
        add(jCloseButton);
        jCloseButton.setBounds(381 - 45, 2, 50, 20); //tdw9: modified 40 -> 50 so 'x' shows up
        
        // pin button
        // change to icon image?
        jPinButton.setFont(thisFont);
        jPinButton.setText("Pin [Alt+P]");
        jPinButton.setToolTipText("Toggle Pinning"); // tdw9: added tooltip
        jPinButton.setMnemonic(KeyEvent.VK_P);
		
        
        
        jPinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPinButtonActionPerformed(evt);
            }
        });
        add(jPinButton);
        jPinButton.setBounds(381 - 145, 2, 100, 20);
        
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTable1.setFont(new java.awt.Font("Helvetica", 0, 11));
               
        jTable1.addMouseListener(new MouseAdapter() {               	
            public void mousePressed(MouseEvent e) {
            	javax.swing.JTable table =(javax.swing.JTable) e.getSource();
                Point p = e.getPoint();
                final int selectedRow = table.rowAtPoint(p);                
                
                if (e.getClickCount() == 1) {
                	closeInfoJFrame();
                	// open an information pop up box 
					infoFrame = new InfoJFrame(panel_, 
								panel_.getObservation(obKeys[selectedRow][0], obKeys[selectedRow][1],
										obKeys[selectedRow][2], obKeys[selectedRow][3], obKeys[selectedRow][4],
										obKeys[selectedRow][5]),
								selectedRecords[selectedRow],
								selectedRow);
				
					// decide pop up location
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int maxHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
					
					Point loc = panel_.getOverlapFrameLocation();					
					int x = loc.x + 381 + 2; 
					int y = loc.y;
					if(x > (int) screenSize.getWidth() - 382)
						x = loc.x-383;
					if(y + 200 > maxHeight)
						y = (loc.y + tHeight +32) - 200;			
						
					infoFrame.setBounds(x, y, 381, 200);
					
						
					if(!selectedRecords[selectedRow].mark_status.equalsIgnoreCase("S"))
					{
						selectedRecords[selectedRow].mark_status="R";
						status[selectedRow] = "R";
						panel_.repaint();
						
					}
					
					infoFrame.setVisible(true);
					
                	
                }
                else if (e.getClickCount() == 2) {                        	
                    panel_.showNoteViewer(obKeys[selectedRow][0], obKeys[selectedRow][1],
							obKeys[selectedRow][2], obKeys[selectedRow][3], obKeys[selectedRow][4],
							obKeys[selectedRow][5], 
							selectedRecords[selectedRow]);
                	selectedRecords[selectedRow].mark_status="R";
                	status[selectedRow] = "R";
					panel_.repaint();
                }
            }
        });

        jLeftButton.setFont(thisFont);
        jLeftButton.setText("<<");
        jLeftButton.setToolTipText("Previous Page"); // tdw9: added tooltip
        jLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLeftButtonActionPerformed(evt);                
            }
        });
        add(jLeftButton);
        jLeftButton.setBounds(10, 2, 50, 20); // tdw9: changed 45 -> 50 to let text on button show

        jRightButton.setFont(thisFont);
        jRightButton.setText(">>");
        jRightButton.setToolTipText("Next Page"); // tdw9: added tooltip
        jRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRightButtonActionPerformed(evt);   // search next up to 10 overlapped ticks              
            }
        });
        add(jRightButton);
        jRightButton.setBounds(60, 2, 50, 20); // tdw9: changed 45 -> 50 to let text on button show
        
        if(cAggr.getLeftMostIndx() == 0) // if current list is the left end of the data stream 
    		jLeftButton.setEnabled(false);
		else
			jLeftButton.setEnabled(true);
		if(cAggr.getAllRecords().size() -1 == cAggr.getRightMostIndx()) // if current list is the right end of the data stream
    		jRightButton.setEnabled(false); // disable right arrow button
		else 
			jRightButton.setEnabled(true);
        
    } 
 
    // close detailed information pop up box
    public void closeInfoJFrame()  
    {
    	if(infoFrame != null) 
    	{
    		infoFrame.dispose();
			infoFrame = null;
		}
		
    }

    public void closeOverlapPopup()
    {
    	closeInfoJFrame();
    	
    	pin = false;
    	panel_.prev_a = -1;
    	removeOverlapTag();
        setVisible(false);
        //dispose();
        
    }
    private void jPinButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
    	if(pin == false)
    	{
    		pin = true;
    		setAlwaysOnTop (true);
    		jPinButton.setText("Unpin [Alt+U]");    		
    		jPinButton.setMnemonic(KeyEvent.VK_U);
    		jPinButton.updateUI();
    	}
    	else
    	{
    		pin = false;
    		setAlwaysOnTop (false);
    		jPinButton.setText("Pin [Alt+P]");
    		jPinButton.setMnemonic(KeyEvent.VK_P);    		
    		jPinButton.updateUI();
    	}
    		
    }
    
    private void jCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	pin = false;
    	panel_.prev_a = -1;
    	closeInfoJFrame();
    	removeOverlapTag();
        setVisible(false);
        dispose();
    }
    
    private void jLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	removeOverlapTag();
    	selRecs = cAggr.findNxtNeighbrsList(lMostIndx, -1);
    	if( selRecs != null)
		{
        	initAggrVal();
        	redrawOverlapPopup();
         	parseOverlapList();     	
		}    	
    }
    
    private void jRightButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	removeOverlapTag();
    	selRecs = cAggr.findNxtNeighbrsList(rMostIndx, 1);
    	if( selRecs != null)
		{
    		initAggrVal();
    		redrawOverlapPopup();
        	parseOverlapList(); 
		}    	
    }
    
    public void removeOverlapTag()
    {
    	int k=0;
    	for(int i=0; i<numRetrieved; i++)
		{
    		selRecs[i].mark_overlap = false;
    		if(selRecs[i].mark_status.equalsIgnoreCase("O")) {
    			selRecs[i].mark_status="N";  // change overlap to normal status, leave starred and read tags
    			k++;
    		}
		}
    	panel_.repaint();
    }
    
	 public void markOverlapTag()
	 {
	    	for(int i=0; i<numRetrieved; i++)
			{
	    		selRecs[i].mark_overlap = true;
	    		if(selRecs[i].mark_status.equalsIgnoreCase("N"))
	    			selRecs[i].mark_status="O"; // Selected overlapped record to show in a pop up view
			}
			panel_.repaint();
	 } 
	 
	 public boolean cmpSelOverlaps()
	 {
		 
		 return false;
	 }
	
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
      
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	new InfoJFrameOverlapRecs(null, null).setVisible(true);
            }
        });
    }
    
    public void setInfo(String str) {
    }
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jCloseButton;
    private javax.swing.JButton jPinButton;
    private javax.swing.JButton jLeftButton;
    private javax.swing.JButton jRightButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jPanel1;
    private JTable jTable1;
    private DefaultTableModel dtm ;
    // End of variables declaration//GEN-END:variables
    
    public static class FrameDragListener extends MouseAdapter {

        private final JFrame frame;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }
    
    
    class ColorColumnRenderer extends DefaultTableCellRenderer
    {
       Color inputColr;
       int rowIndx;
         
       public ColorColumnRenderer(Color col) {
          super();
          inputColr = col;
         
       }
         
       public Component getTableCellRendererComponent
            (JTable table, Object value, boolean isSelected,
             boolean hasFocus, int row, int column)
       {
    	   Component cell = super.getTableCellRendererComponent
	             (table, value, isSelected, hasFocus, row, column);
    	   
	   	    if (cell instanceof JLabel && value != null) {
	   	        int availableWidth = table.getColumnModel().getColumn(column).getWidth();
	   	        availableWidth -= table.getIntercellSpacing().getWidth();
	   	        Insets borderInsets = getBorder().getBorderInsets(cell);
	   	        availableWidth -= (borderInsets.left + borderInsets.right);
	   	        FontMetrics fm = getFontMetrics( getFont() );
	   	        String cellText = value.toString();
	   	 
	   	        if (fm.stringWidth(cellText) > availableWidth) ((javax.swing.JLabel) cell).setToolTipText(value.toString());
	   	        else ((javax.swing.JLabel) cell).setToolTipText(null);
	   	    }
   	 
	       for(int i=0; i<status.length; i++)
	        {
	    	   if(row ==i)
	    	   {
		    	   if(status[i].equals("R")) // read(gray)
					{
						cell.setBackground(Color.GRAY);
					}
					else if(status[i].equals("S")) // starred (yellow)
						cell.setBackground(Color.decode("0xf9a51e"));
					else // original tick color
						cell.setBackground(inputColr);
	    	   }
	    	   
	        }
	        if (isSelected) {
	        	jTable1.setValueAt("", curIndx, 0);
	        	jTable1.setValueAt(">>", row, 0);
	        	curIndx = row;
	        	
	       }
	        
          return cell;
       }
       
		
		
    }
    
}

