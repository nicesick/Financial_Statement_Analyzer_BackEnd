package com.jihun.study.openDartApi.utils.stream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipStream {
    /**
     * getZipStream
     *
     * Zip(Binary) 파일 bytes 를 받아 파일로 저장합니다.
     *
     * @param data
     * @param CHARSET
     *
     * @return
     * @throws IOException
     */
    public static List<String> streamZip(final byte[] data, final String CHARSET) throws IOException {
        List<String>            output              = new ArrayList<>();

        ByteArrayInputStream    inputStream         = null;
        ZipInputStream          zipInputStream      = null;
        ZipEntry                zipEntry            = null;

        FileOutputStream        outputStream        = null;
        OutputStreamWriter      outputStreamWriter  = null;
        BufferedWriter          bufferedWriter      = null;

        InputStreamReader       inputStreamReader   = null;
        BufferedReader          bufferedReader      = null;
//
//        StringBuffer            stringBuffer        = null;


        inputStream         = new ByteArrayInputStream(data);
        zipInputStream      = new ZipInputStream(inputStream);
        inputStreamReader   = new InputStreamReader(zipInputStream, CHARSET);
        bufferedReader      = new BufferedReader(inputStreamReader);
//
//            stringBuffer        = new StringBuffer();
        while((zipEntry = zipInputStream.getNextEntry()) != null) {
//                if (stringBuffer.length() > 0) {
//                    stringBuffer.delete(0, stringBuffer.length() - 1);
//                }
//
            outputStream        = new FileOutputStream(zipEntry.getName());
            outputStreamWriter  = new OutputStreamWriter(outputStream, CHARSET);
            bufferedWriter      = new BufferedWriter(outputStreamWriter);

            String line;
            while((line = bufferedReader.readLine()) != null) {
//                    stringBuffer.append(line);
                bufferedWriter.write(line);
            }

//                output.put(zipEntry.getName(), stringBuffer.toString());
            output.add(zipEntry.getName());

            zipInputStream.closeEntry();
            bufferedWriter.close();
        }


        return output;
    }
}
