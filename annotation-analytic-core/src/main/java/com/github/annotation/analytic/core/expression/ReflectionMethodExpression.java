/**
 * 
 */
package com.github.annotation.analytic.core.expression;

import java.lang.reflect.Method;

import com.github.annotation.analytic.core.annotations.Analysed;

/**
 * @author shivam
 */
public class ReflectionMethodExpression implements Expression
{
    private Method method;

    private String name;

    public ReflectionMethodExpression(Method method)
    {
        this.method = method;
        Analysed fieldAnnotation = method.getAnnotation(Analysed.class);
        if (fieldAnnotation != null && !fieldAnnotation.name().equals(DEFAULT_NAME))
            this.name = fieldAnnotation.name();
        else
            this.name = method.getName();
    }

    public String getName()
    {
        return this.name;
    }

    public Object evaluate(Object obj)
    {
        Object value = null;
        try
        {
            boolean isAccessible = this.method.isAccessible();
            this.method.setAccessible(true);
            value = this.method.invoke(obj);
            this.method.setAccessible(isAccessible);            
        }
        catch (Exception e)
        {

        }
        return value;
    }

    @Override
    public String toString()
    {
        return "ReflectionMethodExpression [method=" + method + ", name=" + name + "]";
    }

}
