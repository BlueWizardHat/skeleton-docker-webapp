package net.bluewizardhat.dockerwebapp.database.entities;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * This is _totally_ a real animal..
 */
@Data
@Entity
@Table(name = "bananafish")
public class BananaFish {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String name;

	private OffsetDateTime created;

	@JsonIgnore
	@Version
	private Long version;
}
