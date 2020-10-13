package org.ts.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.ts.utils.Lookup;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Defines structure and relationship(s) for the <code>encounter</code> table.
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
public class Encounter extends AbstractRetirableEntity {

	private int encounterType;
	
	@Past(message = "Encounter date should be in the past")
	@NotNull(message = "Encounter date should not be blank")
	private LocalDateTime encounterDate;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Patient patient;

	@OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL)
	private List<Observation> observation = new ArrayList<Observation>();


	public Encounter(int encounterType, LocalDateTime encounterDate) {

		this.encounterType = encounterType;
		this.encounterDate = encounterDate;

		this.setRetired(Lookup.NOT_RETIRED);
		this.setVoided(Lookup.NOT_VOIDED);
	}
}
