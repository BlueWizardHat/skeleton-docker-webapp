package net.bluewizardhat.dockerwebapp.database.entities;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

/**
 * This is _totally_ a real animal..
 */
@Data
@Entity
@Table(name = "bananafish")
public class BananaFish {

	@Id
	@GeneratedValue(generator = "bananafish_id_generator", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "bananafish_id_generator", sequenceName = "bananafish_id_seq", allocationSize = 10)
	private Long id;

	private String name;

	private OffsetDateTime created;

}
