/*
 * Copyright (c) 2006-2017 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Heekyong Park (hpark25) (MGH)
 *     
 */

package edu.harvard.i2b2.timeline.lifelines;


import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class SearchTextFrame extends JFrame {
	private String find = "";
	private TextViewerFrame noteFrame;
	private JTextArea jTextArea2;
	int caretPosition = 0;
	int cntFound = -1;
	int numFound =0;
	String foundMsg=" ";
	int opt = 0; // search option
		// case insensitive (default): 0
		// case sensitive: 1
		// case sensitive + whole word: 2
		// case insensitive + whole word: 3
		// regular expression: 4 				
	
	public  SearchTextFrame(TextViewerFrame ntFrame, JTextArea jTxtArea2) {			
			noteFrame = ntFrame;
			jTextArea2=jTxtArea2;
			
		
	        JLabel label = new JLabel("Type in the string for searching: ");
	        final JLabel numFoundMsg = new JLabel("");
	        final JTextField keywordInput = new JTextField();
	        final JCheckBox case_CheckBox = new JCheckBox("Case sensitive");
	        final JCheckBox whole_CheckBox = new JCheckBox("Whole Word");
	        final JCheckBox regExpr_CheckBox = new JCheckBox("Regular expressions");
	        JButton findButton = new JButton("Search");
	        JButton prevButton = new JButton("<< Previous");
	        JButton nextButton = new JButton("Next >>");
	        JButton cancelButton = new JButton("Cancel");

	        case_CheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	        whole_CheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	        regExpr_CheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

	        GroupLayout layout = new GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        getContentPane().setPreferredSize(new Dimension(420, 130));
	        layout.setAutoCreateGaps(true);
	        layout.setAutoCreateContainerGaps(true);

	        layout.setHorizontalGroup(layout.createSequentialGroup()
	        	.addGroup(layout.createParallelGroup(LEADING)
	        		.addComponent(label)
	        		.addGroup(layout.createSequentialGroup()
	        				.addPreferredGap(label, keywordInput, ComponentPlacement.INDENT)
	        				.addComponent(keywordInput)
		            )
		            .addGroup(layout.createParallelGroup(TRAILING) 
		            	.addComponent(numFoundMsg)
		                .addGroup(layout.createSequentialGroup()
		                        .addComponent(case_CheckBox)
		                        .addComponent(whole_CheckBox)
		                        .addComponent(regExpr_CheckBox))		                
		                .addGroup(layout.createSequentialGroup()
		                		.addPreferredGap(label, findButton, ComponentPlacement.INDENT)
		        				.addComponent(findButton)
		    	                .addComponent(prevButton)
		    	                .addComponent(nextButton)
		    	                .addComponent(cancelButton))
		            )
	             )
	        );
	        
	        layout.linkSize(SwingConstants.HORIZONTAL, findButton, prevButton, nextButton, cancelButton);

	        layout.setVerticalGroup(layout.createSequentialGroup()
	                .addComponent(label)                
	                .addComponent(keywordInput)	
	                .addComponent(numFoundMsg)
	                
	            .addGroup(layout.createParallelGroup(LEADING)
	            		.addComponent(numFoundMsg)
	                .addGroup(layout.createSequentialGroup()
	                    .addGroup(layout.createParallelGroup(BASELINE)
	                        .addComponent(case_CheckBox)
	                        .addComponent(whole_CheckBox)
	                        .addComponent(regExpr_CheckBox))))
	            .addGroup(layout.createParallelGroup(LEADING)
	                    	.addComponent(findButton)
	                    	.addComponent(prevButton)
	                    	.addComponent(nextButton)
	                    	.addComponent(cancelButton))	            
	            );
	        
	        setTitle("Search");

	    	        
	        keywordInput.addKeyListener(new KeyAdapter() {        		
	        	public void keyReleased (KeyEvent e) { 
	        		int keyCde = e.getKeyCode();
	        		
	                if((keyCde == KeyEvent.VK_ESCAPE))
	                {
	                	noteFrame.removeHighlights(jTextArea2);
	  	        	  	setVisible(false);
	                } 
	                else
	                {
		                find = keywordInput.getText();
		                if(find.equals(""))
		                	noteFrame.removeHighlights(jTextArea2);
		                caretPosition=0;
		                opt=setSearchOption(case_CheckBox.isSelected(), whole_CheckBox.isSelected(), regExpr_CheckBox.isSelected());
		        		jFindActionPerformed(find, opt);		        		
		        		numFound = jTextArea2.getHighlighter().getHighlights().length;
		        		//System.out.println("hkpark) "+numFound+" words are found.");
		        		if(numFound==0 && find.equals(""))
		        			foundMsg=" ";
		        		else
		        			foundMsg=numFound+" words are found.";
		        		numFoundMsg.setText(foundMsg);
		        		numFoundMsg.setForeground(Color.decode("0x092746"));
		    			
		        		
	                }
	              };
	        });
	        
	        regExpr_CheckBox.addItemListener(new ItemListener(){                
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED){
                    	case_CheckBox.setEnabled(false);
            	        whole_CheckBox.setEnabled(false);
                    }
                    else if(e.getStateChange() == ItemEvent.DESELECTED){
                    	case_CheckBox.setEnabled(true);
            	        whole_CheckBox.setEnabled(true);
                    }
                }
            });
	        
	        case_CheckBox.addItemListener(new ItemListener(){                
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED){
                    	regExpr_CheckBox.setEnabled(false);
                    }
                    else if(e.getStateChange() == ItemEvent.DESELECTED && !whole_CheckBox.isSelected()){
                    	regExpr_CheckBox.setEnabled(true);
                    }
                }
            });
	        
	        whole_CheckBox.addItemListener(new ItemListener(){                
                public void itemStateChanged(ItemEvent e) {
                	if(e.getStateChange() == ItemEvent.SELECTED){
                    	regExpr_CheckBox.setEnabled(false);
                    }
                    else if(e.getStateChange() == ItemEvent.DESELECTED && !case_CheckBox.isSelected()){
                    	regExpr_CheckBox.setEnabled(true);
                    }
                }
            });
	     
	        findButton.addActionListener(new ActionListener()
	        {
	        	public void actionPerformed(ActionEvent e) {
	        		find = keywordInput.getText();
	        		opt=setSearchOption(case_CheckBox.isSelected(), whole_CheckBox.isSelected(), regExpr_CheckBox.isSelected());
	        		jFindActionPerformed(find, opt);
	        		numFound = jTextArea2.getHighlighter().getHighlights().length;
	        		System.out.println(numFound+" words are found.");
	        		if(numFound==0 && find.equals(""))
	        			foundMsg=" ";
	        		else
	        			foundMsg=numFound+" words are found.";
	        		numFoundMsg.setText(foundMsg);
	        		numFoundMsg.setForeground(Color.decode("0x092746"));
				}
	        });
	        
	        prevButton.addActionListener(new ActionListener()
	        {
		          public void actionPerformed(ActionEvent e)
		          {
		            cntFound = noteFrame.highlightPrev(jTextArea2, cntFound);

		            if(cntFound == 0)
		            	foundMsg= "1st of "+numFound+" words";
		            else if(cntFound == 1)
		            	foundMsg= "2nd of "+numFound+" words";
		            else 
		            	foundMsg= (cntFound+1) +"th of "+numFound+" words";
		            
		            numFoundMsg.setText(foundMsg);
		            numFoundMsg.setForeground(Color.decode("0x092746"));
		          }
		      });
	        
	        nextButton.addActionListener(new ActionListener()
	        {
	          public void actionPerformed(ActionEvent e)
	          {
	            cntFound = noteFrame.highlightNext(jTextArea2, cntFound);
	            
	            if(cntFound == 0)
	            	foundMsg= "1st of "+numFound+" words";
	            else if(cntFound == 1)
	            	foundMsg= "2nd of "+numFound+" words";
	            else 
	            	foundMsg= (cntFound+1) +"th of "+numFound+" words";
	            
	            numFoundMsg.setText(foundMsg);
	            numFoundMsg.setForeground(Color.decode("0x092746"));
	          }
	        });
	        
	        cancelButton.addActionListener(new ActionListener()
	        {
	          public void actionPerformed(ActionEvent e)
	          {
	        	  noteFrame.removeHighlights(jTextArea2);
	        	  setVisible(false);
	          }
	        });

	        pack();    
	    }
	

	// search option
	// case insensitive (default): 0
	// case sensitive: 1
	// case sensitive + whole word: 2
	// case insensitive + whole word: 3
	// regular expression: 4
	private int setSearchOption(boolean caseSen, boolean whole, boolean regEx)
	{
		int searchOpt = 0;
		if(caseSen == true)
		{
			if(whole == true && regEx == false)
				searchOpt = 2; // case sensitive + whole word: 2
			else
				searchOpt = 1; // case sensitive: 1
			
		}
		else 
		{
			if(whole == true && regEx == false)
				searchOpt = 3; // case insensitive + whole word: 3
			else if(whole == false && regEx == true) // regular expression: 4
				searchOpt = 4;			
			else
				searchOpt = 0; // case insensitive (default)
		}
		return searchOpt;
	}
	
	private void jFindActionPerformed(String keyword, int searchOpt)
    {
      caretPosition=0;
      cntFound=-1;
      findTerm(keyword, searchOpt);
    }
	
	public int findTerm(String keyword, int searchOpt)
	{ 		
		int newCaretPosition=0;
		 if (keyword != null && !keyword.equals("")) {
	    			 newCaretPosition = noteFrame.highlight(jTextArea2, keyword, searchOpt);
	    			if (newCaretPosition != -1)
	    			{
	    				try
	    				{
		    				Rectangle viewRect = jTextArea2.modelToView(newCaretPosition);
		    				jTextArea2.scrollRectToVisible(viewRect);
		    				jTextArea2.setCaretPosition(newCaretPosition);
	    				} catch (Exception exp) {
	                        exp.printStackTrace();
	                    }
	    				
	    			}
	    		}
		 return newCaretPosition;
	}

}
