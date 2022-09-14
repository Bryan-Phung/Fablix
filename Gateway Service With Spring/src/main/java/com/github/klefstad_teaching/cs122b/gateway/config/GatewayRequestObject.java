package com.github.klefstad_teaching.cs122b.gateway.config;

import java.time.Instant;

public class GatewayRequestObject {
    private String ip_address, path;
    private Instant call_time;

    public String getIp_address() {
        return ip_address;
    }

    public GatewayRequestObject setIp_address(String ip_address) {
        this.ip_address = ip_address;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequestObject setPath(String path) {
        this.path = path;
        return this;
    }

    public Instant getCall_time() {
        return call_time;
    }

    public GatewayRequestObject setCall_time(Instant call_time) {
        this.call_time = call_time;
        return this;
    }
}
