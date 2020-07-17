# LoadBalanceApp
Author: Antonio Casado

Load balancer implementing Random and Round Robin algorithms

## Basic Assumptions
- Default strategy is ROUNDROBIN and is defined in application.yml
- LoadbalancerApplication main  as the a consumer interface.
- I have used sleep(xxx) in order to simulate real world requests and responses.



### Instructions to run the app
1. Clone git repository
2. Execute the command `mvn install` to build the application using Maven.
3. Execute the command `mvn spring-boot:run` to run the app.
4. ctrl+c to interrupt the process.
