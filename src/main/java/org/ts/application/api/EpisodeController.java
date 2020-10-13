package org.ts.application.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.application.services.EpisodeService;
import org.ts.data.entities.Episode;

@Controller
@RequestMapping(path = "/episodes")
public class EpisodeController {

	@Autowired
	private EpisodeService episodeService;

	/**
	 * 
	 * Adds a new episode to an existing encounter via POST through URL:
	 * <code>/episodes</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 *{
	 *   "name": "Tim Smith",
	 *   "jobTitle": "Sales Director",
	 *   "email": "pd@mail.com",
	 *   "institution": "University of Venda",
	 *   "country": "UK",
	 *   "contactNumber" : "08934514355",
	 *   "addressLine1" : "56 Barnet Street", 
	 *   "addressLine2" : "Cape Town",
	 *   "addressLine3" : "", 
	 *   "postcode" : "8800", 
	 *   "encounter": {"id": 111}
	 * }
	 * </code>
	 * 
	 * @param episode the episode (can be a JSON payload) to be added to the
	 *                  system.
	 * 
	 * @return the newly added episode
	 */
	@PostMapping(path = "")
	public @ResponseBody Episode addNewApplicationEpisode(@RequestBody Episode episode) {
		try {
			Episode newepisode = episodeService.addEpisode(episode);
			return newepisode;
		} catch (ConstraintViolationException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e);
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Fetches an active Episode via GET through URL:
	 * <code>/episodes/active/{episodeId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /episodes/active/1
	 * </code>
	 * 
	 * @param episodeId the episode ID as a request parameter to be used in the
	 *                    query
	 * 
	 * @return an active episode if found
	 */
	@GetMapping(path = "/active/{episodeId}")
	public @ResponseBody Episode getEpisode(@PathVariable Long episodeId) {
		try {
			Episode episode = episodeService.getActiveEpisode(episodeId);
			return episode;
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Deletes a episode via DELETE method through base URL:
	 * <code>/episodes/{episodeId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /episodes/1
	 * </code>
	 * 
	 * @param episodeId the ID of the episode that is to be deleted from the
	 *                    system.
	 * 
	 * @return a string that says 'Deleted'
	 * 
	 */
	@DeleteMapping(path = "/{episodeId}")
	public @ResponseBody String deleteEpisode(@PathVariable Long episodeId) {
		try {
			episodeService.deleteEpisode(episodeId);
			return "Deleted";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Retires a episode via POST through URL:
	 * <code>/episodes/retire/{episodeId}</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 *  /episodes/retire/1
	 * </code>
	 * 
	 * @param episodeId the ID of the episode to be retired from the system.
	 * 
	 * @return a string that says 'Retired'
	 */
	@PostMapping(path = "/retire/{episodeId}")
	public @ResponseBody String retireEpisode(@PathVariable Long episodeId) {
		try {
			episodeService.retireEpisode(episodeId);
			return "Retired";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Fetches all active qualification types via GET through URL:
	 * <code>/episodes</code>.
	 * 
	 * @return a list of all active qualification types in JSON or XML depending on
	 *         client episodes
	 * 
	 */
	@GetMapping(path = "")
	public @ResponseBody Iterable<Episode> getAllEpisodes() {
		// This returns a JSON or XML with the episodes
		return episodeService.getEpisodes();
	}

	/**
	 * 
	 * Fetches active episode entries for a particular encounter via GET through
	 * URL: <code>/episodes/encounter/{encounterId}</code>.
	 * 
	 * @param encounterId the ID of the encounter ID to filter work experience
	 *                    entries for.
	 * 
	 * @return a list of all active episode entries for a particular encounter
	 * 
	 */
	@GetMapping(path = "/encounter/{encounterId}")
	public @ResponseBody Iterable<Episode> getEpisodes(@PathVariable Long encounterId) {
		// This returns a JSON or XML with the workExperiences
		return episodeService.getEpisodes(encounterId);
	}

}
