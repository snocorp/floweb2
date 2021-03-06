package net.sf.flophase.floweb.cashflow;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.register;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;

/**
 * The data access object for cash flow data.
 */
public class FloCashFlowDAO implements CashFlowDAO {

	/**
	 * Register the objects to be stored and loaded.
	 */
	static {
		register(CashFlow.class);
		register(CashFlowImportStatus.class);
	}

	@Override
	public CashFlow getCashFlow(User user) {
		// find the single cash flow associated with the current user
		Query<CashFlow> query = ofy().load().type(CashFlow.class)
				.filter("user = ", user);

		return query.first().now();
	}

	@Override
	public CashFlow createCashFlow(User user) {
		// create a new cash flow
		CashFlow cashflow = new CashFlow(user);

		// store the cash flow
		ofy().save().entity(cashflow).now();

		return cashflow;
	}

	@Override
	public CashFlowImportStatus createCashFlowImportStatus(CashFlow cashflow,
			int total) {
		CashFlowImportStatus status = new CashFlowImportStatus(
				cashflow.getKey(), total);

		ofy().save().entity(status).now();

		return status;
	}

	@Override
	public CashFlowImportStatus getCashFlowImportStatus(CashFlow cashflow,
			long id) {
		return ofy()
				.load()
				.key(Key.create(cashflow.getKey(), CashFlowImportStatus.class,
						id)).now();
	}

	@Override
	public Result<Key<CashFlowImportStatus>> updateCashFlowImportStatus(
			CashFlowImportStatus status) {
		return ofy().save().entity(status);
	}

	@Override
	public Result<Void> deleteCashFlowImportStatus(CashFlowImportStatus status) {
		return ofy().delete().entity(status);
	}

	@Override
	public CashFlow getCashFlow(Key<CashFlow> key) {
		return ofy().load().key(key).now();
	}

}
