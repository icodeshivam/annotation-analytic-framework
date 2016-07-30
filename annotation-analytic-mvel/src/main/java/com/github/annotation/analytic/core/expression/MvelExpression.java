package com.github.annotation.analytic.core.expression;

import org.mvel2.MVEL;

import com.github.annotation.analytic.core.expression.Expression;

import java.io.Serializable;

/**
 * @author shivam
 *
 */
public class MvelExpression implements Expression {

    private String name;
    private Serializable expression;

    public MvelExpression(String name, Serializable expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object evaluate(Object expr) {
        return MVEL.executeExpression(expression, expr);
    }
}
