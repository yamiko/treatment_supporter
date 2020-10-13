package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Regimen;

/**
 * 
 * Provides CRUD operations for {@link Regimen}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface RegimenRepository extends CrudRepository<Regimen, Long> {

	/**
	 * Returns an optional {@link Regimen} given its ID.
	 *
	 * @param id identifier to be used in the search criteria
	 * 
	 * @return an optional regimen instance that matches the search criteria
	 */
	Optional<Regimen> findById(Long id);

	/**
	 * Saves the given {@link Regimen}.
	 *
	 * @param regimen a regimen instance to be persisted in the database
	 * 
	 * @return the persisted regimen instance from the database
	 */
	<S extends Regimen> S save(S regimen);

	/**
	 * Saves and flushes the given {@link Regimen}.
	 *
	 * @param regimen a regimen instance to be persisted in the database
	 * 
	 * @return the persisted regimen instance from the database
	 */
	<S extends Regimen> S saveAndFlush(S regimen);

	/**
	 * Returns all {@link Regimen} instances from the database.
	 *
	 * @param
	 * 
	 * @return list of regimen instances from the database
	 */
	List<Regimen> findAll();

	/**
	 * Returns all {@link Regimen} instances that match a given name.
	 *
	 * @param name a name to be used as a search parameter
	 * 
	 * @return list of regimen instances that match the search criteria
	 */
	List<Regimen> findAllByName(String name);

}
