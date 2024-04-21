package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.file.postfix;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristics;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristicsInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristicsOccurrence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static ru.textanalysis.tawt.ms.constant.Const.LAVAL_COMPRESS;
import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.*;
import static template.wrapper.classes.Lzma2FileHelper.compressionFile;

@Slf4j
public class PostfixBinaryFile {

	private final File file;
	private final PostfixMorphologicalCharacteristicsInfo postfixMorphologicalCharacteristicsInfo;

	public PostfixBinaryFile(PostfixMorphologicalCharacteristicsInfo postfixMorphologicalCharacteristicsInfo) {
		this.postfixMorphologicalCharacteristicsInfo = postfixMorphologicalCharacteristicsInfo;
		String path = System.getProperty("java.io.tmpdir");
		this.file = Paths.get(path, FOLDER, MS_VERSION, POSTFIXES).toFile();
	}

	/**
	 * Создание бинарного файла по собранной статистике
	 */
	public void recreate() {
		Map<String, PostfixMorphologicalCharacteristicsOccurrence> morphologicalCharacteristicsOccurrences = postfixMorphologicalCharacteristicsInfo.getMorphologicalCharacteristicsOccurrences();

		file.getParentFile().mkdirs();
		try (FileOutputStream stream = new FileOutputStream(file)) {
			for (Map.Entry<String, PostfixMorphologicalCharacteristicsOccurrence> entry : morphologicalCharacteristicsOccurrences.entrySet()) {
				String postfix = entry.getKey();
				if (postfix == null) {
					continue;
				}
				byte[] postfixBytes = postfix.getBytes();

				PostfixMorphologicalCharacteristicsOccurrence postfixMorphologicalCharacteristicsOccurrence = entry.getValue();
				if (postfixMorphologicalCharacteristicsOccurrence != null) {
					PostfixMorphologicalCharacteristics initialForm = postfixMorphologicalCharacteristicsOccurrence.getInitialFormPostfix();
					List<Byte> partOfSpeechesList = postfixMorphologicalCharacteristicsOccurrence.getPartOfSpeeches();
					List<Long> tagsList = postfixMorphologicalCharacteristicsOccurrence.getTags();

					if (!partOfSpeechesList.isEmpty() && partOfSpeechesList.size() == tagsList.size()) {
						stream.write(ByteBuffer.allocate(1).put((byte) postfixBytes.length).array());
						stream.write(postfixBytes);

						for (int i = 0; i < partOfSpeechesList.size(); i++) {
							if (i == 5) {
								break;
							}

							String initialFormPostfix = initialForm.getPostfix();
							if (postfix.equals(initialFormPostfix)) {
								stream.write(ByteBuffer.allocate(1).put((byte) (partOfSpeechesList.get(i) | 0x80)).array());
								stream.write(ByteBuffer.allocate(8).putLong(initialForm.getTagsLong()).array());
							} else {
								stream.write(ByteBuffer.allocate(1).put(partOfSpeechesList.get(i)).array());

								byte[] initialFormPostfixBytesCount = initialFormPostfix.getBytes();
								stream.write(ByteBuffer.allocate(1).put((byte) initialFormPostfixBytesCount.length).array());
								stream.write(initialFormPostfixBytesCount);

								stream.write(ByteBuffer.allocate(8).putLong(tagsList.get(i)).array());
								stream.write(ByteBuffer.allocate(8).putLong(initialForm.getTagsLong()).array());
							}
						}

						stream.write(ByteBuffer.allocate(1).put((byte) 0xFF).array());
					}
				}
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
