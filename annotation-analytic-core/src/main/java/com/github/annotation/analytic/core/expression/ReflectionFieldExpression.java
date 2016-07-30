/**
 * 
 */
package com.github.annotation.analytic.core.expression;

import java.lang.reflect.Field;

import com.github.annotation.analytic.core.annotations.Analysed;

/**
 * @author shivam
 */
public class ReflectionFieldExpression implements Expression
{
    private Field field;

    private String name;

    public ReflectionFieldExpression(Field field)
    {
        this.field = field;
        Analysed fieldAnnotation = field.getAnnotation(Analysed.class);
        if (fieldAnnotation != null && !fieldAnnotation.name().equals(DEFAULT_NAME))
            this.name = fieldAnnotation.name();
        else
            this.name = field.getName();
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
            boolean isAccessible = this.field.isAccessible();
            this.field.setAccessible(true);
            value = this.field.get(obj);
            this.field.setAccessible(isAccessible);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    @Override
    public String toString()
    {
        return "ReflectionFieldExpression [field=" + field + ", name=" + name + "]";
    }

}
