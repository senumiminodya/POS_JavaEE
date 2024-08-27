package lk.ijse.eebackend.dao;

import lk.ijse.eebackend.dto.OrderDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {
    boolean saveOrder(OrderDTO orderDTO, Connection connection) throws SQLException;
    List<OrderDTO> getAllOrders(Connection connection) throws SQLException;
}
