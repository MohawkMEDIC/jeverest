package org.marc.everest.rmim.uv.cdar2.test;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.ST;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.SubstanceAdministration;

@Structure(name = "SubstanceAdministration", model = "POCD_MT000040", namespaceUri="urn:justin", structureType =StructureType.MESSAGETYPE )
public class CustomAdministration extends SubstanceAdministration {

	private BL extraProperty;
	@Property(name = "extra", conformance = ConformanceType.MANDATORY, propertyType = PropertyType.STRUCTURAL)
	public BL getExtraProperty() { return this.extraProperty; }
	public void setExtraProperty(BL value)  { this.extraProperty = value; }

	private ST extraProperty1;
	@Property(name = "testData", conformance = ConformanceType.MANDATORY, propertyType = PropertyType.NONSTRUCTURAL, namespaceUri = "urn:justin")
	public ST getExtraData() { return this.extraProperty1; }
	public void setExtraData(ST value)  { this.extraProperty1 = value; }

}