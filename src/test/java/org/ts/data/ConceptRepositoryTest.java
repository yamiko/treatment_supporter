package org.ts.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.ts.data.entities.Concept;
import org.ts.data.repositories.ConceptRepository;
import org.ts.utils.Lookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import lombok.extern.slf4j.Slf4j;

/**
 * Integration tests for {@link ConceptRepository}. 
 * 
 * @author Yamiko Msosa
 *
 */
@Transactional
@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=validate" })
public class ConceptRepositoryTest {

	@Autowired
	ConceptRepository repository;

	@Autowired
	private Validator validator;

	Concept concept, invalidConcept, fetchedConcept;

	int FALSE = 0;
	
	@BeforeEach
    void init() {
		concept = new Concept();
		concept.setName("Concept Name1");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);

		invalidConcept = new Concept();
		invalidConcept.setRetired(Lookup.NOT_RETIRED);
		invalidConcept.setVoided(Lookup.NOT_VOIDED);
	}

	@Test
	@WithMockUser
	public void testAddConcept() {
		concept = repository.save(concept);

		assertAll("Properties", 
				() -> assertTrue(concept.getId() > 0),
		        () -> assertTrue(concept.getName().equals("Concept Name1")), 
		        () -> assertNotNull(concept.getCreatedDate()), 
		        () -> assertNotNull(concept.getCreatedBy()), 
		        () -> assertNotNull(concept.getLastModifiedBy()), 
		        () -> assertNotNull(concept.getModifiedDate()), 
		        () -> {
			        fetchedConcept = repository.findAllByName("Concept Name1").stream().findFirst().orElse(null);
			        assertNotNull(fetchedConcept);
		        }
			);
	}

	@Test
	@WithMockUser
	public void AmendConcept() {
		concept = repository.save(concept);
		log.info("Added concept with ID: " + concept.getId());
		concept.setRetired(Lookup.RETIRED);
		concept = repository.save(concept);	
		assertTrue(concept.getRetired() == Lookup.RETIRED);	
	}

	@Test
	@WithMockUser("Peter")
	public void testAuditing() {
		concept = repository.save(concept);

		assertAll("Properties", 
				() -> assertTrue(concept.getCreatedBy().equals("Peter")),
				() -> assertTrue(concept.getLastModifiedBy().equals("Peter"))
			);
	}

	@Test
	@WithMockUser
	public void testConstraintViolations() {
		// Validate using Bean constraints
		Set<ConstraintViolation<Concept>> violations = validator.validate(invalidConcept);

		assertAll("Properties", 
				() -> assertNotNull(violations.stream().filter(v -> v.getMessage().contentEquals("Concept name should not be blank")).findAny().orElse(null)),
				() -> assertFalse(violations.isEmpty())
			);
	}


}
