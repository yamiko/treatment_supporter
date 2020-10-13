package org.ts.data.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import org.ts.data.entities.VocabularySet;

/**
 * 
 * Provides CRUD operations for {@link VocabularySet}.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
public interface VocabularySetRepository extends CrudRepository<VocabularySet, Long> {

	/**
	 * Returns an optional {@link VocabularySet} given its ID.
	 *
	 * @param id identifier to be used in the search criteria
	 * 
	 * @return an optional vocabularySet instance that matches the search criteria
	 */
	Optional<VocabularySet> findById(Long id);

	/**
	 * Saves the given {@link VocabularySet}.
	 *
	 * @param vocabularySet a vocabularySet instance to be persisted in the database
	 * 
	 * @return the persisted vocabularySet instance from the database
	 */
	<S extends VocabularySet> S save(S vocabularySet);

	/**
	 * Saves and flushes the given {@link VocabularySet}.
	 *
	 * @param vocabularySet a vocabularySet instance to be persisted in the database
	 * 
	 * @return the persisted vocabularySet instance from the database
	 */
	<S extends VocabularySet> S saveAndFlush(S vocabularySet);

	/**
	 * Returns all {@link VocabularySet} instances from the database.
	 *
	 * @param
	 * 
	 * @return list of vocabularySet instances from the database
	 */
	List<VocabularySet> findAll();

	/**
	 * Returns all {@link VocabularySet} instances that match a given name.
	 *
	 * @param name a name to be used as a search parameter
	 * 
	 * @return list of vocabularySet instances that match the search criteria
	 */
	List<VocabularySet> findAllByName(String name);

}
