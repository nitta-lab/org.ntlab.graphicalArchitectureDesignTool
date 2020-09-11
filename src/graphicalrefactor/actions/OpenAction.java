package graphicalrefactor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import graphicalrefactor.editor.Editor;

public class OpenAction extends AbstractEditorAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8290761032629599683L;
	
	private String lastDir = null;
	
	public OpenAction(Editor editor) {
		super("Open...", editor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (editor != null) {
			String wd = (lastDir  != null) ? lastDir : System.getProperty("user.dir");

			JFileChooser fc = new JFileChooser(wd);

			// Adds file filter for supported file format
			FileFilter defaultFilter = new FileFilter() {

				public boolean accept(File file) {
					String lcase = file.getName().toLowerCase();
					return lcase.endsWith(".model");
				}

				@Override
				public String getDescription() {
					return null;
				}
			};
			fc.addChoosableFileFilter(defaultFilter);
			int rc = fc.showDialog(null, "Open Model File");
			if (rc == JFileChooser.APPROVE_OPTION) {
				lastDir = fc.getSelectedFile().getParent();
				editor.open(fc.getSelectedFile());
			}
		}
	}

}
