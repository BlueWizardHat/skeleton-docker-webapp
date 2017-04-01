package net.bluewizardhat.dockerwebapp.database.util.converters;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OffsetDateTimeAttributeConverter implements AttributeConverter<OffsetDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(OffsetDateTime attribute) {
		return attribute != null ? Timestamp.from(attribute.toInstant()) : null;
	}

	@Override
	public OffsetDateTime convertToEntityAttribute(Timestamp dbData) {
		return dbData != null ? OffsetDateTime.ofInstant(dbData.toInstant(), ZoneOffset.UTC) : null;
	}

}
