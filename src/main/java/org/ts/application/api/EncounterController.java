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
import org.ts.application.services.EncounterService;
import org.ts.data.entities.Encounter;

@Controller 
@RequestMapping(path = "/encounters") 
public class EncounterController {

	@Autowired
	private EncounterService encounterService;

	/**
	 * 
	 * Adds a new encounter to an existing patient via POST through URL:
	 * <code>/encounters</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 * {
	 *   "name": "PhD in Agroforestry",
	 *   "institution": "university of Texas",
	 *   "country": "USA",
	 *   "dateObtained": "2008-06-15",
	 *   "patient": {"id": 111},
	 *   "encounterType" : {"id": 98}
	 *  }
	 * </code>
	 * 
	 * @param encounter the encounter (can be a JSON payload) to be added to
	 *                      the system.
	 * 
	 * @return the newly added encounter
	 */
	@PostMapping(path = "")
	public @ResponseBody Encounter addNewApplicationEncounter(@RequestBody Encounter encounter) {
		try {
			Encounter newencounter = encounterService.addEncounter(encounter);
			return newencounter;
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
	 * Fetches an active encounter via GET through URL:
	 * <code>/encounters/active/{encounterId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /encounters/active/1
	 * </code>
	 * 
	 * @param encounterId the encounter ID to be used in the query
	 * 
	 * @return an active encounter if found
	 */
	@GetMapping(path = "/active")
	public @ResponseBody Encounter getEncounter(@PathVariable Long encounterId) {
		try {
			Encounter encounter = encounterService.getActiveEncounter(encounterId);
			return encounter;
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Deletes a encounter via DELETE method through base URL:
	 * <code>/encounters/{encounterId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /encounters/1
	 * </code>
	 * 
	 * @param encounterId the encounter ID of the encounter to be
	 *                        deleted from the system.
	 * 
	 * @return a string that says 'Deleted'
	 * 
	 */
	@DeleteMapping(path = "/{encounterId}")
	public @ResponseBody String deleteEncounter(@PathVariable Long encounterId) {
		try {
			encounterService.deleteEncounter(encounterId);
			return "Deleted";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Retires a encounter via POST through URL:
	 * <code>/encounters/retire/{encounterId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /encounters/retire/1
	 * </code>
	 * 
	 * @param encounterId the ID of the encounter that is to be retired from
	 *                        the system.
	 * 
	 * @return a string that says 'Retired'
	 * 
	 */
	@PostMapping(path = "/retire/{encounterId}")
	public @ResponseBody String retireEncounter(@PathVariable Long encounterId) {

		try {
			encounterService.retireEncounter(encounterId);
			return "Retired";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Fetches all active encounters via GET through URL:
	 * <code>/encounters</code>.
	 * 
	 * @return a list of all active encounters
	 *
	 */
	@GetMapping(path = "")
	public @ResponseBody Iterable<Encounter> getAllEncounters() {
		// This returns a JSON or XML with the encounters
		return encounterService.getEncounters();
	}

	/**
	 * 
	 * Fetches all active encounter entries via GET through URL:
	 * <code>/encounters/patient/{patientId}</code>.
	 * 
	 * @param patientId the ID of the patient ID to filter work experience
	 *                    entries for.
	 * 
	 * @return a list of all active encounters entries for a particular
	 *         patient
	 * 
	 */
	@GetMapping(path = "/patient/{patientId}")
	public @ResponseBody Iterable<Encounter> getEncounters(@PathVariable Long patientId) {
		// This returns a JSON or XML with the workExperiences
		return encounterService.getEncounters(patientId);
	}

}
