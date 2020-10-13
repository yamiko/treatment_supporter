package org.ts.data.entities;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Defines structure and relationship(s) for the <code>patient</code> table.
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
public class Patient extends AbstractRetirableEntity {

	@NotBlank(message = "First name should not be blank")
	private String firstName;

	@NotBlank(message = "Last name should not be blank")
	private String lastName;

	@NotBlank(message = "Address line 1 should not be blank")
	private String addressLine1;

	@NotBlank(message = "Country should not be blank")
	private String country;

	@Pattern(regexp = "M|F", message = "Gender should be M for Male or F for Female")
	@NotBlank(message = "Gender should not be blank")
	private String gender;

	@NotBlank(message = "Email should not be blank")
	@Email(message = "Invalid email")
	private String email;

	private String title, middleName, preferredContactNumber, alternativeContactNumber, addressLine2, addressLine3,
	        postcode;

	@Past(message = "Date of birth should be in the past")
	@NotNull(message = "Date of birth should not be blank")
	private LocalDate dateOfBirth;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private ApplicationUser applicationUser;

	public Patient(String firstName, String middleName, String lastName) {
		final int FALSE = 0;

		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.setRetired(FALSE);
		this.setVoided(FALSE);
	}
}
