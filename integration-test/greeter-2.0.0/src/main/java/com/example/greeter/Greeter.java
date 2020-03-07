package com.example.greeter;

public class Greeter {

    public String hello(String name, String from) {
        return "Hello, " + name + " from " + from + " (Greeter 2.0.0)";
    }

    public String goodBye(String name, String from) {
        return "Good bye, " + name + " from " + from + " (Greeter 2.0.0)";
    }
}
