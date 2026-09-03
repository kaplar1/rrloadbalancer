# Load Balancer Simulation



## Requirements
	Scheduling order: Round Robin
	Objects: Servers "a" - "g"

## Setup
Language: Java 21 LTS
Build: Maven 3.2.5 or newer
IDE: vscode
Extension: Microsof Java Extension(s)

## Design

Server (Record of servers to be created)
ServerPool (List of servers)
LoadBalancer (interface to call next)
RoundRobinBalancer (getting the next server in rr fashion)
Demo (main function to call)
RoundRobinTest (JUnit Test for RR Algo)
