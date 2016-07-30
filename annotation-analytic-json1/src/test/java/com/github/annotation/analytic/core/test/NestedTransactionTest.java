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
public class NestedTransactionTest extends BaseTest
{

    @Test
    public void test()
    {
        AnalyticService.init();
        transactionalMethod1();
    }
    
    @AnalyseTransaction(name = "transaction1")
    private void transactionalMethod1()
    {
        try{
            AnalyticService.update(annotatedMembersBean);
            transactionalMethod2();
            AnalyticService.update("randomKey", "randomValue1");
            throw new RuntimeException();
        } catch (Exception e){
            System.out.println("Exception Caught");
        }
        AnalyticService.update("allgood", "yes");
    }
    
    @AnalyseTransaction(name = "transaction2")
    private void transactionalMethod2()
    {
        AnalyticService.update(noAnnotatedMembersBean);
        transactionalMethod3();
        AnalyticService.update("randomKey", "randomValue2");
        throw new RuntimeException();
    }
    
    @AnalyseTransaction(name = "transaction3")
    private void transactionalMethod3()
    {
        AnalyticService.update("randomKey", "randomValue3");
        throw new RuntimeException();
    }

}
