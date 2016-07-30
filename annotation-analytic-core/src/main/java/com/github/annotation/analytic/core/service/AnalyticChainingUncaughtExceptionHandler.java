/**
 * 
 */
package com.github.annotation.analytic.core.service;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shivam
 *
 */
public class AnalyticChainingUncaughtExceptionHandler implements UncaughtExceptionHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticChainingUncaughtExceptionHandler.class);
    private UncaughtExceptionHandler previousHandler;
    
    public AnalyticChainingUncaughtExceptionHandler(UncaughtExceptionHandler previousHandler){
        this.previousHandler = previousHandler;
    }
    
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        LOG.trace("testing uncaught Exception thread");
        if (previousHandler != null){
            previousHandler.uncaughtException(t, e);
        }
        AnalyticContextInterface.endGracefully(t, e);
    }

}
