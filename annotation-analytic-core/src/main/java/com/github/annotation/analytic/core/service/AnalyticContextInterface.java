/**
 * 
 */
package com.github.annotation.analytic.core.service;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

/**
 * @author shivam
 */
class AnalyticContextInterface
{
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticContextInterface.class);

    private static final ThreadLocal<AnalyticContext> analyticContextValue =
            new ThreadLocal<AnalyticContext>();

    private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat
            .getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", TimeZone.getTimeZone("UTC"));

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String TIME_TAKEN = "timeTaken";
    private static final String THREAD_NAME = "threadName";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String EXCEPTION_STACK_TRACE = "exceptionStackTrace";
    public static final String TX_NAME = "transactionName";

    public static void beginTransaction(String name)
    {
        AnalyticContext analyticContext = analyticContextValue.get();
        if (analyticContext != null)
        {
            if (name == null)
            {
                LOG.warn("Developer forget to call the end Method, so I am calling and fushing data");
                endGracefully(null, null);
                analyticContextValue.remove();
            }
            else
            {
                LOG.trace(
                        "Looking for any existing transaction with name: {}, if exists I will end",
                        name);
                while (analyticContext.getTransactionKeyStore().contains(
                        new TransactionKeyStore(name)))
                {
                    endTransaction(null);
                }
            }
        }
        analyticContext = analyticContextValue.get();
        if (analyticContext == null)
            analyticContextValue.set(new AnalyticContext());
        analyticContext = analyticContextValue.get();
        TransactionKeyStore transactionKeyStore = new TransactionKeyStore(name);
        transactionKeyStore.startTime = System.currentTimeMillis();
        Stack<TransactionKeyStore> transactionKeyStoreStack =
                analyticContext.getTransactionKeyStore();
        LOG.trace("Starting new Transaction with name {}, pending Transaction size: {}",
                transactionKeyStore.getTransactionName(), transactionKeyStoreStack.size());
        transactionKeyStoreStack.push(transactionKeyStore);
        Thread.UncaughtExceptionHandler previousHandler =
                Thread.currentThread().getUncaughtExceptionHandler();
        if (previousHandler == null
                || !(previousHandler instanceof AnalyticChainingUncaughtExceptionHandler))
        {
            AnalyticChainingUncaughtExceptionHandler uncaughtExceptionHandler =
                    new AnalyticChainingUncaughtExceptionHandler(previousHandler);
            Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
    }

    public static void updateValue(String key, Object value)
    {
        AnalyticContext analyticContext = analyticContextValue.get();
        if (analyticContext == null)
            beginTransaction(null);

        analyticContext = analyticContextValue.get();
        analyticContext.getKeyValueStore().put(key, value);

        TransactionKeyStore transactionKeyStore = analyticContext.getTransactionKeyStore().peek();
        transactionKeyStore.addTransactionKey(key);
        LOG.trace("Added key: {} in transaction: {} with value: {}", new Object[] { key,
                transactionKeyStore.getTransactionName(), value });
    }

    public static void updateAllValue(Map<String, Object> values)
    {
        for (Entry<String, Object> entry : values.entrySet())
        {
            updateValue(entry.getKey(), entry.getValue());
        }
    }

    public static Map<String, Object> endTransaction(Throwable th)
    {
        AnalyticContext analyticContext = analyticContextValue.get();
        if (analyticContext == null)
            return null;

        boolean isTransactionKeyStoreEmpty = analyticContext.getTransactionKeyStore().isEmpty();
        if (isTransactionKeyStoreEmpty)
        {
            LOG.trace("No transaction in store, so will flush all data");
            analyticContextValue.remove();
            return null;
        }

        TransactionKeyStore transactionKeyStore = analyticContext.getTransactionKeyStore().pop();
        transactionKeyStore.endTime = System.currentTimeMillis();

        isTransactionKeyStoreEmpty = analyticContext.getTransactionKeyStore().isEmpty();

        Set<String> transactionKeys = transactionKeyStore.getTransactionKeys();
        Map<String, Object> endpointConsumableMap = new HashMap<String, Object>();
        if (!transactionKeys.isEmpty())
        {
            for (String key : transactionKeys)
            {
                Object value = analyticContext.getKeyValueStore().get(key);

                endpointConsumableMap.put(key, value);

                if (!isTransactionKeyStoreEmpty)
                    analyticContext.getTransactionKeyStore().peek().addTransactionKey(key);
            }
        }
        endpointConsumableMap.put(START_TIME,
                ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(transactionKeyStore.startTime));
        endpointConsumableMap.put(END_TIME,
                ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(transactionKeyStore.endTime));
        endpointConsumableMap.put(TIME_TAKEN,
                (transactionKeyStore.endTime - transactionKeyStore.startTime));
        endpointConsumableMap.put(THREAD_NAME, Thread.currentThread().getName());
        endpointConsumableMap.put(TX_NAME, transactionKeyStore.getTransactionName());
        if (th != null)
        {
            endpointConsumableMap.put(EXCEPTION_MESSAGE, th.getMessage());
            StringWriter out = new StringWriter();
            th.printStackTrace(new PrintWriter(out));
            endpointConsumableMap.put(EXCEPTION_STACK_TRACE, out.toString());
        }

        LOG.trace("Transactional Data for transaction {} : {}",
                new Object[] { transactionKeyStore.getTransactionName(), endpointConsumableMap });
        LOG.trace("Pending transaction size: {}", analyticContext.getTransactionKeyStore().size());

        if (isTransactionKeyStoreEmpty)
            analyticContextValue.remove();
        return endpointConsumableMap;
    }

    public static void endGracefully(Thread t, Throwable e)
    {
        while (analyticContextValue.get() != null
                && !analyticContextValue.get().getTransactionKeyStore().isEmpty())
        {
            AnalyticService.end(e);
        }
    }
}
