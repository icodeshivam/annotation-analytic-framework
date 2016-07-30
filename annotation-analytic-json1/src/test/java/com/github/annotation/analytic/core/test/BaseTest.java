/**
 * 
 */
package com.github.annotation.analytic.core.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;

/**
 * @author shivam
 *
 */
@Ignore
public class BaseTest
{
    protected static final String KEY = "key";
    protected static final String VALUE = "value";
    protected TestBeanWithAnnotatedMembers annotatedMembersBean = null;
    protected TestBeanWithNoAnnotatedMembers noAnnotatedMembersBean = null;
    
    @Before
    public void setUp() throws Exception
    {
        annotatedMembersBean = new TestBeanWithAnnotatedMembers();
        
        annotatedMembersBean.setPrivatePrimitiveString(VALUE);
        annotatedMembersBean.publicPrimitiveString = VALUE;
        
        annotatedMembersBean.setPrivateStringArray(new String[]{VALUE, VALUE});
        annotatedMembersBean.publicStringArray = new String[]{VALUE, VALUE};
        
        annotatedMembersBean.setPrivateCompositionClass(new CompositionClass(VALUE));
        
        annotatedMembersBean.derivedClass = new DerivedClass();
        annotatedMembersBean.derivedClass.derivedClassString = VALUE;
        annotatedMembersBean.derivedClass.baseClassString = VALUE;
        
        annotatedMembersBean.primitiveList = new ArrayList<String>();
        annotatedMembersBean.primitiveList.add(VALUE);
        annotatedMembersBean.primitiveList.add(VALUE);
        
        annotatedMembersBean.primitiveMap = new HashMap<String, String>();
        annotatedMembersBean.primitiveMap.put(KEY, VALUE);
        annotatedMembersBean.primitiveMap.put(VALUE, KEY);

        CompositionClass obj1 = new CompositionClass(VALUE);
        CompositionClass obj2 = new CompositionClass(KEY);
        
        annotatedMembersBean.nonPrimitiveList = new ArrayList<CompositionClass>();
        annotatedMembersBean.nonPrimitiveList.add(obj1);
        annotatedMembersBean.nonPrimitiveList.add(obj2);
        
        annotatedMembersBean.nonPrimitiveMap = new HashMap<String, CompositionClass>();
        annotatedMembersBean.nonPrimitiveMap.put(KEY, obj1);
        annotatedMembersBean.nonPrimitiveMap.put(VALUE, obj2);
        
        noAnnotatedMembersBean = new TestBeanWithNoAnnotatedMembers();
        noAnnotatedMembersBean.publicPrimitiveString = VALUE;
        
        noAnnotatedMembersBean.publicCompositionClass = new CompositionClass(VALUE);
        
        noAnnotatedMembersBean.publicStringArray = new String[]{VALUE, VALUE};
        
        noAnnotatedMembersBean.publicCompositionClassArray = new CompositionClass[]{obj1, obj2};
    }
}
