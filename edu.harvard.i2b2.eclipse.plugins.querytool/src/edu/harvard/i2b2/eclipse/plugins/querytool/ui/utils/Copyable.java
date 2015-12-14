package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

/* Interface that forces classes to be Copyable (returning a deep copy of themselves)*/
public interface Copyable <T> 
{
	
	/* Make a deep copy of self and return it */
	public T makeCopy();
	
}
