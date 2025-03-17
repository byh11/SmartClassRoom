package com.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@Order(-1)
@Slf4j
public class AuthorizeFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        MultiValueMap<String, String> queryParams = request.getQueryParams();
//        String[] paths = request.getURI().getPath().split("/");
//        if (paths.length>2 && paths[1].equals("student") && (paths[2].equals("login") || paths[2].equals("register"))) {
//            if (queryParams.get("studentid").toString().length() < 8 || queryParams.get("password").toString().length() < 8) {
//                log.info("参数错误");
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        } else if (paths.length>2 && paths[1].equals("teacher")  && (paths[2].equals("login") || paths[2].equals("register"))) {
//            if (queryParams.get("teacherid").toString().length() < 8 || queryParams.get("password").toString().length() < 8) {
//                log.info("参数错误");
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        }
        return chain.filter(exchange);
    }
}
