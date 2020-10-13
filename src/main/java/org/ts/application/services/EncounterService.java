package org.ts.application.services;

import java.util.List;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Encounter;
import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with encounters.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface EncounterService {

	/**
	 * Adds a new {@link Encounter} instance to the the database.
	 *
	 * @param encounter a encounter to added
	 * 
	 * @return a newly added encounter instance
	 */
	public Encounter addEncounter(Encounter encounter);

	/**
	 * Fetches an active {@link Encounter} instance from the database.
	 *
	 * @param encounterId ID to be used as a key field during search
	 * 
	 * @return a encounter instance that matches the search criteria
	 */
	public Encounter getActiveEncounter(Long encounterId) throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Marks a given {@link Encounter} instance as deleted in the database.
	 *
	 * @param encounterId ID to be used as a key field during search
	 * 
	 */
	public void deleteEncounter(Long encounterId) throws EntryNotFoundException;

	/**
	 * Marks a given {@link Encounter} instance as retired in the database.
	 *
	 * @param encounterId ID to be used as a key field during search
	 * 
	 */
	public void retireEncounter(Long encounterId) throws EntryNotFoundException;

	/**
	 * Fetches all active {@link Encounter} instances from the database.
	 *
	 * @param
	 * 
	 * @return a list of all active encounters in the database
	 */
	public List<Encounter> getEncounters();

	/**
	 * Fetches all active {@link Encounter} instances for a specified patient from
	 * the database.
	 *
	 * @param patientId the ID of the patient to filter encounter entries for
	 * 
	 * @return a list of all active encounters in the database
	 */
	public List<Encounter> getEncounters(Long patientId);
}
