package org.ts.application.services;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Encounter;
import org.ts.data.entities.Episode;
import org.ts.data.repositories.EpisodeRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EpisodeServiceImpl implements EpisodeService {

	@Autowired
	private EpisodeRepository episodeRepository;

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private Validator validator;

	@Override
	public Episode addEpisode(Episode episode) throws EntryNotFoundException, EntryNotActiveException {

		Episode greenEpisode = new Episode();

		// Extract all fields to safely add to DB
		greenEpisode.setConcept(episode.getConcept());
		greenEpisode.setStartDate(episode.getStartDate());
		greenEpisode.setEndDate(episode.getEndDate());

		greenEpisode.setVoided(Lookup.NOT_VOIDED);
		greenEpisode.setRetired(Lookup.NOT_RETIRED);

		// Validate using Bean constraints
		Set<ConstraintViolation<Episode>> violations = validator.validate(greenEpisode);
		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<Episode> constraintViolation : violations) {
				sb.append(" -> " + constraintViolation.getMessage());
			}

			throw new ConstraintViolationException("Validation error: " + sb.toString(), violations);
		}

		// Only proceed to search for episode and encounter if we have references
		if (episode.getEncounter() == null || episode.getEncounter().getId() == null) {
			throw new EntryNotFoundException("Unable to find existing ENCOUNTER references");
		}

		Episode newEpisode = new Episode();

		// Get reference entities
		Encounter existingEncounter = new Encounter();
		try {
			existingEncounter = encounterService.getActiveEncounter(episode.getEncounter().getId());
		} catch (EntryNotFoundException e) {
			throw new EntryNotFoundException("Unable to find existing ENCOUNTER reference");
		} catch (EntryNotActiveException e) {
			throw new EntryNotActiveException("Unable to find active ENCOUNTER reference");
		}

		// Only attempt to save the episode if we have an existing encounter
		newEpisode = episodeRepository.save(greenEpisode);

		// Add references to existing Encounter instance
		newEpisode.setEncounter(existingEncounter);
		newEpisode = episodeRepository.save(newEpisode);

		return newEpisode;
	}

	@Override
	public List<Episode> getEpisodes() {
		List<Episode> episodes = episodeRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED)
		        .collect(Collectors.toList());
		episodes.sort(Comparator.comparing(Episode::getId));
		return episodes;
	}

	@Override
	public List<Episode> getEpisodes(Long encounterId) {
		List<Episode> episodes = episodeRepository.findAll().stream().filter(p -> p.getVoided() != Lookup.VOIDED
		        && p.getRetired() != Lookup.RETIRED && p.getEncounter().getId().longValue() == encounterId)
		        .collect(Collectors.toList());
		episodes.sort(Comparator.comparing(Episode::getId));
		return episodes;
	}

	@Override
	public Episode getActiveEpisode(Long episodeId) throws EntryNotActiveException, EntryNotFoundException {
		Episode episode = episodeRepository.findById(episodeId).orElse(null);
		if (episode != null && episode.getVoided() != Lookup.VOIDED && episode.getRetired() != Lookup.RETIRED) {
			return episode;
		} else {
			if (episode == null || episode.getVoided() == Lookup.VOIDED) {
				throw new EntryNotFoundException("Invalid operation for [QUALIFICATION]." + episodeId);
			} else {
				throw new EntryNotActiveException("Invalid operation for [QUALIFICATION]." + episodeId);
			}
		}
	}

	@Override
	public void deleteEpisode(Long episodeId) throws EntryNotFoundException {
		Episode episode = episodeRepository.findById(episodeId).orElse(null);
		if (episode != null && episode.getVoided() != Lookup.VOIDED) {
			episode.setVoided(Lookup.VOIDED);
			episode.setVoidedReason("System operation - voided");
			episodeRepository.save(episode);
			log.info("Deleted episode with ID: " + episodeId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [QUALIFICATION]." + episodeId);
		}
	}

	@Override
	public void retireEpisode(Long episodeId) throws EntryNotFoundException {
		Episode episode = episodeRepository.findById(episodeId).orElse(null);
		if (episode != null && episode.getRetired() != Lookup.RETIRED) {
			episode.setRetired(Lookup.RETIRED);
			episode.setRetiredReason("System operation - retired");
			episodeRepository.save(episode);
			log.info("Retired episode with ID: " + episodeId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [QUALIFICATION]." + episodeId);
		}
	}
}
