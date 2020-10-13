package org.ts.application.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.application.services.ApplicationUserService;
import org.ts.application.services.EncounterService;
import org.ts.application.services.PatientService;
import org.ts.data.entities.ApplicationUser;
import org.ts.data.entities.Encounter;
import org.ts.data.entities.Patient;
import org.ts.data.repositories.EncounterRepository;
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
 * Integration tests for {@link EncounterRepository}.
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
public class EncounterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	EncounterService encounterService;

	@Autowired
	PatientService patientService;

	@Autowired
	ApplicationUserService userService;

	Encounter encounter, encounter2, invalidEncounter, fetchedEncounter;

	Patient patient;
	
	ApplicationUser user;

	@BeforeEach
	void init() {
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

		// Create valid encounters
		encounter = new Encounter();
		encounter.setEncounterDate(LocalDateTime.of(2020, Month.JUNE, 15, 13, 45));
		encounter.setRetired(Lookup.NOT_RETIRED);
		encounter.setVoided(Lookup.NOT_VOIDED);

		encounter2 = new Encounter();
		encounter2.setEncounterDate(LocalDateTime.of(2020, Month.JUNE, 17, 13, 45));
		encounter2.setRetired(Lookup.NOT_RETIRED);
		encounter2.setVoided(Lookup.NOT_VOIDED);

		// Create an invalid encounter
		invalidEncounter = new Encounter();
		invalidEncounter.setRetired(Lookup.NOT_RETIRED);
		invalidEncounter.setVoided(Lookup.NOT_VOIDED);

	}

	int FALSE = 0;

	@Test
	@WithMockUser
	public void testGetEncounters() throws Exception {
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter2.setPatient(patient);
		encounterService.addEncounter(encounter);
		encounterService.addEncounter(encounter2);

		MvcResult result = mockMvc.perform(get("/encounters").with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.[0].patient.firstName").value("John")).andReturn();

		assertNotNull(result);
	}

	@Test
	@WithMockUser
	public void testAddEncounter() throws Exception {
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		log.info("Patient ID -> " + patient.getId());
		
		encounter.setPatient(patient);

		MvcResult result = mockMvc
		        .perform(post("/encounters").content(asJsonString(encounter))
		                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
		                .with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.patient.firstName").value("John")).andReturn();

		assertNotNull(result);

		String response = result.getResponse().getContentAsString();

		Long encounterId = ((Integer) (JsonPath.parse(response).read("$.id"))).longValue();
		encounter = encounterService.getActiveEncounter(encounterId);

		assertAll("Properties", () -> assertTrue(encounter.getId() > 0),
		        () -> assertTrue(encounter.getId() > 0), () -> assertTrue(encounter.getId() > 0),
		        () -> assertTrue(encounter.getPatient().getFirstName().equals("John")),
		        () -> assertNotNull(encounter.getCreatedDate()),
		        () -> assertNotNull(encounter.getCreatedBy()),
		        () -> assertNotNull(encounter.getLastModifiedBy()),
		        () -> assertNotNull(encounter.getModifiedDate()));
	}

	@Test
	@WithMockUser
	public void testFindNonExistentEncounter() throws Exception {
		Long encounterId = -1L;

		mockMvc.perform(
		        get("/encounters/active/{encounterId}", encounterId).with(csrf().asHeader()))
		        .andExpect(status().isNotFound()).andReturn();
	}

	@Test
	@WithMockUser
	public void testVoidEncounter() throws Exception {
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		log.info("Added encounter with ID: " + encounter.getId());

		Long encounterId = encounter.getId();

		mockMvc.perform(
		        delete("/encounters/{encounterId}", encounterId).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		assertThrows(EntryNotFoundException.class, () -> {
			encounter = encounterService.getActiveEncounter(encounterId);
		});
	}

	@Test
	@WithMockUser
	public void testRetireEncounter() throws Exception {
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		log.info("Added encounter with ID: " + encounter.getId());

		Long encounterId = encounter.getId();

		mockMvc.perform(
		        post("/encounters/retire/{encounterId}", encounterId).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		assertThrows(EntryNotActiveException.class, () -> {
			encounter = encounterService.getActiveEncounter(encounterId);
		});
	}

	@Test
	@WithMockUser("Peter")
	public void testAuditing() throws Exception {
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		log.info("Added encounter with ID: " + encounter.getId());
		
		MvcResult result = null;
		result = mockMvc
		        .perform(post("/encounters").content(asJsonString(encounter))
		                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
		                .with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.patient.firstName").value("John")).andReturn();

		assertNotNull(result);
		String response = result.getResponse().getContentAsString();

		Long encounterId = ((Integer) (JsonPath.parse(response).read("$.id"))).longValue();
		encounter = encounterService.getActiveEncounter(encounterId);

		assertAll("Properties", () -> assertTrue(encounter.getCreatedBy().equals("Peter")),
		        () -> assertTrue(encounter.getLastModifiedBy().equals("Peter")));
	}

	@Test
	@WithMockUser
	public void testConstraintViolations() throws Exception {
		mockMvc.perform(post("/encounters").content(asJsonString(invalidEncounter))
		        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).with(csrf().asHeader()))
		        .andExpect(status().isNotAcceptable()).andReturn();
	}

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
