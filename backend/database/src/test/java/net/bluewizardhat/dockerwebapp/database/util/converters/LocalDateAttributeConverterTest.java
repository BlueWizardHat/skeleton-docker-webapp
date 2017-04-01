package net.bluewizardhat.dockerwebapp.database.util.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.Test;

public class LocalDateAttributeConverterTest {

	private LocalDateAttributeConverter converter = new LocalDateAttributeConverter();

	@Test
	public void testConversion() {
		LocalDate now = LocalDate.now();
		Date date = converter.convertToDatabaseColumn(now);
		LocalDate back = converter.convertToEntityAttribute(date);

		assertEquals(now, back);
	}

	@Test
	public void testNullConversion() {
		assertNull(converter.convertToDatabaseColumn(null));
		assertNull(converter.convertToEntityAttribute(null));
	}
}
