package cl.duoc.pedidos360.bff.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.pedidos360.bff.model.PedidoDto;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final RestClient pedidosClient;

    public PedidoController(
            @Value("${pedidos360.pedidos-service.base-url}")
            String pedidosServiceBaseUrl
    ) {
        this.pedidosClient =
            RestClient.builder()
                .baseUrl(pedidosServiceBaseUrl)
                .build();
    }

    @GetMapping("/public/status")
    public Map<String, Object> statusPublico() {
        return Map.of(
            "mensaje",
            "Pedidos360 BFF operativo",
            "protegido",
            false
        );
    }

    @GetMapping("/pedidos")
    public Map<String, Object> obtenerPedidos(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<PedidoDto> pedidos;

        try {
            PedidoDto[] respuesta =
                pedidosClient
                    .get()
                    .uri("/internal/pedidos")
                    .retrieve()
                    .body(PedidoDto[].class);

            pedidos =
                respuesta != null
                    ? Arrays.asList(respuesta)
                    : List.of();

        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "No fue posible consultar el microservicio de pedidos"
            );
        }

        String usuario =
            jwt.getClaimAsString(
                "preferred_username"
            );

        if (usuario == null) {
            usuario = jwt.getSubject();
        }

        return Map.of(
            "mensaje",
            "JWT validado correctamente",
            "usuario",
            usuario,
            "audience",
            jwt.getAudience(),
            "issuer",
            jwt.getIssuer().toString(),
            "scope",
            jwt.getClaimAsString("scp"),
            "pedidos",
            pedidos
        );
    }
}
