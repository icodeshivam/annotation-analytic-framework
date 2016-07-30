/**
 * 
 */
package com.github.annotation.analytic.core.test;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;

/**
 * @author shivam
 */
@AnalysedEntity(name = "testBean")
@SuppressWarnings("unused")
@Ignore
class TestBeanWithAnnotatedMembers
{
    @Analysed(name = "plainString")
    private String privatePrimitiveString;
    
    @Analysed
    public String publicPrimitiveString;

    private String[] privateStringArray;
    
    public String[] publicStringArray;
    
    @Analysed
    private CompositionClass privateCompositionClass;
    
    public CompositionClass publicCompositionClass;
    
    @Analysed
    public DerivedClass derivedClass;
    
    @Analysed
    public Map<String, String> primitiveMap;

    @Analysed
    public List<String> primitiveList;
    
    @Analysed
    public Map<String, CompositionClass> nonPrimitiveMap;
    
    @Analysed
    public List<CompositionClass> nonPrimitiveList;
    
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

    @Analysed
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

    public void setPrivateCompositionClass(CompositionClass privateCompositionClass)
    {
        this.privateCompositionClass = privateCompositionClass;
    }
    @Analysed
    public CompositionClass getPublicCompositionClass()
    {
        return publicCompositionClass;
    }
    @Analysed
    public void setPublicCompositionClass(CompositionClass publicCompositionClass)
    {
        this.publicCompositionClass = publicCompositionClass;
    }
}
