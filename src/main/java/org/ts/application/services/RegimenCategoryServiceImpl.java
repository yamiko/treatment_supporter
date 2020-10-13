package org.ts.application.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.ts.data.entities.RegimenCategory;
import org.ts.data.repositories.RegimenCategoryRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegimenCategoryServiceImpl implements RegimenCategoryService {


	@Autowired
	private RegimenCategoryRepository regimenCategoryRepository;

	@Override
	public List<RegimenCategory> getRegimenCategories() {
		log.debug("Getting all regimen categories");
		List<RegimenCategory> regimenCategorys = regimenCategoryRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED)
		        .collect(Collectors.toList());
		regimenCategorys.sort(Comparator.comparing(RegimenCategory::getId));
		return regimenCategorys;
	}

}
