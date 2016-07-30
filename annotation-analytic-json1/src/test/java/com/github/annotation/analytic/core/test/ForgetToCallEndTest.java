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
public class ForgetToCallEndTest extends BaseTest
{

    @Test
    public void test() throws InterruptedException
    {
        AnalyticService.init();

        Thread t = new Thread(new Runnable() {

            @Override
            public void run()
            {
                boolean reRun = true;
                boolean firstRun = true;
                while(reRun)
                {

                    if (!firstRun)
                        reRun = false;
                    if (firstRun)
                        firstRun = false;
                    AnalyticService.start();
                    transactionalMethod();
                }
            }
        });
        t.start();
        t.join();
    }

    public void transactionalMethod()
    {
        AnalyticService.update(annotatedMembersBean);
        AnalyticService.update(noAnnotatedMembersBean);
    }
}
