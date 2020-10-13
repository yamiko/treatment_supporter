package org.ts.application.services;

import java.time.LocalDateTime;
import java.util.List;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Patient;
import org.ts.data.entities.RegimenCategory;
import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with patients.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface PatientService {

	/**
	 * Adds a new {@link Patient} instance.
	 *
	 * @param patient a patient instance to add
	 * 
	 * @return a newly added patient instance
	 */
	public Patient addPatient(Patient patient);

	/**
	 * Fetches a given {@link Patient} instance if found and active.
	 *
	 * @param patientId an identifier to be used in the search criteria
	 * 
	 * @return a patient instance if found
	 */
	public Patient getActivePatient(Long patientId) throws EntryNotFoundException, EntryNotActiveException;

	/**
	 * Marks a given {@link Patient} instance as deleted in the database.
	 *
	 * @param patientId an identifier to be used in the search criteria
	 * 
	 */
	public void deletePatient(Long patientId) throws EntryNotFoundException;

	/**
	 * Marks a given {@link Patient} instance as retired in the database.
	 *
	 * @param patientId an identifier to be used in the search criteria
	 * 
	 */
	public void retirePatient(Long patientId) throws EntryNotFoundException;

	/**
	 * Fetches all active {@link Patient} instances from the database.
	 *
	 * @param
	 * 
	 * @return list of active patient instances
	 */
	public List<Patient> getPatients();
	
	/**
	 * Fetches all applicable {@link RegimenCategory} instances for this patient.
	 *
	 * @param patientId the ID of the patient to be used as the filter criteria
	 * @param encounterDate the date of the encounter to be used as the filter criteria
	 * 
	 * @return list of regimen categories that are applicable to this patient
	 */
	public List<RegimenCategory> getRecommendedRegimenCategories(Long patientId, LocalDateTime encounterDate);
	

}
