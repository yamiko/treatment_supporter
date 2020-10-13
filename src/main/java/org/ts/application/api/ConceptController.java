package org.ts.application.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.application.services.ConceptService;
import org.ts.data.entities.Concept;

@Controller 
@RequestMapping(path = "/concepts") 
public class ConceptController {

	@Autowired
	private ConceptService conceptService;


	/**
	 * 
	 * Fetches an active concept via GET through URL:
	 * <code>/concepts/active/{conceptId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /concepts/active/1
	 * </code>
	 * 
	 * @param conceptId the concept ID as a request parameter to be used in the
	 *                    query
	 * 
	 * @return an active concept if found
	 */
	@GetMapping(path = "/active/{conceptId}")
	public @ResponseBody Concept getConcept(@PathVariable Long conceptId) {
		try {
			Concept concept = conceptService.getActiveConcept(conceptId);
			return concept;
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Fetches an active concept via GET through URL:
	 * <code>/concepts/name</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /concepts/name?name=test5
	 * </code>
	 * 
	 * @param name the concept name as a request parameter to be used in the query
	 * 
	 * @return an active concept if found
	 */
	@GetMapping(path = "/name")
	public @ResponseBody Concept getByConceptName(@RequestParam String name) {
		try {
			Concept concept = conceptService.getActiveConcept(name);
			return concept;
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Fetches all active concepts via GET through URL: <code>/concepts</code>.
	 * 
	 * 
	 */
	@GetMapping(path = "")
	public @ResponseBody Iterable<Concept> getAllConcepts() {
		// This returns a JSON or XML with the concepts
		return conceptService.getConcepts();
	}

}
