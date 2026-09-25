package com.team.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {

    @Bean
    RequestInterceptor requestInterceptor(
            @Value("${RESERVAS_HABITACIONES_INTERNAL_TOKEN:}") String internalToken) {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    template.header("Authorization", authorizationHeader);
                }
            }
            // No reenviar credenciales internas recibidas del cliente ni enviarlas a otros servicios.
            if (template.feignTarget() != null && "habitaciones".equals(template.feignTarget().name())
                    && "PUT".equals(template.method())
                    && template.path().matches("/\\d+/(ocupar|liberar)")) {
                template.removeHeader("X-Reservas-Internal-Token");
                if (!internalToken.isBlank()) {
                    template.header("X-Reservas-Internal-Token", internalToken);
                }
            }
        };
    }
}
