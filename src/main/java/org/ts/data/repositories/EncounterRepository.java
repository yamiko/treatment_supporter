package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Encounter;

/**
 * 
 * Provides CRUD operations for {@link Encounter}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface EncounterRepository extends CrudRepository<Encounter, Long> {

	/**
	 * Returns an optional {@link Encounter} given its id.
	 *
	 * @param id ID field user as a key for the search operation
	 * 
	 * @return an optional work experience instance
	 */
	Optional<Encounter> findById(Long id);

	/**
	 * Persists the given {@link Encounter} instance to the database.
	 *
	 * @param encounter work experience instance to be saved to the database
	 * 
	 * @return a work experience instance that was saved in the database
	 */
	<S extends Encounter> S save(S encounter);

	/**
	 * Returns all {@link Encounter} instances.
	 *
	 * @param
	 * 
	 * @return a list of all work experience instances from the database
	 */
	List<Encounter> findAll();

}
