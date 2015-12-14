/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
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
 * 
 */
package edu.harvard.i2b2.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.part.IntroPart;

import edu.harvard.i2b2.eclipse.util.Messages;

/**
 * @author wp066
 *
 */
public class I2B2IntroPart extends IntroPart {
	
	private FormToolkit formToolkit = null;   
	private Form form = null;
	private ImageHyperlink imageHyperlink = null;
	//private ImageHyperlink imageHyperlink_pft = null;
	private ImageHyperlink workbenchlink_pft = null;
	private Text text = null;
	
	/**
	 * This method initializes formToolkit	
	 * 	
	 * @return org.eclipse.ui.forms.widgets.FormToolkit	
	 */
	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.intro.IIntroPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Display display = parent.getDisplay();
		final Font font = new Font(display, "Tahoma", 18, SWT.BOLD); //$NON-NLS-1$
		final Font headerfont = new Font(display, "Tahoma", 24, SWT.NORMAL); //$NON-NLS-1$
		
		form = getFormToolkit().createForm(parent);
		//form.setText("Welcome to the i2b2 Workbench");
		form.setBounds(new Rectangle(0, 0, 484, 264));
		
		text = new Text(form.getBody(), SWT.NONE);
		text.setText(Messages.getString("I2B2IntroPart.WelcomeMessage")); //$NON-NLS-1$
		text.setBounds(50, 70, 500, 35);
		text.setFont(headerfont);
		
		imageHyperlink = getFormToolkit().createImageHyperlink(form.getBody(), SWT.CENTER);
		imageHyperlink.setText(Messages.getString("I2B2IntroPart.Tutorial")); //$NON-NLS-1$
		imageHyperlink.setBounds(new Rectangle(170, 150, 400, 29));
		imageHyperlink.setHref("/edu.harvard.i2b2.eclipse/html/tutorial/page1.html"); //$NON-NLS-1$
		imageHyperlink.setImage(new Image(Display.getCurrent(), I2B2IntroPart.class.getResourceAsStream("big-hive.gif")));		 //$NON-NLS-1$
		imageHyperlink.setFont(font);
		imageHyperlink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				//org.eclipse.swt.program.Program.launch((String) e.getHref());//"http://www.i2b2.org");
				PlatformUI.getWorkbench().getHelpSystem().displayHelpResource((String) e.getHref());
			}

			public void linkEntered(HyperlinkEvent e) {
				
			}

			public void linkExited(HyperlinkEvent e) {
								
			}
			
		});
		
		/*imageHyperlink_pft = getFormToolkit().createImageHyperlink(form.getBody(), SWT.CENTER);
		imageHyperlink_pft.setText("PFT Assistance");
		imageHyperlink_pft.setBounds(new Rectangle(170, 200, 300, 29));
		imageHyperlink_pft.setHref("/edu.harvard.i2b2.eclipse.plugins.pft/html/tasks/extractpftresults.html");
		imageHyperlink_pft.setImage(new Image(Display.getCurrent(), I2B2IntroPart.class.getResourceAsStream("non-core-cell.gif")));		
		imageHyperlink_pft.setFont(font);
		imageHyperlink_pft.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				//org.eclipse.swt.program.Program.launch((String) e.getHref());//"http://www.i2b2.org");
				PlatformUI.getWorkbench().getHelpSystem().displayHelpResource((String) e.getHref());
			}

			public void linkEntered(HyperlinkEvent e) {
				
			}

			public void linkExited(HyperlinkEvent e) {
				
			}
			
		});*/
		
		workbenchlink_pft = getFormToolkit().createImageHyperlink(form.getBody(), SWT.CENTER);
		workbenchlink_pft.setText(Messages.getString("I2B2IntroPart.GotoWorkbench")); //$NON-NLS-1$
		workbenchlink_pft.setBounds(new Rectangle(172, 210, 300, 29));
		workbenchlink_pft.setHref("/edu.harvard.i2b2.eclipse.plugins.pft/html/tasks/extractpftresults.html"); //$NON-NLS-1$
		workbenchlink_pft.setImage(new Image(Display.getCurrent(), I2B2IntroPart.class.getResourceAsStream("go.gif")));		 //$NON-NLS-1$
		workbenchlink_pft.setFont(font);
		workbenchlink_pft.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				IIntroManager manager = PlatformUI.getWorkbench().getIntroManager();
				final IntroPart part = (IntroPart) manager.getIntro();
				
				if(part != null) { 
					manager.closeIntro(part);
				}
			}

			public void linkEntered(HyperlinkEvent e) {
				
			}

			public void linkExited(HyperlinkEvent e) {
				
			}
			
		});
		
		form.getBody().setLayout(null);

	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.intro.IIntroPart#getTitle()
	 */
	@Override
	public String getTitle() {
		return Messages.getString("I2B2IntroPart.Title"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.intro.IIntroPart#getTitleImage()
	 */
	@Override
	public Image getTitleImage() {
		
		return new Image(Display.getCurrent(), I2B2IntroPart.class.getResourceAsStream("hive.gif")); //$NON-NLS-1$
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.intro.IIntroPart#setFocus()
	 */
	@Override
	public void setFocus() {
		form.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.intro.IIntroPart#standbyStateChanged(boolean)
	 */
	public void standbyStateChanged(boolean standby) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
