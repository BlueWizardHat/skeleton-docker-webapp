package net.bluewizardhat.dockerwebapp.database.util.converters;

import java.sql.Time;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts between java.time.OffsetTime and java.sql.Time. Because java.sql.Time is less precise
 * (only second precision) than java.time.OffsetTime (nanosecond precision) this converter does not
 * auto-apply.
 */
@Converter(autoApply = false)
public class OffsetTimeAttributeConverter implements AttributeConverter<OffsetTime, Time> {

	@Override
	public Time convertToDatabaseColumn(OffsetTime attribute) {
		if (attribute == null) {
			return null;
		}
		// Note that this cuts the nanoseconds since Time only stores seconds
		return Time.valueOf(attribute.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime());
	}

	@Override
	public OffsetTime convertToEntityAttribute(Time dbData) {
		if (dbData == null) {
			return null;
		}
		return dbData.toLocalTime().atOffset(ZoneOffset.UTC);
	}

}
