package ru.textanalysis.tawt.ms.model.jmorfsdk;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class PrefixMorfCharacteristicsChanges {

	protected final byte typeOfSpeech;
	protected final long addCharacteristics;
	protected final long deleteCharacteristics;

	public List<Byte> getDeleteCharacteristicsPositions() {
		List<Byte> positions = new ArrayList<>();
		long characteristics = this.deleteCharacteristics;
		byte position = 1;
		while (characteristics != 0) {
			if ((characteristics & 1) != 0) {
				positions.add(position);
			}
			position++;
			characteristics = characteristics >>> 1;
		}
		return positions;
	}
}
