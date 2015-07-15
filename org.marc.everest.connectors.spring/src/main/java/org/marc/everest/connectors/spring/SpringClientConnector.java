/**
 * Copyright 2008-2013 Mohawk College of Applied Arts and Technology
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 * 
 * User: Justin Fyfe
 * Date: 12-14-2012
 */
package org.marc.everest.connectors.spring;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.marc.everest.annotations.Structure;
import org.marc.everest.connectors.ConnectorUtil;
import org.marc.everest.connectors.interfaces.IFormattedConnector;
import org.marc.everest.connectors.interfaces.IReceiveResult;
import org.marc.everest.connectors.interfaces.ISendReceiveConnector;
import org.marc.everest.connectors.interfaces.ISendResult;
import org.marc.everest.exceptions.ConnectorException;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IStructureFormatter;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.ResultDetail;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Represents a client connector capable of communicating via Spring.
 * <p>This class provides a wrapper to the native Spring framework marshallers and 
 * is intended to be comaptible with the .NET version of the Everest framework.</p>
 * <p>This class also allows developers to use the traditional Everest connector pattern
 * with Spring connectors. This can be helpful when implementing applications that use configuration
 * systems to determine the connector at runtime.<p>
 * The following example illustrates the use of the SpringClientConnector to send an instance:
 * <pre>
 * try(XmlIts1Formater fmtr = new XmlIts1Formatter())
 * {
 * 	PRPA_IN101103CA instance = new PRPA_IN101103CA();
 * 
 * 	// Add the R1 formatter as a helper
 * 	fmtr.getGraphAides().add(new DatatypeFormatter());
 * 	
 *  // Instantiate the spring client connector
 *  ApplicationContext appContext = new ClassPathXmlApplicationContext("sample.context.xml");
 *	SpringClientConnector connector = new SpringClientConnector(appContext);
 *  // Set the formatter
 *  connector.setFormatter(fmtr);
 *  // Opent the connector (good practice)
 *  connector.open();
 *  // Send the message to the remote host
 *  SpringConnectorSendResult sendResult = connector.send(instance);
 *  // Pop the response off the receive queue
 *  SpringConnectorReceiveResult receiveResult = connector.receive(receiveResult);
 *  
 *  // Output the structure to the console
 *  fmtr.graph(System.out, receiveResult.getStructure());
 *  
 *  connector.close();
 * }
 * catch(Exception e)
 * {
 * 	e.printStackTrace();
 * }
 * </pre> 
 */
public class SpringClientConnector implements ISendReceiveConnector, IFormattedConnector {

	// Backing field for formatter
	private IXmlStructureFormatter m_formatter;
	
	// Connection string
	private String m_connectionString;
	
	// The client template
	private WebServiceTemplate m_wsTemplate;
	
	// Used for send/receive operations.
	private HashMap<ISendResult, IReceiveResult> m_messageCache = new HashMap<ISendResult, IReceiveResult>();
	
	// Execution context 
	private ApplicationContext m_context;
	
	/**
	 * Creates a new instance of the SpringClientConnector using the specified execution context class
	 */
	public SpringClientConnector(ApplicationContext context)
	{
		this.m_context = context;
	}
	/**
	 * Create a new instance of hte spring client connector class
	 */
	public SpringClientConnector(ApplicationContext context, String connectionString)
	{
		this(context);
		this.m_connectionString = connectionString;
	}
	
	/**
	 * Send data
	 */
	@Override
	public ISendResult send(IGraphable data) {
		return this.send(data, null);
	}
	
	/**
	 * Send the data
	 */
	public ISendResult send(IGraphable data, WebServiceMessageCallback callback) {
		
		if(!this.isOpen())
			throw new ConnectorException("Connector has not been opened");
		
		try
		{
			// Send
			Structure struct = data.getClass().getAnnotation(Structure.class);
			IGraphable response = (IGraphable)this.m_wsTemplate.marshalSendAndReceive(data, callback);
			
			// Collection of parse/send errors
			IFormatterGraphResult graphResult = null;
			if(this.m_wsTemplate.getMarshaller() instanceof EverestMarshaller)
				graphResult = ((EverestMarshaller)this.m_wsTemplate.getMarshaller()).popGraphResult(data);
			IFormatterParseResult parseResult = null;
			if(this.m_wsTemplate.getUnmarshaller() instanceof EverestUnmarshaller)
				parseResult = ((EverestUnmarshaller)this.m_wsTemplate.getUnmarshaller()).popParseResult(response);
			
			// Now construct the Send/Receive results
			SpringConnectorSendResult sendResult = new SpringConnectorSendResult(graphResult.getCode(), graphResult.getDetails());
			SpringConnectorReceiveResult recvResult = new SpringConnectorReceiveResult(parseResult.getCode(), parseResult.getDetails(), parseResult.getStructure());
			
			// Push the result
			this.m_messageCache.put(sendResult, recvResult);
			return sendResult;
		}
		catch(Exception e)
		{
			return new SpringConnectorSendResult(ResultCodeType.Error, Arrays.asList(new IResultDetail[] {
				new ResultDetail(ResultDetailType.ERROR, e.getMessage(), e)	
			}));
		}
	}

	/**
	 * Sets the connection string
	 * 
	 * <p>Connection String options are:</p>
	 * <ul>
	 * 	<li><b>templateBeanName</b> - The name of the template bean</li>
	 * 	<li><b>defaultUri</b> - The default URI of the service, note the configuration file trumps this value</li>
	 * </ul>
	 */
	@Override
	public void setConnectionString(String connectionString) {
		this.m_connectionString = connectionString;
	}

	/**
	 * Gets the connection string
	 */
	@Override
	public String getConnectionString() {
		return this.m_connectionString;
	}
 
	@Override
	public void open() {
		
		// Connection string validation
		if(this.m_connectionString == null)
			throw new ConnectorException("Connection string missing");
		else if(this.m_formatter == null)
			throw new ConnectorException("This connection requires a formatter");
		else if(this.m_context == null)
			throw new ConnectorException("Unspecified application context");
		// Get parameters
		HashMap<String, List<String>> parameters = ConnectorUtil.parseConnectionString(this.m_connectionString);
		
		// Verify others
		if(parameters.get("templatebeanname") == null)
			throw new ConnectorException("templateBeanName parameter must be present in the ConnectionString");
		
		// Load context file
		try
		{
			this.m_wsTemplate = (WebServiceTemplate)this.m_context.getBean(parameters.get("templatebeanname").get(0));
			
			// Set marshallers and unmarshallers
			if(this.m_wsTemplate.getMarshaller() == null)
				this.m_wsTemplate.setMarshaller(new EverestMarshaller(this.m_formatter));
			if(this.m_wsTemplate.getUnmarshaller() == null)
				this.m_wsTemplate.setUnmarshaller(new EverestUnmarshaller(this.m_formatter));
			
			// Set the default URI if not set
			if(parameters.get("defaulturi") != null && this.m_wsTemplate.getDefaultUri() == null)
				this.m_wsTemplate.setDefaultUri(parameters.get("defaulturi").get(0));
		}
		catch(Exception e)
		{
			throw new ConnectorException("Unable to initialize Sping connection", e);
		}
		
	}

	/**
	 * Close the connection
	 */
	@Override
	public void close() {
		if(this.isOpen())
			this.m_wsTemplate = null;
	}

	/**
	 * True when the connection is open
	 */
	@Override
	public boolean isOpen() {
		return this.m_wsTemplate != null;
	}

	/**
	 * Get the formatter
	 */
	@Override
	public IStructureFormatter getFormatter() {
		return this.m_formatter;
	}

	/**
	 * Set the formatter
	 */
	@Override
	public void setFormatter(IStructureFormatter fmtr) {
		if(!(fmtr instanceof IXmlStructureFormatter))
			throw new IllegalArgumentException("fmtr must be of IXmlStructureFormatter");
		this.m_formatter = (IXmlStructureFormatter)fmtr;
	}

	/**
	 * Receive a result
	 * <p>This method actually retrieves a result that has already been received by the underlying Spring framework</p>
	 * <p>Returns null if no results is present</p> 
	 */
	@Override
	public IReceiveResult receive(ISendResult correlate) {
		
		if(!this.isOpen())
			throw new ConnectorException("Connector not open");
		
		// Just return the key
		return this.m_messageCache.get(correlate);
	}

}
