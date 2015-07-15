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
 * Date: 06-25-2011
 */
package org.marc.everest.datatypes;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.datatypes.generic.PDV;
import org.marc.everest.datatypes.generic.QTY;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.IDistanceable;
import org.marc.everest.datatypes.interfaces.IImplicitInterval;
import org.marc.everest.datatypes.interfaces.IOrderedDataType;
import org.marc.everest.datatypes.interfaces.IPointInTime;
import org.marc.everest.datatypes.interfaces.IPqTranslatable;
import org.marc.everest.exceptions.HL7DateFormatException;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;

/**
 * A quantity specifying a point on the axis of natural time.
 * <h3> 
 * Remarks
 * </h3>
 * <p>
 * You will notice that there are a series of constant values in this datatype.
 * These constants represent the precisions of the date object when formatted
 * as a string. We tried using enumerations however Java enumerations cannot
 * be used in switch cases (or so it appears) so yet again, we're stuck kicking 
 * it old school with a bunch of static final integers...
 * </p>
 */
@Structure(name = "TS", structureType = StructureType.DATATYPE)
public class TS extends QTY<String> implements IPointInTime, IPqTranslatable<TS>, IDistanceable<TS>, IImplicitInterval<TS>, IOrderedDataType<TS>
{

	/**
	 * Date is precise to the year
	 */
	public static final int YEAR = 4;
	/**
	 * Date is precise to the month
	 */
	public static final int MONTH = 6;
	/**
	 * Date is precise to the day
	 */
	public static final int DAY = 8;
	/**
	 * Date is precise to the hour without a timezone
	 */
	public static final int HOURNOTIMEZONE = 10;
	/**
	 * Date is precise to the minute without a timezone 
	 */
	public static final int MINUTENOTIMEZONE = 12;
	/**
	 * Date is precise to the second without a timezone
	 */
	public static final int SECONDNOTIMEZONE = 14;
	/**
	 * Date is a full precision without a timezone
	 */
	public static final int FULLNOTIMEZONE = 18;
	/**
	 * Date is precise to the hour with a timezeone
	 */
	public static final int HOUR = 15;
	/**
	 * Date is precise to the minute with a timezone
	 */
	public static final int  MINUTE = 17;
	/**
	 * Date is precise to the minute with a timezone
	 */
	public static final int SECOND = 19;
	/**
	 * Date is full precision with timezone.
	 */
	public static final int FULL = 23;
	/**
	 * Formats for the various flavours
	 */
	private static final HashMap<String, Integer> m_flavorPrecisions;
	/**
	 * Formats for the SimpleDateFormatter mapped to the precision of the date
	 */
	private static final HashMap<Integer, String> m_precisionFormats;
	
	/**
	 * :o No Comment 
	 */
	static {
		m_flavorPrecisions = new HashMap<String, Integer>();
		m_flavorPrecisions.put("DATETIME", TS.SECOND);
		m_flavorPrecisions.put("TS.DATETIME", TS.SECOND);
		m_flavorPrecisions.put("DATE", TS.DAY);
		m_flavorPrecisions.put("TS.DATE", TS.DAY);
		m_flavorPrecisions.put("", TS.FULL);
		m_flavorPrecisions.put("FULLDATETIME", TS.FULL);
		m_flavorPrecisions.put("TS.FULLDATETIME", TS.FULL);
		m_flavorPrecisions.put("TS.FULLDATEWITHTIME", TS.FULL);
		m_flavorPrecisions.put("FULLDATEWITHTIME", TS.FULL);
		m_flavorPrecisions.put("TS.DATE.FULL", TS.DAY);
		m_flavorPrecisions.put("TS.FULLDATE", TS.DAY);
		m_flavorPrecisions.put("TS.DATETIME.FULL", TS.SECOND);
		
		m_precisionFormats = new HashMap<Integer, String>();
		m_precisionFormats.put(TS.DAY, "yyyyMMdd");
		m_precisionFormats.put(TS.FULL, "yyyyMMddHHmmss.SSSZZZZ");
		m_precisionFormats.put(TS.FULLNOTIMEZONE, "yyyyMMddHHmmss.SSS");
		m_precisionFormats.put(TS.HOUR, "yyyyMMddHHZZZZ");
		m_precisionFormats.put(TS.HOURNOTIMEZONE, "yyyyMMddHH");
		m_precisionFormats.put(TS.MINUTE, "yyyyMMddHHmmZZZZ");
		m_precisionFormats.put(TS.MINUTENOTIMEZONE, "yyyyMMddHHmm");
		m_precisionFormats.put(TS.MONTH, "yyyyMM");
		m_precisionFormats.put(TS.SECOND, "yyyyMMddHHmmssZZZZ");
		m_precisionFormats.put(TS.SECONDNOTIMEZONE, "yyyyMMddHHmmss");
		m_precisionFormats.put(TS.YEAR, "yyyy");
	}
	
	/**
	 * Invalid date value 
	 */
	private String m_invalidDateValue;
	/**
	 * Identifies the precision of this object
	 */
	private Integer m_dateValuePrecision;
	/**
	 * The real value of the time stamp
	 */
	private Calendar m_dateValue;
	
	/**
	 * Creates a new instance of the time stamp class
	 */
	public TS() { super(); }
	/**
	 * Creates a new instance of the time stamp class with the specified value
	 * @param value The initial value of the time stamp
	 */
	public TS(Calendar value) { this.setDateValue(value); this.m_dateValuePrecision = TS.FULL; }
	/**
	 * Creates a new instance of the time stamp class with the specified value and precision
	 * @param value The initial value of the time stamp
	 * @param precision The precision of the time stamp
	 */
	public TS(Calendar value, int precision) { this(value); this.m_dateValuePrecision = precision; }

	/**
	 * Get the TS value given a string
	 */
	public static TS valueOf(String value) throws HL7DateFormatException
	{

		TS retVal = new TS();
		retVal.setValue(value);
		return retVal;
		
	}
	
	/**
	 * Get the value of this time stamp object. This property is backed by the DateValue and DateValuePrecision
	 * properties.
	 */
	@Property(name = "value", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	@Override
	public String getValue() { 
		
		if(this.m_invalidDateValue != null)
			return this.m_invalidDateValue;
		else if(this.getDateValue() == null)
			return null;
		
		// Impose flavor formatting
		int precision = TS.FULL;
		if(m_dateValuePrecision != null)
			precision = this.getDateValuePrecision();
		else if(m_flavorPrecisions.containsKey(this.getFlavorId()))
			precision = m_flavorPrecisions.get(this.getFlavorId());
		
		// Attempt to get the precision formats
		String flavorFormat = m_precisionFormats.get(precision);
		if(flavorFormat == null)
			throw new IllegalStateException("DateValuePrecision is not recognized");

		SimpleDateFormat sdf = new SimpleDateFormat(flavorFormat);
		return sdf.format(this.getDateValue().getTime());
	}
	
	/**
	 * Retrieves a flag indicating whether the date stored in Value was invalid
	 */
	public boolean isInvalidDate()
	{
		return this.m_invalidDateValue != null;
	}
	
	/**
	 * Set the value of this object as a string. This property is backed by the DateValue and DateValuePrecision
	 * properties. Setting this property to an arbitrary (HL7v3) date string will force a re-parse of DateValuePrecision
	 * and DateValue. 
	 * @param value The HL7v3 formatted date string to parse
	 * @throws ParseException When the value is an invalid date
	 */
	@Override
	public void setValue(String value) {

		
		 try
        {
		
			this.m_invalidDateValue = null;
            if (value == null)
            {
                this.m_dateValue = null;
                return;
            }
            
            // Correct timezone
            if(value.contains("+") || value.contains("-"))
            {
            	int sTz = value.contains("+") ? value.indexOf("+") : value.indexOf("-");
            	String tzValue = value.substring(sTz + 1);
            	int iTzValue = 0;
        		iTzValue = Integer.parseInt(tzValue);
        		
        		if(iTzValue < 24)
        			value = value.substring(0, sTz + 1) + String.format("%02d00", iTzValue);
        		else
        			value = value.substring(0, sTz + 1) + String.format("%04d", iTzValue);
            }
            
            // Precision
            this.setDateValuePrecision(value.length());

            // HACK: Correct the milliseonds to be three digits if four are passed into the parse function
            if (this.getDateValuePrecision() == 24 || this.getDateValuePrecision() == TS.SECOND && value.contains("."))
            {
                this.setDateValuePrecision(this.getDateValuePrecision() - 1);
                int eMs = value.contains("-") ? value.indexOf("-") - 1 : value.contains("+") ? value.indexOf("+") - 1 : value.length() - 1;
                value = value.substring(0, eMs) + value.substring(eMs + 1);
            }
            else if (this.getDateValuePrecision() > TS.SECOND && value.contains(".")) // HACK: sometimes milliseconds can be one or two digits
            {
                int eMs = value.contains("-") ? value.indexOf("-") - 1 : value.contains("+") ? value.indexOf("+") - 1 : value.length() - 1;
                int sMs = value.indexOf(".");
                value = value.substring(0, eMs + 1) + new String(new char[3 - (eMs - sMs)]).replace('\0', '0') + value.substring(eMs + 1);
                this.setDateValuePrecision(value.length());
            }
	
			 // Flavor format
	        String flavorFormat = m_precisionFormats.get(this.getDateValuePrecision());
	        if(flavorFormat == null)
	            flavorFormat = m_precisionFormats.get(TS.FULL);

	         // Now parse the date string
	         if (value.length() > flavorFormat.length())
	         {
	        	 SimpleDateFormat sdf = new SimpleDateFormat(flavorFormat);
	        	 this.m_dateValue = Calendar.getInstance();
	        	 this.m_dateValue.setTime(sdf.parse(value.substring(0, flavorFormat.length() + (flavorFormat.contains("Z") ? 1 : 0))));
	         }
	         else
	         {
	        	 SimpleDateFormat sdf = new SimpleDateFormat(flavorFormat.substring(0, value.length()));
	        	 this.m_dateValue = Calendar.getInstance();
	        	 this.m_dateValue.setTime(sdf.parse(value));
	         }
	         return;
	     }
	     catch(Exception e)
	     {
	    	 this.m_invalidDateValue = value;
	    	 
	    	 throw new HL7DateFormatException(value);
	     }
	}
	
	/**
	 * Gets the value of the timestamp as a Java Date object
	 * @return The timestamp as a Java Date object
	 */
	public Calendar getDateValue() { return this.m_dateValue; }
	
	/**
	 * Sets the value of the timestamp as a Java Date object
	 * @param value The new Date to represent within this TS
	 */
	public void setDateValue(Calendar value) { this.m_dateValue = value; }
	
	/**
	 * Gets the precision of the DateValue. For example, a date time of January 1, 2009 with precision
	 * of Month means that the date is precise to January 2009, the same date with precision of full
	 * is precise to January 1 2009, 00:00:00.000
	 */
	public Integer getDateValuePrecision() { return this.m_dateValuePrecision; }
	
	/**
	 * Sets the precision of the DateValue 
	 */
	public void setDateValuePrecision(Integer value) { this.m_dateValuePrecision = value; }
	
	/**
	 * Gets the flavor of this instance 
	 */
	@Property(name = "flavorId", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	@Override
	public String getFlavorId() { return super.getFlavorId(); }
	
	/**
	 * Represents this timestamp (with precision) to an interval.
	 * <p>
	 * If given a TS of January 1, 2009 with precision of Month, will create
	 * an IVL&lt;TS> with a low of January 1, 2009 00:00:00 and high of 
	 * January 31, 2009 11:59:59</p>
	 */
	public IVL<TS> toIvl()
	{
		
		// Is this TS full precision? If so, then the high/low are identical
		if(this.m_dateValuePrecision == TS.FULL ||
			this.m_dateValuePrecision == TS.FULLNOTIMEZONE)
			return new IVL<TS>(this, true, this, true);
		
		IVL<TS> retVal = new IVL<TS>();
		
		Calendar lowCal = Calendar.getInstance(), highCal = Calendar.getInstance();
		
		// Get calendar
		Calendar cal = new GregorianCalendar(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), 1);
		int maxMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		// Determine the date value precision
		switch(this.m_dateValuePrecision)
		{
			case TS.YEAR:

				lowCal.set(this.m_dateValue.get(Calendar.YEAR), 0, 1, 0, 0, 0);
				highCal.set(this.m_dateValue.get(Calendar.YEAR), 0, 31, 23, 59, 59);
				lowCal.set(Calendar.MILLISECOND, 0);
				highCal.set(Calendar.MILLISECOND, 999);
				break;
			case TS.MONTH:
				lowCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), 1, 0, 0, 0);
				highCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), maxMonth, 23, 59, 59);
				lowCal.set(Calendar.MILLISECOND, 0);
				highCal.set(Calendar.MILLISECOND, 999);
				break;
			case TS.DAY:
				lowCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), this.m_dateValue.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				highCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), this.m_dateValue.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				lowCal.set(Calendar.MILLISECOND, 0);
				highCal.set(Calendar.MILLISECOND, 999);
				break;
			case TS.HOUR:
			case TS.HOURNOTIMEZONE:
				lowCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), this.m_dateValue.get(Calendar.DAY_OF_MONTH), this.m_dateValue.get(Calendar.HOUR_OF_DAY), 0, 0);
				highCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), this.m_dateValue.get(Calendar.DAY_OF_MONTH), this.m_dateValue.get(Calendar.HOUR_OF_DAY), 59, 59);
				lowCal.set(Calendar.MILLISECOND, 0);
				highCal.set(Calendar.MILLISECOND, 999);
				break;
			case TS.MINUTE:
			case TS.MINUTENOTIMEZONE:
				lowCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), this.m_dateValue.get(Calendar.DAY_OF_MONTH), this.m_dateValue.get(Calendar.HOUR_OF_DAY), this.m_dateValue.get(Calendar.MINUTE), 0);
				highCal.set(this.m_dateValue.get(Calendar.YEAR), this.m_dateValue.get(Calendar.MONTH), this.m_dateValue.get(Calendar.DAY_OF_MONTH), this.m_dateValue.get(Calendar.HOUR_OF_DAY), this.m_dateValue.get(Calendar.MINUTE), 59);
				lowCal.set(Calendar.MILLISECOND, 0);
				highCal.set(Calendar.MILLISECOND, 999);
				break;
				// TODO: Handle Second precision with milliseconds
		}
		
		return new IVL<TS>(new TS(lowCal), true, new TS(highCal), true);
	}
	
	/**
	 * Sets the flavor of this instance 
	 */
	@Override
	public void setFlavorId(String value) {
		if(this.m_dateValuePrecision == null)
		{
			Integer tdprec = m_flavorPrecisions.get(value);
			if(tdprec != null)
				this.m_dateValuePrecision = tdprec;
		}
		super.setFlavorId(value);
	}
	
	/**
	 * Get a new instance of TS representing now
	 */
	public static TS now()
	{
		return new TS(Calendar.getInstance());
	}
	
	/**
	 * Represent this class as an integer, which is the time in milliseconds
	 */
	@Override
	public Integer toInteger() {
		return (int)this.m_dateValue.getTimeInMillis();
	}
	/**
	 * Represent this class as a double, which is the time in milliseconds
	 */
	@Override
	public Double toDouble() {
		return (double)this.m_dateValue.getTimeInMillis();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((m_dateValue == null) ? 0 : m_dateValue.hashCode());
		result = prime
				* result
				+ ((m_dateValuePrecision == null) ? 0 : m_dateValuePrecision
						.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TS other = (TS) obj;
		if (m_dateValue == null) {
			if (other.m_dateValue != null)
				return false;
		} else if (!m_dateValue.equals(other.m_dateValue))
			return false;
		if (m_dateValuePrecision == null) {
			if (other.m_dateValuePrecision != null)
				return false;
		} else if (!m_dateValuePrecision.equals(other.m_dateValuePrecision))
			return false;
		return true;
	}
	/**
	 * Get the next value in the sequence
	 */
	@Override
	public TS nextValue() {
		return this.translateDateInterval(1);
	}
	/**
	 * Get the previous value in the sequence
	 */
	@Override
	public TS previousValue() {
		return this.translateDateInterval(-1);
	}
	/**
	 * Translate the date interval by the specified amount
	 */
	private TS translateDateInterval(int value)
	{
		TS retVal = new TS();
		Calendar newDateValue = Calendar.getInstance();
		newDateValue.setTime(this.m_dateValue.getTime());
		retVal.setDateValue(newDateValue);
		
        switch (this.m_dateValuePrecision)
        {
            case TS.DAY:
                retVal.getDateValue().add(Calendar.DAY_OF_YEAR, value);
                break;
            case TS.HOURNOTIMEZONE:
            case TS.HOUR:
            	retVal.getDateValue().add(Calendar.HOUR_OF_DAY, value);
                break;
            case TS.MINUTE:
            case TS.MINUTENOTIMEZONE:
            	retVal.getDateValue().add(Calendar.MINUTE, value);
                break;
            case TS.MONTH:
            	retVal.getDateValue().add(Calendar.MONTH, value);
                break;
            case TS.SECOND:
            case TS.SECONDNOTIMEZONE:
            	retVal.getDateValue().add(Calendar.SECOND, value);
                break;
            case TS.YEAR:
            	retVal.getDateValue().add(Calendar.YEAR, value);
                break;
            case TS.FULL:
            case TS.FULLNOTIMEZONE:
            	retVal.getDateValue().add(Calendar.MILLISECOND, value);
                break;
            default:
                throw new RuntimeException("Cannot determine how to translate this date");
        }
        retVal.setDateValuePrecision(this.getDateValuePrecision());
        return retVal;
	}
	/**
	 * Subtract two dates
	 */
	public PQ subtract(TS subtrahend) {
		PQ retVal = new PQ();
		if (subtrahend == null)
            return null;
        else if (this.isNull() || subtrahend.isNull())
            retVal.setNullFlavor(NullFlavor.NoInformation);
        else
        {
            retVal = new PQ(BigDecimal.valueOf(this.getDateValue().getTimeInMillis() - subtrahend.getDateValue().getTimeInMillis()), "ms");
        }
		return retVal;
	}
	/**
	 * subtract the specified PQ from this date
	 */
	public TS subtract(PQ subtrahend) {
		TS retVal = new TS();
		if (subtrahend == null)
            return null;
        else if (this.isNull() || subtrahend.isNull())
            retVal.setNullFlavor(NullFlavor.NoInformation);
        else
        {
        	PQ otherMs = subtrahend.convert("ms");
        	Calendar newCalendar = Calendar.getInstance();
        	newCalendar.setTimeInMillis(new BigDecimal(this.getDateValue().getTimeInMillis()).subtract(otherMs.getValue()).longValue());
            retVal = new TS(newCalendar, this.getDateValuePrecision() != null ? this.getDateValuePrecision() : TS.FULL);
            // How many leap days are in-between this and the new value?
            PQ leapDays = TS.getLeapDays(retVal, this);
            if(leapDays.getValue().equals(0))
                retVal.subtract(leapDays);
        }
        return retVal;
	}
	/**
	 * Add two dates
	 */
	public TS add(PQ augend) {
		TS retVal = new TS();
		if (augend == null)
            return null;
        else if (this.isNull() || augend.isNull())
            retVal.setNullFlavor(NullFlavor.NoInformation);
        else
        {
        	PQ otherMs = augend.convert("ms");
        	Calendar newCalendar = Calendar.getInstance();
        	newCalendar.setTimeInMillis(new BigDecimal(this.getDateValue().getTimeInMillis()).add(otherMs.getValue()).longValue());
            retVal = new TS(newCalendar, this.getDateValuePrecision() != null ? this.getDateValuePrecision() : TS.FULL);
            // How many leap days are in-between this and the new value?
            PQ leapDays = TS.getLeapDays(retVal, this);
            if(leapDays.getValue().equals(0))
                retVal.add(leapDays);
        }
        return retVal;
	}
	
	/**
	 * Get the number of leap days between a and b
	 */
	private static PQ getLeapDays(TS a, TS b)
	{
		if (a == null || b == null)
            return null;
        
        PQ retVal = new PQ(new BigDecimal(0), "d");
        TS low = a, high = b;
        int val = 1;
        if (a.compareTo(b) > 0)
        {
            low = b;
            high = a;
            val = -1;
        }
        for (int i = low.getDateValue().get(Calendar.YEAR); i < high.getDateValue().get(Calendar.YEAR); i++)
            if(new GregorianCalendar().isLeapYear(i))
                retVal = retVal.add(new PQ(new BigDecimal(val), "d"));
        return retVal;		
	}
	
	/**
	 * Translate (add)
	 */
	@Override
	public TS translate(PQ translation) {
		return this.add(translation);
	}
	/**
	 * Distance (subtract)
	 */
	@Override
	public PQ distance(TS other) {
		return this.subtract(other);
	}
	/* (non-Javadoc)
	 * @see ca.marc.everest.datatypes.generic.QTY#validate()
	 */
	@Override
	public boolean validate() {
		 return this.isNull() ^ ((this.m_dateValue != null || this.getUncertainRange() != null) &&
	                ((this.getUncertainty() != null && this.getUncertainty() instanceof PQ && PQ.isValidTimeFlavor((PQ)this.getUncertainty())) || (this.getUncertainty() == null)) &&
	                ((this.getUncertainRange() != null && this.getUncertainRange().getLow() instanceof PQ && this.getUncertainRange().getHigh() instanceof PQ && PQ.isValidTimeFlavor((PQ)this.getUncertainRange().getLow()) && PQ.isValidTimeFlavor((PQ)this.getUncertainRange().getHigh())) || this.getUncertainRange() == null) &&
	                (((this.getDateValue() != null) ^ (this.getUncertainRange() != null)) || (this.getDateValue() == null && this.getUncertainRange() == null)));

	}
	/* (non-Javadoc)
	 * @see ca.marc.everest.datatypes.generic.PDV#validateEx()
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
        List<IResultDetail> result = (List<IResultDetail>)super.validateEx();
        if (this.getDateValue() == null)
            result.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "TS", "Value must be populated with an valid HL7 Date", null));
        return result;
	}
	/* (non-Javadoc)
	 * @see ca.marc.everest.datatypes.ANY#semanticEquals(ca.marc.everest.datatypes.interfaces.IAny)
	 */
	@Override
	public BL semanticEquals(IAny other) {
		BL baseEq = super.semanticEquals(other);
        if (!baseEq.toBoolean())
            return baseEq;

        // Null-flavored
        if (this.isNull() && other.isNull())
            return BL.TRUE;
        else if (this.isNull() ^ other.isNull())
            return BL.FALSE;

        // Values are equal?
        TS tsOther = (TS)other;
        
        if (tsOther.getDateValuePrecision() != null &&
            this.getDateValuePrecision() != null &&
            tsOther.hasTimeZone() ^
            this.hasTimeZone())
        {
        	BL retVal = new BL();
            retVal.setNullFlavor(NullFlavor.NoInformation);
            return retVal;
        }
        else if (tsOther.getDateValuePrecision().equals(this.getDateValuePrecision()))
            return BL.fromBoolean(tsOther.getValue().equals(this.getValue()));
        else if (tsOther.getUncertainRange() != null && this.getUncertainRange() != null &&
            !tsOther.getUncertainRange().isNull() && !this.getUncertainRange().isNull())
            return this.getUncertainRange().semanticEquals(tsOther.getUncertainRange());
        return BL.FALSE;
	}

	/**
	 * Return true if this timestamp carries a timezone
	 */
	public boolean hasTimeZone()
	{
		switch (this.getDateValuePrecision())
        {
            case TS.FULLNOTIMEZONE:
            case TS.HOURNOTIMEZONE:
            case TS.MINUTENOTIMEZONE:
            case TS.SECONDNOTIMEZONE:
                return false;
            default:
                return true;
        }
	}
	
	/**
	 * Compare this TS instance to another
	 */
	@Override
	public int compareTo(PDV<?> o)
	{
		if(o instanceof TS)
		{
			TS other = (TS)o;
			 if (other == null || other.isNull())
	             return 1;
	         else if (this.isNull() && !other.isNull())
	             return -1;
	         else
	             return this.getDateValue().compareTo(other.getDateValue());
		}
		else
			return super.compareTo(o);

	}
	
}
