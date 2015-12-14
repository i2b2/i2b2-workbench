package edu.harvard.i2b2.eclipse;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

public class SearchAndUpdateAction extends org.eclipse.jface.action.Action implements IAction {

	private IWorkbenchWindow window;

	public SearchAndUpdateAction(IWorkbenchWindow window) {
		//super();
		this.window = window;
		setId("edu.harvard.i2b2.searchUpdates");
		setText("&Add new features ...");
		setToolTipText("Search for new features for i2b2 workbench");
		window.getWorkbench().getHelpSystem().setHelp(this, 
		//		"org.eclipsercp.hyperbola.updates");
		"edu.harvard.i2b2.newFeatures");
	}
	
	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), 
				new Runnable() {
					public void run() {
						UpdateJob job = new UpdateJob("Search for new features",
								getSearchRequest());
						UpdateManagerUI.openInstaller(window.getShell(), job);
					}
		});
	}
	
	private UpdateSearchRequest getSearchRequest() {
		
		UpdateSearchRequest result = new UpdateSearchRequest(
				UpdateSearchRequest.createDefaultSiteSearchCategory(),
				new UpdateSearchScope());
		result.addFilter(new BackLevelFilter());
		result.addFilter(new EnvironmentFilter());
		UpdateSearchScope scope = new UpdateSearchScope();
		try {
			String homeBase = System.getProperty("i2b2.homebase", "files:/d:/updates");//"http://eclipsercp.org/updates");
			URL url = new URL(homeBase);
			scope.addSearchSite("i2b2 site", url, null);
		}
		catch(MalformedURLException e) {
			
		}
		result.setScope(scope);
		return result;
	}
}

