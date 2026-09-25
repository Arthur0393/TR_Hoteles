package com.team.common.configuration;

import feign.RequestTemplate;
import feign.Target;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeignClientConfigTest {
    private RequestTemplate template(String service, String method, String path) {
        RequestTemplate template = new RequestTemplate();
        template.feignTarget(new Target.HardCodedTarget<>(Runnable.class, service, "http://localhost"));
        template.method(method);
        template.uri(path);
        return template;
    }

    @Test
    void addsConfiguredCredentialOnlyToRoomOccupancyOperations() {
        var interceptor = new FeignClientConfig().requestInterceptor("test-only-key");
        for (String operation : new String[]{"ocupar", "liberar"}) {
            var request = template("habitaciones", "PUT", "/1/" + operation);
            request.header("X-Reservas-Internal-Token", "untrusted");
            interceptor.apply(request);
            assertEquals(java.util.List.of("test-only-key"),
                    java.util.List.copyOf(request.headers().get("X-Reservas-Internal-Token")));
        }
        for (RequestTemplate request : new RequestTemplate[]{
                template("huespedes", "PUT", "/1/ocupar"),
                template("habitaciones", "GET", "/1/ocupar"),
                template("habitaciones", "PUT", "/1/estado/1")}) {
            interceptor.apply(request);
            assertFalse(request.headers().containsKey("X-Reservas-Internal-Token"));
        }
    }

    @Test
    void missingConfigurationDoesNotForwardSuppliedCredential() {
        var request = template("habitaciones", "PUT", "/1/liberar");
        request.header("X-Reservas-Internal-Token", "untrusted");
        new FeignClientConfig().requestInterceptor("").apply(request);
        assertFalse(request.headers().containsKey("X-Reservas-Internal-Token"));
    }
}
