package com.team.huespedes.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
@Tag(name = "test")
public class HuespedController {

    @GetMapping
    ResponseEntity<String> saludo()
    {
        return ResponseEntity.ok("Hola desde "+this.getClass().toString());
    }
}
