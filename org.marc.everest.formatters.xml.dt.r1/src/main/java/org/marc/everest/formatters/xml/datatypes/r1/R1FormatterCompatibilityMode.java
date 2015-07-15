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

/**
 * Identifies the compatibility modes that the R1 formatter can operate
 */
public enum R1FormatterCompatibilityMode {

	/**
	 * Forces the datatype formatter to operate in CA mode
	 */
	Canadian,
	/**
	 * Forces the datatype formatter to operate in UV mode
	 */
	Universal,
	/**
	 * Forces the datatype formatter to operate in CDA mode
	 */
	ClinicalDocumentArchitecture
}
