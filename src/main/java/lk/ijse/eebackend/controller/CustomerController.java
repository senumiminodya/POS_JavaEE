package lk.ijse.eebackend.controller;

import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.eebackend.dto.CustomerDTO;
import lk.ijse.eebackend.bo.impl.CustomerBOImpl;
import org.json.JSONObject;

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

@WebServlet(urlPatterns = "/customer/*", loadOnStartup = 2)
public class CustomerController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());
    private Connection connection;
    private CustomerBOImpl customerBOImpl;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/webPoss");
            this.connection = pool.getConnection();
            this.customerBOImpl = new CustomerBOImpl(); // Initialize the CustomerService
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
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);

            if (customerBOImpl.saveCustomer(customerDTO, connection)) {
                writer.write("Customer saved successfully");
                resp.setStatus(HttpServletResponse.SC_CREATED);
                LOGGER.info("Customer saved successfully: " + customerDTO);
            } else {
                writer.write("Save customer failed");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                LOGGER.warning("Failed to save customer: " + customerDTO);
            }
        } catch (JsonException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.log(Level.SEVERE, "JSON processing error", e);
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
            CustomerDTO updatedCustomer = jsonb.fromJson(req.getReader(), CustomerDTO.class);

            String customerID = updatedCustomer.getId(); // Get ID directly from the parsed object

            if (customerID == null || customerID.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing customer ID");
                LOGGER.warning("Missing customer ID in update request");
                return;
            }

            boolean isUpdated = customerBOImpl.updateCustomer(customerID, updatedCustomer, connection);

            if (isUpdated) {
                writer.write("Customer updated successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
                LOGGER.info("Customer updated successfully: " + updatedCustomer);
            } else {
                writer.write("Update failed");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOGGER.warning("Failed to update customer with ID: " + customerID);
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
        String customerId = req.getPathInfo().substring(1);

        if (customerId == null || customerId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required.");
            LOGGER.warning("Missing customer ID in delete request");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            if (customerBOImpl.deleteCustomer(customerId, connection)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                LOGGER.info("Customer deleted successfully with ID: " + customerId);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Delete Failed");
                LOGGER.warning("Failed to delete customer with ID: " + customerId);
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while deleting the customer.");
            LOGGER.log(Level.SEVERE, "Database error during delete operation", e);
        }

    }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String customerId = req.getParameter("id");

       try (var writer = resp.getWriter()) {
           // Retrieve all customers
           List<CustomerDTO> customers = customerBOImpl.getAllCustomers(connection);

           // Check if any customers were retrieved
           if (customers != null && !customers.isEmpty()) {
               resp.setContentType("application/json");
               Jsonb jsonb = JsonbBuilder.create();
               // Convert the list of customers to JSON
               jsonb.toJson(customers, writer);
               LOGGER.info("Customer data retrieved: " + customers);
           } else {
               // Send a 404 if no customers found
               resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No customers found");
               LOGGER.warning("Customer not found with ID: " + customerId);
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
