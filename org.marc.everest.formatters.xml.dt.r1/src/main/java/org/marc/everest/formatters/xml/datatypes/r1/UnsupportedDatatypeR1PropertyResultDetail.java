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
 * Date: 11-10-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1;

import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.UnsupportedDatatypePropertyResultDetail;

/**
 * Identifies that a property populated within the datatype wasn't rendered to the 
 * output stream by the R1 renderer.
 */
public class UnsupportedDatatypeR1PropertyResultDetail extends UnsupportedDatatypePropertyResultDetail {

	/**
	 * Creates a new instance of the unsupported datatype R1 property result detail class
	 */
	public UnsupportedDatatypeR1PropertyResultDetail() {
		super();
	}

	/**
	 * Creates a new instance of the unsupported datatype R1 property result detail with the specified 
	 * type, datatypename and property name 
	 */
	public UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType type,
			String datatypeName, String propertyName, String location) {
		super(type, datatypeName, propertyName, location);
	}

}
