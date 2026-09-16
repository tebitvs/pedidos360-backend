package cl.duoc.pedidos360.bff.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PedidoController {

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

        List<Map<String, Object>> pedidos =
            List.of(

                Map.of(
                    "id",
                    1,

                    "producto",
                    "Notebook",

                    "estado",
                    "Preparando"
                ),

                Map.of(
                    "id",
                    2,

                    "producto",
                    "Monitor",

                    "estado",
                    "Enviado"
                ),

                Map.of(
                    "id",
                    3,

                    "producto",
                    "Teclado",

                    "estado",
                    "Entregado"
                )
            );


        return Map.of(
            "mensaje",
            "JWT validado correctamente",

            "usuario",
            jwt.getClaimAsString(
                "preferred_username"
            ) != null
                ? jwt.getClaimAsString(
                    "preferred_username"
                )
                : jwt.getSubject(),

            "audience",
            jwt.getAudience(),

            "issuer",
            jwt.getIssuer()
                .toString(),

            "scope",
            jwt.getClaimAsString(
                "scp"
            ),

            "pedidos",
            pedidos
        );
    }
}