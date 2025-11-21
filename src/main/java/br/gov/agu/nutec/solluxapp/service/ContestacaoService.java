package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;
import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.enums.TipoContestacao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.gov.agu.nutec.solluxapp.enums.TipoContestacao.*;

@Service
@RequiredArgsConstructor
public class ContestacaoService {

    private static final Pattern TIPOS_PATTERN = Pattern.compile("\\bTIPO\\s*(1|2|3|4|5)\\b", Pattern.CASE_INSENSITIVE);

    private final SapiensAdapter adapter;
    private final TokenManager tokenManager;


    public List<AudienciaDTO> buscarTipoConstestacao(List<AudienciaDTO> audiencias, String token) {
        tokenManager.setTokenInicial(token);
        for (AudienciaDTO audiencia : audiencias) {
            try {
                String tokenAtual =  tokenManager.getToken();
                String html = adapter.obterHtmlConstestacaoPorCnj(audiencia.getCnj(), tokenAtual);
                TipoContestacao tipo = extrairTipoContestacao(html);
                System.out.println("Tipo encontrado: " + tipo + " para CNJ: " + audiencia.getCnj());
                audiencia.setTipoContestacao(tipo);
            } catch (WebClientResponseException e) {
                System.err.println("Erro ao processar CNJ " + audiencia.getCnj() + ": " + e.getClass().getName());
                audiencia.setTipoContestacao(ERRO_SAPIENS);
            }catch (NullPointerException e){
                audiencia.setTipoContestacao(SEM_CONTESTACAO);
            }
        }

        return audiencias;
    }


    private TipoContestacao extrairTipoContestacao(String html) {
        Matcher matcher = TIPOS_PATTERN.matcher(html);

        if (matcher.find()) {
            String tipo = matcher.group().toUpperCase();
            if (tipo.contains(" ")){
                tipo = tipo.replaceAll(" ", "");
            }
            return TipoContestacao.valueOf(tipo);
        }

        return SEM_TIPO;
    }
}