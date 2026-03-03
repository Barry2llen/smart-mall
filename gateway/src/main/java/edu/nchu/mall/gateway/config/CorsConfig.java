package edu.nchu.mall.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${fronted.url}")
    private List<String> frontedUrl;

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if (frontedUrl == null || frontedUrl.isEmpty()) {
            corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        } else {
            corsConfiguration.setAllowedOriginPatterns(frontedUrl.stream().map(url -> List.of(
                    "http://" + url,
                    "https://" + url,
                    "http://" + url + ":*",
                    "https://" + url + ":*"
            )).flatMap(List::stream).toList());
        }
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(source);
    }
}
