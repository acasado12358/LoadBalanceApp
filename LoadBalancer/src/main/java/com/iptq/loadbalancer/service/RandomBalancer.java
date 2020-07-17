package com.iptq.loadbalancer.service;

import com.iptq.loadbalancer.model.Provider;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;


public class RandomBalancer extends AbstractProviderBalancer {

    private Random random;

    public RandomBalancer() {
        super();
        this.random = new Random();
    }

    @Override
    public Future<String> get() {
        List<Provider> listActiveProviders = getListActiveProviders();

        int randomIndex = new Random().nextInt(listActiveProviders.size());
        Provider targetProvider = listActiveProviders.get(randomIndex);

        return executeProvider(targetProvider);
    }
}
