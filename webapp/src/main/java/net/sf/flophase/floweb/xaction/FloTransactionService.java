package net.sf.flophase.floweb.xaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.entry.Entry;

import com.google.appengine.api.users.UserService;

/**
 * This is the default implementation of the TransactionService interface.
 */
public class FloTransactionService implements TransactionService {

	/**
	 * The maximum length of a transaction name.
	 */
	private static final int MAX_NAME_LENGTH = 30;

	/**
	 * The transaction store.
	 */
	private final TransactionStore xactionStore;

	/**
	 * The user service.
	 */
	private final UserService userService;

	/**
	 * Creates a new {@link FloTransactionService} instance.
	 * 
	 * @param xactionStore
	 *            The transaction store
	 * @param userService
	 *            The user service
	 */
	@Inject
	public FloTransactionService(TransactionStore xactionStore, UserService userService) {
		this.xactionStore = xactionStore;
		this.userService = userService;
	}

	@Override
	public Response<List<FinancialTransaction>> getTransactions(String month) {
		Response<List<FinancialTransaction>> response = new Response<List<FinancialTransaction>>(
		        Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			Date startDate = null;
			if (month == null || month.length() == 0) {
				response.setResult(Response.RESULT_FAILURE);

				response.addMessage("Month is required");
			} else {
				// attempt to parse the date
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
				try {
					startDate = format.parse(month);
				} catch (ParseException e) {
					response.setResult(Response.RESULT_FAILURE);

					response.addMessage("Month was not in the correct format (yyyy-MM)");
				}
			}

			if (response.getResult() == Response.RESULT_SUCCESS) {
				List<FinancialTransaction> transactions = xactionStore.getTransactions(startDate);

				response.setContent(transactions);
			}
		}
		// if the user is not logged in
		else {
			// response with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<FinancialTransaction> addTransaction(String name, String date) {
		Response<FinancialTransaction> response = new Response<FinancialTransaction>(Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			if (name == null || name.length() == 0) {
				response.setResult(Response.RESULT_FAILURE);

				response.addMessage("Description is required");
			} else if (name.length() > MAX_NAME_LENGTH) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the name is missing
				response.addMessage("Description must be no longer than " + MAX_NAME_LENGTH + " characters");
			}

			Date xactionDate = null;
			if (date == null || date.length() == 0) {
				response.setResult(Response.RESULT_FAILURE);

				response.addMessage("Date is required");
			} else {
				// attempt to parse the date
				SimpleDateFormat format = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
				try {
					xactionDate = format.parse(date);
				} catch (ParseException e) {
					response.setResult(Response.RESULT_FAILURE);

					response.addMessage("Date was not in the correct format (" + Constants.ISO_DATE_FORMAT + ")");
				}
			}

			if (response.getResult() == Response.RESULT_SUCCESS) {
				// create the transaction
				Transaction xaction = xactionStore.createTransaction(name, xactionDate);

				response.setContent(new FinancialTransaction(xaction, new HashMap<Long, Entry>()));
			}
		}
		// if the user is not logged in
		else {
			// response with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<Void> deleteTransaction(String key) {
		Response<Void> response = new Response<Void>(Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			try {
				// try to parse the given key
				long id = Long.parseLong(key);

				// delete the account
				xactionStore.deleteTransaction(id);
			}
			// if the key could not be parsed
			catch (NumberFormatException e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key is not valid
				response.addMessage("The key is not valid");
			}
		}
		// if the user is not logged in
		else {
			// response with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<Transaction> editTransaction(String key, String name, String date) {
		Response<Transaction> response = new Response<Transaction>(Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			if (name == null || name.length() == 0) {
				response.setResult(Response.RESULT_FAILURE);

				response.addMessage("Description is required");
			} else if (name.length() > MAX_NAME_LENGTH) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the name is missing
				response.addMessage("Description must be no longer than " + MAX_NAME_LENGTH + " characters");
			}

			Date xactionDate = null;
			if (date == null || date.length() == 0) {
				response.setResult(Response.RESULT_FAILURE);

				response.addMessage("Date is required");
			} else {
				// attempt to parse the date
				SimpleDateFormat format = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
				try {
					xactionDate = format.parse(date);
				} catch (ParseException e) {
					response.setResult(Response.RESULT_FAILURE);

					response.addMessage("Date was not in the correct format (" + Constants.ISO_DATE_FORMAT + ")");
				}
			}

			long id = Long.MIN_VALUE;
			try {
				// try to parse the given key
				id = Long.parseLong(key);
			}
			// if the key could not be parsed
			catch (NumberFormatException e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key is not valid
				response.addMessage("The key is not valid");
			}

			if (response.getResult() == Response.RESULT_SUCCESS) {
				// create the transaction
				Transaction xaction = xactionStore.editTransaction(id, name, xactionDate);

				response.setContent(xaction);
			}
		}
		// if the user is not logged in
		else {
			// response with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

}
