package com.rideprofit.infraestrutura.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class FusoHorarioConfig {

    @PostConstruct
    public void configurarFusoHorario() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}
