package ma.mla.callcards.forms;

import ma.mla.callcards.model.AccountsSummary;
import ma.mla.callcards.utils.DataUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class SummaryComposite extends Composite {
	private Text purchasesText;
	private Text stockText;
	private Text totalCashText;
	private Text providerPaysText;
	private Text creditText;
	private Text providerCreditText;
	private Text balanceText;
	private Font font;
	private Color orange;

	public SummaryComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		FontData[] fd = getFont().getFontData();
		font = new Font(getDisplay(), fd[0].getName(), 20, SWT.BOLD);
		orange = new Color(parent.getDisplay(), 255, 128, 0);
		stockText = FormUtils.newInfoText(this, "Total stock", font);
		totalCashText = FormUtils.newInfoText(this, "Total liquide", font);
		creditText = FormUtils.newInfoText(this, "Total crédit clients", font);
		purchasesText = FormUtils.newInfoText(this, "Total achats", font);
		providerCreditText = FormUtils.newInfoText(this, "Crédit fournisseurs",
				font);
		providerCreditText.setForeground(orange);
		providerPaysText = FormUtils
				.newInfoText(this, "Total versements", font);
		balanceText = FormUtils.newInfoText(this, "Résultat", font);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				orange.dispose();
				font.dispose();
			}
		});
	}

	public void setVisible(boolean visible) {
		for (Control c : getChildren()) {
			c.setVisible(visible);
		}
	}

	public void setSummary(AccountsSummary summary) {
		if (summary != null) {
			stockText.setText(DataUtils.roundString(summary.totalStock));
			totalCashText.setText(DataUtils.roundString(summary.totalCash));
			creditText
					.setText(DataUtils.roundString(summary.totalClientCredit));
			providerCreditText.setText(DataUtils
					.roundString(summary.totalProviderCredit));
			purchasesText
					.setText(DataUtils.roundString(summary.totalPurchases));
			providerPaysText.setText(DataUtils
					.roundString(summary.totalProviderPays));
			balanceText.setText(DataUtils.roundString(summary.balance));
			if (summary.balance < 0) {
				balanceText.setForeground(balanceText.getDisplay()
						.getSystemColor(SWT.COLOR_RED));
			} else {
				balanceText.setForeground(balanceText.getDisplay()
						.getSystemColor(SWT.COLOR_DARK_GREEN));
			}
		} else {
			clearAll();
		}
	}

	private void clearAll() {
		stockText.setText("");
		totalCashText.setText("");
		creditText.setText("");
		providerCreditText.setText("");
		purchasesText.setText("");
		providerPaysText.setText("");
		balanceText.setText("");
	}

}
