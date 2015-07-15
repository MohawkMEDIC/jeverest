/* 
 * Copyright 2008-2011 Mohawk College of Applied Arts and Technology
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
 * Date: 09-09-2011
 */
package org.marc.everest.datatypes;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Flavor;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.interfaces.IAny;

/**
 * Represents a globally unique reference number that identifies an object
 */
@Structure(name = "II", structureType = StructureType.DATATYPE)
public class II extends ANY {

	// Backing field for root
	private String m_root;
	// Backing field for extension
	private String m_extension;
	// Backing field for scope
	private IdentifierScope m_scope;
	// Backing field for reliability
	private IdentifierReliability m_reliability;
	// Backing field for name
	private String m_identifierName;
	// Backing field for displayable property
	private Boolean m_displayable;
	// Backing field for assigning authority name
	private String m_assigningAuthorityName;
	
	/**
	 * Creates a new instance of the II type
	 */
	public II() { super(); }
	/**
	 * Creates a new instance of the II type with the specified
	 * UUID as its root
	 * @param root A UUID instance representing a globally unique identifier (GUID)
	 */
	public II(UUID root) 
	{ 
		this.m_root = root.toString();
	}
	/**
	 * Creates a new instance of the II type with the specified 
	 * string as its root. The string should be an OID in the form
	 * x.y.z.cc.....
	 * @param root A string value (typically an OID) that qualifies the extension
	 */
	public II(String root) 
	{ 
		this.m_root = root;
	}
	/**
	 * Creates a new instance of the II type with the specified
	 * string as its root and string as its extension. Typically
	 * the root will be an OID in dotted notation and the extension
	 * will be a unique identifier within the OID domain.
	 * 
	 * @param root A string value (typically an OID) that qualifies the extension
	 * @param extension A string value that represents the value of the unique identifier within the domain
	 */
	public II(String root, String extension)
	{
		this(root);
		this.m_extension = extension;
	}
	
	/**
	 * Gets a value that guarantees the uniqueness of the extension of this instance identifier
	 */
	@Property(name = "root", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.REQUIRED)
	public String getRoot() { return this.m_root; }
	/**
	 * Sets a value that guarantees the uniqueness of the extension of this instance identifier
	 */
	public void setRoot(String value) { this.m_root = value; }
	/**
	 * Gets a character string that uniquely identifies the object
	 */
	@Property(name = "extension", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.REQUIRED)
	public String getExtension() { return this.m_extension; }
	/**
	 * Sets a character string that uniquely identifies the object
	 */
	public void setExtension(String value) { this.m_extension = value; }
	/**
	 * Gets a value that identifies the scope under which this identifier applies to the object
	 */
	@Property(name = "scope", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public IdentifierScope getScope() { return this.m_scope; }
	/**
	 * Sets a value that identifies the scope under which this identifier applies to the object
	 */
	public void setScope(IdentifierScope value) { this.m_scope = value; }
	/**
	 * Gets a value that specifies the reliability of the instance identifier
	 */
	@Property(name = "reliability", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public IdentifierReliability getReliability() { return this.m_reliability; }
	/**
	 * Sets a value that specifies the reliability of the instance identifier
	 */
	public void setReliability(IdentifierReliability value) { this.m_reliability = value; }
	/**
	 * Gets a value that specifies if the identifier is intended to be displayed on a user screen
	 */
	@Property(name = "displayable", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.REQUIRED)
	public Boolean getDisplayable() { return this.m_displayable; }
	/**
	 * Sets a value that specifies if the identifier is intended to be displayed on a user screen
	 * @param value
	 */
	public void setDisplayable(Boolean value) { this.m_displayable = value; }
	/**
	 * Gets a human readable name for the identifier
	 */
	@Property(name = "identifierName", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public String getIdentifierName() { return this.m_identifierName; }
	/**
	 * Sets a human readable name for the identifier
	 */
	public void setIdentifierName(String value) { this.m_identifierName = value; }
	/**
	 * Gets the authority responsible for the assignment of the identifier
	 */
	@Property(name = "assigningAuthorityName", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public String getAssigningAuthorityName() { return this.m_assigningAuthorityName; }
	/**
	 * Sets the authority that is responsible for the assignment of the identifier.
	 */
	public void setAssigningAuthorityName(String value) { this.m_assigningAuthorityName = value; }
	
	/**
	 * Determines if the root of the specified II is a UUID
	 */
	public static boolean isRootUuid(II ii)
	{
		Pattern rp = Pattern.compile("[{]?[A-F0-9]{8}-?([A-F0-9]{4}-?){3}[A-F0-9]{12}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = rp.matcher(ii.getRoot()); 
 		return matcher.find();
	}
	
    /**
     * Helper function, returns true if the specified II has an OID
     */
    public static boolean isRootOid(II ii)
    {
    	Pattern rp = Pattern.compile("^(\\d+?\\.){1,}\\d+$");
		Matcher matcher = rp.matcher(ii.getRoot()); 
 		return matcher.find();
    }
    
	/**
	 * Valid token flavor
	 */
    @Flavor(name = "II.TOKEN")
    public static Boolean isValidTokenFlavor(II ii)
    {
        // Try to make a guid
        return ii.getDisplayable() == null &&
            ii.getScope() == null &&
            (ii.getRoot() != null && isRootUuid(ii) && ii.getExtension() == null) ^ (ii.getNullFlavor() != null);
    }

    /**
     * Flavor validator for Public
     */
    @Flavor(name = "II.PUBLIC")
    public static Boolean isValidPublicFlavor(II ii)
    {
        return ii.getDisplayable() == true &&
            ii.getScope() == IdentifierScope.BusinessIdentifier &&
            (ii.getRoot() != null && ii.getExtension() != null && isRootOid(ii)) ^ (ii.getNullFlavor() != null);
    }

    /**
     * Flavor validator for OID
     **/
    @Flavor(name = "II.OID")
    public static Boolean isValidOidFlavor(II ii)
    {
        return (ii.getRoot() != null && isRootOid(ii) && ii.getExtension() == null) ^ (ii.getNullFlavor() != null);
    }

    /**
     * BUS flavor validator
     **/
    @Flavor(name = "II.BUS")
    public static Boolean isValidBusFlavor(II ii)
    {
        return ii.getDisplayable() == null &&
            ii.getScope() == IdentifierScope.BusinessIdentifier &&
            (ii.getRoot() != null && ((isRootOid(ii) && ii.getExtension() != null) || (isRootUuid(ii) && ii.getExtension() == null))) ^ (ii.getNullFlavor() != null);
    }

    /**
     * Validator for VER
     */
    @Flavor(name = "II.VER")
    public static Boolean isValidVerFlavor(II ii)
    {
        return ii.getDisplayable() == null &&
            ii.getScope() == IdentifierScope.VersionIdentifier &&
            (ii.getRoot() != null && ii.getExtension() == null && isRootUuid(ii)) ^ (ii.getNullFlavor() != null);
    }
	
    /**
     * Validator for BUS AND VER
     */
    @Flavor(name = "II.BUS_AND_VER")
    public static Boolean isValidBusAndVerFlavor(II ii)
    {
        return isValidVerFlavor(ii) && isValidBusFlavor(ii);
    }
	/**
	 * @see org.marc.everest.datatypes.ANY#semanticEquals(org.marc.everest.datatypes.interfaces.IAny)
	 */
	@Override
	public BL semanticEquals(IAny other) {
		 BL baseSem = super.semanticEquals(other);
         if (BL.FALSE.equals(baseSem))
             return baseSem;

         II otherII = (II)other;
         if(this.isNull() && other.isNull())
        	 return BL.TRUE;
         else if(otherII.getRoot() == this.getRoot() &&
        		 otherII.getExtension() == this.getExtension()) // exact equals
        	 return BL.TRUE;
         // Either root is null xor root is not null and equal AND
         // Either extension is null xor extension is not null and equal 
         else if(((otherII.getRoot() != null && otherII.getRoot().equals(this.getRoot())) ^ (otherII.getRoot() == null)) &&
        		 ((otherII.getExtension() != null && otherII.getExtension().equals(this.getExtension())) ^ (otherII.getExtension() == null)))
        	 return BL.TRUE;
         return BL.FALSE;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((m_assigningAuthorityName == null) ? 0
						: m_assigningAuthorityName.hashCode());
		result = prime * result
				+ ((m_displayable == null) ? 0 : m_displayable.hashCode());
		result = prime * result
				+ ((m_extension == null) ? 0 : m_extension.hashCode());
		result = prime
				* result
				+ ((m_identifierName == null) ? 0 : m_identifierName.hashCode());
		result = prime * result
				+ ((m_reliability == null) ? 0 : m_reliability.hashCode());
		result = prime * result + ((m_root == null) ? 0 : m_root.hashCode());
		result = prime * result + ((m_scope == null) ? 0 : m_scope.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		II other = (II) obj;
		if (m_assigningAuthorityName == null) {
			if (other.m_assigningAuthorityName != null)
				return false;
		} else if (!m_assigningAuthorityName
				.equals(other.m_assigningAuthorityName))
			return false;
		if (m_displayable == null) {
			if (other.m_displayable != null)
				return false;
		} else if (!m_displayable.equals(other.m_displayable))
			return false;
		if (m_extension == null) {
			if (other.m_extension != null)
				return false;
		} else if (!m_extension.equals(other.m_extension))
			return false;
		if (m_identifierName == null) {
			if (other.m_identifierName != null)
				return false;
		} else if (!m_identifierName.equals(other.m_identifierName))
			return false;
		if (m_reliability != other.m_reliability)
			return false;
		if (m_root == null) {
			if (other.m_root != null)
				return false;
		} else if (!m_root.equals(other.m_root))
			return false;
		if (m_scope != other.m_scope)
			return false;
		return true;
	}
	
	/**
	 * Represent this II as a string in format extension@root
	 */
	@Override
	public String toString() {
		return String.format("{ext = %s, root = %s}", this.getExtension(), this.getRoot());
	}
    
}
