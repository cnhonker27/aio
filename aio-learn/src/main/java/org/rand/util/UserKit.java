package org.rand.util;

import java.util.concurrent.ConcurrentHashMap;

public class UserKit {

    private final static ConcurrentHashMap<String,String> TOKEN=new ConcurrentHashMap<>();

    public static void setToken(String userId,String token){
        TOKEN.put(userId,token);
    }

    public static String getToken(String userId){
        return TOKEN.get(userId);
    }

}
