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
 * Date: 01-04-2013
 */
package org.marc.everest.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.interfaces.IUnitConverter;

/**
 * Represents a unit converter that is capable of converting between various Si Units
 *
 */
public class SimpleSiUnitConverter implements IUnitConverter {

	// SI Prefix values
	private static final HashMap<String, Double> s_siPrefixes = new HashMap<String, Double>();
	// SI Units
	private static final List<String> s_siUnits = new ArrayList<String>();

	// Prefix
	private static final int PREFIX_PART = 0;
	// Unit
	private static final int UNIT_PART = 1;
	
	/**
	 * Creates a new instance of the SiUnitConverter
	 */
	static
	{
		s_siPrefixes.put("u", 0.000001d);
		s_siPrefixes.put("m", 0.001d);
		s_siPrefixes.put("c", 0.01d);
		s_siPrefixes.put("d", 0.1d);
		s_siPrefixes.put("", 1d);
		s_siPrefixes.put("h", 100d);
		s_siPrefixes.put("k", 1000d);
		s_siPrefixes.put("M", 1000000d);
		s_siPrefixes.put("G", 1000000000d);
		s_siPrefixes.put("T", 1000000000000d);

		s_siUnits.addAll(Arrays.asList(
				new String[] {
			        "m", // meter
			        "L", // liter
			        "mol", // mol
			        "g", // gram
			        "Pa", // Pascal
			        "K", // Kelvin
			        "N", // Newton
			        "J", // Joule
			        "V", // Volt
			        "W", // Watt
			        "lm"
				}));

	}
	
	/**
	 * Determine if a conversion is possible using this converter
	 */
	@Override
	public boolean canConvert(PQ from, String unitTo) {
        if (from == null || from.isNull())
            throw new IllegalArgumentException("from");
        else if (unitTo == null)
            throw new IllegalArgumentException("unitTo");

        boolean isSiMeasure = this.getSiData(unitTo, UNIT_PART) != null && this.getSiData(from.getUnit(), UNIT_PART) != null;

        if (isSiMeasure)
            isSiMeasure = this.getSiData(unitTo, UNIT_PART).equals(this.getSiData(from.getUnit(), UNIT_PART));
        return isSiMeasure;
	}
	
	/**
	 * Get Si Data
	 */
	private String getSiData(String unitCode, int part)
	{
		if(s_siUnits.contains(unitCode)) // If the provided unit code equals a unit then that is the unit!!!
			return part == UNIT_PART ? unitCode : "";
		else if(unitCode.length() > 0)
		{
			String prefixPart = unitCode.substring(0, 1);
            String unitPart = unitCode.substring(1);
            if (s_siUnits.contains(unitPart) && s_siPrefixes.containsKey(prefixPart))
                return part == UNIT_PART ? unitPart : prefixPart;
		}
		return null;		
	}
	
	/**
	 * Convert the value
	 */
	@Override
	public PQ convert(PQ original, String unitTo) {
        if (this.getSiData(unitTo, UNIT_PART) != null)
        {
            String newSiUnit = this.getSiData(unitTo, UNIT_PART),
            		newSiPrefix = this.getSiData(unitTo, PREFIX_PART),
                oldSiData = this.getSiData(original.getUnit(), UNIT_PART),
                oldSiPrefix = this.getSiData(original.getUnit(), PREFIX_PART);
            double oldSiBase = s_siPrefixes.get(oldSiPrefix),
                newSiBase = s_siPrefixes.get(newSiPrefix);
            return new PQ(BigDecimal.valueOf(original.getValue().doubleValue() * oldSiBase / newSiBase), unitTo);
        }
        PQ retVal = new PQ();
        retVal.setNullFlavor(NullFlavor.Unknown);
        return retVal;
	}

}
