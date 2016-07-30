package com.github.annotation.analytic.core.expression;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.enums.ExprLang;
import com.github.annotation.analytic.core.expression.Expression;
import com.github.annotation.analytic.core.expression.ExpressionBuilder;

/**
 * @author shivam
 *
 */
public class SpelExpressionBuilder implements ExpressionBuilder {

    private static SpelExpressionParser parser;

    @Override
    public ExprLang getName() {
        return ExprLang.SPEL;
    }

    @Override
    public Expression build(Field field) {
        Analysed fieldAnnotation = field.getAnnotation(Analysed.class);
        String name = fieldAnnotation.name();
        if (name.equals("defaultName")) {
            name = field.getName();
        }
        return build(name, fieldAnnotation);
    }

    private Expression build(String name, Analysed analysedMember) {
        if (parser == null) {
            initParser();
        }
        org.springframework.expression.Expression expression = parser.parseExpression(analysedMember.expr());
        return new SpelExpression(name, expression);
    }

    private synchronized void initParser() {
        if (parser == null) {
            parser = new SpelExpressionParser();
        }
    }

    @Override
    public Expression build(Method method) {
        Analysed fieldAnnotation = method.getAnnotation(Analysed.class);
        String name = fieldAnnotation.name();
        if (name.equals("defaultName")) {
            name = method.getName();
        }
        return build(name, fieldAnnotation);
    }
}
