package com.assignment.loadbalancer;


/*
A Server class, it just have a name for now.
The ServerPool will use this template to create a Server object by providing a name.
*/



public record Server(String name) {

public Server {
if(name == null || name.isBlank()) {
throw new IllegalArgumentException("Server name cant be blank");
}
}

@Override
public String toString() {
return name;
}
}
