package net.sf.flophase.floweb.entry;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.register;

import java.util.HashMap;
import java.util.Map;

import net.sf.flophase.floweb.xaction.Transaction;

import com.google.appengine.api.datastore.QueryResultIterable;

/**
 * This is the entry data access object. It uses Objectify to store and retrieve
 * data.
 */
public class FloEntryDAO implements EntryDAO {

	/**
	 * Register the objects to be stored and loaded.
	 */
	static {
		register(Entry.class);
	}

	@Override
	public Entry editEntry(Transaction xaction, long account, double amount) {
		Entry entry = getEntries(xaction).get(account);
		if (entry == null) {
			if (amount != 0.0) {
				// create the entry
				entry = new Entry(xaction.getKey(), account, amount);

				ofy().save().entity(entry).now();
			}
		} else {
			if (amount != 0.0) {
				entry.setAmount(amount);

				// store the entry
				ofy().save().entity(entry);
			} else {
				ofy().delete().entity(entry);

				entry = null;
			}
		}

		return entry;
	}

	@Override
	public Map<Long, Entry> getEntries(Transaction xaction) {
		QueryResultIterable<Entry> results = ofy().load().type(Entry.class)
				.ancestor(xaction).iterable();

		Map<Long, Entry> entries = new HashMap<Long, Entry>();
		for (Entry entry : results) {
			entries.put(entry.getAccount(), entry);
		}

		return entries;
	}
}
