package com.assignment.loadbalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
Requirement 1: Need to handout server on the call of next() in round robin order and will wrap around to 
work indefinetly.
*/

public final class RoundRobinBalancer implements LoadBalancer {

    private final List<Server> servers;
    private final AtomicInteger cursor = new AtomicInteger();

    public RoundRobinBalancer(List<Server> servers) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException("server pool must not be empty");
        }

        this.servers = List.copyOf(servers);
    }

    /* Using floorMod to keep the index positive after max int value wrap around */

    @Override
    public Server next() {
        int index = Math.floorMod(cursor.getAndIncrement(), servers.size());
        return servers.get(index);
    }

    @Override
    public List<Server> servers() {
        return servers;
    }

}
