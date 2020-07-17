package com.iptq.loadbalancer.service;

import com.iptq.loadbalancer.configuration.IpPool;
import com.iptq.loadbalancer.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public abstract class AbstractProviderBalancer implements ProviderBalancer{

    final static Logger log = LoggerFactory.getLogger(AbstractProviderBalancer.class);

    private static final int MAX_PROVIDERS = 10;
    private static final int INITIAL_DELAY = 1;
    private static final int INTERVAL = 10;

    public  static ThreadPoolExecutor threadPool;
    private final  BlockingQueue queue;

    public  ConcurrentHashMap<Integer, Provider> providerServersMap;

    private Map<Provider,Integer> unresponsiveProviders = new HashMap<>();

    private List<Provider> excludedProviders = new ArrayList<>();

    public AbstractProviderBalancer() {
        this.providerServersMap = IpPool.getProviderServersMap();
        this.queue = new ArrayBlockingQueue<Runnable>(MAX_PROVIDERS);
        this.threadPool = new ThreadPoolExecutor(MAX_PROVIDERS, MAX_PROVIDERS,
                0L, TimeUnit.MILLISECONDS, queue);
        this.heartBeatScheduler(INITIAL_DELAY, INTERVAL);
    }

    public static Future<String> executeProvider(Provider provider) {
        return threadPool.submit(provider::get);
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    Runnable heartBeatTask = () -> heartBeatCheck();


    protected void heartBeatScheduler(int initialDelay, int interval) {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(heartBeatTask, initialDelay, interval, TimeUnit.SECONDS);
    }

    protected void heartBeatCheck() {
        log.info("=======================================");
        log.info("Starting heart-beat check");
        for (Map.Entry<Integer, Provider> p : providerServersMap.entrySet()) {

            Provider provider = p.getValue();
            boolean check = provider.check();// cambiar esto

            if (check) {
                //if the provider is active now but wasn't previously increase the counter
                if (provider.getStatus().equals(ProviderStatus.INACTIVE)) {
                    log.info(String.format("[%s] is responsive again!", provider.getIpName()));
                    int i = unresponsiveProviders.getOrDefault(provider, 0) + 1;
                    unresponsiveProviders.put(provider, i);
                    if (unresponsiveProviders.get(provider) >= 2) {
                        activateProvider(provider);
                        unresponsiveProviders.remove(provider);
                        log.info(String.format("[%s] is working again, adding it back", provider.getIpName()));
                    }
                }
            } else if (provider.getStatus().equals(ProviderStatus.INACTIVE)) {  //if it's not active in this iteration but included.
                log.info(String.format("[%s] is not responsive, will be excluded", provider.getIpName()));
                activateProvider(provider);
                unresponsiveProviders.put(provider, 0);
            }
        }
        int capacity = checkCapacity();
        log.info(String.format("Updating loadbalancer request capacity to: [%d]", capacity));

        //update load balancer capacity according to available providers
        this.threadPool.setCorePoolSize(capacity);
        log.info("End of heart-beat check");
        log.info("=======================================");

    }

    private int checkCapacity() {
        int maxCapacity = 0;
        for (Provider p : getListActiveProviders()) {
            maxCapacity += p.getRequestCapacity();
        }
        return maxCapacity;
    }

    public List<Provider> getListActiveProviders() {
        return providerServersMap.values().stream()
                .filter(p -> p.getStatus().equals(ProviderStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    public Provider shutDownProvider(Integer idServer) {
        Provider provider = providerServersMap.get(idServer);
        provider.setStatus(ProviderStatus.INACTIVE);

        providerServersMap.remove(idServer);
        providerServersMap.put(idServer, provider);

        excludedProviders.add(provider);
        return provider;
    }

    public Provider activateProvider(Integer idServer) {
        for (Provider p : excludedProviders) {
            if (p.getId().equals(idServer.toString())) {
                excludedProviders.remove(p);
                p.setStatus(ProviderStatus.ACTIVE);
                providerServersMap.remove(idServer);

                providerServersMap.put(idServer, p);
                return p;
            }
        }
        return null;
    }

    public Provider activateProvider(Provider provider) {
        providerServersMap.put(Integer.parseInt(provider.getId()), provider);
        for (Provider p : excludedProviders) {
            if (p.getId().equals(provider.getId())) {
                excludedProviders.remove(p);
                p.setStatus(ProviderStatus.ACTIVE);
                providerServersMap.remove(p.getId());

                providerServersMap.put(Integer.parseInt(p.getId()), p);
                return p;
            }
        }
        return null;
    }
}

