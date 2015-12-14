package test;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This class demonstrates JFace preferences
 */
public class ShowPrefs {
  /**
   * Runs the application
   */
  public void run() {
    Display display = new Display();

    // Create the preference manager
    PreferenceManager mgr = new PreferenceManager();
    
    // Create the nodes
//    PreferenceNode one = new PreferenceNode("one", "One", null,
//        PrefPageOne.class.getName());
    PreferenceNode one = new PreferenceNode("one", new PrefPageOne());

    PreferenceNode two = new PreferenceNode("two", new PrefPageTwo());

    // Add the nodes
    mgr.addToRoot(one);
    mgr.addTo(one.getId(), two);

    // Create the preferences dialog
    PreferenceDialog dlg = new PreferenceDialog(null, mgr);
    
    
    // Set the preference store
    PreferenceStore ps = new PreferenceStore("showprefs.properties");
    try {
      ps.load();
    } catch (IOException e) {
      // Ignore
    }
 //   dlg.setPreferenceStore(ps);

    // Open the dialog
    dlg.open();

    try {
      // Save the preferences
      ps.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
    display.dispose();
  }

  /**
   * The application entry point
   * 
   * @param args
   *            the command line arguments
   */
  public static void main(String[] args) {
    new ShowPrefs().run();
  }
}

/**
 * This class creates a preference page
 */
class PrefPageOne extends PreferencePage {
  // Names for preferences
  private static final String ONE = "one.one";
  private static final String TWO = "one.two";
  private static final String THREE = "one.three";

  // Text fields for user to enter preferences
  private Text fieldOne;
  private Text fieldTwo;
  private Text fieldThree;

  public PrefPageOne() {
	    super("One");
	    setDescription("Check the checks");
	  }
  
  /**
   * Creates the controls for this page
   */
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));

    // Get the preference store
    IPreferenceStore preferenceStore = getPreferenceStore();

    // Create three text fields.
    // Set the text in each from the preference store
    new Label(composite, SWT.LEFT).setText("Field One:");
    fieldOne = new Text(composite, SWT.BORDER);
    fieldOne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
 //   fieldOne.setText(preferenceStore.getString(ONE));

    new Label(composite, SWT.LEFT).setText("Field Two:");
    fieldTwo = new Text(composite, SWT.BORDER);
    fieldTwo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
 //   fieldTwo.setText(preferenceStore.getString(TWO));

    new Label(composite, SWT.LEFT).setText("Field Three:");
    fieldThree = new Text(composite, SWT.BORDER);
    fieldThree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
 //   fieldThree.setText(preferenceStore.getString(THREE));

    return composite;
  }

  /**
   * Called when user clicks Restore Defaults
   */
  protected void performDefaults() {
    // Get the preference store
    IPreferenceStore preferenceStore = getPreferenceStore();

    // Reset the fields to the defaults
  //  fieldOne.setText(preferenceStore.getDefaultString(ONE));
   // fieldTwo.setText(preferenceStore.getDefaultString(TWO));
  //  fieldThree.setText(preferenceStore.getDefaultString(THREE));
  }

  /**
   * Called when user clicks Apply or OK
   * 
   * @return boolean
   */
  public boolean performOk() {
    // Get the preference store
    IPreferenceStore preferenceStore = getPreferenceStore();

    // Set the values from the fields
    if (fieldOne != null) preferenceStore.setValue(ONE, fieldOne.getText());
    if (fieldTwo != null) preferenceStore.setValue(TWO, fieldTwo.getText());
    if (fieldThree != null)
        preferenceStore.setValue(THREE, fieldThree.getText());

    // Return true to allow dialog to close
    return true;
  }
}


/**
 * This class creates a preference page
 */
class PrefPageTwo extends PreferencePage {
  // Names for preferences
  private static final String ONE = "two.one";
  private static final String TWO = "two.two";
  private static final String THREE = "two.three";

  // The checkboxes
  private Button checkOne;
  private Button checkTwo;
  private Button checkThree;

  /**
   * PrefPageTwo constructor
   */
  public PrefPageTwo() {
    super("Two");
    setDescription("Check the checks");
  }

  /**
   * Creates the controls for this page
   */
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new RowLayout(SWT.VERTICAL));

    // Get the preference store
    IPreferenceStore preferenceStore = getPreferenceStore();

    // Create three checkboxes
    checkOne = new Button(composite, SWT.CHECK);
    checkOne.setText("Check One");
    checkOne.setSelection(preferenceStore.getBoolean(ONE));

    checkTwo = new Button(composite, SWT.CHECK);
    checkTwo.setText("Check Two");
    checkTwo.setSelection(preferenceStore.getBoolean(TWO));

    checkThree = new Button(composite, SWT.CHECK);
    checkThree.setText("Check Three");
    checkThree.setSelection(preferenceStore.getBoolean(THREE));

    return composite;
  }

  /**
   * Add buttons
   * 
   * @param parent the parent composite
   */
  protected void contributeButtons(Composite parent) {
    // Add a select all button
    Button selectAll = new Button(parent, SWT.PUSH);
    selectAll.setText("Select All");
    selectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        checkOne.setSelection(true);
        checkTwo.setSelection(true);
        checkThree.setSelection(true);
      }
    });

    // Add a select all button
    Button clearAll = new Button(parent, SWT.PUSH);
    clearAll.setText("Clear All");
    clearAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        checkOne.setSelection(false);
        checkTwo.setSelection(false);
        checkThree.setSelection(false);
      }
    });

    // Add two columns to the parent's layout
    ((GridLayout) parent.getLayout()).numColumns += 2;
  }

  /**
   * Change the description label
   */
  protected Label createDescriptionLabel(Composite parent) {
    Label label = null;
    String description = getDescription();
    if (description != null) {
      // Upper case the description
      description = description.toUpperCase();

      // Right-align the label
      label = new Label(parent, SWT.RIGHT);
      label.setText(description);
    }
    return label;
  }

  /**
   * Called when user clicks Restore Defaults
   */
  protected void performDefaults() {
    // Get the preference store
    IPreferenceStore preferenceStore = getPreferenceStore();

    // Reset the fields to the defaults
    checkOne.setSelection(preferenceStore.getDefaultBoolean(ONE));
    checkTwo.setSelection(preferenceStore.getDefaultBoolean(TWO));
    checkThree.setSelection(preferenceStore.getDefaultBoolean(THREE));
  }

  /**
   * Called when user clicks Apply or OK
   * 
   * @return boolean
   */
  public boolean performOk() {
    // Get the preference store
    IPreferenceStore preferenceStore = getPreferenceStore();

    // Set the values from the fields
    if (checkOne != null) preferenceStore.setValue(ONE, checkOne.getSelection());
    if (checkTwo != null) preferenceStore.setValue(TWO, checkTwo.getSelection());
    if (checkThree != null)
        preferenceStore.setValue(THREE, checkThree.getSelection());

    // Return true to allow dialog to close
    return true;
  }
}
