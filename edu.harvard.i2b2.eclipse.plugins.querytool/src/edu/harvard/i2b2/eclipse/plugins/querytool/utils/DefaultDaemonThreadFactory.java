package edu.harvard.i2b2.eclipse.plugins.querytool.utils;

import java.util.concurrent.ThreadFactory;

public class DefaultDaemonThreadFactory implements ThreadFactory
{
	public static int numThreads = 0;

	public String myFactoryName = null;

	public DefaultDaemonThreadFactory( String factoryName )
	{ myFactoryName = factoryName; }

	public Thread newThread(Runnable runnable) 
	{
		Thread aThread = new Thread( runnable );
		aThread.setName("[" + myFactoryName + "]" + numThreads );
		aThread.setDaemon( true );
		numThreads++;
		return aThread;
	}	
}
