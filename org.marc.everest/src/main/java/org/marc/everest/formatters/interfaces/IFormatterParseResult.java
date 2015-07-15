/* 
 * Copyright 2008-2011 Mohawk College of Applied Arts and Technology
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
 * Date: 07-21-2011
 */
package org.marc.everest.formatters.interfaces;

import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * Represents a formatter parse result
 */
public interface IFormatterParseResult {

	/**
	 * Gets an array of result details that represent the issues 
	 * detected from the parse operation
	 */
	Iterable<IResultDetail> getDetails();
	/**
	 * Get the code that represents the overall outcome of the
	 * formatting parse result.
	 */
	ResultCodeType getCode();
	/**
	 * Gets the structure that was interpreted from the 
	 * parse operation
	 * @return
	 */
	IGraphable getStructure();
	
}
