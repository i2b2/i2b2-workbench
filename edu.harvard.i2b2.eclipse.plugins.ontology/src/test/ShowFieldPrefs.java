package test;

import java.io.IOException;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Display;

/**
 * This class demonstrates JFace preferences and field editors
 */
public class ShowFieldPrefs {
  /**
   * Runs the application
   */
  public void run() {
    Display display = new Display();

    // Create the preference manager
    PreferenceManager mgr = new PreferenceManager();

    // Create the nodes
    PreferenceNode one = new PreferenceNode("one", "One", null,
        FieldEditorPageOne.class.getName());
    PreferenceNode two = new PreferenceNode("two", "Two", null,
        FieldEditorPageTwo.class.getName());

    // Add the nodes
    mgr.addToRoot(one);
    mgr.addToRoot(two);

    // Create the preferences dialog
    PreferenceDialog dlg = new PreferenceDialog(null, mgr);

    // Set the preference store
    PreferenceStore ps = new PreferenceStore("showfieldprefs.properties");
    try {
      ps.load();
    } catch (IOException e) {
      // Ignore
    }
    dlg.setPreferenceStore(ps);

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
    new ShowFieldPrefs().run();
  }
}

/**
 * This class demonstrates field editors
 */

class FieldEditorPageOne extends FieldEditorPreferencePage {
  public FieldEditorPageOne() {
    // Use the "flat" layout
    super(FLAT);
  }

  /**
   * Creates the field editors
   */
  protected void createFieldEditors() {
    // Add a boolean field
    BooleanFieldEditor bfe = new BooleanFieldEditor("myBoolean", "Boolean",
        getFieldEditorParent());
    addField(bfe);

    // Add a color field
    ColorFieldEditor cfe = new ColorFieldEditor("myColor", "Color:",
        getFieldEditorParent());
    addField(cfe);

    // Add a directory field
    DirectoryFieldEditor dfe = new DirectoryFieldEditor("myDirectory",
        "Directory:", getFieldEditorParent());
    addField(dfe);

    // Add a file field
    FileFieldEditor ffe = new FileFieldEditor("myFile", "File:",
        getFieldEditorParent());
    addField(ffe);

    // Add a font field
    FontFieldEditor fontFe = new FontFieldEditor("myFont", "Font:",
        getFieldEditorParent());
    addField(fontFe);

    // Add a radio group field
    RadioGroupFieldEditor rfe = new RadioGroupFieldEditor("myRadioGroup",
        "Radio Group", 2, new String[][] { { "First Value", "first" },
            { "Second Value", "second" },
            { "Third Value", "third" },
            { "Fourth Value", "fourth" } }, getFieldEditorParent(),
        true);
    addField(rfe);

    // Add a path field
    PathEditor pe = new PathEditor("myPath", "Path:", "Choose a Path",
        getFieldEditorParent());
    addField(pe);
  }
}
/**
 * This class demonstrates field editors
 */

class FieldEditorPageTwo extends FieldEditorPreferencePage {
  public FieldEditorPageTwo() {
    // Use the "grid" layout
    super(GRID);
  }

  /**
   * Creates the field editors
   */
  protected void createFieldEditors() {
    // Add an integer field
    IntegerFieldEditor ife = new IntegerFieldEditor("myInt", "Int:",
        getFieldEditorParent());
    addField(ife);

    // Add a scale field
    ScaleFieldEditor sfe = new ScaleFieldEditor("myScale", "Scale:",
        getFieldEditorParent(), 0, 100, 1, 10);    addField(sfe);

    // Add a string field
    StringFieldEditor stringFe = new StringFieldEditor("myString",
        "String:", getFieldEditorParent());
    addField(stringFe);
  }
}


//showfieldprefs.properties

/*
#Sat Feb 28 16:06:57 GMT-05:00 2004
myPath=C\:\\Documents and Settings\\Owner\\My Documents;C\:\\;
myRadioGroup=
myScale=0
myColor=0,128,0
myFont=1|Terminal|8|0|WINDOWS|1|-13|0|0|0|400|0|0|0|-1|1|2|1|49|Terminal;
myFile=.\\0249f1701.bmp
myString=
myBoolean=true
myDirectory=C\:\\Documents and Settings\\Owner\\My Documents


*/
