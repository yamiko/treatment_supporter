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
import org.ts.application.services.EpisodeService;
import org.ts.application.services.PatientService;
import org.ts.data.entities.ApplicationUser;
import org.ts.data.entities.Encounter;
import org.ts.data.entities.Episode;
import org.ts.data.entities.Patient;
import org.ts.data.repositories.EpisodeRepository;
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
 * Integration tests for {@link EpisodeRepository}.
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
public class EpisodeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	EpisodeService episodeService;

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

	Episode episode, episode2, invalidEpisode, fetchedEpisode;

	Patient patient;
	
	Encounter encounter;
	
	ApplicationUser user;

	@BeforeEach
	void init() {
		// Create a valid encounter
		encounter = new Encounter();
		encounter.setEncounterDate(LocalDateTime.of(2020, Month.JUNE, 15, 13, 45));
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

		// Create valid episodes
		episode = new Episode();
		episode.setStartDate(LocalDateTime.of(2020, Month.JUNE, 15, 13, 45));
		episode.setRetired(Lookup.NOT_RETIRED);
		episode.setVoided(Lookup.NOT_VOIDED);

		episode2 = new Episode();
		episode2.setStartDate(LocalDateTime.of(2020, Month.JUNE, 17, 13, 45));
		episode2.setRetired(Lookup.NOT_RETIRED);
		episode2.setVoided(Lookup.NOT_VOIDED);

		// Create an invalid episode
		invalidEpisode = new Episode();
		invalidEpisode.setRetired(Lookup.NOT_RETIRED);
		invalidEpisode.setVoided(Lookup.NOT_VOIDED);

	}

	int FALSE = 0;

	@Test
	@WithMockUser
	public void testGetEpisodes() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		episode.setEncounter(encounter);
		episode.setConcept(conceptService.getActiveConcept("Fever"));

		episode2.setEncounter(encounter);
		episode2.setConcept(conceptService.getActiveConcept("Chronic dehydration"));

		episodeService.addEpisode(episode);
		episodeService.addEpisode(episode2);

		MvcResult result = mockMvc.perform(get("/episodes").with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.[0].encounter.patient.firstName").value("John")).andReturn();

		assertNotNull(result);
	}

	@Test
	@WithMockUser
	public void testAddEpisode() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);

		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		episode.setEncounter(encounter);
		episode.setConcept(conceptService.getActiveConcept("Fever"));
		
		MvcResult result = mockMvc
		        .perform(post("/episodes").content(asJsonString(episode))
		                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
		                .with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.encounter.patient.firstName").value("John")).andReturn();

		assertNotNull(result);

		String response = result.getResponse().getContentAsString();

		Long episodeId = ((Integer) (JsonPath.parse(response).read("$.id"))).longValue();
		episode = episodeService.getActiveEpisode(episodeId);

		assertAll("Properties", () -> assertTrue(episode.getId() > 0),
		        () -> assertTrue(episode.getId() > 0), () -> assertTrue(episode.getId() > 0),
		        () -> assertTrue(episode.getEncounter().getPatient().getFirstName().equals("John")),
		        () -> assertNotNull(episode.getCreatedDate()),
		        () -> assertNotNull(episode.getCreatedBy()),
		        () -> assertNotNull(episode.getLastModifiedBy()),
		        () -> assertNotNull(episode.getModifiedDate()));
	}

	@Test
	@WithMockUser
	public void testFindNonExistentEpisode() throws Exception {
		Long episodeId = -1L;

		mockMvc.perform(
		        get("/episodes/active/{episodeId}", episodeId).with(csrf().asHeader()))
		        .andExpect(status().isNotFound()).andReturn();
	}

	@Test
	@WithMockUser
	public void testVoidEpisode() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);

		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		episode.setEncounter(encounter);
		episode.setConcept(conceptService.getActiveConcept("Fever"));

		episode = episodeService.addEpisode(episode);
		log.info("Added episode with ID: " + episode.getId());

		Long episodeId = episode.getId();

		mockMvc.perform(
		        delete("/episodes/{episodeId}", episodeId).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		assertThrows(EntryNotFoundException.class, () -> {
			episode = episodeService.getActiveEpisode(episodeId);
		});
	}

	@Test
	@WithMockUser
	public void testRetireEpisode() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);

		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		episode.setEncounter(encounter);
		episode.setConcept(conceptService.getActiveConcept("Fever"));

		episode = episodeService.addEpisode(episode);
		log.info("Added episode with ID: " + episode.getId());

		Long episodeId = episode.getId();

		mockMvc.perform(
		        post("/episodes/retire/{episodeId}", episodeId).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		assertThrows(EntryNotActiveException.class, () -> {
			episode = episodeService.getActiveEpisode(episodeId);
		});
	}

	@Test
	@WithMockUser("Peter")
	public void testAuditing() throws Exception {
		metaDataService.loadDefaultMetaData();
		user = userService.addUser(user);
		patient.setApplicationUser(user);
		patient = patientService.addPatient(patient);
		
		encounter.setPatient(patient);
		encounter = encounterService.addEncounter(encounter);
		
		episode.setEncounter(encounter);
		episode.setConcept(conceptService.getActiveConcept("Fever"));

		episode = episodeService.addEpisode(episode);
		log.info("Added episode with ID: " + episode.getId());
		
		MvcResult result = null;
		result = mockMvc
		        .perform(post("/episodes").content(asJsonString(episode))
		                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
		                .with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.encounter.patient.firstName").value("John")).andReturn();

		assertNotNull(result);
		String response = result.getResponse().getContentAsString();

		Long episodeId = ((Integer) (JsonPath.parse(response).read("$.id"))).longValue();
		episode = episodeService.getActiveEpisode(episodeId);

		assertAll("Properties", () -> assertTrue(episode.getCreatedBy().equals("Peter")),
		        () -> assertTrue(episode.getLastModifiedBy().equals("Peter")));
	}

	@Test
	@WithMockUser
	public void testConstraintViolations() throws Exception {
		mockMvc.perform(post("/episodes").content(asJsonString(invalidEpisode))
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
