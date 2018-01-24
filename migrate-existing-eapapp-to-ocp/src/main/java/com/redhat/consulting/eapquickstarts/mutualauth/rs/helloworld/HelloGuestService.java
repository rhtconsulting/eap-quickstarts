package com.redhat.consulting.eapquickstarts.mutualauth.rs.helloworld;

/**
 * A simple Restful service which is able to say hello to someone
 * 
 * @author cadjai@redhat.com
 * 
 */
public class HelloGuestService {

    String createHelloMessage(String name) {
        return "Hello " + name + "!";
    }

    String getMyNameMessage(String name) {
        return "Hello " + name + "!";
    }

}
