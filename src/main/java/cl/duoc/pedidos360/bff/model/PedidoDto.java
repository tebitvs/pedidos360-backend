package cl.duoc.pedidos360.bff.model;

public record PedidoDto(
        Long id,
        String producto,
        String estado
) {
}
