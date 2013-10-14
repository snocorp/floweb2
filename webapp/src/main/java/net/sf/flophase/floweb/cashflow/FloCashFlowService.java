package net.sf.flophase.floweb.cashflow;

import java.lang.reflect.Type;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.UserService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Provider;

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
	private final CashFlowTradeStore cashFlowTradeStore;

	/**
	 * The user service.
	 */
	private final UserService userService;

	/**
	 * The gson instance.
	 */
	private final Gson gson;

	/**
	 * A provider for queues.
	 */
	private final Provider<Queue> queueProvider;

	/**
	 * Creates a new FloCashFlowService instance.
	 * 
	 * @param userService
	 *            The user service
	 * @param cashFlowStore
	 *            The cash flow store
	 * @param cashFlowExportStore
	 *            The cash flow export store
	 * @param queueProvider
	 *            The queue provider
	 * @param gson
	 *            The json library
	 */
	@Inject
	public FloCashFlowService(UserService userService,
			CashFlowStore cashFlowStore,
			CashFlowTradeStore cashFlowExportStore,
			Provider<Queue> queueProvider, Gson gson) {
		this.userService = userService;
		this.cashFlowStore = cashFlowStore;
		this.cashFlowTradeStore = cashFlowExportStore;
		this.queueProvider = queueProvider;
		this.gson = gson;
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
			CashFlowExport cashflow = cashFlowTradeStore.getCashFlowExport();

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

	@Override
	public Response<CashFlowImportStatus> importCashFlow(String cashflowToImport) {
		// respond with success by default
		Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {

			CashFlowImportStatus status = cashFlowStore.createCashFlowImport(0);

			Queue queue = queueProvider.get();
			queue.add(TaskOptions.Builder.withUrl("/cashflow/import")
					.method(Method.PUT)
					.param("key", status.getKey().toString())
					.payload(cashflowToImport));

			response.setContent(status);
		}
		// if the user is not logged in
		else {
			// respond with failure
			response = new Response<CashFlowImportStatus>(
					Response.RESULT_FAILURE);

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<CashFlowImportStatus> importCashFlow(String key,
			String cashflowToImport) {
		// respond with success by default
		Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			try {
				// try to parse the given key
				long id = Long.parseLong(key);

				CashFlowImportStatus status = cashFlowStore
						.getCashFlowImportStatus(id);

				@SuppressWarnings("serial")
				Type exportResponseType = new TypeToken<Response<CashFlowExport>>() {
				}.getType();

				@SuppressWarnings("unchecked")
				Response<CashFlowExport> cashflowExport = (Response<CashFlowExport>) gson
						.fromJson(cashflowToImport, exportResponseType);

				cashFlowTradeStore.importCashFlow(status,
						cashflowExport.getContent());

				response.setContent(status);
			}
			// if the key could not be parsed
			catch (NumberFormatException e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the key is not valid
				response.addMessage("The key is not valid");
			} catch (Exception e) {
				// respond with failure
				response.setResult(Response.RESULT_FAILURE);

				// indicate the cash flow is not valid
				response.addMessage("The cash flow is not valid");
			}
		}
		// if the user is not logged in
		else {
			// respond with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<CashFlowImportStatus> getCashFlowImportStatus(String key) {
		// TODO
		return null;
	}

}
