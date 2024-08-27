package lk.ijse.eebackend.bo;

import lk.ijse.eebackend.dto.ItemDTO;
import lk.ijse.eebackend.dto.OrderDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OrderBO {
    boolean saveOrder(OrderDTO orderDTO, Connection connection) throws SQLException;
    List<OrderDTO> getAllOrders(Connection connection) throws SQLException;
}
