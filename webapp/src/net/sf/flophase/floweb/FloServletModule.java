package net.sf.flophase.floweb;

import net.sf.flophase.floweb.account.AccountQueryServlet;
import net.sf.flophase.floweb.account.AddAccountServlet;
import net.sf.flophase.floweb.account.DeleteAccountServlet;
import net.sf.flophase.floweb.account.EditAccountServlet;
import net.sf.flophase.floweb.entry.EditEntryServlet;
import net.sf.flophase.floweb.xaction.AddTransactionServlet;
import net.sf.flophase.floweb.xaction.DeleteTransactionServlet;
import net.sf.flophase.floweb.xaction.EditTransactionServlet;
import net.sf.flophase.floweb.xaction.TransactionQueryServlet;

import com.google.inject.servlet.ServletModule;

/**
 * Binds all the servlets to the appropriate URLs.
 */
public class FloServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		serve("/account/add").with(AddAccountServlet.class);
		serve("/account/delete").with(DeleteAccountServlet.class);
		serve("/account/edit").with(EditAccountServlet.class);
		serve("/account/q").with(AccountQueryServlet.class);

		serve("/xaction/q").with(TransactionQueryServlet.class);
		serve("/xaction/add").with(AddTransactionServlet.class);
		serve("/xaction/delete").with(DeleteTransactionServlet.class);
		serve("/xaction/edit").with(EditTransactionServlet.class);

		serve("/entry/add").with(EditEntryServlet.class);
		serve("/entry/edit").with(EditEntryServlet.class);
	}

}
