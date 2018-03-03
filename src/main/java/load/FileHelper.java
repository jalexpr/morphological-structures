/*
 * Copyright (C) 2017  Alexander Porechny alex.porechny@mail.ru
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0) as published by the Creative Commons.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike
 * 3.0 Unported (CC BY-SA 3.0) along with this program.
 * If not, see <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Thanks to Sergey Politsyn and Katherine Politsyn for their help in the development of the library.
 *
 *
 * Copyright (C) 2017 Александр Поречный alex.porechny@mail.ru
 *
 * Эта программа свободного ПО: Вы можете распространять и / или изменять ее
 * в соответствии с условиями Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0), опубликованными Creative Commons.
 *
 * Эта программа распространяется в надежде, что она будет полезна,
 * но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; без подразумеваемой гарантии
 * КОММЕРЧЕСКАЯ ПРИГОДНОСТЬ ИЛИ ПРИГОДНОСТЬ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.
 * См. Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * для более подробной информации.
 *
 * Вы должны были получить копию Attribution-NonCommercial-ShareAlike 3.0
 * Unported (CC BY-SA 3.0) вместе с этой программой.
 * Если нет, см. <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Благодарим Сергея и Екатерину Полицыных за оказание помощи в разработке библиотеки.
 */
package load;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import morphologicalstructures.Property;

public class FileHelper {

    public static Scanner openScannerFromZipFile(String pathZipFile, String pathFile, String encoding) throws Exception {
        try {
            return new Scanner(openZipFile(pathZipFile, pathFile), encoding);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.%sПроверте наличие %s, в случае отсуствия скачайте с репозитория %s%s",
                    System.lineSeparator(), pathFile, Property.MY_REPOSITORY, System.lineSeparator());
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
            throw new Exception();
        }
    }

    public static ZipInputStream openZipFile(String zipPath, String nameLibrary) throws IOException {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(new File(zipPath)));
        for (ZipEntry e; (e = zip.getNextEntry()) != null;) {
            if (e.getName().equals(nameLibrary)) {
                return zip;
            }
        }
        throw new EOFException("Cannot find " + nameLibrary);
    }

    public static void zipCompressFile(String pathFile, String nameFile) {
        try(ZipOutputStream zip = new ZipOutputStream((new FileOutputStream((new File(pathFile + ".zip")))))) {
            BufferedInputStream bufferedInput = openBufferedInputStream(pathFile);
            int inputSize = available(bufferedInput);
            byte[] buf = new byte[inputSize];
            FileHelper.read(bufferedInput, buf);

            zip.putNextEntry(new ZipEntry(nameFile));
            zip.write(buf, 0, inputSize);

            zip.flush();
            zip.close();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "Ошибка при чтении файла.\r\n", ex);
        }
    }

    public static BufferedReader openBufferedReaderStream(String pathFile) {
        return openBufferedReaderStream(pathFile, "UTF-8");
    }

    public static BufferedReader openBufferedReaderStream(String pathFile, String encoding) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        } catch (UnsupportedEncodingException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n1)Проверте кодировку %s в соотвевствии с параметрами в property.xml.\r\n2)При отсутствии property.xml кодировка по умолчанию %s\r\n\r\n",
                    pathFile, encoding);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }

        return bufferedReader;
    }

    public static FileOutputStream openFileOutputStream(String pathFile) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pathFile);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при открытия файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return fileOutputStream;
    }

    public static FileInputStream openFileInputStream(String pathFile) {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(pathFile);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при открытия файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return fileInputStream;
    }

    public static BufferedInputStream openBufferedInputStream(String pathFile) {
        BufferedInputStream bufferedInput = null;
        try {
            bufferedInput = new BufferedInputStream(new FileInputStream(pathFile));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return bufferedInput;
    }

    public static int available(BufferedInputStream bufferedInput) {
        try {
            return bufferedInput.available();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static int read(BufferedInputStream bufferedInput, byte[] buf) {
        try {
            return bufferedInput.read(buf);
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "Ошибка при чтении файла.\r\n", ex);
        }
        return 0;
    }

    public static BufferedWriter openBufferedWriterStream(String pathFile, String encoding) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        } catch (UnsupportedEncodingException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n1)Проверте кодировку %s в соотвевствии с параметрами в property.xml.\r\n2)При отсутствии property.xml кодировка по умолчанию %s\r\n\r\n",
                    pathFile, encoding);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, messages, ex);
        }
        return bufferedWriter;
    }

    public static String readLine(BufferedReader bufferedReader) {
        try {
            if(ready(bufferedReader)) {
                return bufferedReader.readLine();
            } else {
                return "";
            }
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

    }

    public static boolean ready(BufferedReader bufferedReader) {
        try {
            return bufferedReader.ready();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void write(FileOutputStream fileOutputStream, byte[] bytes) {
        try {
            fileOutputStream.write(bytes);
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void write(FileOutputStream fileOutputStream, byte[] bytes, int sizeNewFile) {
        try {
            fileOutputStream.write(bytes, 0, sizeNewFile);
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeFile(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "Не удалось закрыть файл!", ex);
        }
    }

    public static void closeFile(OutputStream inputStream) {
        try {
            inputStream.flush();
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeFile(Scanner scanner) {
        scanner.close();
    }

    public static void closeFile(Reader reader) {
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeFile(Writer writer) {
        try {
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteFile(String pathFile) {
        File file = new File(pathFile);
        file.delete();
    }

}
