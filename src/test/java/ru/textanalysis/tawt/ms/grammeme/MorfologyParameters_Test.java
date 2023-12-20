package ru.textanalysis.tawt.ms.grammeme;

import org.junit.jupiter.api.Test;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters.TypeOfSpeech;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MorfologyParameters_Test {

	@Test
	public void dd() throws IllegalAccessException {
		Field[] fields = TypeOfSpeech.class.getFields();

		Set<Byte> actual = new HashSet<>();
		Set<String> expected = new HashSet<>();
		for (Field field : fields) {
			if (!"IDENTIFIER".equals(field.getName())) {
				expected.add(field.getName());
				actual.add(field.getByte(null));
			}
		}

		assertEquals(expected.size(), actual.size());
	}
}
