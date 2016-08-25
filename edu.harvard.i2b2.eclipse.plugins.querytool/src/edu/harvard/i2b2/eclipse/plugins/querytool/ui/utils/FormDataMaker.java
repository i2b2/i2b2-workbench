/*
 * Copyright (c) 2006-2016 Partners HealthCare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

public class FormDataMaker 
{

	
	public static FormData makeFullFormData()
	{  return makeFormData(0, 100, 0, 100); }
	
	public static FormData makeBorderingFormData()
	{  return makeFormData(0, 1, 100, -1, 0, 1, 100, -1); }

	
	// the order of inputs is always TOP, BOTTOM, LEFT, RIGHT
	//  Parameters for top/bottom/left/right can be null
	
	public static FormData makeFormData( Control a, Control b, Control c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }
	
	public static FormData makeFormData( Integer a, Integer b, Integer c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Integer a, Integer b, Integer c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Control b, Control c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Control b, Integer c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Control b, Integer c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Integer b, Control c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Integer b, Control c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Integer b, Integer c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Control a, Integer b, Integer c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Integer a, Control b, Control c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Integer a, Control b, Control c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Integer a, Control b, Integer c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Integer a, Control b, Integer c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }

	public static FormData makeFormData( Integer a, Integer b, Control c, Control d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }
	
	public static FormData makeFormData( Integer a, Integer b, Control c, Integer d )
	{ return makeFormData(  a, 0, b, 0, c, 0, d, 0 ); }



	
	public static FormData makeFormData( Control a, int aOffset, Control b, int bOffset, Control c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}
	
	public static FormData makeFormData( Integer a, int aOffset, Integer b, int bOffset, Integer c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Control b, int bOffset, Control c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Control b, int bOffset, Integer c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Control b, int bOffset, Integer c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Integer b, int bOffset, Control c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Integer b, int bOffset, Control c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Integer b, int bOffset, Integer c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Control a, int aOffset, Integer b, int bOffset, Integer c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Integer a, int aOffset, Control b, int bOffset, Control c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}
	
	public static FormData makeFormData( Integer a, int aOffset, Control b, int bOffset, Control c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Integer a, int aOffset, Control b, int bOffset, Integer c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Integer a, int aOffset, Control b, int bOffset, Integer c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Integer a, int aOffset, Integer b, int bOffset, Control c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Integer a, int aOffset, Integer b, int bOffset, Control c, int cOffset, Integer d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}

	public static FormData makeFormData( Integer a, int aOffset, Integer b, int bOffset, Integer c, int cOffset, Control d, int dOffset )
	{
		return makeFormData(new FormDataParam(a, aOffset),
							new FormDataParam(b, bOffset),
							new FormDataParam(c, cOffset),
							new FormDataParam(d, dOffset));
	}


	
	public static FormData makeFormData( FormDataParam top, FormDataParam bottom, FormDataParam left, FormDataParam right )
	{
		FormData fd = new FormData();
		FormDataParam [] params = new FormDataParam[] { top, bottom, left, right };
		for ( int i = 0; i < 4 ; i++ )
		{
			FormAttachment fa = null;
			if ( params[i] != null && !params[i].isNull() )
			{
				if ( params[i].isControlConstraint() )
					fa = new FormAttachment( (Control)params[i].getPlacementConstraint(), params[i].getOffset() );
				else
					fa = new FormAttachment( (Integer)params[i].getPlacementConstraint(), params[i].getOffset() );
			}
			else
				continue;
			
			switch(i)
			{
				case 0:
					fd.top 		= fa;
					break;
				case 1:
					fd.bottom	= fa;
					break;
				case 2:
					fd.left		= fa;
					break;
				case 3:
					fd.right	= fa;
					break;
			}
		}
		return fd;
	}

}
