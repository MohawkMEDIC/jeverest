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
 * Date: 11-09-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marc.everest.datatypes.interfaces.IPredicate;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * Datatype formatter parse result
 */
public class DatatypeFormatterParseResult implements IFormatterParseResult {

	// Backing field for details
	private List<IResultDetail> m_details = new ArrayList<IResultDetail>(10);

	// Backing field for compatibility mode
	private R1FormatterCompatibilityMode m_hostCompatibilityMode = R1FormatterCompatibilityMode.Universal;
	
	// Backing field for validate conformance
	private boolean m_validateConformance = true;
	
	// Backing field for the structure
	private IGraphable m_structure;
	
	// backing field for result code
	private ResultCodeType m_resultCode;
	
	
	/**
	 * Gets a value indicating whether the host formatter is validating conformance
	 */
	public boolean getValidateConformance() { return this.m_validateConformance; }

	/**
	 * Gets a value indicating the compatibility mode for the formatter
	 * @return
	 */
	public R1FormatterCompatibilityMode getCompatibilityMode() { return this.m_hostCompatibilityMode; }

	/**
	 * Package scoped constructor
	 */
	DatatypeFormatterParseResult(R1FormatterCompatibilityMode compatibilityMode, boolean validatingConformance) {
		this.m_validateConformance = validatingConformance;
		this.m_hostCompatibilityMode = compatibilityMode;
	}
	
	/**
	 * Package scoped constructor
	 */
	DatatypeFormatterParseResult(R1FormatterCompatibilityMode compatibilityMode, boolean validatingConformance, ResultCodeType result, Collection<IResultDetail> details)
	{
		this(compatibilityMode, validatingConformance);
		this.m_details = new ArrayList<IResultDetail>(details);
		this.m_resultCode = result;
	}
	
	/**
	 * Add a result detail item to the result collection
	 */
	public void addResultDetail(IResultDetail detail)
	{
		if(detail != null)
			this.m_details.add(detail);
	}
	/**
	 * Add a result detail item to the result collection
	 */
	public void addResultDetail(Iterable<IResultDetail> detail)
	{
		for(IResultDetail dtl : detail)
			this.m_details.add(dtl);
	}	
	/**
	 * Removes all result details matching the predicate
	 * @param removeFilter
	 */
	public void removeResultDetail(IPredicate<IResultDetail> removeFilter)
	{
		if(removeFilter == null)
			throw new IllegalArgumentException("removeFilter");
		for(int i = this.m_details.size() - 1; i >= 0; i--)
			if(removeFilter.match(this.m_details.get(i)))
				this.m_details.remove(i);
	}

	/**
	 * Get results of formatting
	 */
	@Override
	public Iterable<IResultDetail> getDetails() {
		return this.m_details;
	}

	/**
	 * Get overall code of formatting
	 */
	@Override
	public ResultCodeType getCode() {
		return this.m_resultCode;
	}

	/**
	 * Set the code of the parse
	 */
	public void setCode(ResultCodeType code)
	{
		this.m_resultCode = code;
	}
	
	/**
	 * Get structure from formatting
	 */
	@Override
	public IGraphable getStructure() {
		return this.m_structure;
	}

	/**
	 * Set the structure
	 */
	void setStructure(IGraphable structure)
	{
		this.m_structure = structure;
	}

}
