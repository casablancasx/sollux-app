package br.gov.agu.nutec.solluxapp.dto;


public record PlanilhaResponseDTO(
        String message,
        String file,
        String user,
        String hash,
        int totalAudiencias) {
}
