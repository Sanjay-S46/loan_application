package com.loanapp.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashPassword {
    
    // Configuration constants
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // in bits

     // Generate a hashed password using PBKDF2
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
    
    // Generate a random salt
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 128-bit salt
        random.nextBytes(salt);
        return salt;
    }
    
    public String[] generateHashedPassword(String password) {
        try {
            byte[] salt = generateSalt();
            
            String hashedPassword = hashPassword(password, salt);
            String[] passwordWithSalt = new String[2];
            passwordWithSalt[0] = Base64.getEncoder().encodeToString(salt);
            passwordWithSalt[1] = hashedPassword;
            return passwordWithSalt;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // method used to verify the password
    public static boolean verifyPassword(String inputPassword, String storedPassword, String storedSaltBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] storedSalt = Base64.getDecoder().decode(storedSaltBase64);
        String inputHash = hashPassword(inputPassword, storedSalt);
        return storedPassword.equals(inputHash);
    }
}
