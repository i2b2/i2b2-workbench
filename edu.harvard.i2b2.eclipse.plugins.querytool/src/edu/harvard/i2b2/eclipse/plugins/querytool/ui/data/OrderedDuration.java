package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;


public class OrderedDuration 
{

	public static enum Operator { GT, GTE, E, LTE, LT };
	
	private Operator 	myOperator;
	private	TemporalRelationship.TimeUnit 	myUnit;
	private int			myNumber;


	public OrderedDuration()
	{
		myOperator 	= Operator.GT;
		myUnit		= TemporalRelationship.TimeUnit.DAYS;
		myNumber	= 1;
	}


	public Operator 						getOperator()	{ return myOperator; }
	public TemporalRelationship.TimeUnit 	getUnit()		{ return myUnit; }
	public int								getNumber()		{ return myNumber; }


	public void setDuration(Operator op, TemporalRelationship.TimeUnit unit, int number )
	{
		myOperator 	= op;
		myUnit		= unit;
		myNumber	= number;
	}

	public String toString()
	{
		return myOperator + " " + myNumber + " " + myUnit;
	}
}
