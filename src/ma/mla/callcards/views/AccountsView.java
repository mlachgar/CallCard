package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.AccountsSummary;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class AccountsView extends ViewPart {

	public static final String ID = "ma.mla.view.accounts";

	private Composite rootComposite;
	private Text purchasesText;
	private Text stockText;
	private Text totalCashText;
	private Text providerPaysText;
	private Text creditText;
	private Text providerCreditText;
	private Text balanceText;
	private PropertyChangeListener pcl;
	private Font font;
	private Color orange;

	@Override
	public void createPartControl(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(2, false));
		FontData[] fd = rootComposite.getFont().getFontData();
		font = new Font(rootComposite.getDisplay(), fd[0].getName(), 20,
				SWT.BOLD);
		orange = new Color(parent.getDisplay(), 255, 128, 0);
		stockText = FormUtils.newInfoText(rootComposite, "Total stock", font);
		totalCashText = FormUtils.newInfoText(rootComposite, "Total liquide",
				font);
		creditText = FormUtils.newInfoText(rootComposite,
				"Total crédit clients", font);
		purchasesText = FormUtils.newInfoText(rootComposite, "Total achats",
				font);
		providerCreditText = FormUtils.newInfoText(rootComposite,
				"Crédit fournisseurs", font);
		providerCreditText.setForeground(orange);
		providerPaysText = FormUtils.newInfoText(rootComposite,
				"Total versements", font);
		balanceText = FormUtils.newInfoText(rootComposite, "Résultat", font);

		updateUI();
		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				updateUI();
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
	}

	private void updateUI() {
		AccountsSummary summary = StorageManager.getStorage()
				.getAccountsSummary();
		stockText.setText(DataUtils.roundString(summary.totalStock));
		totalCashText.setText(DataUtils.roundString(summary.totalCash));
		creditText.setText(DataUtils.roundString(summary.totalCredit));
		providerCreditText.setText(DataUtils
				.roundString(summary.totalProviderCredit));
		purchasesText.setText(DataUtils.roundString(summary.totalPurchases));
		providerPaysText.setText(DataUtils
				.roundString(summary.totalProviderPays));
		balanceText.setText(DataUtils.roundString(summary.balance));
		if (summary.balance < 0) {
			balanceText.setForeground(balanceText.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		} else {
			balanceText.setForeground(balanceText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		orange.dispose();
		font.dispose();
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

}
