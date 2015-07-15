/* 
 * Copyright 2011-2012 Mohawk College of Applied Arts and Technology
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
 * Date: 27-11-2012
 */
package org.marc.everest.formatters.xml.its1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * Represents a formatter parse result from the XML ITS1 formatter
 */
public class XmlIts1FormatterParseResult implements IFormatterParseResult {
	
	// Backing field for code
	private ResultCodeType m_code = ResultCodeType.Accepted;
	// Backing field for results
	private List<IResultDetail> m_results = new ArrayList<IResultDetail>();
	// Backing field for structure
	private IGraphable m_structure;
	/**
	 * Creates a new instance of the XmlIts1FormatterGraphResult 
	 */
	XmlIts1FormatterParseResult(ResultCodeType code, Collection<IResultDetail> results)
	{
		this.m_code = code;
		if(results != null)
			this.m_results = new ArrayList<IResultDetail>(results);
	}
	
	/**
	 * Adds a result detail to the current result set
	 * @param dtl
	 */
	public void addResultDetail(IResultDetail dtl)
	{
		this.m_results.add(dtl);
	}
	
	/**
	 * Add all result details from the other iterable object
	 */
	public void addResultDetail(Iterable<IResultDetail> other)
	{
		if(other != null)
			for (IResultDetail irdtl : other) {
				this.m_results.add(irdtl);
			}
	}
	
	/**
	 * Gets the result details of the format operation
	 */
	@Override
	public Iterable<IResultDetail> getDetails() {
		return this.m_results;
	}

	/**
	 * Gets the result code of the format operation 
	 */
	@Override
	public ResultCodeType getCode() {
		return this.m_code;
	}

	/**
	 * Sets the current value of code
	 */
	public void setCode(ResultCodeType value)
	{
		this.m_code = value;
	}

	/**
	 * Set the structure
	 */
	public void setStructure(IGraphable structure)
	{
		this.m_structure = structure;
	}
	
	/**
	 * Get the structure parsed
	 */
	@Override
	public IGraphable getStructure() {
		return this.m_structure;
	}

}
