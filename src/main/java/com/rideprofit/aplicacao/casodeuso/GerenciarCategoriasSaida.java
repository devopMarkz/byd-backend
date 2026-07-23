package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.CategoriaSaidaRequest;
import com.rideprofit.aplicacao.dto.CategoriaSaidaResponse;
import com.rideprofit.dominio.entidade.CategoriaSaida;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.CategoriaSaidaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class GerenciarCategoriasSaida {
    private final CategoriaSaidaRepositorio categorias; private final DespesaRepositorio despesas;
    public GerenciarCategoriasSaida(CategoriaSaidaRepositorio categorias, DespesaRepositorio despesas) { this.categorias = categorias; this.despesas = despesas; }
    public List<CategoriaSaidaResponse> listar() { Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado(); return categorias.findByTenantIdAndAtivoTrueOrderByNomeAsc(u.getTenantId()).stream().map(this::resposta).toList(); }
    public CategoriaSaidaResponse criar(CategoriaSaidaRequest r) { Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado(); CategoriaSaida c = new CategoriaSaida(); c.setTenantId(u.getTenantId()); preencher(c,r); return resposta(categorias.save(c)); }
    public CategoriaSaidaResponse editar(UUID id, CategoriaSaidaRequest r) { CategoriaSaida c=obter(id); preencher(c,r); return resposta(categorias.save(c)); }
    public void excluir(UUID id) { Usuario u=UsuarioAutenticadoService.obterUsuarioAutenticado(); CategoriaSaida c=obter(id); if(despesas.existsByTenantIdAndCategoriaSaidaIdAndAtivoTrue(u.getTenantId(),id)) throw new IllegalStateException("A categoria possui despesas vinculadas e nao pode ser excluida."); c.setAtivo(false); categorias.save(c); }
    private CategoriaSaida obter(UUID id) { Usuario u=UsuarioAutenticadoService.obterUsuarioAutenticado(); return categorias.findByIdAndTenantIdAndAtivoTrue(id,u.getTenantId()).orElseThrow(()->new EntityNotFoundException("Categoria de saida nao encontrada.")); }
    private void preencher(CategoriaSaida c,CategoriaSaidaRequest r){c.setNome(r.nome().trim());c.setDescricao(r.descricao());c.setTipo(r.tipo());}
    private CategoriaSaidaResponse resposta(CategoriaSaida c){return new CategoriaSaidaResponse(c.getId(),c.getNome(),c.getDescricao(),c.getTipo());}
}
