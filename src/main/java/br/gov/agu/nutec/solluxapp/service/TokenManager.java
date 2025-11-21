package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenManager {

    private String token;
    private Instant cronometro = Instant.now();
    private final SapiensAdapter adapter;

    public TokenManager(SapiensAdapter adapter) {
        this.adapter = adapter;
    }

    public synchronized void setTokenInicial(String token) {
        this.token = token;
    }

    private synchronized String renovarTokenSeNecessario(){
        if (this.token == null || ChronoUnit.MINUTES.between(cronometro, Instant.now()) >= 25) {
            System.out.println("Token renovado com sucesso!");
            this.token = adapter.renovarToken(token);
            this.cronometro = Instant.now();
        }
        return this.token;
    }

    public String getToken() {
        return renovarTokenSeNecessario();
    }
}
