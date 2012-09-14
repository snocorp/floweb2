package net.sf.flophase.floweb.cashflow;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.users.UserService;

public class FloCashFlowService implements CashFlowService {

	/**
	 * The cash flow store to execute the business logic.
	 */
	private final CashFlowStore cashFlowStore;

	/**
	 * The user service.
	 */
	private final UserService userService;

	/**
	 * Creates a new FloCashFlowService instance.
	 * 
	 * @param userService
	 *            The user service
	 * @param cashFlowStore
	 *            The cash flow store
	 */
	@Inject
	public FloCashFlowService(UserService userService, CashFlowStore cashFlowStore) {
		this.userService = userService;
		this.cashFlowStore = cashFlowStore;
	}

	@Override
	public Response<CashFlow> getCashFlow() {
		// respond with success by default
		Response<CashFlow> response = new Response<CashFlow>(
				Response.RESULT_SUCCESS);
		
		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			CashFlow cashflow = cashFlowStore.getCashFlow();
			
			response.setContent(cashflow);
		}
		// if the user is not logged int
		else {
			// respond with failure
			response = new Response<CashFlow>(Response.RESULT_FAILURE);

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}
		
		return response;
	}

}
