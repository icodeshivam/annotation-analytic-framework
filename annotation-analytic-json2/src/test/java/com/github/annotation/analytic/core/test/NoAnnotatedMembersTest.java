/**
 * 
 */
package com.github.annotation.analytic.core.test;

import org.junit.Test;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;

/**
 * @author shivam
 *
 */
public class NoAnnotatedMembersTest extends BaseTest
{
    @Test
    public void test()
    {
        AnalyticService.init();
        transactionalMethod();
    }
    
    @AnalyseTransaction(name = "testTransaction")
    public void transactionalMethod()
    {
        AnalyticService.update(noAnnotatedMembersBean);
        AnalyticService.update("randomKey", "randomValue");
    }

}
