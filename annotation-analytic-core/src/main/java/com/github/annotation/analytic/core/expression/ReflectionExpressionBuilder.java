package com.github.annotation.analytic.core.expression;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.annotation.analytic.core.enums.ExprLang;

/**
 * @author shivam
 *
 */
public class ReflectionExpressionBuilder implements ExpressionBuilder
{
    
    public ExprLang getName()
    {
        return ExprLang.REFLECT;
    }

    public Expression build(Field field)
    {
        return new ReflectionFieldExpression(field);
    }

    public Expression build(Method method)
    {
        return new ReflectionMethodExpression(method);
    }

}
