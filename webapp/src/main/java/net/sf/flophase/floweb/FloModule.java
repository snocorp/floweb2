package net.sf.flophase.floweb;

import net.sf.flophase.floweb.account.AccountDAO;
import net.sf.flophase.floweb.account.AccountService;
import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.account.FloAccountService;
import net.sf.flophase.floweb.account.FloAccountStore;
import net.sf.flophase.floweb.cashflow.CashFlowDAO;
import net.sf.flophase.floweb.cashflow.CashFlowService;
import net.sf.flophase.floweb.cashflow.CashFlowStore;
import net.sf.flophase.floweb.cashflow.FloCashFlowService;
import net.sf.flophase.floweb.cashflow.FloCashFlowStore;
import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.DAO;
import net.sf.flophase.floweb.common.FloDAO;
import net.sf.flophase.floweb.entry.EntryDAO;
import net.sf.flophase.floweb.entry.EntryService;
import net.sf.flophase.floweb.entry.EntryStore;
import net.sf.flophase.floweb.entry.FloEntryService;
import net.sf.flophase.floweb.entry.FloEntryStore;
import net.sf.flophase.floweb.user.FloUserService;
import net.sf.flophase.floweb.user.FloUserStore;
import net.sf.flophase.floweb.user.UserService;
import net.sf.flophase.floweb.user.UserStore;
import net.sf.flophase.floweb.xaction.FloTransactionService;
import net.sf.flophase.floweb.xaction.FloTransactionStore;
import net.sf.flophase.floweb.xaction.TransactionDAO;
import net.sf.flophase.floweb.xaction.TransactionService;
import net.sf.flophase.floweb.xaction.TransactionStore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.googlecode.objectify.annotation.Parent;

/**
 * This class performs all the bindings to allow for dependency injection.
 */
public class FloModule extends AbstractModule {

	@Override
	protected void configure() {
		bindGson();

		bindAppEngineServices();

		bindFloWebServices();
	}

	/**
	 * This binds all the logic interfaces to their implementations.
	 */
	protected void bindFloWebServices() {
		DAO floDAO = new FloDAO();

		bind(UserStore.class).to(FloUserStore.class);

		bind(AccountDAO.class).toInstance(floDAO);
		bind(AccountService.class).to(FloAccountService.class);
		bind(AccountStore.class).to(FloAccountStore.class);

		bind(TransactionDAO.class).toInstance(floDAO);
		bind(TransactionService.class).to(FloTransactionService.class);
		bind(TransactionStore.class).to(FloTransactionStore.class);

		bind(EntryDAO.class).toInstance(floDAO);
		bind(EntryService.class).to(FloEntryService.class);
		bind(EntryStore.class).to(FloEntryStore.class);

		bind(CashFlowDAO.class).toInstance(floDAO);
		bind(CashFlowStore.class).to(FloCashFlowStore.class);
		bind(CashFlowService.class).to(FloCashFlowService.class);

		bind(UserStore.class).to(FloUserStore.class);
		bind(UserService.class).to(FloUserService.class);
	}

	/**
	 * Bind all the App Engine specific services to their instances.
	 */
	protected void bindAppEngineServices() {
		bind(com.google.appengine.api.users.UserService.class).toInstance(
				UserServiceFactory.getUserService());
		bind(MemcacheService.class).toInstance(
				MemcacheServiceFactory.getMemcacheService());
		bind(DatastoreService.class).toInstance(
				DatastoreServiceFactory.getDatastoreService());
	}

	/**
	 * This binds the JSON formatter to a properly configured instance.
	 */
	protected void bindGson() {
		ExclusionStrategy strategy = new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				// we want to skip this annotation when serializing to JSON as
				// it is not necessary to know the parent.
				return f.getAnnotation(Parent.class) != null;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		};

		Gson gson = new GsonBuilder()
				.addSerializationExclusionStrategy(strategy)
				.setDateFormat(Constants.ISO_DATE_FORMAT).create();
		bind(Gson.class).toInstance(gson);
	}

}
