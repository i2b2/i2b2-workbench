/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 *     
 */
package edu.harvard.i2b2.smlib;/******************************************************************************
 * Copyright (c) 1998, 2004 Jackwind Li Guojie
 * 
 * Created on 2004-3-31 1:19:00 by JACK
 * $Id: SimpleProgressC.java,v 1.7 2010/07/06 14:04:23 mem61 Exp $
 * 
 *****************************************************************************/


import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class SimpleProgressC {
  private Thread simpleProgressThread;
  private ProgressBar progressBar;
  public SimpleProgressC(Composite poComposite, int piMaxSeconds) {
    final Composite oComposite = poComposite;
    final int iMax = piMaxSeconds;  // because it needs to be final 
    progressBar = new ProgressBar(oComposite, SWT.SMOOTH);
    progressBar.setBackground(oComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    progressBar.setMinimum(0);
    progressBar.setMaximum(iMax);
    simpleProgressThread = new Thread(){
      @Override
	public void run() {
        for(int i=0; i<=iMax; i++) {
          final int num = i;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          oComposite.getDisplay().asyncExec(new Runnable(){
            public void run() {
              if(progressBar.isDisposed())
                return;
              progressBar.setSelection(num);
              //progressBar.redraw();
            }
          });
        }
      }
    };
    progressBar.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        //System.out.println("PAINT");
        // string to draw. 
        String string = Math.round(progressBar.getSelection() * 1.0 /(progressBar.getMaximum()-progressBar.getMinimum()) * 100) + "%";
        Point point = progressBar.getSize();
        Font font = new Font(oComposite.getDisplay(),"Courier",10,SWT.BOLD);
        e.gc.setFont(font);
        e.gc.setForeground(oComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        FontMetrics fontMetrics = e.gc.getFontMetrics();
        int stringWidth = fontMetrics.getAverageCharWidth() * string.length();
        int stringHeight = fontMetrics.getHeight();
        e.gc.drawString(string, (point.x-stringWidth)/2 , (point.y-stringHeight)/2, true);
        font.dispose();
      }
    });
    progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  public void Start() {
	  simpleProgressThread.start();
  }
  public void StopAndDestroy() {
	  //simpleProgressThread.stop();
	  //simpleProgressThread.destroy();
  }
  public void GetTo(int num) {
      progressBar.setSelection(num);
  }

  public static void main(String[] args) {
	    Display display = new Display();
	    Shell shell = new Shell(display);
	    shell.setLayout(new FillLayout(SWT.HORIZONTAL));
	    shell.setText("CountNumbers Test");
	    shell.setSize(300,100);
	    SimpleProgressC countNumbers = new SimpleProgressC(shell, 100);
	    countNumbers.Start();
		//shell.pack();
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    display.dispose();
  }
}
