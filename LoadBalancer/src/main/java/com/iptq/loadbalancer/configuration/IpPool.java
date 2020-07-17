package com.iptq.loadbalancer.configuration;

import com.iptq.loadbalancer.model.Provider;
import com.iptq.loadbalancer.service.ProviderStatus;

import java.util.concurrent.ConcurrentHashMap;

public class IpPool {

    static ConcurrentHashMap<Integer, Provider> providerServersMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Provider> getProviderServersMap(){
        providerServersMap.put(1, new Provider("1", "192.168.1.1", ProviderStatus.ACTIVE));
        providerServersMap.put(2, new Provider("2", "192.168.1.2", ProviderStatus.ACTIVE));
        providerServersMap.put(3, new Provider("3", "192.168.1.3", ProviderStatus.ACTIVE));
        providerServersMap.put(4, new Provider("4", "192.168.1.4", ProviderStatus.ACTIVE));
        providerServersMap.put(5, new Provider("5", "192.168.1.5", ProviderStatus.ACTIVE));
        providerServersMap.put(6, new Provider("6", "192.168.1.6", ProviderStatus.ACTIVE));
        providerServersMap.put(7, new Provider("7", "192.168.1.7", ProviderStatus.ACTIVE));
        providerServersMap.put(8, new Provider("8", "192.168.1.8", ProviderStatus.ACTIVE));
        providerServersMap.put(9, new Provider("9", "192.168.1.9", ProviderStatus.ACTIVE));
        providerServersMap.put(10, new Provider("10", "192.168.1.10", ProviderStatus.ACTIVE));
        return providerServersMap;
    }
}
