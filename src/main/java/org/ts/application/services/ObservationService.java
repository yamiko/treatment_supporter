package org.ts.application.services;

import java.util.List;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Observation;
import org.ts.data.entities.Encounter;
import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with observations for
 * specific encounters.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface ObservationService {

	/**
	 * Adds a {@link Observation} to an existing {@link Encounter} instance.
	 *
	 * @param observation a new observation that has a valid reference to an
	 *                      existing encounter
	 * 
	 * @return a observation that has been added to a encounters CV
	 */
	public Observation addObservation(Observation observation)
	        throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Gets an active {@link Observation} instance given its identifier.
	 *
	 * @param observationId an identifier to be used in the search criteria
	 * 
	 * @return an active observation if found
	 */
	public Observation getActiveObservation(Long observationId)
	        throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Marks a given {@link Observation} instance as deleted in the database.
	 *
	 * @param observationId an identifier to be used in the search criteria
	 * 
	 */
	public void deleteObservation(Long observationId) throws EntryNotFoundException;

	/**
	 * Marks a given {@link Observation} instance as retired, to bar it from
	 * future usage.
	 *
	 * @param observationId an identifier to be used in the search criteria
	 * 
	 */
	public void retireObservation(Long observationId) throws EntryNotFoundException;

	/**
	 * Lists all active {@link Observation} instances from the database.
	 *
	 * @param
	 * 
	 * @return a list of active observation instances
	 */
	public List<Observation> getObservations();

	/**
	 * Lists all active {@link Observation} instances for a specified encounter
	 * from the database.
	 *
	 * @param encounterId the encounter to filter observation instances for
	 * 
	 * @return a list of active observation instances
	 */
	public List<Observation> getObservations(Long encounterId);
}
