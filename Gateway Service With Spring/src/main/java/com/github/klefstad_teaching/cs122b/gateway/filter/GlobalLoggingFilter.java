package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.github.klefstad_teaching.cs122b.gateway.config.GatewayRequestObject;
import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.repo.GatewayRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered
{
    private static final Logger    LOG          = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private static final Scheduler DB_SCHEDULER = Schedulers.boundedElastic();


    private final GatewayRepo          gatewayRepo;
    private final GatewayServiceConfig config;
    LinkedBlockingQueue<GatewayRequestObject> requests = new LinkedBlockingQueue<>();

    @Autowired
    public GlobalLoggingFilter(GatewayRepo gatewayRepo, GatewayServiceConfig config)
    {
        this.gatewayRepo = gatewayRepo;
        this.config = config;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
//        exchange.getResponse().setStatusCode()
        GatewayRequestObject object = new GatewayRequestObject()
                .setIp_address(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress())
                .setCall_time(Instant.now())
                .setPath(exchange.getRequest().getPath().toString());

        requests.add(object);

        if (requests.size() >= config.getMaxLogs()) {
            //Add it to the gateWay repo
            drainRequests();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        return -1;
    }

    public void drainRequests()
    {
        List<GatewayRequestObject> drainRequest = new ArrayList<>();
        requests.drainTo(drainRequest);

        //Call with repo statement
        gatewayRepo.createInsertMono(drainRequest)
                .subscribeOn(DB_SCHEDULER)
                .subscribe();
    }
}
