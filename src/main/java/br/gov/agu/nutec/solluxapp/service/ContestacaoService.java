package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;
import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.enums.TipoContestacao;

import static br.gov.agu.nutec.solluxapp.enums.TipoContestacao.SEM_TIPO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ContestacaoService {

    private static final Pattern TIPOS_PATTERN = Pattern.compile("\\b(TIPO1|TIPO2|TIPO3|TIPO4|TIPO5)\\b");

    private final SapiensAdapter adapter;
    private final TokenService tokenService;


    public List<AudienciaDTO> buscarTipoConstestacao(List<AudienciaDTO> audiencias, String token) {
        return audiencias.parallelStream()
                .peek(audiencia -> {
                    long processoId = adapter.getProcessoIdPorCnj(audiencia.getCnj(), token);
                    long documentoContestacaoId = adapter.getIdDocumentoContestacao(processoId, token);
                    String htmlBase64Contestacao = adapter.getHtmlBase64Documento(documentoContestacaoId, token);

                    String html = decodeHtmlFromBase64(htmlBase64Contestacao);
                    TipoContestacao tipo = extrairTipoContestacao(html);

                    audiencia.setTipoContestacao(tipo);
                })
                .toList();
    }

    private String decodeHtmlFromBase64(String htmlBase64) {
        byte[] decodedBytes = Base64.getDecoder().decode(htmlBase64);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private TipoContestacao extrairTipoContestacao(String html) {
        Matcher matcher = TIPOS_PATTERN.matcher(html);

        if (matcher.find()) {
            return TipoContestacao.valueOf(matcher.group().toUpperCase());
        }

        return SEM_TIPO;
    }
}
