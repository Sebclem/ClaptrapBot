package net.Broken.Tools.UserManager;

import java.security.SecureRandom;

public class UserRegister {


    public String generateToken(){
        SecureRandom random = new SecureRandom();
        long longToken = Math.abs( random.nextLong() );
        String randomStr = Long.toString( longToken, 16 );
        return randomStr;
    }
}
