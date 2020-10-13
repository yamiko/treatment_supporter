package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Episode;

/**
 * 
 * Provides CRUD operations for {@link Episode}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface EpisodeRepository extends CrudRepository<Episode, Long> {

	/**
	 * Returns an optional {@link Episode} given its id.
	 *
	 * @param id ID field user as a key for the search operation
	 * 
	 * @return an optional work experience instance
	 */
	Optional<Episode> findById(Long id);

	/**
	 * Persists the given {@link Episode} instance to the database.
	 *
	 * @param episode work experience instance to be saved to the database
	 * 
	 * @return a work experience instance that was saved in the database
	 */
	<S extends Episode> S save(S episode);

	/**
	 * Returns all {@link Episode} instances.
	 *
	 * @param
	 * 
	 * @return a list of all work experience instances from the database
	 */
	List<Episode> findAll();

}
