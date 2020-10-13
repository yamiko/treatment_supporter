package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Action;

/**
 * 
 * Provides CRUD operations for {@link Action}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface ActionRepository extends CrudRepository<Action, Long> {

	/**
	 * Returns an optional {@link Action} given its ID.
	 *
	 * @param id key to be used in the search criteria
	 * 
	 * @return an optional action instance from the database that matches the search
	 *         criteria
	 */
	Optional<Action> findById(Long id);

	/**
	 * Saves the given {@link Action} instance.
	 *
	 * @param action a action instance to be persisted in the database
	 * 
	 * @return an instance of action that was saved in the database
	 */
	<S extends Action> S save(S action);

	/**
	 * Returns all {@link Action} instances from the database.
	 *
	 * @param
	 * 
	 * @return a list of all action instances from the database
	 */
	List<Action> findAll();

	/**
	 * Returns all {@link Action} instances from the database.
	 *
	 * @param description field to be used as the search key
	 * 
	 * @return a list of all action instances from the database
	 */
	List<Action> findAllByDescription(String description);

}
