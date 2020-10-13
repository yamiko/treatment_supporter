package org.ts.utils;

/**
 * 
 * Provides a convenient reference container for commonly used values.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public class Lookup {

	public static final long DEFAULT_USER_ID = -1L;
	public static final int TRUE = 1;
	public static final int FALSE = 0;
	public static final int RETIRED = 1;
	public static final int NOT_RETIRED = 0;
	public static final int VOIDED = 1;
	public static final int NOT_VOIDED = 0;
	
	/*
	 * Treatment frequency atomic abbreviations
	 */
	public static final String EVERY = "q";
	public static final String OTHER = "o";
	public static final String HOURS = "h";
	public static final String DAYS = "d";
	
	/*
	 * Encounter types
	 */
	public static final int SELF_REPORTED = 0;
	public static final int SCHEDULED_VISIT = 1;
	public static final int UNSCHEDULED_VISIT = 2;

	/*
	 * Condition types
	 */
	public static final int CONCEPT_CONDITION = 0;
	public static final int INTEGER_CONDITION = 1;
	public static final int STRING_CONDITION = 2;
	public static final int DATE_TIME_CONDITION = 3;

	/*
	 * Relator types
	 */
	public static final int EQUALS = 0;
	public static final int GREATER_OR_EQUAL = 1;
	public static final int LESS = 2;
	public static final int NOT_EQUAL = 3;
	
	/*
	 * Episode stages
	 */
	public static final int ACTIVE = 0;
	public static final int INACTIVE = 0;
	public static final int ENDED = 0;

	/*
	 * Treatment units
	 */
	public static final String MILLIGRAMS = "mg";
	public static final String GRAMS = "g";
	public static final String TABLETS = "tablets";
}
