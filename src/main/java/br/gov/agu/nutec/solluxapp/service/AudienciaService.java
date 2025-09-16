package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;

import br.gov.agu.nutec.solluxapp.dto.AudienciaMessage;
import br.gov.agu.nutec.solluxapp.enums.Status;
import br.gov.agu.nutec.solluxapp.producer.AudienciaProducer;
import lombok.RequiredArgsConstructor;
import static br.gov.agu.nutec.solluxapp.enums.Status.EM_ANDAMENTO;
import static br.gov.agu.nutec.solluxapp.enums.Status.FINALIZADO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AudienciaService {

    private final AudienciaProducer audienciaProducer;


    public void enviarAudiencias(List<AudienciaDTO> audiencias) {

        for (AudienciaDTO audiencia : audiencias) {
            Status status = audiencia.equals(audiencias.getLast()) ? FINALIZADO : EM_ANDAMENTO;
            audienciaProducer.enviarAudiencia(new AudienciaMessage(status, audiencia));
        }

    }
}
