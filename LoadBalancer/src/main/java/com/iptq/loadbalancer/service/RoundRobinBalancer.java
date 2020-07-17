package com.iptq.loadbalancer.service;

import com.iptq.loadbalancer.model.Provider;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinBalancer extends AbstractProviderBalancer {

    private final AtomicInteger position;

    public RoundRobinBalancer() {
        super();
        this.position = new AtomicInteger(0);
    }

    @Override
    public Future<String> get() {
        List<Provider> activeProviders = getListActiveProviders();
        Provider targetProvider = null;

        synchronized (position) {
            if (position.get() > activeProviders.size() - 1) {
                position.set(0);
            }
            targetProvider = activeProviders.get(position.get());

            position.incrementAndGet();
        }
        return executeProvider(targetProvider);
    }

}
