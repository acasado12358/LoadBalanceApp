package com.iptq.loadbalancer.model;

import com.iptq.loadbalancer.service.ProviderStatus;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Provider {

    String id;
    String ipName;
    ProviderStatus status;
    int requestCapacity;

    public int getRequestCapacity() {
        return requestCapacity;
    }

    public Provider() {
        this.id = UUID.randomUUID().toString();
        this.requestCapacity = 10;
    }

    public Provider(String id, String ipName, ProviderStatus status) {
        this.id = id;
        this.ipName = ipName;
        this.status = status;
        this.requestCapacity = 10;
    }

    public Provider(String id, int requestCapacity) {
        this.id = id;
        this.requestCapacity = requestCapacity;
    }


    public String get() throws InterruptedException {
        //add some delay time for the request
        Thread.sleep(100);
        return this.ipName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpName() {
        return ipName;
    }

    public void setIpName(String ipName) {
        this.ipName = ipName;
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public void setStatus(ProviderStatus status) {
        this.status = status;
    }

    public boolean check() {
        Random rnd = new Random();
        return rnd.nextBoolean();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equals(id, provider.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ipName, status);
    }
}
