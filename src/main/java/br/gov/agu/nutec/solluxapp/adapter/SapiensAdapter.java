package br.gov.agu.nutec.solluxapp.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
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
        String orderParam = "order={\"numeracaoSequencial\":\"ASC\"}";


        URI uri = UriComponentsBuilder.fromUriString("https://supersapiensbackend.agu.gov.br/v1/administrativo/juntada")
                .query(whereParam)
                .query(limitParam)
                .query(populateParam)
                .query(offsetParam)
                .query(orderParam)
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


    private String getArquivoBase64Documento(long documentoId, String token){

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
                .header("Authorization", "Bearer " + tokenAntigo)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block().get("token").asText();
    }

    private String extrairTextoPdf(byte[] bytesPdf) {
        try (PDDocument documento = PDDocument.load(bytesPdf)) {
            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(documento);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler PDF: ", e);
        }

    }

    public String obterArquivoConstestacaoPorCnj(String cnj, String token){
        Long processoId = getProcessoIdPorCnj(cnj, token);
        Long idDocumentoContestacao = getIdDocumentoContestacao(processoId, token);
        String arquivoConstestacaoBase64 = getArquivoBase64Documento(idDocumentoContestacao, token);
        byte[] bytesArquivo = Base64.getDecoder().decode(arquivoConstestacaoBase64);
        String inicioArquivo = new String(bytesArquivo, StandardCharsets.ISO_8859_1);

        if (inicioArquivo.startsWith("%PDF")) {
            return extrairTextoPdf(bytesArquivo);

        } else {
            return new String(bytesArquivo, StandardCharsets.UTF_8);

        }

    }


}
