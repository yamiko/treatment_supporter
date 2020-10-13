package org.ts.application.services;

import java.util.List;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Concept;
import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with concepts.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface ConceptService {

	/**
	 * Fetches a given {@link Concept} instance if found and active.
	 *
	 * @param conceptId an identifier to be used in the search criteria
	 * 
	 * @return a concept instance if found
	 */
	public Concept getActiveConcept(Long conceptId) throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Fetches a given {@link Concept} instance if found and active.
	 *
	 * @param name the name of the concept to be used in the search criteria
	 * 
	 * @return a concept instance if found
	 */
	public Concept getActiveConcept(String name) throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Fetches all active {@link Concept} instances from the database.
	 *
	 * @param
	 * 
	 * @return list of active concept instances
	 */
	public List<Concept> getConcepts();

}
