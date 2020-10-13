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
import org.ts.application.services.ObservationService;
import org.ts.data.entities.Observation;

@Controller
@RequestMapping(path = "/observations")
public class ObservationController {

	@Autowired
	private ObservationService observationService;

	/**
	 * 
	 * Adds a new observation to an existing encounter via POST through URL:
	 * <code>/observations</code>.
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
	 * @param observation the observation (can be a JSON payload) to be added to the
	 *                  system.
	 * 
	 * @return the newly added observation
	 */
	@PostMapping(path = "")
	public @ResponseBody Observation addNewApplicationObservation(@RequestBody Observation observation) {
		try {
			Observation newobservation = observationService.addObservation(observation);
			return newobservation;
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
	 * Fetches an active Observation via GET through URL:
	 * <code>/observations/active/{observationId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /observations/active/1
	 * </code>
	 * 
	 * @param observationId the observation ID as a request parameter to be used in the
	 *                    query
	 * 
	 * @return an active observation if found
	 */
	@GetMapping(path = "/active/{observationId}")
	public @ResponseBody Observation getObservation(@PathVariable Long observationId) {
		try {
			Observation observation = observationService.getActiveObservation(observationId);
			return observation;
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Deletes a observation via DELETE method through base URL:
	 * <code>/observations/{observationId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /observations/1
	 * </code>
	 * 
	 * @param observationId the ID of the observation that is to be deleted from the
	 *                    system.
	 * 
	 * @return a string that says 'Deleted'
	 * 
	 */
	@DeleteMapping(path = "/{observationId}")
	public @ResponseBody String deleteObservation(@PathVariable Long observationId) {
		try {
			observationService.deleteObservation(observationId);
			return "Deleted";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Retires a observation via POST through URL:
	 * <code>/observations/retire/{observationId}</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 *  /observations/retire/1
	 * </code>
	 * 
	 * @param observationId the ID of the observation to be retired from the system.
	 * 
	 * @return a string that says 'Retired'
	 */
	@PostMapping(path = "/retire/{observationId}")
	public @ResponseBody String retireObservation(@PathVariable Long observationId) {
		try {
			observationService.retireObservation(observationId);
			return "Retired";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Fetches all active qualification types via GET through URL:
	 * <code>/observations</code>.
	 * 
	 * @return a list of all active qualification types in JSON or XML depending on
	 *         client observations
	 * 
	 */
	@GetMapping(path = "")
	public @ResponseBody Iterable<Observation> getAllObservations() {
		// This returns a JSON or XML with the observations
		return observationService.getObservations();
	}

	/**
	 * 
	 * Fetches active observation entries for a particular encounter via GET through
	 * URL: <code>/observations/encounter/{encounterId}</code>.
	 * 
	 * @param encounterId the ID of the encounter ID to filter work experience
	 *                    entries for.
	 * 
	 * @return a list of all active observation entries for a particular encounter
	 * 
	 */
	@GetMapping(path = "/encounter/{encounterId}")
	public @ResponseBody Iterable<Observation> getObservations(@PathVariable Long encounterId) {
		// This returns a JSON or XML with the workExperiences
		return observationService.getObservations(encounterId);
	}

}
