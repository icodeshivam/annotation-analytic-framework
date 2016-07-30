/**
 * 
 */
package com.github.annotation.analytic.core.utils;

import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shivam
 */
public final class AnalyticConfigReader
{
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticConfigReader.class);
    
    private static final String PROPERTY_FILENAME = "analytic.properties";
    private static final String ANNOTATION_PACKAGE_SCAN = "annotation-package-scan";
    private static final String EXPRESSION_BUILDER_PACKAGE_SCAN = "expression-builder-package-scan";
    private static final String ENDPOINT_CLASS = "endpoint-class";
    
    private static final String DEFAULT_EXPRESSION_BUILDER_PACKAGE_SCAN = "com.github.annotation.analytic.core.expression";
    private static final String DEFAULT_ENDPOINT_CLASS = "com.github.annotation.analytic.core.endpoint.LogEndpoint";
    
    static
    {
        init();
    }

    private static void init()
    {
        String fileName = null;
        try
        {
            fileName = System.getProperty(PROPERTY_FILENAME) == null
                    ? PROPERTY_FILENAME
                    : System.getProperty(PROPERTY_FILENAME);
            LOG.trace("Reading properties from file {}", fileName);
    
            PropertiesConfiguration config = new PropertiesConfiguration(fileName);
            
            // Read Annotation Packages
            String[] annotationPackages = config.getStringArray(ANNOTATION_PACKAGE_SCAN);
            if (annotationPackages.length != 0)
            {
	            for (String annotationPackage : annotationPackages)
	                AnalyticConfig.annotationPackages.add(annotationPackage);
            }
            
            // Read Expression Builder Packages
            String[] expressionBuilderPackages = config.getStringArray(EXPRESSION_BUILDER_PACKAGE_SCAN);
            if (expressionBuilderPackages.length == 0 || (expressionBuilderPackages.length == 1 && StringUtils.isBlank(expressionBuilderPackages[0])))
            {
                AnalyticConfig.expressionBuilderPackages.add(DEFAULT_EXPRESSION_BUILDER_PACKAGE_SCAN);
            }
            else
            {
                for (String expressionBuilderPackage : expressionBuilderPackages)
                    AnalyticConfig.expressionBuilderPackages.add(expressionBuilderPackage);
            }
            
            // Read Endpoint Def Packages
            String endpointClassName = config.getString(ENDPOINT_CLASS, DEFAULT_ENDPOINT_CLASS);
            AnalyticConfig.endpointClass = endpointClassName;
        }
        catch (Exception e)
        {
            LOG.warn("Could not able to initialize property file: {}, so I will take default values", fileName);
            AnalyticConfig.endpointClass = DEFAULT_ENDPOINT_CLASS;
            AnalyticConfig.expressionBuilderPackages.add(DEFAULT_EXPRESSION_BUILDER_PACKAGE_SCAN);
        }
    }

    public static Set<String> getAnnotationPackages()
    {
        return AnalyticConfig.annotationPackages;
    }

    public static Set<String> getExpressionBuilderPackage()
    {
        return AnalyticConfig.expressionBuilderPackages;
    }

    public static String getEndpointClassName()
    {
        return AnalyticConfig.endpointClass;
    }
}
