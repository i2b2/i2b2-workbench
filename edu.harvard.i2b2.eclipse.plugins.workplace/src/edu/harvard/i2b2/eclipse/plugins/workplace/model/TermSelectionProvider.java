/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class TermSelectionProvider implements ISelectionProvider{
		
	
	private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
 	
	private static TermSelectionProvider thisInstance;
	static {
		thisInstance = new TermSelectionProvider();
	}

	public static TermSelectionProvider getInstance() {
		return thisInstance;
	}
	

		public void fireSelectionChanged(ISelection selection){
			SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
			
			Iterator<ISelectionChangedListener> it = listeners.iterator();
			while (it.hasNext()){
					ISelectionChangedListener listener = (ISelectionChangedListener) it.next();
					listener.selectionChanged(event);
			}
		}
		

		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			// TODO Auto-generated method stub
			
			listeners.add(listener);
			
		}

		public ISelection getSelection() {
			// TODO Auto-generated method stub
			return null;
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			// TODO Auto-generated method stub
			listeners.remove(listener);
			
		}

		public void setSelection(ISelection selection) {
			// TODO Auto-generated method stub
			
		}
		
		
	}

