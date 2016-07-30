/**
 * 
 */
package com.github.annotation.analytic.core.test;

import org.junit.Test;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;

/**
 * @author shivam
 */
public class UncaughtExceptionInTransaction extends BaseTest
{

    @Test
    public void test() throws InterruptedException
    {
        AnalyticService.init();

        Thread t = new Thread(new Runnable() {

            @Override
            public void run()
            {
                transactionalMethod();
            }
        });
        t.start();
        t.join();
    }

    @AnalyseTransaction(name = "testTransaction")
    public void transactionalMethod()
    {
        AnalyticService.update(annotatedMembersBean);
        AnalyticService.update(noAnnotatedMembersBean);
        throw new RuntimeException();
    }

}
