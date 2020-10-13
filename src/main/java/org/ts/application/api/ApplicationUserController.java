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
import org.ts.application.services.ApplicationUserService;
import org.ts.application.services.PatientService;
import org.ts.data.entities.ApplicationUser;
import org.ts.data.entities.Patient;

/**
 * 
 * REST service endpoint for <b>application user</b> services available on
 * <code>/users</code>.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Controller
@RequestMapping(path = "/users")
public class ApplicationUserController {

	@Autowired
	private ApplicationUserService userService;

	@Autowired
	private PatientService patientService;
	/**
	 * 
	 * Adds a new patient with application user details via POST through URL:
	 * <code>/users</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 * {
	 * 	 "title": "Mr",
	 *   "firstName" : "Test",
	 *   "middleName" : "",
	 *   "lastName" : "Patient",
	 *   "gender" : "F", 
	 *   "email" : "test@mail.com", 
	 *   "preferredContactNumber" : "08934514355",
	 *	 "alternativeContactNumber" : "", 
	 *   "addressLine1" : "56 Test Road", 
	 *   "addressLine2" : "London",
	 *   "addressLine3" : "", 
	 *   "postcode" : "SE5 3SD", 
	 *   "country": "UK",
	 *   "dateOfBirth": "1995-05-26",
	 *   "applicationUser" : {
	 *     "fullName" : "New User",
	 *     "password" : "testpass",
	 *     "username" : "testuser"
	 * 	 }
	 * }	 
	 * * 
	 * </code>
	 * 
	 * @param user the user (can be a JSON payload) to be added to the system.
	 * 
	 * @return the newly added application user
	 */
	@PostMapping(path = "")
	public @ResponseBody Patient addNewApplicationUser(@RequestBody Patient patient) {
		try {
			
			ApplicationUser newUser = patient.getApplicationUser();
			newUser = userService.addUser(newUser);
			
			patient.setApplicationUser(newUser);
			
			Patient newPatient = patientService.addPatient(patient);
			return newPatient;
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
	 * Fetches an active application user via GET method through URL:
	 * <code>/users/active/{userId}</code>.
	 * <p>
	 * 
	 * Example URL:
	 * 
	 * <code> 
	 *  /users/active/1
	 * </code>
	 * 
	 * @param userId the user ID as a request parameter to be used in the search
	 *               query
	 * 
	 * @return an active application user if found
	 */
	@GetMapping(path = "/active/{userId}")
	public @ResponseBody ApplicationUser getUser(@PathVariable Long userId) {
		try {
			ApplicationUser user = userService.getActiveUser(userId);
			return user;
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (EntryNotActiveException e) {
			throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Deletes an application user via DELETE method through base URL:
	 * <code>/users/{userId}</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 *  /users/1
	 * </code>
	 * 
	 * @param userId the ID of the user to be deleted from the system.
	 * 
	 * @return a string that says 'Deleted'
	 */
	@DeleteMapping(path = "/{userId}")
	public @ResponseBody String deleteUser(@PathVariable Long userId) {
		try {
			userService.deleteUser(userId);
			return "Deleted";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Retires an application user via POST through URL:
	 * <code>/users/retire/{userId}</code>.
	 * <p>
	 * 
	 * Example payload:
	 * 
	 * <code> 
	 *  /users/retire/1
	 * </code>
	 * 
	 * @param userId the ID of the user to be retired from the system.
	 * 
	 * @return a string that says 'Retired'
	 */
	@PostMapping(path = "/retire/{userId}")
	public @ResponseBody String retireUser(@PathVariable Long userId) {

		try {
			userService.retireUser(userId);
			return "Retired";
		} catch (EntryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Fetches all active application users via GET through URL:
	 * <code>/users/all</code>.
	 * 
	 * @return a list of all active application users in JSON or XML depending on
	 *         client preferences
	 * 
	 */
	@GetMapping(path = "")
	public @ResponseBody Iterable<ApplicationUser> getAllUsers() {
		return userService.getUsers();
	}

}
