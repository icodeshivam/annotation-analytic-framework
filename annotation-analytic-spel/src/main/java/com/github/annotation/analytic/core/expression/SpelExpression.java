package com.github.annotation.analytic.core.expression;

import com.github.annotation.analytic.core.expression.Expression;

/**
 * @author shivam
 *
 */
public class SpelExpression implements Expression {

    private org.springframework.expression.Expression expression;

    private String                                    name;

    public SpelExpression(String name, org.springframework.expression.Expression expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object evaluate(Object expr) {
        return expression.getValue(expr);
    }
}
