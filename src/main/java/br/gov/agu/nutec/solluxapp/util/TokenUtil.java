package br.gov.agu.nutec.solluxapp.util;

import br.gov.agu.nutec.solluxapp.exceptions.UserUnauthorizedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class TokenUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**

     *
     * @param token Token do Super Sapiens
     * @return sapiens_ID ou lanca excecao se o token for invalido
     */
    public static long getSapiensIdFromToken(String token) {


       try {
           token = token.replace("Bearer ", "");

           String[] parts = token.split("\\.");
           String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
           JsonNode jsonNode = objectMapper.readTree(payload);
           return jsonNode.get("id").asLong();
       }catch (Exception e) {
              throw new UserUnauthorizedException("Token invalido");
       }

    }

    private  TokenUtil() {}
}
