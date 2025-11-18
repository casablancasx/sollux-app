package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final SapiensAdapter adapter;
    private final AtomicInteger contador = new AtomicInteger(500);

    public String renovarTokenSeNecessario(String token) {
        if (contador.decrementAndGet() <= 0) {
            System.out.println("Token Renovada com sucesso");
            contador.set(500);
            return adapter.renovarToken(token);
        }
        return token;
    }
}