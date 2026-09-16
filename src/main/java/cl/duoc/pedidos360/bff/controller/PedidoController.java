package cl.duoc.pedidos360.bff.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.pedidos360.bff.model.Pedido;
import cl.duoc.pedidos360.bff.repository.PedidoRepository;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    public PedidoController(
            PedidoRepository pedidoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/public/status")
    public Map<String, Object> statusPublico() {
        return Map.of(
            "mensaje", "Pedidos360 BFF operativo",
            "protegido", false
        );
    }

    @GetMapping("/pedidos")
    public Map<String, Object> obtenerPedidos(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<Pedido> pedidos =
            pedidoRepository.findAll();

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
