package org.ts.application.services;

import org.springframework.stereotype.Service;

/**
 * 
 * Provides service operations that can be carried out with application users.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Service
public interface MetaDataService {

	/**
	 * Loads default metadata into the DB if there is no metadata.
	 *
	 * 
	 * @return a boolean flag that indicating whether metadata has been loade (true) or not (false)
	 */
	public boolean loadDefaultMetaData();

}
