Annotation Analytic Framework
==================
This module aims to provide an open solution for analysing data in any process. It will provide the analytic data for the underlying Analytic Processor say ElasticSearch and Kibana.

## Three steps to Analytic ##
1. Preparing the module
2. Decide the data to be analysed
3. Analyse

###1. Preparing the module ###
Analytic Framework uses AOP for starting and end transactions. To use that you need to compile your code with aspectj compiler. You can use the aspectj-maven-plugin from org.codehaus.mojo
```sh
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.6</version>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <source>1.6</source>
        <target>1.6</target>
        <complianceLevel>1.6</complianceLevel>
        <aspectDirectory>${project.build.sourceDirectory}/src/main/java</aspectDirectory>
        <showWeaveInfo>true</showWeaveInfo>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>com.github</groupId>
                <artifactId>annotation-analytic-core</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
</plugin>

```


Add the required module in your classpath from the list below:

 1. `annotation-analytic-core`
    This module includes all the core classes. Adding just the dependency of this module won't suffice as it does not include Endpoint. Modules listed below are dependent on this module.
 2. `annotation-analytic-mvel`
    This module allows you to use `MvEL` expression as the
    way to get analytic data
 2. `annotation-analytic-spel`
    This module allows you to use `SpEL` expression as the way to get analytic data
 3. `annotation-analytic-json1`
    This module provides LogEndpoint which uses jackson dependency from `codehaus`
 4. `annotation-analytic-json2`
    This module provides LogEndpoint which uses jackson dependency from `fasterxml`

```sh
<!-- I want LogEndpoint from fasterxml and use MvEL as expression Language -->
<dependency>
	<groupId>com.github</groupId>
	<artifactId>annotation-analytic-json2</artifactId>
	<version>0.0.1</version>
</dependency>
<dependency>
	<groupId>com.github</groupId>
	<artifactId>annotation-analytic-mvel</artifactId>
	<version>0.0.1</version>
</dependency>
```


###2. Decide the data to be analysed ###
Annotate the type with `@AnalysedEntity` and provide a name to it. Additionally annotate the members which you want to analyse with `@Analysed` and provide name to it (default name will be name of the class member).

For any type annotated with `@AnalysedEntity` with no member annotated with `@Analysed` the service will take all the public primitives members(including their Wrapper classes)
```sh
@AnalysedEntity
public class ChargingDetails
{
    @Analysed(name = "msisdn")
    public String msisdn;
    
    @Analysed(name = "operator")
    public String operator;
    
    public int amountToBeCharged;
    
    @Analysed(name = "chargedAmount")
    public int getAmountCharged()
	{
	    return amountToBeCharged * factor;
	}
    
    @Analysed(name = "value", lang = ExprLang.MVEL, expr = "value * factor")
    public int value;
}
```
###3. Analyse ###
Annotate the process/method where you want to analyse you data with `@AnalysedTransaction` and provide a name to this transaction (default name will be `defaultTransaction`).
Call method `AnalyticService.update(bean);` when your analytic bean is ready to be analysed.
```java
@AnalysedTransaction(name = "charger")
public void debitBalance(String msisdn, String operator, int amountToBeCharged, int validity)
{
    ....
    ChargingDetails chargeDetails = new ChargingDetails();
    ....
    ....
    AnalyticService.update(chargeDetails);
	....
	return;
}
```
One can also start a new transaction within the code by calling `AnalyticService.start("transactionName");` api and end the transaction by `AnalyticService.end();`. This way of starting a transaction is discouraged but if you still want to use make sure you call `AnalyticService.end();` else it would lead to memory leaks and making your Application going `OutOfMemory`. We have addressed this problem by calling the *end* method if your thread start a new transaction with same name. We will call *end* method till we that transaction is not in the stack.
```sh
public void computeRevenue()
{
    ....
    ....
    AnalyticService.start("revenue");
    ....
    AnalyticService.update(bean);
    ....
    AnalyticService.update(bean2);
    ....
    AnalyticService.end();
    ....
    ....
}
```

