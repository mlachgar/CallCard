package ma.mla.callcards.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class SafePropertyListener implements PropertyChangeListener {

	abstract protected void safePropertyChange(PropertyChangeEvent evt);

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		UIUtils.async(new Runnable() {

			@Override
			public void run() {
				safePropertyChange(evt);
			}
		});
	}
}
