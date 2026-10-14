package tn.iteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class MsGatewayserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayserviceApplication.class, args);
        System.out.println("***Gateway Service started successfully****");
    }

    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        // On construit les routes
        return builder.routes()
                .route(r -> r.path("/customers/**").uri("lb://MS-CUSTOMER"))
                .route(r -> r.path("/accounts/**").uri("lb://MS-ACCOUNT"))
                .build();
    }

    // Routage dynamique : une route est créée automatiquement pour chaque service enregistré
    // dans Eureka, ex. /ms-customer/customers -> lb://MS-CUSTOMER/customers
    @Bean
    DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc,
                                                        DiscoveryLocatorProperties dlp) {
        return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
    }
}
