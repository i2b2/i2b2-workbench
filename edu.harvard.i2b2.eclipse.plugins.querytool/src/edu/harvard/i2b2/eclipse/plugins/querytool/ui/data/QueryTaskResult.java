package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

public class QueryTaskResult extends TaskResult 
{
	public enum StatusCode { SUCCESS, TIMEOUT, ERROR }
	
	private Integer 	myNumPatients 	= null;
	private Boolean 	isObfuscated	= null;
	private StatusCode	myStatusCode	= null;
	
	// constructing the 'success' query result
	public QueryTaskResult( int numPatientsReturned, boolean obfuscated )
	{
		super();
		myStatusCode	= StatusCode.SUCCESS;
		myNumPatients 	= numPatientsReturned;
		isObfuscated	= obfuscated;
	}

	// construction a query result with errors
	public QueryTaskResult( Exception exception, String message, String reason, StatusCode status )
	{
		super( exception, message, reason );
		this.myStatusCode = status;
	}

	public int getNumPatients()
	{ return this.myNumPatients; }

	public boolean isObfuscated()
	{ return this.isObfuscated; }
	
	public StatusCode getStatusCode()
	{ return this.myStatusCode; }

	public String getFormattedResultCount()
	{
		if ( isObfuscated )
		{
			if ( this.myNumPatients < 3 )
				return "<3"; // add plus/minus 3
			else
				return this.myNumPatients + "\u00B13"; // add plus/minus 3				
		}
		else
			return this.myNumPatients + "";
	}
}
