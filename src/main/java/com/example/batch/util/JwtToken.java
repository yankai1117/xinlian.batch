package com.example.batch.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


public class JwtToken {
	/**
    *
    * @return
    * @throws UnsupportedEncodingException
    */
   public static String getencodedJWT (String iss, String secret) throws Exception {
       Algorithm algorithm = Algorithm.HMAC256(secret);
       String token = JWT.create()
               .withIssuer(iss)
               .withExpiresAt(new Date(System.currentTimeMillis() + 86400L * 1000))
               .sign(algorithm);
       return token;
   }
}
