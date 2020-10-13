package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.Patient;

/**
 * 
 * Provides CRUD operations for {@link Patient}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface PatientRepository extends CrudRepository<Patient, Long> {

	/**
	 * Returns an optional {@link Patient} given its ID.
	 *
	 * @param id the identifier to be used as the search key
	 * 
	 * @return an optional patient DTO that matches the search criteria
	 */
	Optional<Patient> findById(Long id);

	/**
	 * Persists a given {@link Patient} instance.
	 *
	 * @param patient the patient instance to be persisted in the database
	 * 
	 * @return the patient that has been persisted in this operation
	 */
	<S extends Patient> S save(S patient);

	/**
	 * Persists and flushes a given {@link Patient} instance.
	 *
	 * @param patient the patient instance to be persisted in the database
	 * 
	 * @return the patient that has been persisted in this operation
	 */
	<S extends Patient> S saveAndFlush(S patient);

	/**
	 * Returns all {@link Patient}s from the database.
	 *
	 * @param
	 * 
	 * @return list of all patient instances from the database
	 */
	List<Patient> findAll();

}
