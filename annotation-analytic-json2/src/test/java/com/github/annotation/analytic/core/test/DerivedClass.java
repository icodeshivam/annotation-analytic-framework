/**
 * 
 */
package com.github.annotation.analytic.core.test;

import org.junit.Ignore;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;

/**
 * @author shivam
 *
 */
@AnalysedEntity
@Ignore
class DerivedClass extends BaseClass
{
    public String derivedClassString;
}
