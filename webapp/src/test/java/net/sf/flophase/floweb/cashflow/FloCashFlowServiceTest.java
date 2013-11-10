package net.sf.flophase.floweb.cashflow;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;
import net.sf.flophase.floweb.account.FloAccountService;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.MockProvider;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.inject.Provider;
import com.googlecode.objectify.Key;

/**
 * This class tests the {@link FloAccountService} class.
 */
public class FloCashFlowServiceTest {

	/**
	 * The message returned when permission is denied.
	 */
	private static final String MSG_PERMISSION_DENIED = "Permission denied";

	/**
	 * The message returned when the key is invalid.
	 */
	private static final String MSG_INVALID_KEY = "The key is not valid";

	/**
	 * The message returned when the cash flow is invalid.
	 */
	private static final String MSG_INVALID_CASH_FLOW = "The cash flow is not valid";

	/**
	 * The message when the import has already started.
	 */
	private static final String MSG_IMPORT_HAS_STARTED = "Import has already started";

	/**
	 * The id of the import status.
	 */
	protected static final long IMPORT_STATUS_ID = 1L;

	/**
	 * The id of the cash flow.
	 */
	protected static final long CASH_FLOW_ID = 1000L;

	/**
	 * App engine helper for when we need a key.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The class under test.
	 */
	private FloCashFlowService service;

	/**
	 * The user service that the service depends upon.
	 */
	private UserService userService;

	/**
	 * The cash flow store that the service depends upon.
	 */
	private CashFlowStore cashFlowStore;

	/**
	 * The cash flow trade store that the service depends upon.
	 */
	private CashFlowTradeStore cashFlowTradeStore;

	/**
	 * The mock queue.
	 */
	private Queue queue;

	/**
	 * The mock cash flow import store.
	 */
	private CashFlowImportStore cashFlowImportStore;

	/**
	 * Sets up the test case. Creates mock user service and account store.
	 * Creates the service to be tested.
	 */
	@Before
	public void setUp() {
		helper.setUp();

		userService = context.mock(UserService.class);
		cashFlowStore = context.mock(CashFlowStore.class);
		cashFlowImportStore = context.mock(CashFlowImportStore.class);
		cashFlowTradeStore = context.mock(CashFlowTradeStore.class);
		queue = context.mock(Queue.class);
		Provider<Queue> queueProvider = new Provider<Queue>() {

			@Override
			public Queue get() {
				return queue;
			}

		};

		service = new FloCashFlowService(userService,
				new MockProvider<CashFlowStore>(cashFlowStore),
				cashFlowImportStore, cashFlowTradeStore, queueProvider,
				new Gson());
	}

	/**
	 * Tears down the test case.
	 */
	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlow()} method.
	 */
	@Test
	public void testGetCashFlow() {
		final CashFlow cashflow = new CashFlow();

		context.checking(new Expectations() {
			{
				// there is a user that is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				// return the list of accounts
				oneOf(cashFlowStore).getCashFlow();
				will(returnValue(cashflow));
			}
		});

		Response<CashFlow> response = service.getCashFlow();

		assertThat(response.getContent(), is(equalTo(cashflow)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlowExport()} method.
	 */
	@Test
	public void testGetCashFlowExport() {
		final CashFlowExport cashflow = new CashFlowExport();

		context.checking(new Expectations() {
			{

				// there is a user that is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				// return the list of accounts
				oneOf(cashFlowTradeStore).getCashFlowExport();
				will(returnValue(cashflow));
			}
		});

		Response<CashFlowExport> response = service.getCashFlowExport();

		assertThat(response.getContent(), is(equalTo(cashflow)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlow()} method when there is
	 * no logged in user.
	 */
	@Test
	public void testGetCashFlowWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<CashFlow> response = service.getCashFlow();

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that the status is retrieved from the store.
	 */
	@Test
	public void testGetCashFlowImportStatus() {
		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(cashFlowImportStore).getCashFlowImportStatus(
						IMPORT_STATUS_ID);
			}
		});

		service.getCashFlowImportStatus(String.valueOf(IMPORT_STATUS_ID));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlowImportStatus(String)}
	 * method when there is no logged in user.
	 */
	@Test
	public void testGetCashFlowImportStatusWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<CashFlowImportStatus> response = service
				.getCashFlowImportStatus(String.valueOf(IMPORT_STATUS_ID));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlowExport()} method when
	 * there is no logged in user.
	 */
	@Test
	public void testGetCashFlowExportWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<CashFlowExport> response = service.getCashFlowExport();

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that a task is added to the queue when a request to import a cash
	 * flow is made.
	 */
	@Test
	public void testImportCashFlowAsync() {
		CashFlowExport export = new CashFlowExport();
		Response<CashFlowExport> exportResponse = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, export);

		String cashflowToImport = new Gson().toJson(exportResponse);

		final CashFlowImportStatus status = new CashFlowImportStatus() {

			@Override
			public Key<CashFlow> getCashflow() {
				return Key.create(CashFlow.class, CASH_FLOW_ID);

			}

			@Override
			public Key<CashFlowImportStatus> getKey() {
				return Key.create(CashFlowImportStatus.class, IMPORT_STATUS_ID);
			}

		};

		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(cashFlowImportStore).createCashFlowImportStatus();
				will(returnValue(status));

				oneOf(queue).add(with(aNonNull(TaskOptions.class)));
			}
		});

		Response<CashFlowImportStatus> statusResponse = service
				.importCashFlow(cashflowToImport);

		assertThat(statusResponse.getResult(), equalTo(Response.RESULT_SUCCESS));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that permission is denied when an import is attempted with no
	 * logged in user.
	 */
	@Test
	public void testImportCashFlowAsyncWithNoLoggedInUser() {
		CashFlowExport export = new CashFlowExport();
		Response<CashFlowExport> exportResponse = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, export);

		String cashflowToImport = new Gson().toJson(exportResponse);

		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<CashFlowImportStatus> response = service
				.importCashFlow(cashflowToImport);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that the cash flow trade store is called correctly with the
	 * transformed cash flow.
	 */
	@Test
	public void testImportCashFlowSync() {
		CashFlowExport export = new CashFlowExport();
		Response<CashFlowExport> exportResponse = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, export);

		String cashflowToImport = new Gson().toJson(exportResponse);

		final CashFlowImportStatus status = new CashFlowImportStatus(null,
				CashFlowImportStatus.NOT_STARTED) {

			@Override
			public Key<CashFlowImportStatus> getKey() {
				return Key.create(CashFlowImportStatus.class, IMPORT_STATUS_ID);
			}

		};

		context.checking(new Expectations() {
			{
				oneOf(cashFlowStore).setCashFlowId(CASH_FLOW_ID);

				oneOf(cashFlowImportStore).getCashFlowImportStatus(
						IMPORT_STATUS_ID);
				will(returnValue(status));

				oneOf(cashFlowTradeStore).importCashFlow(with(equal(status)),
						with(any(CashFlowExport.class)));
			}
		});

		Response<CashFlowImportStatus> statusResponse = service.importCashFlow(
				String.valueOf(CASH_FLOW_ID), String.valueOf(IMPORT_STATUS_ID),
				cashflowToImport);

		assertThat(statusResponse.getResult(), equalTo(Response.RESULT_SUCCESS));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that the correct error message is returned when an invalid key is
	 * input.
	 */
	@Test
	public void testImportCashFlowSyncWithInvalidCashFlowKey() {
		CashFlowExport export = new CashFlowExport();
		Response<CashFlowExport> exportResponse = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, export);

		String cashflowToImport = new Gson().toJson(exportResponse);

		Response<CashFlowImportStatus> response = service.importCashFlow("abc",
				String.valueOf(IMPORT_STATUS_ID), cashflowToImport);

		assertThat(response.getResult(), equalTo(Response.RESULT_FAILURE));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that the correct error message is returned when an invalid key is
	 * input.
	 */
	@Test
	public void testImportCashFlowSyncWithInvalidImportStatusKey() {
		CashFlowExport export = new CashFlowExport();
		Response<CashFlowExport> exportResponse = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, export);

		String cashflowToImport = new Gson().toJson(exportResponse);

		Response<CashFlowImportStatus> response = service.importCashFlow(
				String.valueOf(CASH_FLOW_ID), "abc", cashflowToImport);

		assertThat(response.getResult(), equalTo(Response.RESULT_FAILURE));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that the correct error message is returned when an invalid cash
	 * flow is input.
	 */
	@Test
	public void testImportCashFlowSyncWithInvalidCashFlow() {

		final CashFlowImportStatus status = new CashFlowImportStatus(null,
				CashFlowImportStatus.NOT_STARTED) {

			@Override
			public Key<CashFlowImportStatus> getKey() {
				return Key.create(CashFlowImportStatus.class, IMPORT_STATUS_ID);
			}

		};

		context.checking(new Expectations() {
			{
				oneOf(cashFlowStore).setCashFlowId(CASH_FLOW_ID);

				oneOf(cashFlowImportStore).getCashFlowImportStatus(
						IMPORT_STATUS_ID);
				will(returnValue(status));
			}
		});

		Response<CashFlowImportStatus> response = service.importCashFlow(
				String.valueOf(CASH_FLOW_ID), String.valueOf(IMPORT_STATUS_ID),
				"xyz");

		assertThat(response.getResult(), equalTo(Response.RESULT_FAILURE));

		assertThat(MSG_INVALID_CASH_FLOW, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that import fails when an import is attempted after it has already
	 * started.
	 */
	@Test
	public void testImportCashFlowSyncWithAlreadyStartedImport() {
		CashFlowExport export = new CashFlowExport();
		Response<CashFlowExport> exportResponse = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, export);

		String cashflowToImport = new Gson().toJson(exportResponse);

		final CashFlowImportStatus status = new CashFlowImportStatus(null, 1) {

			@Override
			public Key<CashFlowImportStatus> getKey() {
				return Key.create(CashFlowImportStatus.class, IMPORT_STATUS_ID);
			}

		};

		context.checking(new Expectations() {
			{
				oneOf(cashFlowStore).setCashFlowId(CASH_FLOW_ID);

				// get the status
				oneOf(cashFlowImportStore).getCashFlowImportStatus(
						IMPORT_STATUS_ID);
				will(returnValue(status));
			}
		});

		Response<CashFlowImportStatus> response = service.importCashFlow(
				String.valueOf(CASH_FLOW_ID), String.valueOf(IMPORT_STATUS_ID),
				cashflowToImport);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_IMPORT_HAS_STARTED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}
}
