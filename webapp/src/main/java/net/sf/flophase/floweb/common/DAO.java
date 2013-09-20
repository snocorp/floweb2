package net.sf.flophase.floweb.common;

import net.sf.flophase.floweb.account.AccountDAO;
import net.sf.flophase.floweb.cashflow.CashFlowDAO;
import net.sf.flophase.floweb.entry.EntryDAO;
import net.sf.flophase.floweb.xaction.TransactionDAO;

/**
 * The DAO interface provides a common interface for manipulating all types of
 * entities.
 */
public interface DAO extends AccountDAO, CashFlowDAO, TransactionDAO, EntryDAO {

}