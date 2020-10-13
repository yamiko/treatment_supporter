package org.ts.data.entities;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * Defines structure and relationship(s) for the <code>regimen_category</code> table.
 * 
 * @author Yamiko J. Msosa
 * @version 1.0
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@id")
public class RegimenCategory extends AbstractRetirableEntity {
	@NotBlank(message = "Regimen category name should not be blank")
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Regimen regimen;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "regimen_category_condition", joinColumns = @JoinColumn(name = "regimen_category_id"), inverseJoinColumns = @JoinColumn(name = "condition_id"))
	private Set<Condition> condition = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "regimen_category_action", joinColumns = @JoinColumn(name = "regimen_category_id"), inverseJoinColumns = @JoinColumn(name = "action_id"))
	private Set<Action> action = new HashSet<>();
}

