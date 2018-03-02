package conversiondictionary;

import load.FileHelper;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.LZMAOutputStream;

import java.io.*;

import static load.FileHelper.openFileInputStream;
import static load.FileHelper.openFileOutputStream;

public class Compression {

    public static final String EXPANSION = ".7z";

    public static void compression(String pathFile, int lavalCompress) throws IOException {
        LZMA2Options options = new LZMA2Options();
        options.setPreset(lavalCompress);

        System.err.println("Encoder memory usage: " + options.getEncoderMemoryUsage() + " KiB");
        System.err.println("Decoder memory usage: " + options.getDecoderMemoryUsage() + " KiB");

        InputStream in = openFileInputStream(pathFile);
        BufferedInputStream bis = new BufferedInputStream(in);
        int inputSize = bis.available();
        OutputStream out = openFileOutputStream(pathFile + EXPANSION);
        LZMAOutputStream encoder = new LZMAOutputStream(out, options, inputSize);


        byte[] buf = new byte[inputSize];
        bis.read(buf, 0, inputSize);
        encoder.write(buf, 0, inputSize);

        encoder.finish();
        out.flush();
    }

    public static void deCompression(String pathFile) {
        try {
            InputStream in = new FileInputStream(pathFile);
            FileOutputStream bufw = FileHelper.openFileOutputStream(pathFile + ".txt");
            in = new BufferedInputStream(in);
            in = new LZMAInputStream(in);
            byte[] buf = new byte[282217094];
            int size;
            while ((size = in.read(buf)) != -1)
                bufw.write(buf);
        } catch (FileNotFoundException e) {
            System.err.println("LZMADecDemo: Cannot open " + pathFile + ": " + e.getMessage());
        } catch (EOFException e) {
            System.err.println("LZMADecDemo: Unexpected end of input on " + pathFile);
        } catch (IOException e) {
            System.err.println("LZMADecDemo: Error decompressing from " + pathFile + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
//        compression("dict.opcorpora.txt", 9);
        deCompression("dict.opcorpora.txt" + EXPANSION);
    }

}
