package com.iptq.loadbalancer.service;

import com.iptq.loadbalancer.model.Provider;
import java.util.concurrent.Future;

public interface ProviderBalancer {

    Future<String> get();

    Provider shutDownProvider(Integer idServer);

    Provider activateProvider(Integer idServer);

}
