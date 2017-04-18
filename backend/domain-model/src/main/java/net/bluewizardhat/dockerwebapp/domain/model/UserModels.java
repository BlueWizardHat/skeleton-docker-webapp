package net.bluewizardhat.dockerwebapp.domain.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

public class UserModels {

	@Data
	public static class UserCreateRequest {
		@NotNull
		@Size(max = 64)
		private String loginName;

		@NotNull
		@Size(max = 128)
		private String displayName;

		@NotNull
		@Size(max = 128)
		private String email;

		@NotNull
		@Size(min = 8)
		private String password;
	}

	@Data
	public static class UserPasswordUpdateRequest {
		@NotNull
		@Size(max = 64)
		private String loginName;

		@NotNull
		@Size(min = 8)
		private String oldPassword;

		@NotNull
		@Size(min = 8)
		private String newPassword;
	}

}
