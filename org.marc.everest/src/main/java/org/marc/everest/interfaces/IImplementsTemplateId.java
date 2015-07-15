/* 
 * Copyright 2008-2014 Mohawk College of Applied Arts and Technology
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
 * Date: 07-20-2014
 */
package org.marc.everest.interfaces;

import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.LIST;

/**
 * Represents a class which contains a list of template identifiers
 */
public interface IImplementsTemplateId extends IGraphable {

	/**
	 * Get the templateId of the object 
	 */
	LIST<II> getTemplateId();
	
	/**
	 * Sets the templateId
	 */
	void setTemplateId(LIST<II> templateId);
	
}
