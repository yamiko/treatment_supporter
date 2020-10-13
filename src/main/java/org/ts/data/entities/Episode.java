package org.ts.data.entities;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
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
 * Defines structure and relationship(s) for the <code>episode</code> table.
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
public class Episode extends AbstractRetirableEntity {

	@Past(message = "Start date should be in the past")
	@NotNull(message = "Start date should not be blank")
	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private int status;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Concept concept;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Encounter encounter;

	public Episode(LocalDateTime startDate, Concept concept, Concept conceptValue) {

		this.startDate = startDate;
		this.concept = concept;

		this.setRetired(Lookup.NOT_RETIRED);
		this.setVoided(Lookup.NOT_VOIDED);
	}
}
