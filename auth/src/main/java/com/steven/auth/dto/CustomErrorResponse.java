package com.steven.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
