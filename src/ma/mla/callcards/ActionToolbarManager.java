package ma.mla.callcards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;

public class ActionToolbarManager extends ToolBarManager {

	List<IAction> actions = new ArrayList<IAction>();

	public ActionToolbarManager(int style) {
		super(style);
	}

	@Override
	public void add(IAction action) {
		super.add(action);
		actions.add(action);
	}

	public void update(boolean force) {
		for (IAction action : actions) {
			action.setEnabled(action.isEnabled());
		}
		super.update(force);
	}
}
