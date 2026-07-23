package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.OrigemRequest;
import com.rideprofit.aplicacao.dto.OrigemResponse;
import com.rideprofit.dominio.entidade.Origem;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.OrigemRepositorio;
import com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class GerenciarOrigens {
    private final OrigemRepositorio origens;
    private final ReceitaRepositorio receitas;
    public GerenciarOrigens(OrigemRepositorio origens, ReceitaRepositorio receitas) { this.origens = origens; this.receitas = receitas; }
    public List<OrigemResponse> listar() { Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado(); return origens.findByTenantIdAndAtivoTrueOrderByNomeAsc(u.getTenantId()).stream().map(this::resposta).toList(); }
    public OrigemResponse criar(OrigemRequest r) { validarImagem(r.imagemBase64()); Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado(); Origem o = new Origem(); o.setTenantId(u.getTenantId()); preencher(o, r); return resposta(origens.save(o)); }
    public OrigemResponse editar(UUID id, OrigemRequest r) { validarImagem(r.imagemBase64()); Origem o = obter(id); preencher(o, r); return resposta(origens.save(o)); }
    public void excluir(UUID id) { Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado(); Origem o = obter(id); if (receitas.existsByTenantIdAndOrigemIdAndAtivoTrue(u.getTenantId(), id)) throw new IllegalStateException("A origem possui receitas vinculadas e nao pode ser excluida."); o.setAtivo(false); origens.save(o); }
    private Origem obter(UUID id) { Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado(); return origens.findByIdAndTenantIdAndAtivoTrue(id, u.getTenantId()).orElseThrow(() -> new EntityNotFoundException("Origem nao encontrada.")); }
    private void preencher(Origem o, OrigemRequest r) { o.setNome(r.nome().trim()); o.setDescricao(r.descricao()); o.setImagemBase64(r.imagemBase64()); }
    private void validarImagem(String imagem) { if (imagem == null || imagem.isBlank()) return; if (!(imagem.startsWith("data:image/jpeg;base64,") || imagem.startsWith("data:image/png;base64,"))) throw new IllegalArgumentException("A imagem deve estar em JPG ou PNG no formato Base64."); if (imagem.length() > 1_400_000) throw new IllegalArgumentException("A imagem deve ter no maximo 1MB."); }
    private OrigemResponse resposta(Origem o) { return new OrigemResponse(o.getId(), o.getNome(), o.getDescricao(), o.getImagemBase64()); }
}
