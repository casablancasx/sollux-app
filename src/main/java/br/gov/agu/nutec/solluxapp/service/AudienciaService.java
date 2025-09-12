package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.producer.AudienciaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AudienciaService {

    private final AudienciaProducer audienciaProducer;


    public void enviarAudiencia(List<AudienciaDTO> audiencias) {

        for (AudienciaDTO audienciaDTO : audiencias) {

            String status = "EM_PROCESSAMENTO";

            if (audienciaDTO.equals(audiencias.getLast())){
                status = "FINALIZADO";
            }

            Map<String, Object> mensagem = Map.of(
                    "status", status,
                    "audiencia", audienciaDTO
            );


            audienciaProducer.enviarAudiencia(mensagem);
        }

    }
}
