package org.ts.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.ts.data.entities.Patient;
import org.ts.data.repositories.PatientRepository;
import org.ts.utils.Lookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import lombok.extern.slf4j.Slf4j;

/**
 * Integration tests for {@link PatientRepository}. 
 * 
 * @author Yamiko Msosa
 *
 */
@Transactional
@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=validate" })
public class PatientRepositoryTest {

	@Autowired
	PatientRepository repository;

	@Autowired
	private Validator validator;

	Patient patient, invalidPatient, fetchedPatient;

	int FALSE = 0;
	
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

		//Create an invalid patient
		invalidPatient = new Patient("", "", "");

		invalidPatient.setGender("K");
		invalidPatient.setDateOfBirth(LocalDate.of(2080, Month.JUNE, 15));

		invalidPatient.setRetired(Lookup.NOT_RETIRED);
		invalidPatient.setVoided(Lookup.NOT_VOIDED);

	}

	@Test
	@WithMockUser
	public void testAddPatient() {
		patient = repository.save(patient);

		assertAll("Properties", 
				() -> assertTrue(patient.getId() > 0),
		        () -> assertTrue(patient.getFirstName().equals("John")), 
		        () -> assertTrue(patient.getLastName().equals("Smith")), 
		        () -> assertTrue(patient.getCountry().equals("UK")), 
		        () -> assertTrue(patient.getAddressLine1().equals("Address 1")), 
		        () -> assertTrue(patient.getGender().equals("M")), 
		        () -> assertTrue(patient.getEmail().equals("email@email.com")), 
		        () -> assertTrue(patient.getDateOfBirth().isEqual(LocalDate.of(1987, Month.JUNE, 15))), 
		        () -> assertNotNull(patient.getCreatedDate()), 
		        () -> assertNotNull(patient.getCreatedBy()), 
		        () -> assertNotNull(patient.getLastModifiedBy()), 
		        () -> assertNotNull(patient.getModifiedDate()), 
		        () -> {
			        fetchedPatient = repository.findById(patient.getId()).orElse(null);
			        assertNotNull(fetchedPatient);
		        }
			);
	}

	@Test
	@WithMockUser
	public void testAmendPatient() {
		patient = repository.save(patient);
		log.info("Added patient with ID: " + patient.getId());
		patient.setRetired(Lookup.RETIRED);
		patient = repository.save(patient);	
		assertTrue(patient.getRetired() == Lookup.RETIRED);	
	}

	@Test
	@WithMockUser("Peter")
	public void testAuditing() {
		patient = repository.save(patient);

		assertAll("Properties", 
				() -> assertTrue(patient.getCreatedBy().equals("Peter")),
				() -> assertTrue(patient.getLastModifiedBy().equals("Peter"))
			);
	}

	@Test
	@WithMockUser
	public void testConstraintViolations() {
		// Validate using Bean constraints
		Set<ConstraintViolation<Patient>> violations = validator.validate(invalidPatient);

		assertAll("Properties", 
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("First name should not be blank")).findAny().orElse(null)),
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Last name should not be blank")).findAny().orElse(null)),
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Address line 1 should not be blank")).findAny().orElse(null)),
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Country should not be blank")).findAny().orElse(null)),
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Gender should be M for Male or F for Female")).findAny().orElse(null)),
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Email should not be blank")).findAny().orElse(null)),
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Date of birth should be in the past")).findAny().orElse(null)),
				() -> assertFalse(violations.isEmpty())
			);
	}


}
