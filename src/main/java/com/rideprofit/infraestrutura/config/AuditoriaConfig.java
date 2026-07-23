package com.rideprofit.infraestrutura.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "usuarioAuditorAware")
public class AuditoriaConfig {
}
