package com.example.dell.menu.data;

import java.math.BigInteger;
import java.nio.IntBuffer;
import java.nio.channels.InterruptibleChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Dell on 12.11.2017.
 */

public class MD5Hash {
    public static String getHash(String message) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(message.getBytes(Charset.forName("UTF8")));
        byte[] messageDigest = md5.digest();

        StringBuilder hexString = new StringBuilder();
        for(byte aMessageDigest : messageDigest){
            String h = Integer.toHexString(0xFF & aMessageDigest);
            while (h.length() < 2)
                h = "0"+h;
            hexString.append(h);
        }
        return hexString.toString();
    }
}

