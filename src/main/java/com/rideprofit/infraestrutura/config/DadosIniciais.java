package com.rideprofit.infraestrutura.config;

import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.CategoriaSaida;
import com.rideprofit.dominio.entidade.Perfil;
import com.rideprofit.dominio.entidade.Tenant;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.entidade.Veiculo;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.CategoriaSaidaRepositorio;
import com.rideprofit.infraestrutura.persistencia.TenantRepositorio;
import com.rideprofit.infraestrutura.persistencia.UsuarioRepositorio;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.tenant.TenantContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final TenantRepositorio tenantRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VeiculoRepositorio veiculoRepositorio;
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;
    private final CategoriaSaidaRepositorio categoriaSaidaRepositorio;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(TenantRepositorio tenantRepositorio,
                         UsuarioRepositorio usuarioRepositorio,
                         VeiculoRepositorio veiculoRepositorio,
                         CategoriaDespesaRepositorio categoriaDespesaRepositorio,
                         CategoriaSaidaRepositorio categoriaSaidaRepositorio,
                         PasswordEncoder passwordEncoder) {
        this.tenantRepositorio = tenantRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.veiculoRepositorio = veiculoRepositorio;
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
        this.categoriaSaidaRepositorio = categoriaSaidaRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (tenantRepositorio.count() > 0) {
            return;
        }

        Tenant tenant = criarTenant();
        TenantContext.setTenantId(tenant.getId());
        try {
            Usuario usuario = criarUsuario(tenant);
            criarVeiculo(tenant, usuario.getId());
            criarCategoriasDespesa(tenant, usuario.getId());
            criarCategoriasSaida(tenant, usuario.getId());
        } finally {
            TenantContext.limpar();
        }
    }

    private Tenant criarTenant() {
        Tenant tenant = new Tenant();
        tenant.setNome("Padrao");
        tenant.setTarifaEnergiaKwh(new BigDecimal("0.9500"));
        return tenantRepositorio.save(tenant);
    }

    private Usuario criarUsuario(Tenant tenant) {
        Usuario usuario = new Usuario();
        usuario.setTenantId(tenant.getId());
        usuario.setNome("Motorista");
        usuario.setEmail("marcos@gmail.com");
        usuario.setSenha(passwordEncoder.encode("12345678"));
        usuario.setPerfil(Perfil.ROLE_MOTORISTA);
        return usuarioRepositorio.save(usuario);
    }

    private void criarVeiculo(Tenant tenant, UUID usuarioId) {
        Veiculo veiculo = new Veiculo();
        veiculo.setTenantId(tenant.getId());
        veiculo.setCriadoPor(usuarioId);
        veiculo.setAtualizadoPor(usuarioId);
        veiculo.setMarca("BYD");
        veiculo.setModelo("Dolphin");
        veiculo.setAno(2024);
        veiculo.setTipo("ELETRICO");
        veiculo.setCapacidadeBateriaKwh(new BigDecimal("44.9"));
        veiculo.setAutonomiaKm(new BigDecimal("340"));
        veiculo.setConsumoMedioKwhKm(new BigDecimal("0.1188"));
        veiculoRepositorio.save(veiculo);
    }

    private void criarCategoriasDespesa(Tenant tenant, UUID usuarioId) {
        List<String> nomesPadrao = List.of(
                "Energia eletrica",
                "Manutencao",
                "Pneus",
                "Seguro",
                "Impostos",
                "Lavagem",
                "Estacionamento",
                "Financiamento",
                "Outros"
        );

        for (String nome : nomesPadrao) {
            CategoriaDespesa categoria = new CategoriaDespesa();
            categoria.setTenantId(tenant.getId());
            categoria.setCriadoPor(usuarioId);
            categoria.setAtualizadoPor(usuarioId);
            categoria.setNome(nome);
            categoria.setPadrao(true);
            categoriaDespesaRepositorio.save(categoria);
        }
    }

    private void criarCategoriasSaida(Tenant tenant, UUID usuarioId) {
        List<String> nomesPadrao = List.of(
                "Energia eletrica",
                "Manutencao",
                "Pneus",
                "Seguro",
                "Impostos",
                "Lavagem",
                "Estacionamento",
                "Financiamento",
                "Recarga",
                "Outros"
        );

        for (String nome : nomesPadrao) {
            CategoriaSaida categoria = new CategoriaSaida();
            categoria.setTenantId(tenant.getId());
            categoria.setCriadoPor(usuarioId);
            categoria.setAtualizadoPor(usuarioId);
            categoria.setNome(nome);
            categoria.setTipo(CategoriaSaida.TipoCategoriaSaida.CUSTO_VARIAVEL);
            categoriaSaidaRepositorio.save(categoria);
        }
    }
}
