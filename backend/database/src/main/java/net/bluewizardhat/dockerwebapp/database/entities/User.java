package net.bluewizardhat.dockerwebapp.database.entities;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;
import net.bluewizardhat.dockerwebapp.database.entities.embedded.TwoFactorDetails;

@Data
@Entity
@Table(name = "user_table", uniqueConstraints = @UniqueConstraint(name = "user_name_key", columnNames = "user_name"))
@ToString(exclude = "hashedPassword")
public class User {

	public static enum UserType {
		USER,
		ADMIN
	}

	public static enum UserState {
		/**
		 * Created but not validated
		 */
		INACTIVE,
		/**
		 * Fully active
		 */
		ACTIVE,
		/**
		 * Disabled
		 */
		DISABLED,
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 64)
	@Column(name = "user_name", nullable = false, length = 64)
	private String userName;

	@JsonIgnore
	@NotNull
	@Size(max = 128)
	@Column(name = "hashed_password", nullable = false, length = 128)
	private String hashedPassword;

	@NotNull
	@Size(max = 128)
	@Column(name = "display_name", nullable = false, length = 128)
	private String displayName;

	@NotNull
	@Size(max = 128)
	@Column(name = "email", nullable = false, length = 128)
	private String email;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "user_type", nullable = false, length = 16)
	private UserType type;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "user_state", nullable = false, length = 16)
	private UserState state;

	@Valid
	@Embedded
	private TwoFactorDetails twoFactorDetails;

	@NotNull
	@Column(name = "created", nullable = false)
	private OffsetDateTime created;

	@Column(name = "last_login", nullable = true)
	private OffsetDateTime lastLogin;

	@JsonIgnore
	@Column(name = "last_login_attempt", nullable = true)
	private OffsetDateTime lastLoginAttempt;

	@JsonIgnore
	@Column(name = "failed_login_attempts", nullable = true)
	private Integer failedLoginAttempts;

	@JsonIgnore
	@Version
	private Long version;

}
