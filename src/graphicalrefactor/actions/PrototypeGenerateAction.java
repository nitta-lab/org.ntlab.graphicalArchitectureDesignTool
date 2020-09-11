package graphicalrefactor.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import algorithms.*;
import code.ast.*;
import graphicalrefactor.editor.Editor;
import models.dataConstraintModel.IdentifierTemplate;
import models.dataFlowModel.DataFlowModel;
import models.dataFlowModel.ResourceDependencyGraph;

public class PrototypeGenerateAction extends AbstractEditorAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3694103632055735068L;

	private String lastDir = null;

	public PrototypeGenerateAction(Editor editor) {
		super("Generate Prototype", editor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ResourceDependencyGraph graph = editor.getResourceDependencyGraph();
		if (graph != null) {
			DataFlowModel model = editor.getModel();
			FinalDecisionOfStoringResourceStates.doDecide(graph);
			String fileName = editor.getCurFileName();
			String mainTypeName = fileName.split("\\.")[0];
			boolean exist = false;
			for (IdentifierTemplate id: model.getIdentifierTemplates()) {
				String resourceName = id.getResourceName().substring(0, 1).toUpperCase() + id.getResourceName().substring(1);
				if (mainTypeName.equals(resourceName)) {
					exist = true;
				}
			}
			if (!exist) {
				CodeGenerator.setMainTypeName(mainTypeName);	// use model's file name as the main type's name.
			} else {
				CodeGenerator.resetMainTypeName();			// use the default main type's name.
			}
			editor.setCodes(MethodBodyGenerator.doGenerate(graph, model, CodeGenerator.doGenerate(graph, model)));
			for (CompilationUnit file : editor.getCodes()) {
				System.out.println(file);
			}
			
			String wd = (lastDir  != null) ? lastDir : System.getProperty("user.dir");
			JFileChooser fc = new JFileChooser(wd);
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int rc = fc.showSaveDialog(null);
			if (rc == JFileChooser.APPROVE_OPTION) {
				lastDir = fc.getSelectedFile().getPath();
				for (CompilationUnit cu : editor.getCodes()) {
					save(fc.getSelectedFile(), cu);
				}
			}
		}
	}

	private void save(File dir, CompilationUnit cu) {
		File javaFile = new File(dir.getPath(), cu.getFileName());
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile));
			writer.write(cu.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
