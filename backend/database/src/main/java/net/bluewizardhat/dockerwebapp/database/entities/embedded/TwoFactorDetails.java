package net.bluewizardhat.dockerwebapp.database.entities.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "secret")
@Embeddable
public class TwoFactorDetails {
	public static enum TwoFactorType {
		/**
		 * TOTP (also known as Google Authenticator)
		 */
		TOTP
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "two_factor_type", nullable = true, length = 16)
	private TwoFactorType type;

	@JsonIgnore
	@NotNull
	@Size(max = 64)
	@Column(name = "two_factor_secret", nullable = true, length = 64)
	private String secret;

}
