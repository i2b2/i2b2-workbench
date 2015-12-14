/**
 * 
 */
package edu.harvard.i2b2.eclipse;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

/**
 * @author wp066
 *
 */
public class UpdateAction extends org.eclipse.jface.action.Action implements IAction {

	private IWorkbenchWindow window;

	public UpdateAction(IWorkbenchWindow window) {
		//super();
		this.window = window;
		setId("edu.harvard.i2b2.newUpdates");
		setText("&Update 1.6.0 ...");
		setToolTipText("Search for updates for i2b2 workbench");
		window.getWorkbench().getHelpSystem().setHelp(this, 
				"edu.harvard.i2b2.updates");
	}
	
	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), 
				new Runnable() {
					public void run() {
						UpdateJob job = new UpdateJob("Search for updates",
								getSearchRequest());
						UpdateManagerUI.openInstaller(window.getShell(), job);
					}
		});
	}
	
	private UpdateSearchRequest getSearchRequest() {
		
		UpdateSearchRequest result = new UpdateSearchRequest(
				UpdateSearchRequest.createDefaultUpdatesSearchCategory(),
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
