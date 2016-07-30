package com.github.annotation.analytic.core.expression;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.annotation.analytic.core.enums.ExprLang;

/**
 * @author shivam
 *
 */
public interface ExpressionBuilder
{
    ExprLang getName();
    
    Expression build(Field field);
    
    Expression build(Method method);
}
