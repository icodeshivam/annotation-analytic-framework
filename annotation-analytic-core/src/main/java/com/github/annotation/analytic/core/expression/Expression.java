/**
 * 
 */
package com.github.annotation.analytic.core.expression;


/**
 * @author shivam
 */
public interface Expression
{
    static final String DEFAULT_NAME = "defaultName";
    
    String getName();
    
    Object evaluate(Object expr);
}
