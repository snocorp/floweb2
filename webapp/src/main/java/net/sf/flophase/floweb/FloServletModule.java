package net.sf.flophase.floweb;

import net.sf.flophase.floweb.account.AccountQueryServlet;
import net.sf.flophase.floweb.account.AddAccountServlet;
import net.sf.flophase.floweb.account.DeleteAccountServlet;
import net.sf.flophase.floweb.account.EditAccountServlet;
import net.sf.flophase.floweb.cashflow.CashFlowExportServlet;
import net.sf.flophase.floweb.cashflow.CashFlowQueryServlet;
import net.sf.flophase.floweb.entry.EditEntryServlet;
import net.sf.flophase.floweb.ui.UserInterfaceServlet;
import net.sf.flophase.floweb.user.UserSettingsServlet;
import net.sf.flophase.floweb.xaction.AddTransactionServlet;
import net.sf.flophase.floweb.xaction.CopyTransactionServlet;
import net.sf.flophase.floweb.xaction.DeleteTransactionServlet;
import net.sf.flophase.floweb.xaction.EditTransactionServlet;
import net.sf.flophase.floweb.xaction.TransactionQueryServlet;

import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;

/**
 * Binds all the servlets to the appropriate URLs.
 */
public class FloServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		filter("/*").through(ObjectifyFilter.class);

		serve("/cashflow/q").with(CashFlowQueryServlet.class);
		serve("/cashflow/export").with(CashFlowExportServlet.class);

		serve("/account/add").with(AddAccountServlet.class);
		serve("/account/delete").with(DeleteAccountServlet.class);
		serve("/account/edit").with(EditAccountServlet.class);
		serve("/account/q").with(AccountQueryServlet.class);

		serve("/xaction/q").with(TransactionQueryServlet.class);
		serve("/xaction/add").with(AddTransactionServlet.class);
		serve("/xaction/copy").with(CopyTransactionServlet.class);
		serve("/xaction/delete").with(DeleteTransactionServlet.class);
		serve("/xaction/edit").with(EditTransactionServlet.class);

		serve("/entry/edit").with(EditEntryServlet.class);

		serve("/user/setting").with(UserSettingsServlet.class);

		serve("/").with(UserInterfaceServlet.class);
	}

}
