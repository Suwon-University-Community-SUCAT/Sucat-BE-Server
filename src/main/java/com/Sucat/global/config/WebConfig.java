package com.Sucat.global.config;

import com.Sucat.global.annotation.resolver.CurrentUserArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Value("${security.origin1}")
    private String allowedOrigin1;
    @Value("${security.origin2}")
    private String allowedOrigin2;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    public WebConfig(CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .exposedHeaders("Set-Cookie", "Authorization", "Authorization-refresh") // 한번에 설정
                .allowedOrigins(allowedOrigin1, allowedOrigin2)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 메서드 설정
                .allowedHeaders("Authorization", "Authorization-refresh", "Content-Type")
                .allowCredentials(true);
    }
}
