package graphicalrefactor.views;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.mxgraph.view.mxGraph;

import graphicalrefactor.actions.AbstractEditorAction;
import graphicalrefactor.actions.CircleLayoutAction;
import graphicalrefactor.actions.ExitAction;
import graphicalrefactor.actions.OpenAction;
import graphicalrefactor.actions.PrototypeGenerateAction;
import graphicalrefactor.actions.SaveAction;
import graphicalrefactor.actions.TreeLayoutAction;
import graphicalrefactor.actions.ZoomInAction;
import graphicalrefactor.actions.ZoomOutAction;
import graphicalrefactor.editor.Editor;

public class GraphicalRefactorMenuBar extends JMenuBar {
	private static final long serialVersionUID = 4811536194182272888L;
	
	private GraphicalRefactor graphicalModelRefactor = null;
	private OpenAction openAction = null;
	private PrototypeGenerateAction prototypeGenerateAction = null;
	private TreeLayoutAction treeLayoutAction = null;
	private CircleLayoutAction circleLayoutAction = null;
	
	public GraphicalRefactorMenuBar(GraphicalRefactor graphicalModelRefactor) {
		this.graphicalModelRefactor = graphicalModelRefactor;
		JMenu menu = null;
		menu = add(new JMenu("File"));
		menu.add(openAction = new OpenAction(graphicalModelRefactor.getEditor()));
		menu.addSeparator();
		menu.add(new SaveAction());
		menu.addSeparator();
		menu.add(new ExitAction());
		
		menu = add(new JMenu("Layout"));
		menu.add(treeLayoutAction  = new TreeLayoutAction(graphicalModelRefactor.getEditor()));
		menu.add(circleLayoutAction   = new CircleLayoutAction(graphicalModelRefactor.getEditor()));
		
		menu = add(new JMenu("View"));
		menu.add(new ZoomInAction(graphicalModelRefactor.getGraphComponent()));
		menu.add(new ZoomOutAction(graphicalModelRefactor.getGraphComponent()));
		
		menu = add(new JMenu("Generate"));
		menu.add(prototypeGenerateAction = new PrototypeGenerateAction(graphicalModelRefactor.getEditor()));
	}

	public Editor getEditor() {
		return graphicalModelRefactor.getEditor();
	}

	public void setEditor(Editor editor) {
		openAction.setEditor(editor);
		prototypeGenerateAction.setEditor(editor);
		treeLayoutAction.setEditor(editor);
		circleLayoutAction.setEditor(editor);
	}
}
