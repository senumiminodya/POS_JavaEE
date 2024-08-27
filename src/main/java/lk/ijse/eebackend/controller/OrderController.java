package lk.ijse.eebackend.controller;

import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.eebackend.bo.impl.ItemBOImpl;
import lk.ijse.eebackend.bo.impl.OrderBOImpl;
import lk.ijse.eebackend.dto.ItemDTO;
import lk.ijse.eebackend.dto.OrderDTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/order/*", loadOnStartup = 2)
public class OrderController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(OrderController.class.getName());
    private Connection connection;
    private OrderBOImpl orderBOImpl;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/webPoss");
            this.connection = pool.getConnection();
            this.orderBOImpl = new OrderBOImpl();
            LOGGER.info("Database connection established successfully");
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "Database driver class not found", e);
            throw new ServletException("Database driver class not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw new ServletException("Failed to establish database connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase().contains("application/json")) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            LOGGER.warning("Unsupported media type: " + req.getContentType());
            return;
        }

        try (var writer = resp.getWriter()) {
            Jsonb jsonb = JsonbBuilder.create();
            OrderDTO orderDTO = jsonb.fromJson(req.getReader(), OrderDTO.class);

            if (orderBOImpl.saveOrder(orderDTO, connection)) {
                writer.write("Order saved successfully");
                resp.setStatus(HttpServletResponse.SC_CREATED);
                LOGGER.info("Order saved successfully: " + orderDTO);
            } else {
                writer.write("Save order failed");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                LOGGER.warning("Failed to save order: " + orderDTO);
            }
        } catch (JsonException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.log(Level.SEVERE, "JSON processing error", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderId = req.getParameter("orderId");

        try (var writer = resp.getWriter()) {
            // Retrieve all orders
            List<OrderDTO> orders = orderBOImpl.getAllOrders(connection);

            // Check if any orders were retrieved
            if (orders != null && !orders.isEmpty()) {
                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                // Convert the list of orders to JSON
                jsonb.toJson(orders, writer);
                LOGGER.info("order data retrieved: " + orders);
            } else {
                // Send a 404 if no items found
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No order found");
                LOGGER.warning("Order not found with orderId: " + orderId);
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
            LOGGER.log(Level.SEVERE, "Database error during get operation", e);
        } catch (JsonException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
            LOGGER.log(Level.SEVERE, "JSON processing error", e);
        }
    }
}
