package ru.textanalysis.tawt.ms.loader;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.*;
import static template.wrapper.classes.Lzma2FileHelper.ARCHIVE_EXPANSION;
import static template.wrapper.classes.Lzma2FileHelper.deCompressionFile;

@Slf4j
public class PostfixInfoFile {

	private static final String path = System.getProperty("java.io.tmpdir");
	private final File file;

	public PostfixInfoFile() {
		this.file = Paths.get(path, FOLDER, MS_VERSION, POSTFIXES).toFile();
	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}

	public void decompress() {
		boolean needDecompress;
		//todo проверка, что версия старая
		needDecompress = !file.exists();
		if (needDecompress) {
			System.out.println("Decompress file. Please wait a few minutes");
			File dir = file.getParentFile();
			if (!dir.mkdirs()) {
				log.debug("Not create dir '{}' for decompress", dir.getAbsolutePath());
			}
			String nameExp = file.getName() + ARCHIVE_EXPANSION;
			String path = FOLDER + nameExp;
			URL pathZip = getClass().getClassLoader().getResource(path);
			if (pathZip != null) {
				deCompressionFile(path, 138829120, file);
			} else {
				log.info("Not create file '{}' for decompress", path);
			}
		}
	}
}
