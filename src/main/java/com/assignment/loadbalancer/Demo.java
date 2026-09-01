package com.assignment.loadbalancer;

public final class Demo {

    public static void main(String[] args) {
        LoadBalancer balancer = new RoundRobinBalancer(ServerPool.defaultPool());

        for (int i = 0; i < 10; i++) {
            System.out.println(balancer.next().name());
        }
    }
}
