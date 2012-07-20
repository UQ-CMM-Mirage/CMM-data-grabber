/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import au.edu.uq.cmm.paul.PaulException;

/**
 * A collection of helpewr methods for ccreating and combining hashes.
 * 
 * @author scrawley
 */
public class HashUtils {
    
    private static String ALGORITHM = "SHA-512";
    private static int HASH_STRING_LENGTH = 128; // should the hash length in bytes * 2

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append("0123456789ABCDEF".charAt(0xF & (b >> 4)));
            sb.append("0123456789ABCDEF".charAt(0xF & b));
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String str) throws InvalidHashException {
        byte[] res = new byte[str.length() / 2];
        for (int i = 0; i < res.length; i++) {
            try {
                res[i] = Byte.parseByte(str.substring(i * 2, 2));
            } catch (NumberFormatException ex) {
                throw new InvalidHashException("Hash string contains invalid characters");
            }
        }
        return res;
    }
    
    public static String combineHashes(String hash1, String hash2) throws InvalidHashException {
        if (hash1 == null) {
            return hash2;
        } else if (hash2 == null) {
            return hash1;
        }
        if (hash1.length() != HASH_STRING_LENGTH || hash2.length() != HASH_STRING_LENGTH) {
            throw new InvalidHashException("Incorrect hash string length");
        }
        byte[] h1 = hexStringToBytes(hash1);
        byte[] h2 = hexStringToBytes(hash2);
        for (int i = 0; i < h1.length; i++) {
            h1[i] = (byte) (h1[i] ^ h2[i]);
        }
        return bytesToHexString(h1);
    }
    
    public static MessageDigest createDigester() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            throw new PaulException("Can't find the required secure hash algorithm", ex);
        }
    }

    public static String fileHash(File source) throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream(source)) {
            MessageDigest md = HashUtils.createDigester();
            byte[] data = new byte[8192];
            int count;
            while ((count = fis.read(data)) > 0) {
                md.update(data, 0, count);
            }
            byte[] hash = md.digest();
            return HashUtils.bytesToHexString(hash);
        }
    }
}
