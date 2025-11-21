package br.gov.agu.nutec.solluxapp.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SapiensAdapter {


    private final WebClient webClient;


    private Long getIdDocumentoContestacao(long processoId, String token){



        String whereParam = String.format("where={\"volume.processo.id\":\"eq:%s\",\"documento.tipoDocumento.id\":\"eq:85\"}",processoId);
        String populateParam = "populate=[\"documento\", \"documento.componentesDigitais\"]";
        String limitParam = "limit=1";
        String offsetParam = "offset=0";


        URI uri = UriComponentsBuilder.fromUriString("https://supersapiensbackend.agu.gov.br/v1/administrativo/juntada")
                .query(whereParam)
                .query(limitParam)
                .query(populateParam)
                .query(offsetParam)
                .build()
                .toUri();

        var respostaRequisicao = webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();

        return respostaRequisicao.getBody().get("entities").get(0).get("documento").get("componentesDigitais").get(0).get("id").asLong();
    }


    private String getHtmlBase64Documento(long documentoId, String token){

        var respostaRequisicao = webClient.get()
                .uri(String.format("/v1/administrativo/componente_digital/%s/download",documentoId))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();

        String conteudo = respostaRequisicao.getBody().get("conteudo").asText();


        int index = conteudo.indexOf("base64,");
        return conteudo.substring(index + 7);

    }

    private Long getProcessoIdPorCnj(String numeroProcesso, String token){

        String cnjDesformatado = numeroProcesso.replaceAll("[.-]", "");


        String whereParam = "where={\"andX\":[{\"vinculacoesProcessosJudiciaisProcessos.processoJudicial.numero\":\"like:"+ cnjDesformatado +"%\"}]}";
        String limitParam = "limit=1";
        String offsetParam = "offset=0";

        URI uri = UriComponentsBuilder.fromUriString("https://supersapiensbackend.agu.gov.br/v1/administrativo/processo")
                .query(whereParam)
                .query(limitParam)
                .query(offsetParam)
                .build()
                .toUri();



        var respostaRequisicao = webClient.get()
                .uri(uri)
                .header("Authorization",  "Bearer " + token)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();


        return respostaRequisicao.getBody().get("entities").get(0).get("id").asLong();
    }

    public String renovarToken(String tokenAntigo){
        return webClient.get()
                .uri("/auth/refresh_token")
                .header("Authorization",  tokenAntigo)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block().get("token").asText();
    }

    public String obterHtmlConstestacaoPorCnj(String cnj, String token){
        Long processoId = getProcessoIdPorCnj(cnj, token);
        Long idDocumentoContestacao = getIdDocumentoContestacao(processoId, token);
        String htmlConstestacaoBase64 = getHtmlBase64Documento(idDocumentoContestacao, token);
        return new String(Base64.getDecoder().decode(htmlConstestacaoBase64), StandardCharsets.UTF_8);
    }


}
