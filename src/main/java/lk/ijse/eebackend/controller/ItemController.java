package lk.ijse.eebackend.controller;

import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.eebackend.bo.impl.ItemBOImpl;
import lk.ijse.eebackend.dto.CustomerDTO;
import lk.ijse.eebackend.dto.ItemDTO;
import jakarta.servlet.annotation.WebServlet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/item/*", loadOnStartup = 2)
public class ItemController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ItemController.class.getName());
    private Connection connection;
    private ItemBOImpl itemBOImpl;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/webPoss");
            this.connection = pool.getConnection();
            this.itemBOImpl = new ItemBOImpl();
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
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);

            if (itemBOImpl.saveItem(itemDTO, connection)) {
                writer.write("Item saved successfully");
                resp.setStatus(HttpServletResponse.SC_CREATED);
                LOGGER.info("Item saved successfully: " + itemDTO);
            } else {
                writer.write("Save item failed");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                LOGGER.warning("Failed to save item: " + itemDTO);
            }
        } catch (JsonException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.log(Level.SEVERE, "JSON processing error", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase().contains("application/json")) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            LOGGER.warning("Unsupported media type: " + req.getContentType());
            return;
        }

        try (var writer = resp.getWriter()) {
            Jsonb jsonb = JsonbBuilder.create();
            ItemDTO updatedItem = jsonb.fromJson(req.getReader(), ItemDTO.class);

            String code = updatedItem.getCode(); // Get ID directly from the parsed object

            if (code == null || code.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing item code");
                LOGGER.warning("Missing item code in update request");
                return;
            }

            boolean isUpdated = itemBOImpl.updateItem(code, updatedItem, connection);

            if (isUpdated) {
                writer.write("Item updated successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
                LOGGER.info("Item updated successfully: " + updatedItem);
            } else {
                writer.write("Update failed");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOGGER.warning("Failed to update item with code: " + code);
            }
        } catch (JsonException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.log(Level.SEVERE, "JSON processing error", e);
        }catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().substring(1);

        if (code == null || code.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item code is required.");
            LOGGER.warning("Missing item code in delete request");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            if (itemBOImpl.deleteItem(code, connection)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                LOGGER.info("Item deleted successfully with code: " + code);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Delete Failed");
                LOGGER.warning("Failed to delete item with code: " + code);
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while deleting the item.");
            LOGGER.log(Level.SEVERE, "Database error during delete operation", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        try (var writer = resp.getWriter()) {
            // Retrieve all items
            List<ItemDTO> items = itemBOImpl.getAllItems(connection);

            // Check if any items were retrieved
            if (items != null && !items.isEmpty()) {
                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                // Convert the list of items to JSON
                jsonb.toJson(items, writer);
                LOGGER.info("item data retrieved: " + items);
            } else {
                // Send a 404 if no items found
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No items found");
                LOGGER.warning("Item not found with code: " + code);
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
