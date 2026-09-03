# Load Balancer Simulation



## Requirements
	Scheduling order: Round Robin
	Objects: Servers "a" - "g"

## Setup
Language: Java 21 LTS\
Build: Maven 3.2.5 or newer\
IDE: vscode\
Extension: Microsof Java Extension(s)\
JUnit: 5.10

## Design

Server (Record of servers to be created)\
ServerPool (List of servers)\
LoadBalancer (interface to call next)\
RoundRobinBalancer (getting the next server in rr fashion)\
Demo (main function to call)\
RoundRobinTest (JUnit Test for RR Algo)

## Assumptions and Decisions

The server pool is static.\
Server" is a name only. No port I/O or memory\
Thread-safety was treated as an implicit requirement\
No persistence or external transport.

## Running it
	mvn test                                                                 #Run all tests
	mvn compile exec:java -Dexec.mainClass=com.assignment.loadbalancer.Demo  #Compile and run demo
	
	mvn compile                                                              #Compile and Run direct
	java -cp target/classes com.assignment.loadbalancer.Demo

## Test coverage

RoundRobinBalancerTest: Strict a→g ordering over one full cycle, wraparound, Immutability and distribution of calls across servers under concurrent access
