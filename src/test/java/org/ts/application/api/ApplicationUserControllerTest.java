package org.ts.application.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import javax.transaction.Transactional;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.application.services.ApplicationUserService;
import org.ts.data.entities.ApplicationUser;
import org.ts.data.entities.Patient;
import org.ts.data.repositories.ApplicationUserRepository;
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
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Integration tests for {@link ApplicationUserRepository}.
 * 
 * @author Yamiko Msosa
 *
 */
@Transactional
@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=validate" })
@AutoConfigureMockMvc
public class ApplicationUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ApplicationUserService userService;

	ApplicationUser user, user2, user3, invalidApplicationUser, fetchedApplicationUser;

	Patient patient;
	
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

		// Create valid users
		user = new ApplicationUser("test1", "password1", "Test User1");
		user.setRetired(Lookup.NOT_RETIRED);
		user.setVoided(Lookup.NOT_VOIDED);

		user2 = new ApplicationUser("test2", "password1", "Test User2");
		user2.setRetired(Lookup.NOT_RETIRED);
		user2.setVoided(Lookup.NOT_VOIDED);

		user3 = new ApplicationUser("test3", "password1", "Test User3");
		user3.setRetired(Lookup.NOT_RETIRED);
		user3.setVoided(Lookup.NOT_VOIDED);

		// Create an invalid new user
		invalidApplicationUser = new ApplicationUser("test1", "", "");
		invalidApplicationUser.setRetired(Lookup.NOT_RETIRED);
		invalidApplicationUser.setVoided(Lookup.NOT_VOIDED);

	}

	int FALSE = 0;

	@Test
	@WithMockUser
	public void testGetUsers() throws Exception {
		userService.addUser(user);
		userService.addUser(user2);
		userService.addUser(user3);

		MvcResult result = mockMvc.perform(get("/users").with(csrf().asHeader())).andExpect(status().isOk())
			        .andExpect(content().contentType("application/json"))
			        .andExpect(jsonPath("$.[1].username").value("test2")).andReturn();

		assertNotNull(result);
	}


	@Test
	@WithMockUser
	public void testAddUser() throws Exception {
		patient.setApplicationUser(user);
		
		MvcResult result = mockMvc
		        .perform(post("/users").content(asJsonString(patient)).contentType(MediaType.APPLICATION_JSON)
		                .accept(MediaType.APPLICATION_JSON).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.applicationUser.username").value("test1")).andReturn();

		assertNotNull(result);

		String response = result.getResponse().getContentAsString();

		Long userId = ((Integer) (JsonPath.parse(response).read("$.applicationUser.id"))).longValue();
		user = userService.getActiveUser(userId);

		assertAll("Properties", () -> assertTrue(user.getId() > 0), () -> assertTrue(user.getId() > 0),
		        () -> assertTrue(user.getFullName().equals("Test User1")), () -> assertNotNull(user.getCreatedDate()),
		        () -> assertNotNull(user.getCreatedBy()), () -> assertNotNull(user.getLastModifiedBy()),
		        () -> assertNotNull(user.getModifiedDate()));
	}

	@Test
	public void testAddUserWithoutToken() throws Exception {
		patient.setApplicationUser(user);
		
		MvcResult result = mockMvc
		        .perform(post("/users").content(asJsonString(patient)).contentType(MediaType.APPLICATION_JSON)
		                .accept(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.applicationUser.username").value("test1")).andReturn();

		assertNotNull(result);

		String response = result.getResponse().getContentAsString();

		Long userId = ((Integer) (JsonPath.parse(response).read("$.applicationUser.id"))).longValue();
		user = userService.getActiveUser(userId);

		assertAll("Properties", () -> assertTrue(user.getId() > 0), () -> assertTrue(user.getId() > 0),
		        () -> assertTrue(user.getFullName().equals("Test User1")), () -> assertNotNull(user.getCreatedDate()),
		        () -> assertNotNull(user.getCreatedBy()), () -> assertNotNull(user.getLastModifiedBy()),
		        () -> assertNotNull(user.getModifiedDate()));
	}

	@Test
	@WithMockUser
	public void testLoginWithoutToken() throws Exception {
		user = userService.addUser(user);
		
		mockMvc
		        .perform(post("/users/login").content(asJsonString(new PassUser("test1", "password1"))).contentType(MediaType.APPLICATION_JSON)
		                .accept(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk())
		        .andExpect(header().exists("Authorization"));

	}

	@Test
	@WithMockUser
	public void testFindNonExistentUser() throws Exception {
		Long userId = -1L;

		mockMvc.perform(get("/users/active/{userId}", userId).with(csrf().asHeader())).andExpect(status().isNotFound())
		        .andReturn();
	}

	@Test
	@WithMockUser
	public void testVoidApplicationUser() throws Exception {
		user = userService.addUser(user);
		log.info("Added user with ID: " + user.getId());

		Long userId = user.getId();

		mockMvc.perform(delete("/users/{userId}", userId).with(csrf().asHeader())).andExpect(status().isOk())
		        .andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		assertThrows(EntryNotFoundException.class, () -> {
			user = userService.getActiveUser(userId);
		});
	}

	@Test
	@WithMockUser
	public void testRetireUser() throws Exception {
		user = userService.addUser(user);
		log.info("Added user with ID: " + user.getId());

		Long userId = user.getId();

		mockMvc.perform(post("/users/retire/{userId}", userId).with(csrf().asHeader())).andExpect(status().isOk())
		        .andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		assertThrows(EntryNotActiveException.class, () -> {
			user = userService.getActiveUser(userId);
		});
	}

	@Test
	@WithMockUser("Peter")
	public void testAuditing() throws Exception {
		patient.setApplicationUser(user);

		MvcResult result = mockMvc
		        .perform(post("/users").content(asJsonString(patient)).contentType(MediaType.APPLICATION_JSON)
		                .accept(MediaType.APPLICATION_JSON).with(csrf().asHeader()))
		        .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
		        .andExpect(jsonPath("$.applicationUser.username").value("test1")).andReturn();

		assertNotNull(result);
		String response = result.getResponse().getContentAsString();

		Long userId = ((Integer) (JsonPath.parse(response).read("$.applicationUser.id"))).longValue();
		user = userService.getActiveUser(userId);

		assertAll("Properties", () -> assertTrue(user.getCreatedBy().equals("Peter")),
		        () -> assertTrue(user.getLastModifiedBy().equals("Peter")));
	}

	@Test
	@WithMockUser
	public void testConstraintViolations() throws Exception {
		patient.setApplicationUser(invalidApplicationUser);

		mockMvc.perform(post("/users").content(asJsonString(patient))
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

@Getter
@Setter
@AllArgsConstructor
class PassUser{
	String username;
	String password;
}