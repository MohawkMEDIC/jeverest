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
package org.marc.everest.connectors.interfaces;

import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * Represents the outcome of a receive operation.
 * <p>This interface is implemented by specific connector implementations and may contain
 * additional data that is relevant to that particular connector</p>
 */
public interface IReceiveResult {

	/**
	 * Get the outcome code of the receive operation
	 */
	ResultCodeType getCode();
	
	/**
	 * Get the details from the formatting of the result message
	 */
	Iterable<IResultDetail> getDetails();
	
	/**
	 * Get the structure that was parsed from "the wire"
	 */
	IGraphable getStructure();
	
}
