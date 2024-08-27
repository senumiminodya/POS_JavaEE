package lk.ijse.eebackend.bo.impl;

import lk.ijse.eebackend.bo.OrderBO;
import lk.ijse.eebackend.dao.ItemDAO;
import lk.ijse.eebackend.dao.OrderDAO;
import lk.ijse.eebackend.dao.impl.ItemDAOImpl;
import lk.ijse.eebackend.dao.impl.OrderDAOImpl;
import lk.ijse.eebackend.dto.OrderDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderBOImpl implements OrderBO {
    private final OrderDAO orderDAO = new OrderDAOImpl();
    @Override
    public boolean saveOrder(OrderDTO orderDTO, Connection connection) throws SQLException {
        try {
            return orderDAO.saveOrder(orderDTO, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    @Override
    public List<OrderDTO> getAllOrders(Connection connection) throws SQLException {
        return orderDAO.getAllOrders(connection);
    }
}
