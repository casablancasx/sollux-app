package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.dto.AudienciaMessage;
import br.gov.agu.nutec.solluxapp.entity.PlanilhaEntity;
import br.gov.agu.nutec.solluxapp.enums.Status;
import br.gov.agu.nutec.solluxapp.producer.AudienciaProducer;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AudienciaAsyncService {

    private final ContestacaoService contestacaoService;
    private final AudienciaProducer audienciaProducer;
    private final PlanilhaRepository planilhaRepository;

    @Async
    public void processarAudienciasAsync(List<AudienciaDTO> audiencias, PlanilhaEntity planilha, String token) {
        try {
            audiencias = contestacaoService.buscarTipoConstestacao(audiencias, token);

            for (AudienciaDTO audiencia : audiencias) {
                Status status = audiencia.equals(audiencias.getLast()) ? Status.FINALIZADO : Status.EM_ANDAMENTO;
                audienciaProducer.enviarAudiencia(new AudienciaMessage(status, audiencia));
            }

            planilha.setProcessamentoConcluido(true);
            planilhaRepository.save(planilha);
        } catch (Exception e) {
            planilha.setProcessamentoConcluido(false);
            planilhaRepository.save(planilha);
            throw new RuntimeException("Erro ao processar audiências em segundo plano", e);
        }
    }
}
