package net.bluewizardhat.dockerwebapp.database.util.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import org.junit.Test;

public class OffsetDateTimeAttributeConverterTest {

	private OffsetDateTimeAttributeConverter converter = new OffsetDateTimeAttributeConverter();

	@Test
	public void testConversion() {
		OffsetDateTime now = OffsetDateTime.now();
		Timestamp timestamp = converter.convertToDatabaseColumn(now);
		OffsetDateTime back = converter.convertToEntityAttribute(timestamp);

		assertEquals(now.toInstant(), back.toInstant());
	}

	@Test
	public void testNullConversion() {
		assertNull(converter.convertToDatabaseColumn(null));
		assertNull(converter.convertToEntityAttribute(null));
	}
}
