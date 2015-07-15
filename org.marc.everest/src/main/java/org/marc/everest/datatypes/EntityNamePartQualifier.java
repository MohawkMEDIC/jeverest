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
 * Date: 10-19-2012
 */
package org.marc.everest.datatypes;

import java.util.Arrays;
import java.util.List;

import org.marc.everest.interfaces.IEnumeratedVocabulary;


public class EntityNamePartQualifier implements IEnumeratedVocabulary {
	/**
	 * For organizations indicating legal status
	 */
	public static final EntityNamePartQualifier LegalStatus = new EntityNamePartQualifier("LS", new EntityNamePartType[] { EntityNamePartType.Title });
	/**
	 * Indicates a prefix like "Dr" or suffix like "MD" is an academic title
	 */
	public static final EntityNamePartQualifier Academic = new EntityNamePartQualifier("AC", new EntityNamePartType[] { EntityNamePartType.Title });
	/**
	 * In europe and asia there are some individuals with titles of nobility
	 */
	public static final EntityNamePartQualifier Nobility = new EntityNamePartQualifier("NB", new EntityNamePartType[] { EntityNamePartType.Title });
	/**
	 * Primarily used in British imperial culture, people tend to have an abbreviation of their professional 
	 * organizations as part of credential suffixes (FACP, ESQ, etc..)
	 */
	public static final EntityNamePartQualifier Professional = new EntityNamePartQualifier("PR", new EntityNamePartType[] { EntityNamePartType.Title });
	/**
	 * An honorific
	 */
	public static final EntityNamePartQualifier Honorific = new EntityNamePartQualifier("HON", new EntityNamePartType[] { EntityNamePartType.Title });
	/**
	 * A name the person has shortly after being born
	 */
	public static final EntityNamePartQualifier Birth = new EntityNamePartQualifier("BR", new EntityNamePartType[] { EntityNamePartType.Family, EntityNamePartType.Given });
	/**
	 * A name acquired through adoption or a chosen name
	 */
	public static final EntityNamePartQualifier Acquired = new EntityNamePartQualifier("AD", new EntityNamePartType[] { EntityNamePartType.Family, EntityNamePartType.Given });
	/**
	 * A name assumed from a partner such as a marital relationship
	 */
	public static final EntityNamePartQualifier Spouse = new EntityNamePartQualifier("SP", new EntityNamePartType[] { EntityNamePartType.Family, EntityNamePartType.Given });
	/**
	 * A call me name is usually a preferred name (such as Bill for William)
	 */
	public static final EntityNamePartQualifier CallMe = new EntityNamePartQualifier("CL", new EntityNamePartType[] { EntityNamePartType.Family, EntityNamePartType.Given });
	/**
	 * Indicates that a name is part of an initial
	 */
	public static final EntityNamePartQualifier Initial = new EntityNamePartQualifier("IN", new EntityNamePartType[] { EntityNamePartType.Given, EntityNamePartType.Family });
	/**
	 * Inidicates a middle name
	 */
	public static final EntityNamePartQualifier Middle = new EntityNamePartQualifier("MID", new EntityNamePartType[] { EntityNamePartType.Given, EntityNamePartType.Family });
	/**
	 * Identifies a prefix
	 */
	public static final EntityNamePartQualifier Prefix = new EntityNamePartQualifier("PFX", new EntityNamePartType[] { EntityNamePartType.Title, EntityNamePartType.Given, EntityNamePartType.Family });
	/**
	 * Identifies a suffix
	 */
	public static final EntityNamePartQualifier Suffix = new EntityNamePartQualifier("SFX", new EntityNamePartType[] { EntityNamePartType.Title, EntityNamePartType.Given, EntityNamePartType.Family  });

	// backing field for code
	private String m_code;
	// backing field for types
	private EntityNamePartType[] m_allowedTypes;
	
	/**
	 * Creates a new instance of the entity name part qualifier
	 */
	public EntityNamePartQualifier(String code, EntityNamePartType[] allowedPartTypes)
	{
		this.m_code = code;
		this.m_allowedTypes = allowedPartTypes;
	}
	/**
	 * Determine if this part can qualify the specified type
	 */
	public boolean canQualifyPartType(EntityNamePartType m_type) {
		if(this.m_allowedTypes == null)
			return true;
		return Arrays.asList(this.m_allowedTypes).contains(m_type);
	}

	/**
	 * Get allowed part types
	 */
	public List<EntityNamePartType> getAllowedPartTypes() {
		return Arrays.asList(this.m_allowedTypes);
	}

	/**
	 * Get the code mnemonic
	 */
	@Override
	public String getCode() {
		return this.m_code;
	}

	/**
	 * Get the code system for code mnemonics in this enumeration
	 */
	@Override
	public String getCodeSystem() {
		return "2.16.840.1.113883.5.1122";
	}

	/**
	 * Represent as a string
	 */
	@Override
	public String toString() {
		return this.getCode();
	}
}
