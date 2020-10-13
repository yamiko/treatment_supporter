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

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.ConstraintViolationException;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.application.exceptions.InconsistentDataException;
import org.ts.application.services.PatientService;
import org.ts.data.entities.Patient;
import org.ts.data.entities.RegimenCategory;

/**
 * 
 * REST service endpoint for <b>patient</b> resources.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Controller
@RequestMapping(path = "/patients")
public class PatientController {

	@Autowired
	private PatientService patientService;

	/**
	 * 
	 * Fetches all applicable recommendations via POST through URL:
	 * <code>/patients/{patientId}/recommendations</code>.
	 * 
	 * 
	 */
	@PostMapping(path = "/{patientId}/recommendations")
	public @ResponseBody Iterable<RegimenCategory> getRecommendedRegimenCategories(@PathVariable Long patientId) {
		// This returns a JSON or XML with the recommended regimen categories
		return patientService.getRecommendedRegimenCategories(patientId, LocalDateTime.now());
	}

}
