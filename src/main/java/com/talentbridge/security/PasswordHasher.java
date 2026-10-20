package com.talentbridge.security;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
public final class PasswordHasher {
    private static final int ITERATIONS=120_000, KEY_BITS=256;
    private static final SecureRandom RANDOM=new SecureRandom();
    private PasswordHasher(){}
    public static String hash(String password){
        byte[] salt=new byte[16];RANDOM.nextBytes(salt);
        try {
            return ITERATIONS+":"+Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(derive(password.toCharArray(),salt,ITERATIONS));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("PBKDF2 password hashing is unavailable", ex);
        }
    }
    public static boolean verify(String password,String stored){
        try{
            String[] parts=stored.split(":",3);int iterations=Integer.parseInt(parts[0]);
            byte[] salt=Base64.getDecoder().decode(parts[1]),expected=Base64.getDecoder().decode(parts[2]);
            byte[] actual=derive(password.toCharArray(),salt,iterations);if(expected.length!=actual.length)return false;
            int mismatch=0;for(int i=0;i<expected.length;i++)mismatch|=expected[i]^actual[i];return mismatch==0;
        }catch(RuntimeException|GeneralSecurityException ex){return false;}
    }
    private static byte[] derive(char[] password,byte[] salt,int iterations) throws GeneralSecurityException{
        PBEKeySpec spec=new PBEKeySpec(password,salt,iterations,KEY_BITS);
        try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}
        finally{spec.clearPassword();}
    }
}
