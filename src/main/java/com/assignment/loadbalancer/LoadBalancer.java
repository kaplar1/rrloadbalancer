package com.assignment.loadbalancer;

import java.util.List;

/*load balancer selects a server from the pool*/


public interface LoadBalancer {

Server next();

List<Server> servers();
}
