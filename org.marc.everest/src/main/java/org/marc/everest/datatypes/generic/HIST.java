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
 * Date: 10-01-2012
 */
package org.marc.everest.datatypes.generic;

import java.util.Arrays;

import org.marc.everest.annotations.*;
import org.marc.everest.interfaces.IGraphable;


/**
 * Represents a collection of items that are ordered based on their history
 */
@Structure(name = "HIST", structureType = StructureType.DATATYPE)
public class HIST<T extends IGraphable> extends LIST<T> {

	/**
	 * Creates a new instance of HIST 
	 */
	public HIST() { super(); }
	/**
	 * Creates a new instance of HIST populated with the specified items
	 * @param items The initial list of items to populate the HIST
	 */
	public HIST(Iterable<? extends IGraphable> items) { super(items); }
	
	/**
	 * Creates a new instance of HIST with the specified seeded items
	 * @param items The initial set of items to seed the HIST with
	 */
	public static <T extends IGraphable> HIST<T> createHIST(T... items)
	{
		return new HIST<T>(Arrays.asList(items));
	}
	
}
