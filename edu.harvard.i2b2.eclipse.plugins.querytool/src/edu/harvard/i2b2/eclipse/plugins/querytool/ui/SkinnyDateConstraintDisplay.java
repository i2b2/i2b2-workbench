package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IDateStruct;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public class SkinnyDateConstraintDisplay extends DateConstraintDisplay 
{

	
	public SkinnyDateConstraintDisplay(Composite parent, IDateStruct data,int style) 
	{
		super(parent, data, style);
	}
	
	@Override // SkinnyDateConstraintDisplay ones display differently
	protected void updateDisplay()
	{
		myDateDisplay.setForeground( Colors.BLACK );
		if ( myData.getStartDate() == null && myData.getEndDate() == null )
		{
			myDateDisplay.setText( NO_CONSTRAINTS );
			myDateDisplay.setToolTipText( CLICK_TO_CHANGE );
		}
		else if (  myData.getStartDate() != null  &&  myData.getEndDate() == null)
		{
			String startString =  DateParser.toFormat( myData.getStartDate() );
			myDateDisplay.setText( startString + " and after" );
			myDateDisplay.setToolTipText( startString + " " + INCLUSIVE + " " +STARTING_AND_AFTER + "." );
		}
		else if (  myData.getStartDate() == null &&  myData.getEndDate() != null)
		{
			String endString =  DateParser.toFormat( myData.getEndDate() );
			myDateDisplay.setText( endString + " and before");
			myDateDisplay.setToolTipText( endString + " " + INCLUSIVE + NEW_LINE + " and anytime before." );
		}
		else // ( myStartDate != null && myEndDate != null )
		{
			String startString =  DateParser.toFormat( myData.getStartDate() );
			String endString =  DateParser.toFormat( myData.getEndDate() );
			myDateDisplay.setText( startString + "-" + endString );
			myDateDisplay.setToolTipText( startString + " " + INCLUSIVE + NEW_LINE + TO + NEW_LINE + endString +  " " + INCLUSIVE);
		}
		// relayout to be centered
		myDateDisplay.setLayoutData( FormDataMaker.makeFormData( 50, -myDateDisplay.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, 100, 0));
	}

	
}
