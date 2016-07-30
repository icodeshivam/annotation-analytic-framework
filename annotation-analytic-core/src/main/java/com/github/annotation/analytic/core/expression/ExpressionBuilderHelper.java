/**
 * 
 */
package com.github.annotation.analytic.core.expression;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.enums.ExprLang;
import com.github.annotation.analytic.core.utils.AnalyticConfigReader;

/**
 * @author shivam
 */
public class ExpressionBuilderHelper
{
    private static final Logger LOG = LoggerFactory.getLogger(ExpressionBuilderHelper.class);
    
    private static final Map<ExprLang, ExpressionBuilder> exprBuilders = new HashMap<ExprLang, ExpressionBuilder>();

    static
    {
        init();
    }

    private static void init()
    {
        Set<String> expressionBuilderPackages = AnalyticConfigReader.getExpressionBuilderPackage();
        LOG.trace("Looking for ExpressionBuilders in packages {}", expressionBuilderPackages);
        for (String expressionBuilderPackage: expressionBuilderPackages)
        {
            Reflections reflections =
                    new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage(expressionBuilderPackage)).setUrls(
                            ClasspathHelper.forPackage(expressionBuilderPackage)));
            
            Set<Class<? extends ExpressionBuilder>> exprBuilderTypes = reflections.getSubTypesOf(ExpressionBuilder.class);
            for (Class<? extends ExpressionBuilder> clazz : exprBuilderTypes)
            {
                try
                {
                    LOG.trace("Adding ExpressionBuilder {} in the Cache", clazz.getCanonicalName());
                    ExpressionBuilder exprBuilder = clazz.newInstance();
                    exprBuilders.put(exprBuilder.getName(), exprBuilder);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public static Expression buildExpression(Field field)
    {
        Analysed fieldAnnotation = field.getAnnotation(Analysed.class);
        ExprLang exprLang = ExprLang.REFLECT;
        if (fieldAnnotation != null)
            exprLang = fieldAnnotation.lang();
        
        ExpressionBuilder expressionBuilder = exprBuilders.get(exprLang);
        
        if (expressionBuilder == null) {
            throw new RuntimeException("No expresstion builder found for language : " + exprLang);
        }
        
        return expressionBuilder.build(field);
    }
    
    public static Expression buildExpression(Method method)
    {
        Analysed methodAnnotation = method.getAnnotation(Analysed.class);
        ExprLang exprLang = ExprLang.REFLECT;
        if (methodAnnotation != null)
            exprLang = methodAnnotation.lang();
        
        ExpressionBuilder expressionBuilder = exprBuilders.get(exprLang);
        
        if (expressionBuilder == null) {
            throw new RuntimeException("No expresstion builder found for language : " + exprLang);
        }
        return expressionBuilder.build(method);
    }
}
