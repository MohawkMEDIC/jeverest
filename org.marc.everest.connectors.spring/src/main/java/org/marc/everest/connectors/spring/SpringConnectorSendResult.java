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

import org.marc.everest.connectors.interfaces.ISendResult;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * Represents a send result for a result received from Spring
 */
public class SpringConnectorSendResult implements ISendResult {

	// Backing field for result
	private ResultCodeType m_result;
	// Backing field for details
	private Iterable<IResultDetail> m_details;
	
	/**
	 * Package scoped ctor
	 */
	SpringConnectorSendResult(ResultCodeType result, Iterable<IResultDetail> details) {
		this.m_result = result;
		this.m_details = details;
		
	}
	
	/**
	 * Get the outcome of send operation
	 */
	@Override
	public ResultCodeType getCode() {
		return this.m_result;
	}

	/**
	 * Get the details of receive operation
	 */
	@Override
	public Iterable<IResultDetail> getDetails() {
		return this.m_details;
	}

	

}
