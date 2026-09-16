package cl.duoc.pedidos360.bff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.pedidos360.bff.model.Pedido;

@Repository
public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {
}
