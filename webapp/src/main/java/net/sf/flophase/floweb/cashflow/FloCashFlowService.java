package net.sf.flophase.floweb.cashflow;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	 * The logger
	 */
	private static final Logger log = Logger.getLogger(FloCashFlowService.class
			.getName());

	/**
	 * The cash flow store to execute the business logic.
	 */
	private final Provider<CashFlowStore> cashFlowStore;

	/**
	 * The cash flow import store to execute the import status business logic.
	 */
	private final CashFlowImportStore cashFlowImportStore;

	/**
	 * The cash flow export store to execute the import and export business
	 * logic.
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
	 * @param cashFlowImportStore
	 *            The cash flow import store
	 * @param cashFlowExportStore
	 *            The cash flow export store
	 * @param queueProvider
	 *            The queue provider
	 * @param gson
	 *            The json library
	 */
	@Inject
	public FloCashFlowService(UserService userService,
			Provider<CashFlowStore> cashFlowStore,
			CashFlowImportStore cashFlowImportStore,
			CashFlowTradeStore cashFlowExportStore,
			Provider<Queue> queueProvider, Gson gson) {
		this.userService = userService;
		this.cashFlowStore = cashFlowStore;
		this.cashFlowImportStore = cashFlowImportStore;
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
			CashFlow cashflow = cashFlowStore.get().getCashFlow();

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

			CashFlowImportStatus status = cashFlowImportStore
					.createCashFlowImportStatus();

			Queue queue = queueProvider.get();
			queue.add(TaskOptions.Builder
					.withUrl("/cashflow/import")
					.method(Method.PUT)
					.param("key", String.valueOf(status.getKey().getId()))
					.param("cashflow",
							String.valueOf(status.getCashflow().getId()))
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
	public Response<CashFlowImportStatus> importCashFlow(
			final String cashflowKey, final String key,
			final String cashflowToImport) {
		// respond with success by default
		Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS);

		try {
			// try to parse the given keys
			long cashflowId = Long.parseLong(cashflowKey);
			long id = Long.parseLong(key);

			// inject the id so that any call in this request will have access
			// to the cash flow
			cashFlowStore.get().setCashFlowId(cashflowId);

			CashFlowImportStatus status = cashFlowImportStore
					.getCashFlowImportStatus(id);

			// if the import has not started
			if (status.getTotal() == CashFlowImportStatus.NOT_STARTED) {
				@SuppressWarnings("serial")
				Type exportResponseType = new TypeToken<Response<CashFlowExport>>() {
				}.getType();

				@SuppressWarnings("unchecked")
				Response<CashFlowExport> cashflowExport = (Response<CashFlowExport>) gson
						.fromJson(cashflowToImport, exportResponseType);

				cashFlowTradeStore.importCashFlow(status,
						cashflowExport.getContent());

				response.setContent(status);
			} else {
				response.setResult(Response.RESULT_FAILURE);
				response.addMessage("Import has already started");
			}
		}
		// if the key could not be parsed
		catch (NumberFormatException e) {
			// respond with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate the key is not valid
			response.addMessage("The key is not valid");
		} catch (Exception e) {
			log.log(Level.SEVERE, "The cash flow is not valid", e);

			// respond with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate the cash flow is not valid
			response.addMessage("The cash flow is not valid");
		}

		return response;
	}

	@Override
	public Response<CashFlowImportStatus> getCashFlowImportStatus(String key) {
		// respond with success by default
		Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS);

		// if the user is logged in
		if (userService.isUserLoggedIn()) {
			try {
				// try to parse the given key
				long id = Long.parseLong(key);

				CashFlowImportStatus status = cashFlowImportStore
						.getCashFlowImportStatus(id);

				response.setContent(status);
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
			// respond with failure
			response.setResult(Response.RESULT_FAILURE);

			// indicate that permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

}
