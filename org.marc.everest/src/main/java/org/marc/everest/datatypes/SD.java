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
 * Date: 07-03-2014
 */
package org.marc.everest.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marc.everest.annotations.*;
import org.marc.everest.datatypes.doc.*;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;

/**
 * Represents a structured document text class
 * 
 * Implements StructDoc.Text
 */
@Structure(name = "SD", structureType = StructureType.DATATYPE)
public class SD extends ANY {

	private List<org.marc.everest.datatypes.doc.StructDocNode> m_content;
	protected String m_language;
	protected String m_id;
	protected String m_styleCode;
	protected String m_mediaType;
	
	/**
	 * Creates a new instance of the SD class
	 */
	 public SD()
     {
         this.m_mediaType = "text/x-hl7-text+xml";
         this.m_content = new ArrayList<StructDocNode>();
     }
	 
	 /**
	  * Creates a new SD with the specified content
	  */
     public SD(StructDocNode ... documentContent) 
     {
    	 this();
         for(StructDocNode nd : documentContent)
        	 this.m_content.add(nd);
     }
     
     /**
      * Creates a text node
      */
     public static StructDocTextNode createText(String text)
     {
         return new StructDocTextNode(text);
     }

     /** 
      * Creates an element node
      */
     public static StructDocElementNode createElement(String elementName, String namespaceUri)
     {
         StructDocElementNode retVal = new StructDocElementNode(elementName, null);
         retVal.setNamespaceUri(namespaceUri);
         return retVal;
     }
     
     /** 
      * Creates an element node
      */
     public static StructDocElementNode createElement(String elementName, String value, String namespaceUri)
     {
         StructDocElementNode retVal = new StructDocElementNode(elementName, value);
         retVal.setNamespaceUri(namespaceUri);
         return retVal;
     }

     /**
      *  Creates an attribute node
      */
     public static StructDocAttributeNode createAttribute(String attributeName, String value)
     {
         return new StructDocAttributeNode(attributeName, value);
     }
     
	/**
	 * Gets the list of nodes within the structured document
	 * @return
	 */
	@Property(name = "content", conformance = ConformanceType.MANDATORY, propertyType = PropertyType.NONSTRUCTURAL)
	public List<org.marc.everest.datatypes.doc.StructDocNode> getContent() {
		return m_content;
	}
	
	/**
	 * Sets the list of nodes within the structured document
	 */
	public void setContent(
			List<org.marc.everest.datatypes.doc.StructDocNode> value) {
		this.m_content = value;
	}
	
	/**
	 * Gets the language of the content. Valid codes are taken from IETF language/culture codes
	 */
	@Property(name = "language", conformance = ConformanceType.MANDATORY, propertyType = PropertyType.STRUCTURAL)
	public String getLanguage() {
		return m_language;
	}
	/**
	 * Sets the language of the content. Valid codes are taken from IETF language/culture codes
	 */
	public void setLanguage(String value) {
		this.m_language = value;
	}
	
	/**
	 * Gets the IDREF for this SD instance
	 */
	@Property(name = "ID", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getId() {
		return m_id;
	}
	/**
	 * Sets the IDREF for this SD instance
	 */
	public void setId(String value) {
		this.m_id = value;
	}
	/**
	 * Gets the style code of the SD instance
	 */
	@Property(name = "styleCode", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getStyleCode() {
		return m_styleCode;
	}
	/**
	 * Sets the style code of the SD instance
	 * @param value
	 */
	public void setStyleCode(String value) {
		this.m_styleCode = value;
	}
	/**
	 * Gets the media type of the SD instance
	 */
	@Property(name = "mediaType", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getMediaType() {
		return m_mediaType;
	}
	/**
	 * Sets the media type of the SD instance
	 * @param value
	 */
	public void setMediaType(String value) {
		this.m_mediaType = value;
	}

	/**
	 * Determine equality of the object
	 */
	@Override
	public boolean equals(Object other) {
		Boolean equals = super.equals(other);
		if(!equals || !(other instanceof SD)) return false;
		
		SD otherSd = (SD)other;
        equals &= this.m_mediaType == otherSd.m_mediaType;
        equals &= this.m_language == otherSd.m_language;
        equals &= this.m_styleCode == otherSd.m_styleCode;
        equals &= this.m_id == otherSd.m_id;

        if (this.m_content != null && otherSd.m_content != null &&
            this.m_content.size() == otherSd.m_content.size())
            for (int i = 0; i < this.m_content.size(); i++)
                equals &= this.m_content.get(i).equals(otherSd.m_content.get(i));
        else
            equals = false;
        return equals;
	}

	/**
	 * Validate this SD
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
		Collection<IResultDetail> retVal = super.validateEx();
        if (!this.m_mediaType.equals("text/x-hl7-text+xml"))
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.WARNING, "SD", String.format(EverestValidationMessages.MSG_INVALID_VALUE, this.m_mediaType, "MediaType"), null));
        else if (!(this.isNull() ^ (this.m_content.size() > 0)))
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "SD", EverestValidationMessages.MSG_NULLFLAVOR_WITH_VALUE, null));
        return retVal;
	}

	/**
	 * Validates this SD
	 */
	@Override
	public boolean validate() {
		return super.validate() && (this.isNull() ^ (this.m_content.size() > 0)) &&
                this.m_mediaType == "text/x-hl7-text+xml";
	}

	/**
	 * Find a node by ID
	 */
	public StructDocNode findNodeById(String id)
	{
		if(id.startsWith("#"))
			id = id.substring(1);
		
		// Recursively find the node
		for(StructDocNode node : this.m_content)
		{
			StructDocNode res = node.findNodeById(id);
			if(res != null)
				return res;
				
		}
		return null;
	}

	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		if(this.getContent() == null) return "EMPTY";
		for(StructDocNode nd : this.getContent())
			retVal.append(nd.toString());
		return retVal.toString();
	}

	/**
	 * Represents the SD as a plain (no tags) string
	 * @return
	 */
	public String toPlainString() {
		StringBuilder retVal = new StringBuilder();
		if(this.getContent() == null) return "EMPTY";
		for(StructDocNode nd : this.getContent())
			retVal.append(nd.toPlainString());
		return retVal.toString();
	}	
	
}
