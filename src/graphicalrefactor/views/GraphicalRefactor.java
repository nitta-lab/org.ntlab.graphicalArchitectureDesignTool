package graphicalrefactor.views;

import javax.swing.JFrame;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.view.mxGraph;

import graphicalrefactor.editor.Editor;

public class GraphicalRefactor extends JFrame {
	private static final long serialVersionUID = -8690140317781055614L;
	
	private Editor editor;
	private mxGraph graph;
	private GraphicalRefactorMenuBar menuBar;
	private mxGraphComponent graphComponent;

	public GraphicalRefactor() {
		setTitle("Graphical Refactor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		graph = new mxGraph() {
			public boolean isPort(Object cell) {
				mxGeometry geo = getCellGeometry(cell);
				
				return (geo != null) ? geo.isRelative() : false;
			}
			
			public boolean isCellFoldable(Object cell, boolean collapse) {
				return false;
			}
		};
		
		graphComponent = new mxGraphComponent(graph) {			
			protected mxICellEditor createCellEditor() {
				return new ComboBoxCellEditor(this);
			}
		};
		getContentPane().add(graphComponent);
		new mxRubberband(graphComponent);
		
		editor = new Editor(graph);
		
		menuBar = new GraphicalRefactorMenuBar(this);
		setJMenuBar(menuBar);
		setSize(870, 640);
	}

	public mxGraph getGraph() {
		return graph;
	}

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}

}
