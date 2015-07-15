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
 * Date: 01-07-2013
 */
package org.marc.everest.datatypes.generic;

import java.util.Arrays;
import java.util.Collection;

import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;

/**
 * Non-parametric probability distribution
 */
@Structure(name = "NPPD", structureType = StructureType.DATATYPE)
public class NPPD<T extends IAny> extends SET<UVP<T>> {

	/**
	 * Creates a new instance of the Non-Paramtric probability distribution
	 */
	public NPPD() { super(); }
	/**
	 * Creates a new instance of the Non-Paramtric probability distribution
	 */
	public NPPD(Iterable<? extends IGraphable> items)
	{
		super(items);
	}

	/**
	 * Creates a new instance of the non-parametric probability distribution class 
	 */
	public static <T extends IAny> NPPD<T> createNPPD(UVP<T>... items)
	{
		return new NPPD<T>(Arrays.asList(items));	
	}

	
}
