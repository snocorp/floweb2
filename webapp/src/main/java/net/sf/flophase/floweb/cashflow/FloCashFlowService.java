package net.sf.flophase.floweb.cashflow;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.users.UserService;

/**
 * This class provides functionality to access the cash flow data.
 */
public class FloCashFlowService implements CashFlowService {

	/**
	 * The cash flow store to execute the business logic.
	 */
	private final CashFlowStore cashFlowStore;

	/**
	 * The cash flow export store to execute the business logic.
	 */
	private final CashFlowExportStore cashFlowExportStore;

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
	 * @param cashFlowExportStore
	 *            The cash flow export store
	 */
	@Inject
	public FloCashFlowService(UserService userService,
			CashFlowStore cashFlowStore, CashFlowExportStore cashFlowExportStore) {
		this.userService = userService;
		this.cashFlowStore = cashFlowStore;
		this.cashFlowExportStore = cashFlowExportStore;
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
		// if the user is not logged in
		else {
			// respond with failure
			response = new Response<CashFlow>(Response.RESULT_FAILURE);

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<CashFlowExport> getCashFlowExport() {
		// respond with success by default
		Response<CashFlowExport> response = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			CashFlowExport cashflow = cashFlowExportStore.getCashFlowExport();

			response.setContent(cashflow);
		}
		// if the user is not logged in
		else {
			// respond with failure
			response = new Response<CashFlowExport>(Response.RESULT_FAILURE);

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

}
