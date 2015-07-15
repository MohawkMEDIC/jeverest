/* 
 * Copyright 2012 Mohawk College of Applied Arts and Technology
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
 * Date: 11-08-2012
 */
package org.marc.everest.formatters.interfaces;

/**
 * Identifies a class that renders datatypes
 * 
 * Allows users to access the supported properties of a datatype formatter
 * at runtime 
 */
public interface IDatatypeStructureFormatter extends IStructureFormatter {

	/**
	 * Gets the supported properties for the specified datatype
	 */
	Iterable<String> getSupportedPropertyNames(Class<?> type);
}
