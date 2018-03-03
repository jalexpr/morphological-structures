package load;

import load.FileHelper;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.LZMAOutputStream;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static load.FileHelper.*;

public class Lzma2FileHelper {

    public static final String ARCHIVE_EXPANSION = ".7z";
    private static final int LAVAL_COMPRESS = 9;

    public static int compressionFile(String pathFile) {
        return compressionFile(pathFile, LAVAL_COMPRESS);
    }

    public static int compressionFile(String pathFile, int lavalCompress) {
        LZMA2Options options = new LZMA2Options();
        setPresetInOptions(options, lavalCompress);

        System.err.println("Encoder memory usage: " + options.getEncoderMemoryUsage() + " KiB");
        System.err.println("Decoder memory usage: " + options.getDecoderMemoryUsage() + " KiB");

        BufferedInputStream bufferedInput = openBufferedInputStream(pathFile);
        int inputSize = available(bufferedInput);
        byte[] buf = new byte[inputSize];
        FileHelper.read(bufferedInput, buf);

        LZMAOutputStream encoder = openLzmaOutputStream(pathFile, options, inputSize);
        write(encoder, buf, 0, inputSize);

        closeFile(bufferedInput);
        closeLZMAFile(encoder);
        return inputSize;
    }

    public static void deCompressionFile(String pathFile, String newNameFile) {
        deCompressionFile(pathFile, Integer.MAX_VALUE / 2, newNameFile);
    }

    public static void deCompressionFile(String pathFile, int sizeFileInByte, String newNameFile) {
        FileOutputStream deCompressFile = openFileOutputStream(newNameFile);
        InputStream compressFile = openLzmaInputStream(pathFile);
        byte[] buf = new byte[sizeFileInByte];
        int sizeDecompressFile = read(compressFile, buf, 0, sizeFileInByte);
        FileHelper.write(deCompressFile, buf, sizeDecompressFile);
    }

    public static LZMAOutputStream openLzmaOutputStream(String pathFile, LZMA2Options options, long var3) {
        LZMAOutputStream lzmaOutputStream = null;
        try {
            String fullNameFile = String.format("%s%s", pathFile, ARCHIVE_EXPANSION);
            lzmaOutputStream = new LZMAOutputStream(openFileOutputStream(fullNameFile), options, var3);
        } catch (IOException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return lzmaOutputStream;
    }

    public static LZMAInputStream openLzmaInputStream(String pathFile) {
        LZMAInputStream lzmaInputStream = null;
        try {
            lzmaInputStream = new LZMAInputStream(openFileInputStream(pathFile));
        } catch (IOException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return lzmaInputStream;
    }

    public static void setPresetInOptions(LZMA2Options options, int lavalCompress) {
        try {
            options.setPreset(lavalCompress);
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void write(LZMAOutputStream encoder, byte[] buf, int var, int inputSize) {
        try {
            encoder.write(buf, var, inputSize);
        } catch (IOException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n");
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
    }

    public static int read(InputStream compressFile, byte[] buf, int var, int sizeFileInByte) {
        try {
            return compressFile.read(buf, var, sizeFileInByte);
        } catch (IOException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n");
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return 0;
    }

    public static void closeLZMAFile(LZMAOutputStream lzmaOutputStream) {
        try {
            lzmaOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "Не удалось закрыть файл!", ex);
        }
    }

    public static void main(String[] args) {
        String nameFile = "dict.opcorpora.txt";
//        int size = 282217094;
        int size = compressionFile(nameFile, 9);
        deCompressionFile(nameFile + ARCHIVE_EXPANSION, size, nameFile);
    }

}
