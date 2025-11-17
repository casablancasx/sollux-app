package br.gov.agu.nutec.solluxapp.dto;

import br.gov.agu.nutec.solluxapp.enums.Status;

public record AudienciaMessage(
        Status status,
        AudienciaDTO audiencia
) {
}
