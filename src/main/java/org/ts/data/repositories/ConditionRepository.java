package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Condition;

/**
 * 
 * Provides CRUD operations for {@link Condition}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface ConditionRepository extends CrudRepository<Condition, Long> {

	/**
	 * Returns an optional {@link Condition} given its ID.
	 *
	 * @param id the identifier to be used as a key in the search criteria
	 * 
	 * @return a condition instance that matches a search criteria from the database
	 */
	Optional<Condition> findById(Long id);

	/**
	 * Saves the given {@link Condition} instance to the database.
	 *
	 * @param condition a condition instance to be saved in the database
	 * 
	 * @return a condition instance that was saved in the database
	 */
	<S extends Condition> S save(S condition);

	/**
	 * Returns all {@link Condition} instances from the database.
	 *
	 * @param
	 * 
	 * @return a list of all condition instances from the database
	 */
	List<Condition> findAll();

	/**
	 * Returns all {@link Condition} instances from the database.
	 *
	 * @param description key to be used as the search key
	 * 
	 * @return a list of all condition instances from the database
	 */
	List<Condition> findAllByDescription(String description);

}
