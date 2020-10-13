package org.ts.application.services;

import org.ts.data.entities.Action;
import org.ts.data.entities.Concept;
import org.ts.data.entities.Condition;
import org.ts.data.entities.Frequency;
import org.ts.data.entities.Regimen;
import org.ts.data.entities.RegimenCategory;
import org.ts.data.repositories.ActionRepository;
import org.ts.data.repositories.ConceptRepository;
import org.ts.data.repositories.ConditionRepository;
import org.ts.data.repositories.FrequencyRepository;
import org.ts.data.repositories.RegimenCategoryRepository;
import org.ts.data.repositories.RegimenRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetaDataServiceImpl implements MetaDataService {

	@Autowired
	private ConceptRepository conceptRepository;

	@Autowired
	private FrequencyRepository frequencyRepository;

	// @Autowired
	// private VocabularySetRepository vocabularySetRepository;

	@Autowired
	private RegimenRepository regimenRepository;

	@Autowired
	private RegimenCategoryRepository regimenCategoryRepository;

	@Autowired
	private ConditionRepository conditionRepository;

	@Autowired
	private ActionRepository actionRepository;

	@Override
	public boolean loadDefaultMetaData() {

		boolean loaded = false;
		if (conceptRepository.findAll().size() == 0) {
			log.info("Loading default metadata");

			loadConcepts();
			loadConditions();
			loadFrequencies();
			loadActions();
			loadRegimens();
			loadRegimenCategories();

			loaded = true;
		}
		return loaded;
	}

	private void loadConcepts() {

		Concept concept = new Concept();
		concept.setName("Presenting condition");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Fever");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Age");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Severe malnourishment");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Chronic dehydration");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Temperature");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Take oral medication");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Self-referral");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Hospital");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Paracetamol");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);

		concept = new Concept();
		concept.setName("Q4H");
		concept.setRetired(Lookup.NOT_RETIRED);
		concept.setVoided(Lookup.NOT_VOIDED);
		conceptRepository.save(concept);
	}

	private void loadConditions() {

		Condition condition = new Condition();
		condition = new Condition();
		condition.setDescription("Presenting with chronic dehydration");
		condition.setConditionType(Lookup.CONCEPT_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Presenting condition").stream().findFirst().orElse(null));
		condition.setConceptValue(
		        conceptRepository.findAllByName("Chronic dehydration").stream().findFirst().orElse(null));
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Presenting with severe malnourishment");
		condition.setConditionType(Lookup.CONCEPT_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Presenting condition").stream().findFirst().orElse(null));
		condition.setConceptValue(
		        conceptRepository.findAllByName("Severe malnourishment").stream().findFirst().orElse(null));
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("High temperature from 38 degrees");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Temperature").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.GREATER_OR_EQUAL);
		condition.setIntValue(38);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("High temperature up to 40 degrees");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Temperature").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.LESS);
		condition.setIntValue(40);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Very high temperature from 40 degrees");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Temperature").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.GREATER_OR_EQUAL);
		condition.setIntValue(40);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Age from 10 years");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Age").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.GREATER_OR_EQUAL);
		condition.setIntValue(10);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Age to 11 years");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Age").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.LESS);
		condition.setIntValue(11);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Age from 11 years");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Age").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.GREATER_OR_EQUAL);
		condition.setIntValue(11);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Age to 16 years");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Age").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.LESS);
		condition.setIntValue(16);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

		condition = new Condition();
		condition.setDescription("Age from 16 years");
		condition.setConditionType(Lookup.INTEGER_CONDITION);
		condition.setConcept(conceptRepository.findAllByName("Age").stream().findFirst().orElse(null));
		condition.setRelator(Lookup.GREATER_OR_EQUAL);
		condition.setIntValue(16);
		condition.setRetired(Lookup.NOT_RETIRED);
		condition.setVoided(Lookup.NOT_VOIDED);
		conditionRepository.save(condition);

	}

	private void loadActions() {

		Action action = new Action();
		action.setDescription("Take paracetamol (500mg)");
		action.setConcept(conceptRepository.findAllByName("Take oral medication").stream().findFirst().orElse(null));
		action.setConceptValue(conceptRepository.findAllByName("Paracetamol").stream().findFirst().orElse(null));
		action.setFrequency(
		        frequencyRepository.findAllByDescription("Every 4 hours").stream().findFirst().orElse(null));
		action.setStartDosage(500);
		action.setStartDosage(500);
		action.setUnit(Lookup.MILLIGRAMS);
		action.setRetired(Lookup.NOT_RETIRED);
		action.setVoided(Lookup.NOT_VOIDED);
		actionRepository.save(action);

		action = new Action();
		action.setDescription("Take paracetamol (625mg)");
		action.setConcept(conceptRepository.findAllByName("Take oral medication").stream().findFirst().orElse(null));
		action.setConceptValue(conceptRepository.findAllByName("Paracetamol").stream().findFirst().orElse(null));
		action.setFrequency(
		        frequencyRepository.findAllByDescription("Every 4 hours").stream().findFirst().orElse(null));
		action.setStartDosage(625);
		action.setStartDosage(625);
		action.setUnit(Lookup.MILLIGRAMS);
		action.setRetired(Lookup.NOT_RETIRED);
		action.setVoided(Lookup.NOT_VOIDED);
		actionRepository.save(action);

		action = new Action();
		action.setDescription("Take paracetamol (500 - 1000mg)");
		action.setConcept(conceptRepository.findAllByName("Take oral medication").stream().findFirst().orElse(null));
		action.setConceptValue(conceptRepository.findAllByName("Paracetamol").stream().findFirst().orElse(null));
		action.setFrequency(
		        frequencyRepository.findAllByDescription("Every 4 hours").stream().findFirst().orElse(null));
		action.setStartDosage(500);
		action.setStartDosage(1000);
		action.setUnit(Lookup.MILLIGRAMS);
		action.setRetired(Lookup.NOT_RETIRED);
		action.setVoided(Lookup.NOT_VOIDED);
		actionRepository.save(action);

		action = new Action();
		action.setDescription("Seek medical attention - see a medical doctor");
		action.setConcept(conceptRepository.findAllByName("Self-referral").stream().findFirst().orElse(null));
		action.setConceptValue(conceptRepository.findAllByName("Hospital").stream().findFirst().orElse(null));
		action.setRetired(Lookup.NOT_RETIRED);
		action.setVoided(Lookup.NOT_VOIDED);
		actionRepository.save(action);
	}

	private void loadFrequencies() {
		Frequency frequency = new Frequency();
		frequency.setDescription("Every 4 hours");
		frequency.setConcept(conceptRepository.findAllByName("Q4H").stream().findFirst().orElse(null));
		frequency.setFrequencyType(Lookup.EVERY);
		frequency.setTime(4);
		frequency.setUnit(Lookup.HOURS);
		frequency.setRetired(Lookup.NOT_RETIRED);
		frequency.setVoided(Lookup.NOT_VOIDED);
		frequencyRepository.save(frequency);
	}

	private void loadRegimens() {
		Regimen regimen = new Regimen();
		regimen.setName("Treatment of fever");

		regimen.setRetired(Lookup.NOT_RETIRED);
		regimen.setVoided(Lookup.NOT_VOIDED);
		regimenRepository.save(regimen);
	}

	private void loadRegimenCategories() {

		RegimenCategory regimenCategory = new RegimenCategory();
		regimenCategory.setName("Fever in children from 10 to 11 years");
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature from 38 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature up to 40 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Age from 10 years").stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Age to 11 years").stream().findFirst().orElse(null));

		regimenCategory.getAction().add(
		        actionRepository.findAllByDescription("Take paracetamol (500mg)").stream().findFirst().orElse(null));
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.setRetired(Lookup.NOT_RETIRED);
		regimenCategory.setVoided(Lookup.NOT_VOIDED);
		regimenCategoryRepository.save(regimenCategory);

		regimenCategory = new RegimenCategory();
		regimenCategory.setName("Fever in children from 11 to 16 years");
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature from 38 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature up to 40 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Age from 11 years").stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Age to 16 years").stream().findFirst().orElse(null));

		regimenCategory.getAction().add(
		        actionRepository.findAllByDescription("Take paracetamol (625mg)").stream().findFirst().orElse(null));
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.setRetired(Lookup.NOT_RETIRED);
		regimenCategory.setVoided(Lookup.NOT_VOIDED);
		regimenCategoryRepository.save(regimenCategory);

		regimenCategory = new RegimenCategory();
		regimenCategory.setName("Fever in adults (16+ years)");
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature from 38 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature up to 40 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Age from 16 years").stream().findFirst().orElse(null));

		regimenCategory.getAction().add(actionRepository.findAllByDescription("Take paracetamol (500 - 1000mg)")
		        .stream().findFirst().orElse(null));
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.setRetired(Lookup.NOT_RETIRED);
		regimenCategory.setVoided(Lookup.NOT_VOIDED);
		regimenCategoryRepository.save(regimenCategory);
		
		regimenCategory = new RegimenCategory();
		regimenCategory.setName("Fever in those presenting with severe malnourishment");
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature from 38 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature up to 40 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Presenting with severe malnourishment").stream().findFirst().orElse(null));

		regimenCategory.getAction().add(
		        actionRepository.findAllByDescription("Take paracetamol (500mg)").stream().findFirst().orElse(null));
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.setRetired(Lookup.NOT_RETIRED);
		regimenCategory.setVoided(Lookup.NOT_VOIDED);
		regimenCategoryRepository.save(regimenCategory);
		
		regimenCategory = new RegimenCategory();
		regimenCategory.setName("Fever in those presenting with chronic dehydration");
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature from 38 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("High temperature up to 40 degrees")
		        .stream().findFirst().orElse(null));
		regimenCategory.getCondition()
		        .add(conditionRepository.findAllByDescription("Presenting with chronic dehydration").stream().findFirst().orElse(null));

		regimenCategory.getAction().add(
		        actionRepository.findAllByDescription("Take paracetamol (500mg)").stream().findFirst().orElse(null));
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.setRetired(Lookup.NOT_RETIRED);
		regimenCategory.setVoided(Lookup.NOT_VOIDED);
		regimenCategoryRepository.save(regimenCategory);

		regimenCategory = new RegimenCategory();
		regimenCategory.setName("Fever in those with very high temperature");
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.getCondition().add(conditionRepository.findAllByDescription("Very high temperature from 40 degrees")
		        .stream().findFirst().orElse(null));

		regimenCategory.getAction().add(
		        actionRepository.findAllByDescription("Seek medical attention - see a medical doctor").stream().findFirst().orElse(null));
		regimenCategory
		        .setRegimen(regimenRepository.findAllByName("Treatment of fever").stream().findFirst().orElse(null));
		regimenCategory.setRetired(Lookup.NOT_RETIRED);
		regimenCategory.setVoided(Lookup.NOT_VOIDED);
		regimenCategoryRepository.save(regimenCategory);
		
	}

}