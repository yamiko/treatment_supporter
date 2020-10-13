package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Concept;

/**
 * 
 * Provides CRUD operations for {@link Concept}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface ConceptRepository extends CrudRepository<Concept, Long> {

	/**
	 * Returns an optional {@link Concept} given its ID.
	 *
	 * @param id the identifier to be used as a search key
	 * 
	 * @return an optional concept DTO that matches the search criteria
	 */
	Optional<Concept> findById(Long id);

	/**
	 * Persists a given {@link Concept} DTO to the database.
	 *
	 * @param concept the concept DTO to be persisted
	 * 
	 * @return the concept instance that has been persisted in the database
	 */
	<S extends Concept> S save(S concept);

	/**
	 * Returns all {@link Concept}s that match a given name.
	 *
	 * @param name name to be used in the search criteria
	 * 
	 * @return list of concept instances that match the search criteria
	 */
	List<Concept> findAllByName(String name);

	/**
	 * Returns all {@link Concept}s.
	 *
	 * @param
	 * 
	 * @return list of all concept instances from the database
	 */
	List<Concept> findAll();

}
