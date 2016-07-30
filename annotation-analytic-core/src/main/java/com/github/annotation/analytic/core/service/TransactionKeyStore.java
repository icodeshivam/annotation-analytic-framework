package com.github.annotation.analytic.core.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author shivam
 */
final class TransactionKeyStore
{
    private static final String defaultTransactionName = "defaultTransaction";
    
    long startTime;
    long endTime;
    
    private String transactionName;
    private Set<String> transactionKeys;

    public TransactionKeyStore(String transactionName)
    {
        if (StringUtils.isBlank(transactionName))
            this.transactionName = defaultTransactionName;
        else
            this.transactionName = transactionName;
        this.transactionKeys = new HashSet<String>();
    }
    
    public void setTransactionName(String transactionName)
    {
        this.transactionName = transactionName;
    }

    public String getTransactionName()
    {
        if (transactionName == null)
            transactionName = defaultTransactionName;
        return transactionName;
    }

    public Set<String> getTransactionKeys()
    {
        if (transactionKeys == null)
            transactionKeys = new HashSet<String>();
        return transactionKeys;
    }

    public boolean addTransactionKey(String key)
    {
        if (transactionKeys == null)
            transactionKeys = new HashSet<String>();
        return transactionKeys.add(key);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof TransactionKeyStore))
			return false;
		TransactionKeyStore tKeyStore = (TransactionKeyStore) obj;
		if (this.transactionName.equals(tKeyStore.transactionName))
			return true;
		return false;
	}
    
}