package br.gov.agu.nutec.solluxapp.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class TokenUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extrai o sapiens_ID (campo "id") do JWT usando Jackson para parsing eficiente.
     *
     * @param token JWT completo (Bearer removido se necessário)
     * @return sapiens_ID ou lanca excecao se o token for invalido
     */
    public static long getSapiensIdFromToken(String token) {


       try {
           String[] parts = token.split("\\.");
           String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
           JsonNode jsonNode = objectMapper.readTree(payload);
           return jsonNode.get("id").asLong();
       }catch (Exception e) {
              throw new RuntimeException("Token invalido", e);
       }

    }
}
