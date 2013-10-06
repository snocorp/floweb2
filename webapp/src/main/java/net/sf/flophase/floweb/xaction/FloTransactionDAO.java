package net.sf.flophase.floweb.xaction;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.register;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sf.flophase.floweb.cashflow.CashFlow;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;

/**
 * This is the transaction data access object. It uses Objectify to store and
 * retrieve data.
 */
public class FloTransactionDAO implements TransactionDAO {

	/**
	 * Register the objects to be stored and loaded.
	 */
	static {
		register(Transaction.class);
	}

	@Override
	public Transaction createTransaction(CashFlow cashflow, String name,
			Date date) {

		// create the new transaction
		Transaction xaction = new Transaction(cashflow.getKey(), name, date);

		// store the transaction
		ofy().save().entity(xaction).now();

		return xaction;
	}

	@Override
	public Result<Void> deleteTransaction(Key<Transaction> key) {
		// delete the transaction
		return ofy().delete().entity(key);
	}

	@Override
	public Transaction editTransaction(Key<Transaction> key, String name,
			Date date) {
		// load the transaction
		Transaction xaction = ofy().load().key(key).now();

		// update the transaction details
		xaction.setName(name);
		xaction.setDate(date);

		// store the transaction
		ofy().save().entity(xaction);

		return xaction;
	}

	@Override
	public Transaction getTransaction(Key<Transaction> key) {
		Transaction xaction = ofy().load().key(key).now();

		return xaction;
	}

	@Override
	public List<Transaction> getTransactions(CashFlow cashflow, Date startDate) {
		GregorianCalendar calendar = new GregorianCalendar();

		// begin with the start date
		calendar.setTime(startDate);

		// add one month
		calendar.add(Calendar.MONTH, 1);

		// store the end date
		Date endDate = calendar.getTime();

		// query for all transactions that are under the given cash flow that
		// are between the start date (inclusive) and
		// end date (exclusive)
		final Query<Transaction> query = ofy().load().type(Transaction.class)
				.ancestor(cashflow).filter("date >=", startDate)
				.filter("date <", endDate);

		return query.list();
	}

	@Override
	public List<Transaction> getTransactions(CashFlow cashflow) {
		final Query<Transaction> query = ofy().load().type(Transaction.class)
				.ancestor(cashflow);

		return query.list();
	}
}
