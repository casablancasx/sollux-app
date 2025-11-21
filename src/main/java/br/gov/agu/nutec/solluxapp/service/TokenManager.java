package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class TokenManager {

    private String token;
    private Instant cronometro = Instant.now();
    private SapiensAdapter adapter;

    public TokenManager(String token, SapiensAdapter adapter) {
        this.token = token;
        this.adapter = adapter;
    }

    private String renovarTokenSeNecessario(){
        if (ChronoUnit.MINUTES.between(cronometro, Instant.now())  > 25) {
            System.out.println("Token Renovada com sucesso!");
            this.token = adapter.renovarToken(token);
            this.cronometro = Instant.now();
        }
        return this.token;
    }

    public String getToken() {
        return renovarTokenSeNecessario();
    }
}