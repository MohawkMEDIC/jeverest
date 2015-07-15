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

import org.marc.everest.connectors.interfaces.IReceiveResult;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * Represents a receive result from the Spring connector
 */
public class SpringConnectorReceiveResult implements IReceiveResult {

	
	// Backing field for code
	private ResultCodeType m_result;
	// Backing field for details
	private Iterable<IResultDetail> m_details;
	// Backing field for structure
	private IGraphable m_structure;
	
	/**
	 * Package scoped ctor
	 */
	SpringConnectorReceiveResult(ResultCodeType code, Iterable<IResultDetail> details, IGraphable structure)
	{
		this.m_result = code;
		this.m_details = details;
		this.m_structure = structure;
	}
	/**
	 * Get the codified result
	 */
	@Override
	public ResultCodeType getCode() {
		return this.m_result;
	}

	/**
	 * Get the details of the formatting operation
	 */
	@Override
	public Iterable<IResultDetail> getDetails() {
		return this.m_details;
	}

	/**
	 * Get the structure that was processed
	 */
	@Override
	public IGraphable getStructure() {
		return this.m_structure;
	}

	
}
