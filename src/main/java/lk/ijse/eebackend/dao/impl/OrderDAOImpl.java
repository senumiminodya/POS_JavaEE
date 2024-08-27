package lk.ijse.eebackend.dao.impl;

import lk.ijse.eebackend.dao.OrderDAO;
import lk.ijse.eebackend.dto.CustomerDTO;
import lk.ijse.eebackend.dto.OrderDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAOImpl implements OrderDAO {
    private static final Logger LOGGER = Logger.getLogger(OrderDAOImpl.class.getName());
    @Override
    public boolean saveOrder(OrderDTO orderDTO, Connection connection) throws SQLException {
        String sql = "INSERT INTO orders (orderId, customerId, total, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, String.valueOf(orderDTO.getOrderId()));
            pst.setString(2, orderDTO.getCustomerId());
            pst.setString(3, String.valueOf(orderDTO.getTotal()));
            pst.setString(4, String.valueOf(orderDTO.getDate()));
            boolean result = pst.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Order saved successfully: " + orderDTO);
            } else {
                LOGGER.warning("Failed to save Order: " + orderDTO);
            }
            return result;
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving order: " + orderDTO, e);
            throw e;
        }
    }

    @Override
    public List<OrderDTO> getAllOrders(Connection connection) throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setOrderId(rs.getInt("orderId"));
                orderDTO.setCustomerId(rs.getString("customerId"));
                orderDTO.setTotal(Double.parseDouble(rs.getString("total")));
                orderDTO.setDate(rs.getDate("date"));
                orders.add(orderDTO);
            }
            LOGGER.info("Retrieved all orders. Total count: " + orders.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all orders", e);
            throw e;
        }
        return orders;
    }
}
