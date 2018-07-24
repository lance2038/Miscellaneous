package com.utils;

import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

public class Base64Util
{
    public static String fileToBase64(String filePath)
    {
        String imgBase64 = "";
        try
        {
            File file = new File(filePath);
            byte[] content = new byte[(int) file.length()];
            FileInputStream finputstream = new FileInputStream(file);
            finputstream.read(content);
            finputstream.close();
            imgBase64 = new String(encodeBase64(content));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return imgBase64;
    }

    public static void base64ToFile(String base64str, String path)
    {
        byte[] buffer;
        try
        {
            buffer = new BASE64Decoder().decodeBuffer(base64str.replaceAll("\n", ""));
            FileOutputStream out = new FileOutputStream(path);
            out.write(buffer);
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
