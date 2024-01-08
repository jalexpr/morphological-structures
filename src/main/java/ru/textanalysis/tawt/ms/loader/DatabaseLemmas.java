package ru.textanalysis.tawt.ms.loader;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.conversion.dictionary.FormForConversion;
import ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.List;

import static ru.textanalysis.tawt.ms.constant.Const.LAVAL_COMPRESS;
import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.*;
import static template.wrapper.classes.Lzma2FileHelper.*;

@Slf4j
public class DatabaseLemmas {

	private final byte[] CONTROL_VALUE = ByteBuffer.allocate(4).putInt(MorphologicalStructuresProperty.CONTROL_VALUE).array();
    private final File file;

    protected DatabaseLemmas(String path) {
		this.file = Paths.get(path, FOLDER, MS_VERSION, NAME_HASH_AND_MORF_CHARACTERISTICS).toFile();
    }

    public String getFilePath() {
        return file.getAbsolutePath();
    }

    public void recreate(List<List<FormForConversion>> lemmas) {
        file.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(file)) {
            for (List<FormForConversion> lemma : lemmas) {
                for (FormForConversion form : lemma) {
                    stream.write(form.getByteFileFormat());
                }
                stream.write(CONTROL_VALUE);
            }
        } catch (IOException ex) {
            log.error("Ошибка при чтении файла. Файл: {}", file.getAbsolutePath(), ex);
        }
    }

    public void decompressDd() {
        boolean needDecompress;
        if (file.exists()) {
            //todo проверка, что версия старая. Проверка еще не реализована
            needDecompress = false;
        } else {
            needDecompress = true;
        }
        if (needDecompress) {
            System.out.println("Decompress DB. Please wait a few minutes");
            File dir = file.getParentFile();
            if (!dir.mkdirs()) {
                log.debug("Not create dir '{}' for decompress", dir.getAbsolutePath());
            }
            String nameExp = file.getName() + ARCHIVE_EXPANSION;
            String path = FOLDER + nameExp;
            URL pathZip = getClass().getClassLoader().getResource(path);
            if (pathZip != null) {
                deCompressionFile(path, file);
            } else {
                log.info("Not create file '{}' for decompress", path);
            }
        }
    }

    public void compression() {
        compressionFile(file.getAbsolutePath(), LAVAL_COMPRESS);
//        zipCompressFile(file.getAbsolutePath(), file.getName());
    }
}
