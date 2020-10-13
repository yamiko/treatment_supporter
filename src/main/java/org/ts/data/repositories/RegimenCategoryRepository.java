package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.RegimenCategory;

/**
 * 
 * Provides CRUD operations for {@link RegimenCategory}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface RegimenCategoryRepository extends CrudRepository<RegimenCategory, Long> {

	/**
	 * Returns an optional {@link RegimenCategory} given its ID.
	 *
	 * @param id the identifier to be used as a key in the search criteria
	 * 
	 * @return an optional qualification type instance that matches the search
	 *         criteria
	 */
	Optional<RegimenCategory> findById(Long id);

	/**
	 * Saves a given {@link RegimenCategory} instance in to the database.
	 *
	 * @param regimenCategory the qualification type instance to be saved in the
	 *                          database
	 * 
	 * @return the qualification type instance that was saved in the database
	 */
	<S extends RegimenCategory> S save(S regimenCategory);

	/**
	 * Saves and flushes a given {@link RegimenCategory} instance in to the
	 * database.
	 *
	 * @param regimenCategory the qualification type instance to be saved in the
	 *                          database
	 * 
	 * @return the qualification type instance that was saved in the database
	 */
	<S extends RegimenCategory> S saveAndFlush(S regimenCategory);

	/**
	 * Returns all {@link RegimenCategory} instances from the database.
	 *
	 * @param
	 * 
	 * @return list of qualification type instances from the database
	 */
	List<RegimenCategory> findAll();
}
