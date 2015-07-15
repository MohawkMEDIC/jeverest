package org.marc.everest.datatypes.generic;

/**
 * Represents an implementation of a predicate that uses hash codes
 * @author Justin
 *
 */
public class HashPredicate<T> extends Predicate<T> {

	/**
	 * Constructs an equality predicate
	 * @param value The value to match
	 */
	public HashPredicate(T value) {
		super(value);
	}
	
	/**
	 * Perform match
	 */
	@Override
	public boolean match(T i) {
		return i.hashCode() == this.getScopeValue().hashCode();
	}

}
