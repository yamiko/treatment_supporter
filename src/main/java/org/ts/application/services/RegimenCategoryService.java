package org.ts.application.services;

import java.util.List;

import org.ts.data.entities.RegimenCategory;
import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with regimenCategory instances.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface RegimenCategoryService {

	/**
	 * Fetches all active {@link RegimenCategory} instances from the database.
	 *
	 * @param
	 * 
	 * @return list of active regimenCategory instances
	 */
	public List<RegimenCategory> getRegimenCategories();

}
