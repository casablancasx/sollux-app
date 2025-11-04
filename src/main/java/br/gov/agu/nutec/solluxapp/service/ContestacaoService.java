package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;
import br.gov.agu.nutec.solluxapp.enums.TipoContestacao;
import static br.gov.agu.nutec.solluxapp.enums.TipoContestacao.BRANCO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ContestacaoService {

    private final SapiensAdapter adapter;


    public TipoContestacao buscarTipoConstestacao(String cnj, String token){
        int processoId = adapter.getProcessoIdPorCnj(cnj, token);
        int documentoContestacaoId = adapter.getIdDocumentoContestacao(processoId, token);
        String htmlBase64Contestacao = adapter.getHtmlBase64Documento(documentoContestacaoId, token);
        String html = new String(Base64.getDecoder().decode(htmlBase64Contestacao), StandardCharsets.UTF_8);

        Pattern pattern = Pattern.compile("TIPO\\s+\\d+(?=\\s*-\\s*PROVA\\s+DOCUMENTAL)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            int tipoEncontrado = Integer.parseInt(matcher.group().split("-")[1]);
            return TipoContestacao.getTipoContestacao(tipoEncontrado);
        }

        return BRANCO;
    }
}
