package org.ts.application.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.application.services.ApplicationUserService;
import org.ts.application.services.ConceptService;
import org.ts.application.services.EncounterService;
import org.ts.application.services.MetaDataService;
import org.ts.application.services.ObservationService;
import org.ts.application.services.PatientService;
import org.ts.data.entities.ApplicationUser;
import org.ts.data.entities.Encounter;
import org.ts.data.entities.Observation;
import org.ts.data.entities.Patient;
import org.ts.data.repositories.ObservationRepository;
import org.ts.utils.Lookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import lombok.extern.slf4j.Slf4j;

/**
 * Integration tests for {@link ObservationRepository}.
 * 
 * @author Yamiko Msosa
 *
 */
@Transactional
@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=validate" })
@AutoConfigureMockMvc
public class PatientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObservationService observationService;

	@Autowired
	EncounterService encounterService;

	@Autowired
	MetaDataService metaDataService;

	@Autowired
	ConceptService conceptService;

	@Autowired
	PatientService patientService;

	@Autowired
	ApplicationUserService userService;

	Observation observation, observation2, observation3, invalidObservation, fetchedObservation;

	Patient patient;
	
	Encounter encounter;
	
	ApplicationUser user;

	@BeforeEach
	void init() {
		// Create a valid encounter
		encounter = new Encounter();
		encounter.setEncounterDate(LocalDateTime.now());
		encounter.setRetired(Lookup.NOT_RETIRED);
		encounter.setVoided(Lookup.NOT_VOIDED);

		// Create a valid patient
		patient = new Patient("John", "", "Smith");
		
		patient.setAddressLine1("Address 1");
		patient.setCountry("UK");
		patient.setGender("M");
		patient.setEmail("email@email.com");
		patient.setDateOfBirth(LocalDate.of(1987, Month.JUNE, 15));

		patient.setRetired(Lookup.NOT_RETIRED);
		patient.setVoided(Lookup.NOT_VOIDED);
		
		// Create valid user
		user = new ApplicationUser("test1", "password1", "Test User1");
		user.setRetired(Lookup.NOT_RETIRED);
		user.setVoided(Lookup.NOT_VOIDED);

		// Create valid observations
		observation = new Observation();
		observation.setObservationDate(LocalDateTime.now());
		observation.setRetired(Lookup.NOT_RETIRED);
		observation.setVoided(Lookup.NOT_VOIDED);

		observation2 = new Observation();
		observation2.setObservationDate(LocalDateTime.now());
		observation2.setRetired(Lookup.NOT_RETIRED);
		observation2.setVoided(Lookup.NOT_VOIDED);

		observation3 = new Observation();
		observation3.setObservationDate(LocalDateTime.now());
		observation3.setRetired(Lookup.NOT_RETIRED);
		observation3.setVoided(Lookup.NOT_VOIDED);

		// Create an invalid observation
		invalidObservation = new Observation();
		invalidObservation.setRetired(Lookup.NOT_RETIRED);
		invalidObservation.setVoided(Lookup.NOT_VOIDED);

	}

	int FALSE = 0;

	@Test
	@WithMockUser
	public void testGetRecommendedRegimenCategoriesWithVeryHighTemperature() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		observation.setEncounter(encounter);
		observation.setConcept(conceptService.getActiveConcept("Temperature"));
		observation.setIntValue(45);

		
		observationService.addObservation(observation);

		MvcResult result = mockMvc.perform(
		        post("/patients/{patientId}/recommendations", patient.getId()).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.[0].name").value("Fever in those with very high temperature")).andReturn();

		assertNotNull(result);
	}

/*	@Test
	@WithMockUser
	public void testGetRecommendedRegimenCategoriesWithChronicDehydration() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		observation.setEncounter(encounter);
		observation.setConcept(conceptService.getActiveConcept("Temperature"));
		observation.setIntValue(39);

		observation2.setEncounter(encounter);
		observation2.setConcept(conceptService.getActiveConcept("Presenting condition"));
		observation2.setConceptValue(conceptService.getActiveConcept("Chronic dehydration"));
		
		observationService.addObservation(observation);
		observationService.addObservation(observation2);

		MvcResult result = mockMvc.perform(
		        post("/patients/{patientId}/recommendations", patient.getId()).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.[0].name").value("Fever in those presenting with chronic dehydration")).andReturn();

		assertNotNull(result);
	}

	@Test
	@WithMockUser
	public void testGetRecommendedRegimenCategoriesWithSevereMalnourishment() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		observation.setEncounter(encounter);
		observation.setConcept(conceptService.getActiveConcept("Temperature"));
		observation.setIntValue(39);

		observation2.setEncounter(encounter);
		observation2.setConcept(conceptService.getActiveConcept("Presenting condition"));
		observation2.setConceptValue(conceptService.getActiveConcept("Severe malnourishment"));
		
		observationService.addObservation(observation);
		observationService.addObservation(observation2);

		MvcResult result = mockMvc.perform(
		        post("/patients/{patientId}/recommendations", patient.getId()).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.[0].name").value("Fever in those presenting with severe malnourishment")).andReturn();

		assertNotNull(result);
	} */
	
/*	@Test
	@WithMockUser
	public void testGetRecommendedRegimenCategories() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		observation.setEncounter(encounter);
		observation.setConcept(conceptService.getActiveConcept("Presenting condition"));
		observation.setConceptValue(conceptService.getActiveConcept("Fever"));

		observation2.setEncounter(encounter);
		observation2.setConcept(conceptService.getActiveConcept("Presenting condition"));
		observation2.setConceptValue(conceptService.getActiveConcept("Chronic dehydration"));

		observation3.setEncounter(encounter);
		observation3.setConcept(conceptService.getActiveConcept("Temperature"));
		observation3.setIntValue(45);

		// observationService.addObservation(observation);
		
		observationService.addObservation(observation3);

		MvcResult result = mockMvc.perform(
		        post("/patients/{patientId}/recommendations", patient.getId()).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.encounter.patient.firstName").value("John")).andReturn();

		assertNotNull(result);
	}*/


	public static String asJsonString(final Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
