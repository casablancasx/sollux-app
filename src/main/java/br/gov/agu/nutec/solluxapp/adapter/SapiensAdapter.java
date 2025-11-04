package br.gov.agu.nutec.solluxapp.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class SapiensAdapter {


    private final WebClient webClient;


    public int getIdDocumentoContestacao(int processoId, String token){

        var respostaRequisicao = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("where",String.format("{\"volume.processo.id\":\"eq:%s\",\"documento.tipoDocumento.id\":\"eq:85\"}",processoId))
                        .queryParam("populate","[\"documento\", \"documento.componentesDigitais\"]")
                        .queryParam("limit","1")
                        .build()


                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();

        return respostaRequisicao.getBody().get("entities").get(0).get("documento").get("componentesDigitais").get(0).get("id").asInt();
    }


    public String getHtmlBase64Documento(int documentoId, String token){

        var respostaRequisicao = webClient.get()
                .uri(String.format("/v1/administrativo/componente_digital/%s/download",documentoId))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();

        String conteudo = respostaRequisicao.getBody().get("conteudo").asText();

        return conteudo.replace("data:text/html;name=CONTESTAÇÃO.html;charset=utf-8;base64,", "");

    }

    public int getProcessoIdPorCnj(String numeroProcesso, String token){

        String cnjDesformatado = numeroProcesso.replaceAll("[.-]", "");

        var respostaRequisicao = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("where", String.format("{\"andX\":[{\"vinculacoesProcessosJudiciaisProcessos.processoJudicial.numero\":\"like:10068926120254014301%\"}]}",cnjDesformatado))
                        .queryParam("limit", "1")
                        .build())
                .header("Authorization",  token)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();


        return respostaRequisicao.getBody().get("entities").get(0).get("id").asInt();
    }


}
