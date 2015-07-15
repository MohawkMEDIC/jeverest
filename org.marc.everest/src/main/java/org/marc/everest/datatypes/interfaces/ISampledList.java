/* 
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
 * Date: 10-02-2012
 */
package org.marc.everest.datatypes.interfaces;

import java.util.Collection;

import org.marc.everest.datatypes.INT;


/**
 * Represents a list of values whereby the list contains a series of deltas between
 * an origin
 */
public interface ISampledList {

	/**
	 * Gets the origin quantity
	 */
	IQuantity getOrigin();

	/**
	 * Sets the origin quantity
	 */
	void setOrigin(IQuantity value);

	/**
	 * Gets the scale
	 */
	IQuantity getScale();
	
	/**
	 * Sets the scale
	 */
	void setScale(IQuantity value);

	/**
	 * Gets the items from the sampled list
	 */
	Collection<INT> getItems();

}
