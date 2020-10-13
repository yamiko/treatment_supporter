package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Observation;

/**
 * 
 * Provides CRUD operations for {@link Observation}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface ObservationRepository extends CrudRepository<Observation, Long> {

	/**
	 * Returns an optional {@link Observation} given its id.
	 *
	 * @param id ID field user as a key for the search operation
	 * 
	 * @return an optional work experience instance
	 */
	Optional<Observation> findById(Long id);

	/**
	 * Persists the given {@link Observation} instance to the database.
	 *
	 * @param observation work experience instance to be saved to the database
	 * 
	 * @return a work experience instance that was saved in the database
	 */
	<S extends Observation> S save(S observation);

	/**
	 * Returns all {@link Observation} instances.
	 *
	 * @param
	 * 
	 * @return a list of all work experience instances from the database
	 */
	List<Observation> findAll();

}
