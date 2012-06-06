package net.sf.flophase.floweb.entry;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.users.UserService;

/**
 * This is the default implementation of the EntryService interface.
 */
public class FloEntryService implements EntryService {

	/**
	 * The entry store to execute the business logic.
	 */
	private final EntryStore entryStore;

	/**
	 * The user service.
	 */
	private final UserService userService;

	/**
	 * Creates a new {@link FloEntryService} instance.
	 * 
	 * @param userService
	 *            The user service
	 * @param entryStore
	 *            The entry store
	 */
	@Inject
	public FloEntryService(UserService userService, EntryStore entryStore) {
		this.userService = userService;
		this.entryStore = entryStore;
	}

	@Override
	public Response<Entry> editEntry(String account, String xaction, String amount) {
		Response<Entry> response = new Response<Entry>(Response.RESULT_SUCCESS);

		// if the user was logged in
		if (userService.isUserLoggedIn()) {
			long accountId = Long.MIN_VALUE;
			long xactionId = Long.MIN_VALUE;
			double amountValue = Double.NaN;
			if (amount == null || amount.length() == 0) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the balance is missing
				response.addMessage("Amount is required");
			} else {
				try {
					// try to load the amount
					amountValue = Double.parseDouble(amount);
				}
				// if the balance coud not be parsed
				catch (NumberFormatException e) {
					// respond with failure
					response.setResult(Response.RESULT_FAILURE);

					// indicate the balance could not be parsed
					response.addMessage("Amount must be a valid number");
				}
			}

			// account key logic
			try {
				// try to parse the given key
				accountId = Long.parseLong(account);
			}
			// if the key could not be parsed
			catch (NumberFormatException e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key is not valid
				response.addMessage("The account key is not valid");
			}

			// transaction key logic
			try {
				// try to parse the given key
				xactionId = Long.parseLong(xaction);
			}
			// if the key could not be parsed
			catch (NumberFormatException e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key is not valid
				response.addMessage("The transaction key is not valid");
			}

			// if no failures have occurred
			if (response.getResult() == Response.RESULT_SUCCESS) {
				// create the new entry
				Entry entry = entryStore.editEntry(accountId, xactionId, amountValue);

				response.setContent(entry);
			}
		}
		// the user was not logged in
		else {
			// respond with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

}
