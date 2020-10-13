package org.ts.application.services;

import java.util.List;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Episode;
import org.ts.data.entities.Encounter;
import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with episodes for
 * specific encounters.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface EpisodeService {

	/**
	 * Adds a {@link Episode} to an existing {@link Encounter} instance.
	 *
	 * @param episode a new episode that has a valid reference to an
	 *                      existing encounter
	 * 
	 * @return a episode that has been added to a encounters CV
	 */
	public Episode addEpisode(Episode episode)
	        throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Gets an active {@link Episode} instance given its identifier.
	 *
	 * @param episodeId an identifier to be used in the search criteria
	 * 
	 * @return an active episode if found
	 */
	public Episode getActiveEpisode(Long episodeId)
	        throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Marks a given {@link Episode} instance as deleted in the database.
	 *
	 * @param episodeId an identifier to be used in the search criteria
	 * 
	 */
	public void deleteEpisode(Long episodeId) throws EntryNotFoundException;

	/**
	 * Marks a given {@link Episode} instance as retired, to bar it from
	 * future usage.
	 *
	 * @param episodeId an identifier to be used in the search criteria
	 * 
	 */
	public void retireEpisode(Long episodeId) throws EntryNotFoundException;

	/**
	 * Lists all active {@link Episode} instances from the database.
	 *
	 * @param
	 * 
	 * @return a list of active episode instances
	 */
	public List<Episode> getEpisodes();

	/**
	 * Lists all active {@link Episode} instances for a specified encounter
	 * from the database.
	 *
	 * @param encounterId the encounter to filter episode instances for
	 * 
	 * @return a list of active episode instances
	 */
	public List<Episode> getEpisodes(Long encounterId);
}
