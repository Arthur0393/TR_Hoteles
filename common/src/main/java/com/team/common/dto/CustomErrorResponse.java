package com.team.common.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
