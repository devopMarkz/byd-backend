package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.JornadaResponse;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ControlarJornadaRapida {
    private final JornadaOperacionalRepositorio jornadas; private final VeiculoRepositorio veiculos;
    public ControlarJornadaRapida(JornadaOperacionalRepositorio jornadas, VeiculoRepositorio veiculos) { this.jornadas = jornadas; this.veiculos = veiculos; }
    public JornadaResponse iniciar() {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado(); var agora = LocalDateTime.now();
        var veiculo = veiculos.findByTenantIdAndAtivoTrue(usuario.getTenantId()).stream().findFirst().orElseThrow(() -> new EntityNotFoundException("Veiculo padrao nao encontrado."));
        JornadaOperacional jornada = new JornadaOperacional(); jornada.setTenantId(usuario.getTenantId()); jornada.setUsuarioId(usuario.getId()); jornada.setVeiculoId(veiculo.getId()); jornada.setData(agora.toLocalDate()); jornada.setHorarioInicio(agora.toLocalTime()); jornada.setOdometroInicial(BigDecimal.ZERO); jornada.setPercentualBateriaInicial(com.rideprofit.dominio.valor.PercentualBateria.de(BigDecimal.ZERO)); jornada.setStatus(JornadaOperacional.StatusJornada.EM_ANDAMENTO);
        return resposta(jornadas.save(jornada));
    }
    public JornadaResponse encerrar() {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado(); var agora = LocalDateTime.now();
        JornadaOperacional jornada = jornadas.findFirstByStatusAndUsuarioIdOrderByDataDescHorarioInicioDesc(JornadaOperacional.StatusJornada.EM_ANDAMENTO, usuario.getId()).orElseThrow(() -> new IllegalStateException("Nao existe jornada em andamento."));
        jornada.setHorarioFim(agora.toLocalTime()); jornada.setOdometroFinal(jornada.getOdometroInicial()); jornada.setPercentualBateriaFinal(jornada.getPercentualBateriaInicial()); jornada.setStatus(JornadaOperacional.StatusJornada.ENCERRADA); return resposta(jornadas.save(jornada));
    }
    private JornadaResponse resposta(JornadaOperacional j) { return new JornadaResponse(j.getId(),j.getVeiculoId(),j.getData(),j.getHorarioInicio(),j.getHorarioFim(),j.getOdometroInicial(),j.getOdometroFinal(),j.getPercentualBateriaInicial().getValor(),j.getPercentualBateriaFinal() == null ? null : j.getPercentualBateriaFinal().getValor(),j.getStatus().name()); }
}
