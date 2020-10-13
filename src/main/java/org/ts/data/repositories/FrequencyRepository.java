package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Frequency;

/**
 * 
 * Provides CRUD operations for {@link Frequency}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface FrequencyRepository extends CrudRepository<Frequency, Long> {

	/**
	 * Returns an optional {@link Frequency} given its id.
	 *
	 * @param id ID field user as a key for the search operation
	 * 
	 * @return an optional work experience instance
	 */
	Optional<Frequency> findById(Long id);

	/**
	 * Persists the given {@link Frequency} instance to the database.
	 *
	 * @param frequency work experience instance to be saved to the database
	 * 
	 * @return a work experience instance that was saved in the database
	 */
	<S extends Frequency> S save(S frequency);

	/**
	 * Returns all {@link Frequency} instances.
	 *
	 * @param
	 * 
	 * @return a list of all work experience instances from the database
	 */
	List<Frequency> findAll();
	
	/**
	 * Returns all {@link Frequency}(cies) that match a given name.
	 *
	 * @param description description to be used in the search criteria
	 * 
	 * @return list of Frequency instances that match the search criteria
	 */
	List<Frequency> findAllByDescription(String description);

}
