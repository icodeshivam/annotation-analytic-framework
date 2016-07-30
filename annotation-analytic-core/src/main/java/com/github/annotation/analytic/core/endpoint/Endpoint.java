/**
 * 
 */
package com.github.annotation.analytic.core.endpoint;

import java.util.Map;

/**
 * @author shivam
 *
 */
public interface Endpoint
{
    void init();
    
    void invoke(String transactionName, Map<String, Object> endpointData);
    
}
