package net.sf.flophase.floweb;

import net.sf.flophase.floweb.account.AccountService;
import net.sf.flophase.floweb.cashflow.CashFlowService;
import net.sf.flophase.floweb.entry.EntryService;
import net.sf.flophase.floweb.xaction.TransactionService;

import org.jmock.Mockery;

/**
 * This modules binds the service classes to mock instances for dependency
 * injection.
 */
public class TestModule extends FloModule {

	/**
	 * The mock context.
	 */
	private final Mockery context;

	/**
	 * The account service.
	 */
	private AccountService accountService;

	/**
	 * The transaction service.
	 */
	private TransactionService transactionService;

	/**
	 * The entry service.
	 */
	private EntryService entryService;

	/**
	 * The cash flow service.
	 */
	private CashFlowService cashFlowService;

	/**
	 * Creates a new TestModule instance.
	 * 
	 * @param context
	 *            The mock context
	 */
	public TestModule(Mockery context) {
		this.context = context;
	}

	@Override
	protected void bindFloWebServices() {
		// create mock instances of the service classes
		accountService = context.mock(AccountService.class);
		transactionService = context.mock(TransactionService.class);
		entryService = context.mock(EntryService.class);
		cashFlowService = context.mock(CashFlowService.class);

		// bind the services to the instances
		bind(AccountService.class).toInstance(accountService);
		bind(TransactionService.class).toInstance(transactionService);
		bind(EntryService.class).toInstance(entryService);
		bind(CashFlowService.class).toInstance(cashFlowService);
	}

	@Override
	protected void bindAppEngineServices() {
		// override and bind nothing
	}

	/**
	 * Returns the mock account service.
	 * 
	 * @return The account service
	 */
	public AccountService getAccountService() {
		return accountService;
	}

	/**
	 * Returns the mock transaction service.
	 * 
	 * @return The transaction service
	 */
	public TransactionService getTransactionService() {
		return transactionService;
	}

	/**
	 * Returns the mock entry service.
	 * 
	 * @return The entry service
	 */
	public EntryService getEntryService() {
		return entryService;
	}

	/**
	 * Returns the mock cash flow service.
	 * 
	 * @return The cash flow service
	 */
	public CashFlowService getCashFlowService() {
		return cashFlowService;
	}

}
