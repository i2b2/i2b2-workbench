/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.workplace.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.action.StatusLineManager;

public class TreeComposite extends Composite
{
  private NodeBrowser browser;
  private StatusLineManager slm;
  
  public TreeComposite(Composite parent, int inputFlag, StatusLineManager slm)
  {
    super(parent, SWT.NONE);  // used to be SWT.NULL
    this.slm = slm;
    populateControl(inputFlag);
  }

  protected void populateControl(int inputFlag)
  {
    GridLayout compositeLayout = new GridLayout(1, false);
    setLayout(compositeLayout);

    this.browser = new NodeBrowser(this, inputFlag, this.slm);
  }
} 

