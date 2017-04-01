package net.bluewizardhat.dockerwebapp.database.util.converters;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Time;
import java.time.OffsetTime;

import org.junit.Test;

public class OffsetTimeAttributeConverterTest {

	private OffsetTimeAttributeConverter converter = new OffsetTimeAttributeConverter();

	@Test
	public void testConversion() {
		OffsetTime now = OffsetTime.now().withNano(0); // Nanos are cut with this conversion
		Time time = converter.convertToDatabaseColumn(now);
		OffsetTime back = converter.convertToEntityAttribute(time);

		assertTrue(now.isEqual(back));
	}

	@Test
	public void testNullConversion() {
		assertNull(converter.convertToDatabaseColumn(null));
		assertNull(converter.convertToEntityAttribute(null));
	}
}
