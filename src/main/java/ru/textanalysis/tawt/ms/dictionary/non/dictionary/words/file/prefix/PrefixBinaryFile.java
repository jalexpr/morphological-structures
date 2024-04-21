package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.file.prefix;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.MorphologicalCharacteristicsChangesByPrefix;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.PrefixMorphologicalCharacteristicsChangesInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static ru.textanalysis.tawt.ms.constant.Const.LAVAL_COMPRESS;
import static ru.textanalysis.tawt.ms.model.Property.*;
import static template.wrapper.classes.Lzma2FileHelper.compressionFile;

@Slf4j
public class PrefixBinaryFile {

	private final File file;
	private final PrefixMorphologicalCharacteristicsChangesInfo prefixMorphologicalCharacteristicsChangesInfo;

	public PrefixBinaryFile(PrefixMorphologicalCharacteristicsChangesInfo prefixMorphologicalCharacteristicsChangesInfo) {
		this.prefixMorphologicalCharacteristicsChangesInfo = prefixMorphologicalCharacteristicsChangesInfo;
		String path = System.getProperty("java.io.tmpdir");
		this.file = Paths.get(path, FOLDER, VERSION, PREFIXES).toFile();
	}

	/**
	 * Создание бинарного файла по собранной статистике
	 */
	public void recreate() {
		Map<String, PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo> morphologyCharacteristicsChanges = prefixMorphologicalCharacteristicsChangesInfo.getMorphologyCharacteristicsChanges();

		file.getParentFile().mkdirs();
		try (FileOutputStream stream = new FileOutputStream(file)) {
			for (Map.Entry<String, PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo> entry : morphologyCharacteristicsChanges.entrySet()) {
				String prefix = entry.getKey();
				if (prefix == null) {
					continue;
				}
				byte[] prefixBytes = prefix.getBytes();
				stream.write(ByteBuffer.allocate(1).put((byte) prefixBytes.length).array());
				stream.write(prefixBytes);

				PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo = entry.getValue();
				if (prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo != null) {
					Map<Byte, MorphologicalCharacteristicsChangesByPrefix> morphologicalCharacteristicsChanges = prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo.getMorphologicalCharacteristicsChanges();

					for (Map.Entry<Byte, MorphologicalCharacteristicsChangesByPrefix> morphologicalCharacteristicsChangesEntry : morphologicalCharacteristicsChanges.entrySet()) {
						Byte partOfSpeech = morphologicalCharacteristicsChangesEntry.getKey();
						stream.write(ByteBuffer.allocate(1).put((byte) (partOfSpeech | (byte) 64)).array());

						MorphologicalCharacteristicsChangesByPrefix morphologicalCharacteristicsChangesByPrefix = morphologicalCharacteristicsChangesEntry.getValue();
						List<Byte> characteristicChanges = morphologicalCharacteristicsChangesByPrefix.getCharacteristicChanges();
						for (Byte bit : characteristicChanges) {
							stream.write(ByteBuffer.allocate(1).put(bit).array());
						}
					}
				}

				stream.write(ByteBuffer.allocate(1).put((byte) 0xFF).array());
			}

			compression();
		} catch (FileNotFoundException e) {
			String message = String.format("Файл не найден!%s.\n", e.getMessage());
			log.warn(message, e);
		} catch (Exception e) {
			String message = String.format("Ошибка записи в файл!%s.\n", e.getMessage());
			log.warn(message, e);
		}
	}

	/**
	 * Запаковывает бинарный файл
	 */
	private void compression() {
		compressionFile(file.getAbsolutePath(), LAVAL_COMPRESS);
	}
}
