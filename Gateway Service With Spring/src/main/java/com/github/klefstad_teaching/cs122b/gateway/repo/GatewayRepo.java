package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.config.GatewayRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Component
public class GatewayRepo
{
    private NamedParameterJdbcTemplate template;

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public Mono<int[]> createInsertMono(List<GatewayRequestObject> requests)
    {
        return Mono.fromCallable(() -> insertRequests(requests));
    }

    public int[] insertRequests(List<GatewayRequestObject> requests)
    {
        MapSqlParameterSource[] arraySources = getInsertRequest(requests);

        return this.template.batchUpdate(
                "INSERT INTO gateway.request (ip_address, call_time, path)\n" +
                        "VALUES (:ip_address, :call_time, :path);",
                arraySources
        );
    }

    public MapSqlParameterSource[] getInsertRequest(List<GatewayRequestObject> requests)
    {
        return requests.stream()
                .map(
                     s ->
                        new MapSqlParameterSource()
                                .addValue("ip_address", s.getIp_address(), Types.VARCHAR)
                                .addValue("call_time", Timestamp.from(s.getCall_time()), Types.TIMESTAMP)
                                .addValue("path", s.getPath(), Types.VARCHAR)
                )
                .toArray(MapSqlParameterSource[]::new);
    }
}
