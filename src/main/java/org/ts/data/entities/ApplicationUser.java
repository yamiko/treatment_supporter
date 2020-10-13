package org.ts.data.entities;


import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * DTO for an application user.
 * <p>
 * It also defines structure and relationship(s) for the
 * <code>application_user</code> table in the DB.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@id")
public class ApplicationUser extends AbstractRetirableEntity {

	@NotBlank(message = "User name should not be blank")
	private String username;

	@NotBlank(message = "Password should not be blank")
	private String password;

	@NotBlank(message = "Full name should not be blank")
	private String fullName;

	/**
	 * 
	 * Convenient constructor for {@link ApplicationUser}.
	 * 
	 * @param username username for the application user
	 * @param password password for the application user
	 * @param fullName full name for the application user
	 *
	 */
	public ApplicationUser(String username, String password, String fullName) {
		final int FALSE = 0;

		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.setRetired(FALSE);
		this.setVoided(FALSE);
	}

}
