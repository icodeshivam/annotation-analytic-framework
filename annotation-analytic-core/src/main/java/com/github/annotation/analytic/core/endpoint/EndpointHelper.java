package com.github.annotation.analytic.core.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.annotation.analytic.core.utils.AnalyticConfigReader;
/**
 * @author shivam
 *
 */
public class EndpointHelper
{
    private static final Logger LOG = LoggerFactory.getLogger(EndpointHelper.class);
    
    private static Endpoint endpoint;
    
    static
    {
        try
        {
            init();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void init() throws Exception
    {
        String endpointClassName = AnalyticConfigReader.getEndpointClassName();
        LOG.trace("Initialising Endpoint {}", endpointClassName);
        Class<Endpoint> clazz = (Class<Endpoint>) Class.forName(endpointClassName);
        endpoint = clazz.newInstance();
        endpoint.init();
    }
    
    public static Endpoint getEndpoint()
    {
        return endpoint;
    }
}
