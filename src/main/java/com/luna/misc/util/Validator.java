package com.luna.misc.util;

public class Validator {

    public static void validateEmail( String email ) throws Exception {

    }

    public static void validatePassword( String password ) throws Exception {


    }

    public static void validateLogin( String login ) throws Exception {


    }

    public static void validateName( String name ) throws Exception {

    }

    public static void requireNotNullOrEmpty( String value, Exception e ) throws Exception 
    {
        if( value == null || value.isEmpty() )
        {
            throw e;
        }
    }

}