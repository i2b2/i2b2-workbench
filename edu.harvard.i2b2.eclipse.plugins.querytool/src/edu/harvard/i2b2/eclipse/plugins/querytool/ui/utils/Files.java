package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

public class Files 
{
	public static final String TEMPORAL_QUERY_TOOL = "TemporalQueryTool";
	
	public static String getDefaultFileDirectory()
	{
		return System.getProperty("user.dir") + System.getProperty("file.separator") + "files";
	}
	
	public static String getPluginName()
	{ return TEMPORAL_QUERY_TOOL; }
	
}
