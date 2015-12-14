package edu.harvard.i2b2.query.serviceClient;

import javax.xml.bind.JAXBElement;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterInstanceResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Query;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryTaskResult;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryTaskResult.StatusCode;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.QueryToolViewAccessor;
import edu.harvard.i2b2.query.data.QueryFactory;
import edu.harvard.i2b2.query.data.QueryInstanceData;
import edu.harvard.i2b2.query.data.QueryMasterData;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;

public class QueryResultHandler 
{
	public static final String	QUERY_TIMEOUT_MSG 		= "Your query timed out, but it is still running on the server. You may check its status later in the Previous Queries View by right clicking on a node and then selecting 'refresh all'. \n\nClosing the workbench will not terminate its progress.";
	public static final String	QUERY_TIMEOUT_REASON 	= "Your query took longer than the allotted time to process";
	
	public static final	String	DONE 		= "DONE";
	public static final String	ERROR		= "ERROR";
	public static final String	INCOMPLETE 	= "INCOMPLETE";
	public static final String	PROCESSING 	= "PROCESSING";
	public static final String	RUNNING 	= "RUNNING";
	
	private static QueryResultHandler myInstance	= null;

	public static QueryResultHandler getInstance()
	{
		if ( myInstance == null )
			myInstance = new QueryResultHandler();
		return myInstance;
	}
	
	private QueryResultHandler()
	{}
	
	
	
	class PluginUIUpdater implements Runnable
	{
		private Query 			myQuery;
		private QueryMasterData myMasterData;
		private String			myCountStr;
		private String			myRefID;
		private String 			myueryResultInstanceID;
		
		public PluginUIUpdater( Query q, QueryMasterData qmd, String qriID, String counterStr, String refID )
		{
			myQuery					= q;			
			myMasterData			= qmd;
			myueryResultInstanceID	= qriID ;
			myCountStr				= counterStr;
			myRefID					= refID;
		}

		@Override
		public void run() 
		{
			UIUtils.refreshPreviousQuery( myMasterData );
			// tell Analysis plugin to create graphics
			if ( myQuery.isUsingGraphicalAnalysis() )
				UIUtils.sendQueryResultsToAnalysis( myMasterData, myueryResultInstanceID );
			if ( myQuery.isUsingTimeline() )
				UIUtils.sendQueryResultsToTimeLine( myMasterData, myCountStr, myRefID );				
		}
		
	}
	
	
	//bugbug: need to handle query.getResult if it contains exception or messages: needs to pop up dialogs to inform users
	public QueryTaskResult handleQueryResult( final Query query )
	{
		String response = query.getResponse();
		
		String 	countDisplay 	= null;
		boolean obfsc 			= false;
		int		numPatients		= 0;
		
		// handle response
		if (response != null)
		{
			JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
			try 
			{
				@SuppressWarnings("rawtypes")
				JAXBElement jaxbElement = jaxbUtil.unMashallFromString(response);
				ResponseMessageType messageType = (ResponseMessageType) jaxbElement.getValue();
				BodyType bodyType = messageType.getMessageBody();
				MasterInstanceResultResponseType masterInstanceResultResponseType = (MasterInstanceResultResponseType) new JAXBUnWrapHelper().getObjectByClass(bodyType.getAny(),MasterInstanceResultResponseType.class );
				if(masterInstanceResultResponseType.getStatus().getCondition().get(0).getType().equalsIgnoreCase( ERROR ))
					return new QueryTaskResult( null, "An error occurred while submitting your query. You may wish to retry your last action.", "Remote server returned an error", StatusCode.ERROR);

				String queryId = null;
				StatusType statusType = messageType.getResponseHeader().getResultStatus().getStatus();
				String status = statusType.getType();
				String count = "N/A";
				QueryMasterData nameNode = null;
				//QueryInstanceData instanceNode = null;
				String queryResultInstanceID = null;
				
				if (status.equalsIgnoreCase( DONE )) 
				{
					String refId = null;
					try 
					{
						String queryStatus = masterInstanceResultResponseType.getQueryInstance().getQueryStatusType().getName();
						
						if (queryStatus.equalsIgnoreCase( INCOMPLETE ) || queryStatus.equalsIgnoreCase( PROCESSING ) || queryStatus.equalsIgnoreCase( RUNNING ) )				
							return new QueryTaskResult( null, QUERY_TIMEOUT_MSG, QUERY_TIMEOUT_REASON + " (" + System.getProperty(Settings.QT_MAX_WAITING_TIME_KEY) + " second(s)).", StatusCode.TIMEOUT );
						if (queryStatus.equalsIgnoreCase( ERROR )) 
							return new QueryTaskResult( null, "An error occurred while submitting your query. You may wish to retry your last action.", "Remote server returned an error", StatusCode.ERROR);
						
						queryId = new Integer( masterInstanceResultResponseType.getQueryMaster().getQueryMasterId()).toString();

						nameNode = new QueryMasterData();    
						nameNode.name( query.getName() );
						nameNode.visualAttribute("CA");
						nameNode.userId(UserInfoBean.getInstance().getUserName());
						nameNode.tooltip("A query run by " + nameNode.userId());
						nameNode.id(queryId);

						// /loop thru all the results
						for (int i = 0; i < masterInstanceResultResponseType.getQueryResultInstance().size(); i++) 
						{
							QueryResultInstanceType queryResultInstanceType = masterInstanceResultResponseType.getQueryResultInstance().get(i);
							queryResultInstanceID = queryResultInstanceType.getQueryInstanceId();
							//instanceNode = new QueryInstanceData();
							//instanceNode.id(queryResultInstanceType.getQueryInstanceId());
							//System.err.println("instanceID = " + instanceNode.id() );
							
							numPatients = new Integer(queryResultInstanceType.getSetSize());
							count = new Integer(queryResultInstanceType.getSetSize()).toString();
							if ((queryResultInstanceType.getObfuscateMethod() != null) && (queryResultInstanceType.getObfuscateMethod().equalsIgnoreCase("OBTOTAL") || 
									queryResultInstanceType.getObfuscateMethod().equalsIgnoreCase("OBSUBTOTAL"))) 
								obfsc = true;
							if (queryResultInstanceType.getQueryResultType().getName().equalsIgnoreCase("patientset")) 
								refId = new Integer(queryResultInstanceType.getResultInstanceId()).toString();

						}
						
						if (count.equalsIgnoreCase("N/A"))
							countDisplay = " Patient(s) returned: " + count;
						else 
						{
							if ( obfsc )
								countDisplay = " Patient(s) returned: " + "~" + count;
							else
								countDisplay = " Patient(s) returned: " + count;
						}
						System.err.println("-->QueryResultHandler: query result: " + countDisplay );
						
					} 
					catch (Exception exc) 
					{
						exc.printStackTrace();
						return new QueryTaskResult( exc, "An error occurred while submitting your query. You may wish to retry your last action.", "Remote server returned an error", StatusCode.ERROR );
					}
					
					
					// Query has completed in allotted time without errors. Now we do post-processing wrt other plugins
					//PluginUIUpdater updater = new PluginUIUpdater( query, nameNode, queryResultInstanceID, count, refId );
					//Display.getDefault().asyncExec( updater );
					
					UIUtils.refreshPreviousQuery( nameNode );
					// tell Analysis plugin to create graphics
					if ( query.isUsingGraphicalAnalysis() )
						UIUtils.sendQueryResultsToAnalysis( nameNode, queryResultInstanceID );
					if ( query.isUsingTimeline()  && !query.getQueryMode().equals(UIConst.GET_EVERYONE) && ( !count.equalsIgnoreCase("0") && (!count.equalsIgnoreCase("N/A")))) // do not send to Timeline if it's a GET_EVERYONE query (has no term information) OR if it has no results
						UIUtils.sendQueryResultsToTimeLine( nameNode, count, refId );

					
					
					// return number of patients and whether the obfuscated results is returned
					return new QueryTaskResult( numPatients, obfsc );
				} 
				else 
				{
					if (statusType.getValue().startsWith("LOCKEDOUT"))
						return new QueryTaskResult( null, "Unable to process the query because your account has been locked out. Please contact your administrator.", "Account is locked.", StatusCode.ERROR);
					else 
						return new QueryTaskResult( null, "An error occurred while submitting your query. You may wish to retry your last action.", "Remote server returned an error", StatusCode.ERROR);
				}
			} 
			catch (Exception exc) 
			{
				exc.printStackTrace();
				return new QueryTaskResult( exc, "An error occurred while submitting your query. You may wish to retry your last action.",  exc.getMessage(), StatusCode.ERROR );
			}
		}
		else // response == null
			return new QueryTaskResult( null, "An error occurred while submitting your query. You may wish to retry your last action.", "No XML response from remote server.", StatusCode.ERROR );	
	}
	
}
