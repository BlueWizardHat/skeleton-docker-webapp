package net.bluewizardhat.dockerwebapp.domain.logic.exception;

import lombok.Getter;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = -8531303410897622804L;

	@Getter
	private final Class<?> entityType;
	@Getter
	private final Object id;

	public NotFoundException(Class<?> entityType, Object id) {
		super(entityType.getSimpleName() + " with id/key='" + id + "' was not found");
		this.entityType = entityType;
		this.id = id;
	}

}
