/**
 * 
 */
package com.github.annotation.analytic.core.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import com.github.annotation.analytic.core.endpoint.Endpoint;
import com.github.annotation.analytic.core.endpoint.EndpointHelper;
import com.github.annotation.analytic.core.expression.Expression;
import com.github.annotation.analytic.core.expression.ExpressionBuilderHelper;
import com.github.annotation.analytic.core.utils.AnalyticConfigReader;

/**
 * @author shivam
 */
public class AnalyticService
{
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticService.class);
    private static final String defaultName = "defaultName";

    private static Map<String, ClassMembers> analyticCache = new Hashtable<String, ClassMembers>() {

        private static final long serialVersionUID = 1955220601592331913L;

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder("\nAnalyticCache Data:\n");
            for (java.util.Map.Entry<String, ClassMembers> entry : this.entrySet())
            {
                sb.append("\t").append(entry.getKey()).append("\t-->").append("\t")
                        .append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }
    };

    private static Endpoint endpoint = null;

    static
    {
        try
        {
            initInternal();
            endpoint = EndpointHelper.getEndpoint();
        }
        catch (Throwable th)
        {
            LOG.error(th.getMessage(), th);
            throw new RuntimeException(th);
        }
    }

    public static void init()
    {
        // DO NOTHING!!!
    }

    private static void initInternal()
    {
        Set<String> annotationPackages = AnalyticConfigReader.getAnnotationPackages();
        if (annotationPackages != null && !annotationPackages.isEmpty())
        {
            for (String annotationPackage : annotationPackages)
            {
                Reflections reflections =
                        new Reflections(new ConfigurationBuilder().filterInputsBy(
                                new FilterBuilder().includePackage(annotationPackage)).setUrls(
                                ClasspathHelper.forPackage(annotationPackage)));
                Set<Class<?>> analysedTypes =
                        reflections.getTypesAnnotatedWith(AnalysedEntity.class);
                for (Class<?> analysedType : analysedTypes)
                {
                    updateAnalyticCache(analysedType);
                }
            }
        }
        else
        {
            LOG.warn("No annotation package is defined");
        }
        LOG.trace(analyticCache.toString());
    }

    @SuppressWarnings("unchecked")
    private static void updateAnalyticCache(Class<?> analysedType)
    {
        AnalysedEntity annotation = analysedType.getAnnotation(AnalysedEntity.class);
        String name = null;
        if (annotation != null && !annotation.name().equals(defaultName))
            name = annotation.name();
        else {
            name = analysedType.getCanonicalName();
            if (StringUtils.isBlank(name))
                name = analysedType.getName();
        }

        if (!analyticCache.containsKey(name))
        {
            LOG.trace("Annotated class: {}", name);
            Set<Method> methods =
                    ReflectionUtils.getAllMethods(analysedType,
                            ReflectionUtils.withAnnotation(Analysed.class),
                            ReflectionUtils.withParametersCount(0));
            Set<Field> fields =
                    ReflectionUtils.getAllFields(analysedType,
                            ReflectionUtils.withAnnotation(Analysed.class));

            boolean takeOnlyPrimitives = false;

            if (fields.isEmpty() && methods.isEmpty())
            {
                takeOnlyPrimitives = true;
                methods =
                        ReflectionUtils.getAllMethods(analysedType,
                                ReflectionUtils.withParametersCount(0),
                                ReflectionUtils.withModifier(Modifier.PUBLIC));
                fields =
                        ReflectionUtils.getAllFields(analysedType,
                                ReflectionUtils.withModifier(Modifier.PUBLIC));
                LOG.trace("Since no member is annotated, I will take all public primitive members");
            }

            Set<Expression> expressions = new HashSet<Expression>();

            if (!methods.isEmpty())
            {
                LOG.trace("\tMethods:");
                for (Method method : methods)
                {
                    LOG.trace("\t\tMethod Name: {}", method.getName());
                    Class<?> returnType = method.getReturnType();
                    if (!takeOnlyPrimitives || isPrimitive(returnType))
                    {
                        Expression expression = ExpressionBuilderHelper.buildExpression(method);
                        expressions.add(expression);
                    }
                }
            }

            if (!fields.isEmpty())
            {
                LOG.trace("\tFields:");
                for (Field field : fields)
                {
                    LOG.trace("\t\tField Name: {}", field.getName());
                    Class<?> type = field.getType();
                    if (!takeOnlyPrimitives || isPrimitive(type))
                    {
                        Expression expression = ExpressionBuilderHelper.buildExpression(field);
                        expressions.add(expression);
                    }
                }
            }

            ClassMembers classMembers = new ClassMembers(expressions);
            analyticCache.put(name, classMembers);
        }
        else
        {
            LOG.trace("Bean named {} is already in the cache, so will not scan again", name);
        }

    }

    private static boolean isPrimitive(Class<?> clazz)
    {
        return (clazz.isPrimitive() || clazz.equals(Boolean.class) || clazz.equals(Character.class)
                || clazz.equals(Byte.class) || clazz.equals(Short.class)
                || clazz.equals(Integer.class) || clazz.equals(Long.class)
                || clazz.equals(Float.class) || clazz.equals(Double.class) || clazz
                    .equals(String.class))
                || clazz.isArray()
                && !(clazz.equals(Void.class) || clazz.getName().equals("void"));
    }

    private static Map<String, Object> fetchValues(Object bean)
    {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        String name = bean.getClass().getCanonicalName();
        if (StringUtils.isBlank(name))
            name = bean.getClass().getName();
        if (!analyticCache.containsKey(name))
        {
            LOG.trace("Bean {} is not found in the cache, so will update the cache", bean
                    .getClass().getCanonicalName());
            updateAnalyticCache(bean.getClass());
        }

        ClassMembers classMembers = analyticCache.get(name);
        Set<Expression> expressions = classMembers.getExpressions();
        for (Expression expression : expressions)
        {
            if (expression != null)
            {
                String key = expression.getName();
                Object value = expression.evaluate(bean);
                if (value != null)
                {
                    if (isPrimitive(value.getClass()))
                    {
                        valueMap.put(key, value);
                    }
                    else if (value instanceof Collection<?>)
                    {
                        List<?> listValue = getValueForCollection((Collection<?>) value, 0);
                        valueMap.put(key, listValue);
                    }
                    else if (value instanceof Map<?, ?>)
                    {
                        Object mapValue = getValueForCollection((Map<?, ?>) value);
                        valueMap.put(key, mapValue);
                    }
                    else
                    {
                        Map<String, Object> fetchValues = fetchValues(value);
                        valueMap.putAll(fetchValues);
                    }
                }
                else
                    valueMap.put(key, value);
            }
        }
        return valueMap;
    }

    private static List<?> getValueForCollection(Collection<?> collection, int depth)
    {
        depth = depth + 1;
        if (depth > 3)
            return null;
        List<Object> list = new ArrayList<Object>();
        if (collection != null && !collection.isEmpty())
        {
            Iterator<?> iterator = collection.iterator();
            while (iterator.hasNext())
            {
                Object next = iterator.next();
                if (next != null)
                {
                    if (isPrimitive(next.getClass()))
                    {
                        list.add(next);
                    }
                    else if (next instanceof Collection<?>)
                    {
                        getValueForCollection((Collection<?>) next, depth);
                    }
                    else if (next instanceof Map<?, ?>)
                    {
                        getValueForCollection((Map<?, ?>) next);
                    }
                    else
                    {
                        Map<String, Object> values = fetchValues(next);
                        if (values != null && !values.isEmpty())
                            list.add(values);
                    }
                }
                else
                    list.add(next);
            }
        }
        return list;

    }

    private static Object getValueForCollection(Map<?, ?> value)
    {
        Set<?> entrySet = value.entrySet();
        Iterator<?> iterator = entrySet.iterator();
        while (iterator.hasNext())
        {
            Entry<?, ?> next = (Entry<?, ?>) iterator.next();
            if (isPrimitive(next.getKey().getClass()) && isPrimitive(next.getValue().getClass()))
                return value;
        }
        return value.toString();
    }

    public static void start(String name)
    {
        try
        {
            AnalyticContextInterface.beginTransaction(name);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void start()
    {
        start(null);
    }

    public static void update(Object bean)
    {
        try
        {
            if (bean != null)
            {
                Map<String, Object> values = fetchValues(bean);
                AnalyticContextInterface.updateAllValue(values);
                return;
            }
            LOG.trace("bean is null so not updating");
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Integer value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, String value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Boolean value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Long value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Character value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Byte value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Short value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Float value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static void update(String key, Double value)
    {
        try
        {
            AnalyticContextInterface.updateValue(key, value);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
    }

    public static Map<String, Object> end()
    {
        return end(null);
    }

    public static Map<String, Object> end(Throwable e)
    {
        Map<String, Object> endpointData = null;
        try
        {
            endpointData = AnalyticContextInterface.endTransaction(e);
            String transactionName = null;
            if (endpointData != null)
            {
                transactionName = (String) endpointData.get(AnalyticContextInterface.TX_NAME);
            }
            endpoint.invoke(transactionName, endpointData);
        }
        catch (Throwable th)
        {
            LOG.warn("Analytic Exception: {}", th.getMessage(), th);
        }
        return endpointData;
    }

    static final class ClassMembers
    {
        private Set<Expression> expressions;

        public ClassMembers(Set<Expression> expressions)
        {
            super();
            this.expressions = expressions;
        }

        public Set<Expression> getExpressions()
        {
            return expressions;
        }

        @Override
        public String toString()
        {
            StringBuilder sb =
                    new StringBuilder("ClassMembers: ").append("\n\t\t\t\t\tExpressions:\n");
            for (Expression expression : expressions)
                sb.append("\t\t\t\t\t\t").append(expression).append("\n");
            return sb.toString();
        }

    }

}
