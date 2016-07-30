/**
 * 
 */
package com.github.annotation.analytic.core.test;

import org.junit.Ignore;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;

/**
 * @author shivam
 *
 */
@AnalysedEntity
@SuppressWarnings("unused")
@Ignore
class TestBeanWithNoAnnotatedMembers
{
    private String privatePrimitiveString;
    
    public String publicPrimitiveString;

    private String[] privateStringArray;
    
    public String[] publicStringArray;
    
    public CompositionClass[] publicCompositionClassArray;
    
    private CompositionClass privateCompositionClass;
    
    public CompositionClass publicCompositionClass;

    private String getPrivatePrimitiveString()
    {
        return privatePrimitiveString;
    }

    public void setPrivatePrimitiveString(String privatePrimitiveString)
    {
        this.privatePrimitiveString = privatePrimitiveString;
    }

    public String getPublicPrimitiveString()
    {
        return publicPrimitiveString;
    }

    public void setPublicPrimitiveString(String publicPrimitiveString)
    {
        this.publicPrimitiveString = publicPrimitiveString;
    }

    private String[] getPrivateStringArray()
    {
        return privateStringArray;
    }

    public void setPrivateStringArray(String[] privateStringArray)
    {
        this.privateStringArray = privateStringArray;
    }

    public String[] getPublicStringArray()
    {
        return publicStringArray;
    }

    public void setPublicStringArray(String[] publicStringArray)
    {
        this.publicStringArray = publicStringArray;
    }

    private CompositionClass getPrivateCompositionClass()
    {
        return privateCompositionClass;
    }

    private void setPrivateCompositionClass(CompositionClass privateCompositionClass)
    {
        this.privateCompositionClass = privateCompositionClass;
    }

    public CompositionClass getPublicCompositionClass()
    {
        return publicCompositionClass;
    }

    public void setPublicCompositionClass(CompositionClass publicCompositionClass)
    {
        this.publicCompositionClass = publicCompositionClass;
    }
    
}
