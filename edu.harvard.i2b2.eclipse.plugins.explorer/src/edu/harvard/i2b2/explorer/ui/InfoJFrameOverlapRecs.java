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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import edu.harvard.i2b2.common.datavo.pdo.ObservationType;
import edu.harvard.i2b2.timeline.excentric.LiteLabel;
import edu.harvard.i2b2.timeline.lifelines.GenRecord;
import edu.harvard.i2b2.timeline.lifelines.MyColor;
import edu.harvard.i2b2.timeline.lifelines.PDOQueryClient;
import edu.harvard.i2b2.timeline.lifelines.TextViewerFrame;

public class InfoJFrameOverlapRecs extends javax.swing.JFrame {
	
	private TimeLinePanel panel_;
//	private GenRecord thisRecord=null; 
	public int tHeight;
	private InfoJFrame infoFrame = null;
	private String obKeys[][] = new String[10][6];
	public String status[] = new String[10];
	private int boundOriX;
	private int boundOriY;
	private String note = null;
	private TextViewerFrame textFrame = null;
	public boolean pin = false;
	private int prevRow;
//	private JLayeredPane layeredPane;
	 

    /**
     * Creates new form InfoJFrame
     * 
     */
	public InfoJFrameOverlapRecs(TimeLinePanel panel, GenRecord[] selectedRecords, int numRetrieved, int curIndx) { //, int x, int y) {
    	panel_ = panel;    	
//    	thisRecord=selectedRecords[curIndx];
    //	boundOriX = x;
    //	boundOriY = y;
    	Color temp = null;
    	
        initComponents(numRetrieved, selectedRecords);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
           
        for(int i=0; i<selectedRecords.length; i++)
        {
	        if (selectedRecords[i] != null) {
	        	
	        	String start_date;
	        	
				String msg = selectedRecords[i].getInputLine();
				String[] msgs = msg.split(",");			
				
				
				String val = " ";
				String infoStr = null;
				String [] xtras = msgs[7].split("\\$\\$");
				String name = xtras[0];
				String conceptName = "";
				String concept_cd = xtras[2];				
				
				
				// Tag ">>" to current record
				if(i == curIndx)
				{
					jTable1.setValueAt(">>", i, 0);
					prevRow = curIndx;							
				}
				else
					jTable1.setValueAt(null, i, 0);
				
				// show status column (original tick color / read(gray) / starred (yellow)
				MyColor tempColor = new MyColor(msgs[3]);
				temp = tempColor.getColor();
				status[i]=selectedRecords[i].mark_status;
				
				
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
					String cstr = panel_.makeReadableCodeString(concept_cd);
					if(!cstr.equalsIgnoreCase("")) {
						infoStr = conceptName+" ("+cstr+")";
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
        
        DefaultTableCellRenderer colAlignRenderer = new DefaultTableCellRenderer();
        colAlignRenderer.setHorizontalAlignment( JLabel.RIGHT );
        jTable1.getColumnModel().getColumn(4).setCellRenderer( colAlignRenderer );

		TableColumn statCol = jTable1.getColumnModel().getColumn(1);

		statCol.setCellRenderer(new ColorColumnRenderer(temp));
        
        int total = jTable1.getColumnModel().getTotalColumnWidth();
        int w0 = 20, w1 = 10, w2, w3 = 70, w4 = 85;
        w2 = total - (w0+w1+w3+w4); 
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(w0);
		 jTable1.getColumnModel().getColumn(1).setPreferredWidth(w1);
		jTable1.getColumnModel().getColumn(2).setPreferredWidth(w2);
		jTable1.getColumnModel().getColumn(3).setPreferredWidth(w3);
		jTable1.getColumnModel().getColumn(4).setPreferredWidth(w4);
		
		
		
		//jTable1.getColumnModel().getColumn(0).setCellRenderer(new RenderTableColor());
    }
    
    
    private void initComponents(int numRow, final GenRecord[] selectedRecords) {
    	getContentPane().setBackground(Color.decode("0xfaf4ce"));
    	getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new JTable(){
        	
        	
        };
        jPinButton = new JButton();
        jCloseButton = new JButton();        

        setLayout(null);

        jPanel1.setLayout(new java.awt.BorderLayout());
        
        FrameDragListener frameDragListener = new FrameDragListener(this);
        addMouseListener(frameDragListener);
        addMouseMotionListener(frameDragListener);
        setLocationRelativeTo(null);

        
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
        		new String [] {
                        "", "", "Concept name (CD)", "Date", "Value"         				
                     },
         		numRow
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
            /*
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    jc.setToolTipText((String) getValueAt(row, column));
                }
                return c;
            }*/
        });
       
        ((JLabel) jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
  
        jScrollPane1.setViewportView(jTable1);        
        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1);
        //jPanel1.setBounds(7, 30, 366, 162);
        tHeight =  jTable1.getRowHeight() * numRow
        		+ jTable1.getIntercellSpacing().height * (numRow +1)
        		+  jTable1.getTableHeader().getPreferredSize().height;// jTable1.HEIGHT; //jTable1.getHeight();
        
        jPanel1.setBounds(7, 25, 366, tHeight);
        
        Font thisFont = new Font("Helvetica", Font.BOLD, 10);    
        // close button
        // change to icon image?
        jCloseButton.setFont(thisFont);
        jCloseButton.setText("X");
        jCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseButtonActionPerformed(evt);                
            }
        });
        add(jCloseButton);
        //jCloseButton.setBounds(381 - 105, 2, 40, 20);
        jCloseButton.setBounds(381 - 45, 2, 40, 20);
        
        // pin button
        // change to icon image?
        jPinButton.setFont(thisFont);
        jPinButton.setText("Pin [Alt+P]");
        jPinButton.setMnemonic(KeyEvent.VK_P);
		
        
        
        jPinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPinButtonActionPerformed(evt);
            }
        });
        add(jPinButton);
        //jPinButton.setBounds(381 - 105, 2, 60, 20);
        jPinButton.setBounds(381 - 145, 2, 100, 20);

    //    this.layeredPane = layeredPane;
    //    layeredPane.add(this, new Integer(0));
        
        
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        
        jTable1.addMouseListener(new MouseAdapter() {
        	public void mouseEntered(MouseEvent e) {
        		 /*
        		javax.swing.JTable table =(javax.swing.JTable) e.getSource();
                Point p = e.getPoint();
                final int selectedRow = table.rowAtPoint(p);   
                final int selectedColmn = table.columnAtPoint(p);    
                
               // ;
        		//String str;
               
        		System.out.println("hkpark] mouse enter : "+(String) table.getValueAt(selectedRow, selectedColmn));
        		LiteLabel infoTipLabel = new LiteLabel((String) table.getValueAt(selectedRow, selectedColmn),
						new Point(getLocationOnScreen().x+10, getLocationOnScreen().y+30),
						1,
						null,
						Color.gray,
						Color.white);
        		Rectangle r = infoTipLabel.getBounds();
    			infoTipLabel = null;
    			//repaint(r.x, r.y, r.width, r.height);
    			repaint(r.x, r.y, r.width, r.height);*/
						
        	}
        	
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

        
        
    } 
    
    public void closeInfoJFrame()
    {
    	if(infoFrame != null) {
			infoFrame.setVisible(false);
			infoFrame.dispose();
			infoFrame = null;
		}
    }

    private void jPinButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
    	if(pin == false)
    	{
    		pin = true;
    	//	layeredPane.add(this, new Integer(400));
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
    
    private void jCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCloseButtonActionPerformed
    	pin = false;
        setVisible(false);
        dispose();
    }
    
  
    
	
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
      
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	new InfoJFrameOverlapRecs(null, null, -1, -1).setVisible(true);
            }
        });
    }
    
    public void setInfo(String str) {
    }
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jCloseButton;
    private javax.swing.JButton jPinButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jPanel1;
    private JTable jTable1;
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
	       
	      // Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	   
   	    if (cell instanceof JLabel && value != null) {
	    //   if (value != null) {
   	        int availableWidth = table.getColumnModel().getColumn(column).getWidth();
   	        availableWidth -= table.getIntercellSpacing().getWidth();
   	        Insets borderInsets = getBorder().getBorderInsets(cell);
   	        availableWidth -= (borderInsets.left + borderInsets.right);
   	        FontMetrics fm = getFontMetrics( getFont() );
   	        String cellText = value.toString();
   	 
   	        if (fm.stringWidth(cellText) > availableWidth) ((javax.swing.JLabel) cell).setToolTipText(value.toString());
   	        else ((javax.swing.JLabel) cell).setToolTipText(null);
   	    // cell.setToolTipText(value.toString());
   	    }
   	 
   	    //return c;
	      
	      //  cell.setBackground( bkgndColor );
	       // cell.setForeground( fgndColor );
	       for(int i=0; i<status.length; i++)
	        {
	    	   if(row ==i)
	    	   {
		    	   if(status[i].equals("N")) // original tick color
					{
		    		   cell.setBackground(inputColr);					
					}
					else if(status[i].equals("R")) // read(gray)
					{
						cell.setBackground(Color.GRAY);
					}
					else if(status[i].equals("S")) // starred (yellow)
						cell.setBackground(Color.decode("0xf9a51e"));
	    	   }
	    	   
	        }
	       /*
	       if (row == rowIndx) {
 				cell.setForeground(inputColr);
 			}
 			*/
	         
	        if (isSelected) {
	        	jTable1.setValueAt("", prevRow, 0);
	        	jTable1.setValueAt(">>", row, 0);
	        	prevRow = row;
	        	
	       }
	         
          return cell;
       }
       
		
		
    }
    
    
    
    /*
    class CustomRenderer extends DefaultTableCellRenderer 
    {
		public Component getTableCellRendererComponent(	JTable table, Object value,
								boolean isSelected, boolean hasFocus,
								int row, int column)
		{
		    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
		    if(!hasFocus)
		    {
		      c.setBackground(new java.awt.Color(255, 72, 72));
		    }
		    else
		    {
		      c.setBackground(new java.awt.Color(20, 255, 20));
		    }
	
		    return c;
		}
    }
    
    */
    
}

