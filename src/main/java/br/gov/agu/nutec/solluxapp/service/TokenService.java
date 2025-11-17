package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.adapter.SapiensAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final SapiensAdapter adapter;

    private static int contador = 500;
    public String renovarTokenSeNecessario(String token){

        if (contador == 0){
            contador = 500;
            return adapter.renovarToken(token);
        }
        contador--;
        return token;
    }
}
