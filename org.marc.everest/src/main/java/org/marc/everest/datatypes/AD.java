/* 
 * Copyright 2008/2011 Mohawk College of Applied Arts and Technology
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
 * User: Jaspinder Singh
 * Date: 06-28-2011
 */

package org.marc.everest.datatypes;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;

/**
 * Mailing at a home or office addresses is primarily used to communicate data that will allow printing mail labels.
 */
@Structure(name="AD", structureType=StructureType.DATATYPE)
public class AD extends ANY {

	/** A sequence of address parts that makes up an address */
	private List<ADXP> parts;
	
	/** A code that identifies the use for the AD **/
	private SET<CS<PostalAddressUse>> use;
	
	/** Specifies whether or not the order of the address parts is known. **/
	private boolean isNotOrdered;
	
	/** Specifies the usable period for the address **/
	private GTS useablePeriod;
	
	/**
	 * Instantiates a new AD.
	 */
	public AD() {
		super();
		parts = new ArrayList<ADXP>();
	}
	
	/**
	 * Instantiate a new AD using a list of address parts.
	 * @param parts list of address parts to create an AD from.
	 */
	public AD(Collection<ADXP> parts) {
		super();
		this.parts = new ArrayList<ADXP>(parts);
		this.use = new SET<CS<PostalAddressUse>>();
	}
	
	/**
	 * Instantiate a new instance of AD with a specified use and address parts.
	 * @param use the specified use for the address.
	 * @param parts parts that make up the address.
	 */
	public AD(CS<PostalAddressUse> use, Collection<ADXP> parts) {
		this(new SET<CS<PostalAddressUse>>(use), new ArrayList<ADXP>(parts));
	}
	
	/**
	 * Instantiate a new instance of AD with the specified use
	 * @param use The use of the address
	 */
	public AD(PostalAddressUse use)
	{
		this(new CS<PostalAddressUse>(use), null);
	}

	/**
	 * Creates a new instance of the AD datatype with the specified use, and parts
	 * @param use The uses of the address
	 * @param parts The component parts of the parts
	 */
	public AD(SET<CS<PostalAddressUse>> use, Collection<ADXP> parts)
	{
		super();
		this.parts = new ArrayList<ADXP>(parts);
		this.use = use;
	}
	
	/**
	 * Create an AD instance given the parts
	 */
	public static AD createAD(ADXP... parts){
		return new AD(Arrays.asList(parts));
	}
		
	/**
	 * Create an instance of AD given the parts and use
	 */
	public static AD createAD(PostalAddressUse use, ADXP... parts)
	{
		return new AD(new CS<PostalAddressUse>(use), Arrays.asList(parts));
	}
	
	
	/**
	 * Create an instance of AD given the parts and use
	 */
	public static AD createAD(SET<CS<PostalAddressUse>> use, ADXP... parts)
	{
		return new AD(use, Arrays.asList(parts));
	}

	/**
	 * Gets the sequence of address parts that make up the address.
	 * @return the parts that make up the address.
	 */
	@Property(name = "part", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.OPTIONAL, genericSupplier = { ADXP.class })
	public List<ADXP> getPart()
	{
		return this.parts;
	}
	
	/**
	 * Sets the sequence of address parts that make up the address.
	 * @param part the parts that make up the address.
	 */
	public void setPart(List<ADXP> part)
	{
		this.parts = part;
	}
	
	/**
	 * Gets the postal address use codes.
	 * @return the postal address use codes.
	 */
	@Property(name = "use", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public SET<CS<PostalAddressUse>> getUse()
	{
		return this.use;
	}
	
	/**
	 * Gets the postal address use codes.
	 * @return the postal address use codes.
	 */
	public void setUse(SET<CS<PostalAddressUse>> use)
	{
		this.use = use;
	}
	/**
	 * Set isNotOrdered property
	 */
	public void setIsNotOrdered(boolean ordered)
	{
		this.isNotOrdered = ordered;
	}
	/**
	 * A boolean value specifying whether or not the order of the address parts is known.
	 * @return true if the order of the address parts is not known, false otherwise.
	 */
	@Property(name = "isNotOrdered", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public boolean isNotOrdered()
	{
		return this.isNotOrdered;
	}
	
	/**
	 * Sets the timing specification indicating the time when this address is valid
	 * @return
	 */
	@Property(name = "useablePeriod", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public GTS getUseablePeriod() { return this.useablePeriod; }
	/**
	 * Gets the timing specification indiciating the time when this address is valid
	 * @param value
	 */
	public void setUseablePeriod(GTS value) { this.useablePeriod = value; }
	
	/**
	 * Determines whether or not <code>n</code> conforms to the AD.Basic flavor.
	 * @param n The AD to check for AD.Basic conformance.
	 * @return true if the AD conforms to the AD.Basic flavor, false otherwise.
	 */
	public static Boolean isValidBasicFlavor(AD n)
	{
		boolean isBasic = true;
		
		for(ADXP part : n.getPart())
		{
			if (part.getPartType() != AddressPartType.City || 
					part.getPartType() != AddressPartType.State)
			{
				isBasic = false;
				break;
			}
		}
		
		return isBasic;
	}
	/**
	 * Create an address from a simple address
	 * @param use The desired use
	 * @param addressLine1 The first address line
	 * @param addressLine2 The second address line
	 * @param city The city of the address
	 * @param state The state of the address
	 * @param country THe country of the address
	 * @param zip The ZIP code of the address
	 * @return
	 */
	public static AD fromSimpleAddress(PostalAddressUse use, String addressLine1, String addressLine2, String city, String state, String country, String zip)
	{
		AD retVal = new AD();
		if(use != null)
			retVal.setUse(SET.createSET(new CS<PostalAddressUse>(use)));
		if(addressLine1 != null && !addressLine1.isEmpty())
			retVal.getPart().add(new ADXP(addressLine1, AddressPartType.AddressLine));
		if(addressLine2 != null && !addressLine2.isEmpty())
			retVal.getPart().add(new ADXP(addressLine2, AddressPartType.AddressLine));
		if(city != null && !city.isEmpty())
			retVal.getPart().add(new ADXP(city, AddressPartType.City));
		if(state != null && !state.isEmpty())
			retVal.getPart().add(new ADXP(state, AddressPartType.State));
		if(country != null && !country.isEmpty())
			retVal.getPart().add(new ADXP(country, AddressPartType.Country));
		if(zip != null && !zip.isEmpty())
			retVal.getPart().add(new ADXP(zip, AddressPartType.PostalCode));
		return retVal;
	}
	@Override
	public String toString()
	{
		StringWriter writer = new StringWriter();
		
		for(ADXP part : parts) 
		{
			if (part.getPartType() == null)
			{
				writer.write(part.getValue());
			}
			else
			{
				switch(part.getPartType())
				{
					case Delimiter:
						if (part.getValue() == null || part.getValue().trim().equals(""))
						{
							writer.write(System.getProperty("line.sperator"));
						}
						else
						{
							writer.write(part.getValue());
						}
						break;
					default:
						writer.write(part.getValue());
						break;
				}
			}
		}
		
		return writer.toString();
	}
	
	/**
	 * Validate the address.
	 * @return True if there is at least one address part or, alternatively, a null flavor is set.
	 */
	@Override
	public boolean validate()
	{
		return (getNullFlavor()!= null)^(parts.size() > 0);
	}
	
	/**
	 * Extended validation function that returns the detected issues with a particular
	 * instance of the AD data type
	 */
	public Collection<IResultDetail> validateEx()
	{
		ArrayList<IResultDetail> retVal = new ArrayList<IResultDetail>();
		if(!((this.getNullFlavor() != null) ^ (this.parts.size() > 0)))
				retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "AD", "NullFlavor must be specified, or more than one Part must be present", null));
		return retVal;
	}
	
	/**
	 * Determines if the current instance of AD is semantically equal to other
	 * <p>Two instance of AD are semantically equal when:</p>
	 * <ul>
	 * 	<li>Both are non-null and non null-flavored</li>
	 * <li>Both contain the same parts, regardless of order</li>
	 * <li>The Use and IsNotOrdered properties pass equality test</li>
	 * </ul>
	 * @param other The other AD to test
	 * @return True if this instance is semantically equal to other, false if not, or nullFlavor if semantic equality cannot be determined
	 */
	public BL semanticEquals(IAny other)
	{
		BL baseSem = super.semanticEquals(other);
		if(!baseSem.toBoolean())
			return baseSem;
		
		// PRocess semantic equality
		boolean result = true;
		AD otherAd = (AD)other;
		if((this.getPart() == null) ^ (otherAd.getPart() == null))
				return BL.FALSE;
		else if(this.getPart() == otherAd.getPart())
			return BL.TRUE;
		else
		{
			for(ADXP part : this.getPart())
				result &= otherAd.getPart().contains(part);
			return BL.fromBoolean(result && this.getPart().size() == otherAd.getPart().size());
		}
	}

	
}
