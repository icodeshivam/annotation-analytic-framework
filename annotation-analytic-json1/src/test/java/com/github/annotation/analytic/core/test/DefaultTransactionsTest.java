/**
 * 
 */
package com.github.annotation.analytic.core.test;

import org.junit.Test;

import com.github.annotation.analytic.core.service.AnalyticService;

/**
 * @author shivam
 *
 */
public class DefaultTransactionsTest extends BaseTest
{
    @Test
    public void test()
    {
        AnalyticService.init();
        AnalyticService.start();
        transactionalMethod1();
        transactionalMethod2();
        transactionalMethod3();
        AnalyticService.end();
    }
    
    private void transactionalMethod1()
    {
        AnalyticService.update(annotatedMembersBean);
        AnalyticService.update("randomKey1", "randomValue1");
    }
    
    private void transactionalMethod2()
    {
        AnalyticService.update(noAnnotatedMembersBean);
        AnalyticService.update("randomKey2", "randomValue2");
    }
    
    private void transactionalMethod3()
    {
        AnalyticService.update("randomKey3", "randomValue3");
    }
}
