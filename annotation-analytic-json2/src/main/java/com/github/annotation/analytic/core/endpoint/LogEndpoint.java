/**
 * 
 */
package com.github.annotation.analytic.core.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.endpoint.Endpoint;

/**
 * @author shivam
 *
 */
public class LogEndpoint implements Endpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(LogEndpoint.class);
    
    private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
            TimeZone.getTimeZone("UTC"));
    
    private static final String PROPERTY_FILENAME = "analytic.properties";

    private static final String LOG_ENDPOINT_LOGGER_NAME = "log-endpoint-logger-name";
    private static final String DEFAULT_LOG_ENDPOINT_LOGGER_NAME = "analyticLogger";

	private static final String TIMESTAMP = "@timestamp";
    
    private Map<String,Logger> loggers = new HashMap<String, Logger>();
    
    String defaultLogger = null;
    
    @Override
    public void init()
    {
        String fileName = System.getProperty(PROPERTY_FILENAME) == null
                ? PROPERTY_FILENAME
                : System.getProperty(PROPERTY_FILENAME);
        LOG.trace("Reading properties from file {}", fileName);
        try
        {
            PropertiesConfiguration config = new PropertiesConfiguration(fileName);
            defaultLogger = config.getString(LOG_ENDPOINT_LOGGER_NAME, DEFAULT_LOG_ENDPOINT_LOGGER_NAME);
            LOG.trace("Logger for LogEndpoint: {}", defaultLogger);
        }
        catch (Exception e)
        {
            LOG.warn("Error in initializing LogEndpoint, will take the default Logger");
            defaultLogger = DEFAULT_LOG_ENDPOINT_LOGGER_NAME;
        }
    }

    @Override
    public void invoke(String transactionName, Map<String, Object> endpointData)
    {
    	endpointData.put(TIMESTAMP, ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(System.currentTimeMillis()));
        String logger = transactionName;
        if (logger.equals("defaultTransaction")) {
            logger =  defaultLogger;
        }
        Logger logg = loggers.get(logger);
        if (logg == null) {
            logg = LoggerFactory.getLogger(logger);
            loggers.put(logger, logg);
        }
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            logg.info(mapper.writeValueAsString(endpointData));
        }
        catch (Exception e)
        {
            LOG.trace(e.getMessage(), e);
        }
    }

}
