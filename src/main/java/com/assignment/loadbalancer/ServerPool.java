package com.assignment.loadbalancer;

import java.util.Arrays;
import java.util.List;

public final class ServerPool {

    private ServerPool() {
        // utility class, not instantiable
    }

    /* Pool of servers named "a" - "g" */

    public static List<Server> defaultPool() {
        return named("a", "b", "c", "d", "e", "f", "g");
    }

    public static List<Server> named(String... names) {
        return Arrays.stream(names).map(Server::new).toList();
    }

}
