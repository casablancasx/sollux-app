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

    private static final Pattern TIPOS_PATTERN = Pattern.compile("\\b(TIPO1|TIPO2|TIPO3|TIPO4|TIPO5|TIPO 1|TIPO 2|TIPO 3|TIPO 4|TIPO 5)\\b");

    private final SapiensAdapter adapter;
    private final TokenService tokenService;

    public List<AudienciaDTO> buscarTipoConstestacao(List<AudienciaDTO> audiencias, String token) {
        String tokenAtual = token;

        for (AudienciaDTO audiencia : audiencias) {
            try {

                tokenAtual = tokenService.renovarTokenSeNecessario(tokenAtual);
                Long processoId = adapter.getProcessoIdPorCnj(audiencia.getCnj(), tokenAtual);
                Long documentoContestacaoId = adapter.getIdDocumentoContestacao(processoId, tokenAtual);

                String htmlBase64Contestacao = adapter.getHtmlBase64Documento(documentoContestacaoId, tokenAtual);
                String html = new String(Base64.getDecoder().decode(htmlBase64Contestacao), StandardCharsets.UTF_8);
                TipoContestacao tipo = extrairTipoContestacao(html);

                System.out.println("Tipo encontrado: " + tipo + " para CNJ: " + audiencia.getCnj());

                audiencia.setTipoContestacao(tipo);
            } catch (Exception e) {
                System.err.println("Erro ao processar CNJ " + audiencia.getCnj() + ": " + e.getClass().getName());
                audiencia.setTipoContestacao(SEM_TIPO);
            }
        }

        return audiencias;
    }


    private TipoContestacao extrairTipoContestacao(String html) {
        Matcher matcher = TIPOS_PATTERN.matcher(html);

        if (matcher.find()) {
            String tipo = matcher.group().toUpperCase();
            if (tipo.contains(" ")){
                tipo = tipo.replace(" ", "");
            }
            return TipoContestacao.valueOf(tipo);
        }

        return SEM_TIPO;
    }
}