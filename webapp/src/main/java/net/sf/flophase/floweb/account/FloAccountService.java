package net.sf.flophase.floweb.account;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.users.UserService;

/**
 * This is the default implementation of the AccountService interface.
 */
public class FloAccountService implements AccountService {

	/**
	 * The maximum length of account names.
	 */
	private static final int MAX_NAME_LENGTH = 30;

	/**
	 * The account store to execute the business logic.
	 */
	private final AccountStore acctStore;

	/**
	 * The user service.
	 */
	private final UserService userService;

	/**
	 * Creates a new FloAccountService instance.
	 * 
	 * @param userService
	 *            The user service
	 * @param acctStore
	 *            The account store
	 */
	@Inject
	public FloAccountService(UserService userService, AccountStore acctStore) {
		this.userService = userService;
		this.acctStore = acctStore;
	}

	@Override
	public Response<List<Account>> getAccounts() {
		Response<List<Account>> response;

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			List<Account> accounts = acctStore.getAccounts();

			response = new Response<List<Account>>(Response.RESULT_SUCCESS, accounts);
		}
		// if the user is not logged int
		else {
			// respond with failure
			response = new Response<List<Account>>(Response.RESULT_FAILURE, new ArrayList<Account>());

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<Account> addAccount(String name, String balance) {
		Response<Account> response = new Response<Account>(Response.RESULT_SUCCESS);

		// if the user was logged in
		if (userService.isUserLoggedIn()) {
			if (name == null || name.length() == 0) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the name is missing
				response.addMessage("Name is required");
			} else if (name.length() > MAX_NAME_LENGTH) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the name is missing
				response.addMessage("Name must be no longer than " + MAX_NAME_LENGTH + " characters");
			}

			double balanceValue = Double.NaN;
			if (balance == null || balance.length() == 0) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the balance is missing
				response.addMessage("Balance is required");
			} else {
				try {
					// try to load the balance
					balanceValue = Double.parseDouble(balance);
				}
				// if the balance coud not be parsed
				catch (NumberFormatException e) {
					// respond with failure
					response.setResult(Response.RESULT_FAILURE);

					// indicate the balance could not be parsed
					response.addMessage("Balance must be a valid number");
				}
			}

			if (response.getResult() == Response.RESULT_SUCCESS) {
				// create the new account
				Account acct = acctStore.createAccount(name, balanceValue);

				// create a response
				response.setContent(acct);
			}
		}
		// the user was not logged in
		else {
			// respond with failure
			response = new Response<Account>(Response.RESULT_FAILURE, null);

			// indicate permision was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<Void> deleteAccount(String key) {
		Response<Void> response = new Response<Void>(Response.RESULT_SUCCESS);

		// if the user was logged in
		if (userService.isUserLoggedIn()) {
			
			//if there are two or more accounts
			if (acctStore.getAccounts().size() > 1) {
				try {
					// try to parse the given key
					long id = Long.parseLong(key);
	
					// delete the account
					acctStore.deleteAccount(id);
				}
				// if the key could not be parsed
				catch (NumberFormatException e) {
					// respond with failure
					response.setResult(Response.RESULT_FAILURE);
	
					// indicate the key is not valid
					response.addMessage("The key is not valid");
				}
			}
			else {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key is not valid
				response.addMessage("The last account cannot be deleted. Create a new account before deleting the account.");
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

	@Override
	public Response<Account> editAccount(String key, String name, String balance, String negativeThreshold, String positiveThreshold) {
		// respond with success by default
		Response<Account> response = new Response<Account>(Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			long id = -1;
			try {
				// try to parse the key
				id = Long.parseLong(key);
			}
			// if the key coud not be parsed
			catch (NumberFormatException e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key could not be parsed
				response.addMessage("The key is not valid");
			}

			if (name == null || name.length() == 0) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the name is missing
				response.addMessage("Name is required");
			} else if (name.length() > MAX_NAME_LENGTH) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the name is missing
				response.addMessage("Name must be no longer than " + MAX_NAME_LENGTH + " characters");
			}

			double balanceValue = Double.NaN;
			if (balance == null || balance.length() == 0) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the balance is missing
				response.addMessage("Balance is required");
			} else {
				try {
					// try to parse the balance
					balanceValue = Double.parseDouble(balance);
				}
				// if the balance could not be parsed
				catch (NumberFormatException e) {
					response.setResult(Response.RESULT_FAILURE);
					response.addMessage("Balance must be a valid number");
				}
			}

			double negThresholdValue = Double.NaN;
			if (negativeThreshold == null || negativeThreshold.length() == 0) {
				// default to 0.0
				negThresholdValue = 0.0;
			} else {
				try {
					// try to parse the balance
					negThresholdValue = Double.parseDouble(negativeThreshold);
				}
				// if the negative threshold could not be parsed
				catch (NumberFormatException e) {
					response.setResult(Response.RESULT_FAILURE);
					response.addMessage("Negative threshold must be a valid number");
				}
			}

			double posThresholdValue = Double.NaN;
			if (positiveThreshold == null || positiveThreshold.length() == 0) {
				// default to 0.0
				posThresholdValue = 0.0;
			} else {
				try {
					// try to parse the balance
					posThresholdValue = Double.parseDouble(positiveThreshold);
				}
				// if the balance could not be parsed
				catch (NumberFormatException e) {
					response.setResult(Response.RESULT_FAILURE);
					response.addMessage("Positive threshold must be a valid number");
				}
			}

			// if we have not indicated failure yet
			if (response.getResult() == Response.RESULT_SUCCESS) {
				// edit the account
				Account acct = acctStore.editAccount(id, name, balanceValue, negThresholdValue, posThresholdValue);

				// respond with the updated account
				response.setContent(acct);
			}
		}
		// the user was not logged in
		else {
			// response with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

}
