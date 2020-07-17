package com.iptq.loadbalancer;

import com.iptq.loadbalancer.configuration.GlobalProperties;
import com.iptq.loadbalancer.model.Provider;
import com.iptq.loadbalancer.service.ProviderBalancer;
import com.iptq.loadbalancer.service.RandomBalancer;
import com.iptq.loadbalancer.service.RoundRobinBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import static java.lang.Thread.sleep;

@SpringBootApplication
public class LoadbalancerApplication implements CommandLineRunner {

    @Autowired
    private GlobalProperties globalProperties;

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(LoadbalancerApplication.class);
        app.run();
    }

    @Override
    public void run(String... args) throws Exception {

        final Logger log = LoggerFactory.getLogger(LoadbalancerApplication.class);
        log.trace("Using strategy: " + globalProperties.getDefaultStrategy(), globalProperties.getDefaultStrategy());

        if (globalProperties.getDefaultStrategy().equals("ROUNDROBIN")) {
            log.trace("Using strategy: " + globalProperties.getDefaultStrategy(), globalProperties.getDefaultStrategy());

            RoundRobinBalancer loadBalance = new RoundRobinBalancer();
            doGetServer(loadBalance, 100);
            loadBalance.getThreadPool().shutdown();

        } else {
            log.trace("Using strategy: " + "RANDOM", globalProperties.getDefaultStrategy());

            RandomBalancer loadBalance = new RandomBalancer();
            doGetServer(loadBalance, 100);
            loadBalance.getThreadPool().shutdown();
        }
    }

    private static void doGetServer(ProviderBalancer loadBalance, int queryTimes) throws InterruptedException, ExecutionException {
        final Logger log = LoggerFactory.getLogger(LoadbalancerApplication.class);

        log.trace("executing get SERVER ");

        ConcurrentLinkedQueue<Future<String>> futures = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < queryTimes; i++) {
            sleep(1000);
            if (i == 30) {
                log.trace("--------------------------------------");
                log.trace("Using strategy: " + "deactivate provider 1");
                log.trace("--------------------------------------");
                Provider provider = loadBalance.shutDownProvider(1);
            }
            if (i == 60) {
                log.trace("--------------------------------------");
                log.trace("Using strategy: " + "ACTIVATE provider 1");
                log.trace("--------------------------------------");
                Provider provider = loadBalance.activateProvider(1);
            }
            try {
                Future<String> s = loadBalance.get();
                futures.add(s);
            } catch (RejectedExecutionException ex) {
                System.out.println(ex.getMessage());
            }
            checkFutures(futures, loadBalance);
        }
    }

    private static void checkFutures(ConcurrentLinkedQueue<Future<String>> futureTargets, ProviderBalancer loadBalance) throws ExecutionException, InterruptedException {
        Logger log = LoggerFactory.getLogger(LoadbalancerApplication.class);

        Iterator<Future<String>> it = futureTargets.iterator();
        Future<String> element;

        while (it.hasNext()) {
            element = it.next();
            if (element.isDone()) {
                log.info(String.format("[%s]  ProviderBalancer: %s", loadBalance.getClass().getSimpleName(), element.get()));
                it.remove();
            }
        }
    }
}


