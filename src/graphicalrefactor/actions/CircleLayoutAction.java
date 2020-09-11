package graphicalrefactor.actions;

import java.awt.event.ActionEvent;

import graphicalrefactor.editor.Editor;

public class CircleLayoutAction extends AbstractEditorAction {

	public CircleLayoutAction(Editor editor) {
		super("Circle Layout", editor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		editor.setCircleLayout();
	}

}
