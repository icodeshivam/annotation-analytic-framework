package com.github.annotation.analytic.core.expression;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.mvel2.MVEL;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.enums.ExprLang;
import com.github.annotation.analytic.core.expression.Expression;
import com.github.annotation.analytic.core.expression.ExpressionBuilder;

/**
 * @author shivam
 *
 */
public class MvelExpressionBuilder implements ExpressionBuilder {

    @Override
    public ExprLang getName() {
        return ExprLang.MVEL;
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
        Serializable expression = MVEL.compileExpression(analysedMember.expr());
        return new MvelExpression(name, expression);
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
