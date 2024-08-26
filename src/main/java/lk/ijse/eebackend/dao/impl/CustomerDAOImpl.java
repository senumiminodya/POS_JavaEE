package lk.ijse.eebackend.dao.impl;

import lk.ijse.eebackend.dao.CustomerDAO;
import lk.ijse.eebackend.dto.CustomerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerDAOImpl implements CustomerDAO {
    private static final Logger LOGGER = Logger.getLogger(CustomerDAOImpl.class.getName());
    @Override
    public boolean saveCustomer(CustomerDTO customer, Connection connection) throws SQLException {
        String sql = "INSERT INTO customer (id, nic, name, phoneNo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, customer.getId());
            pst.setString(2, customer.getNic());
            pst.setString(3, customer.getName());
            pst.setString(4, customer.getPhoneNo());
            boolean result = pst.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Customer saved successfully: " + customer);
            } else {
                LOGGER.warning("Failed to save customer: " + customer);
            }
            return result;
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving customer: " + customer, e);
            throw e;
        }
    }

    @Override
    public boolean updateCustomer(String id, CustomerDTO customerDTO, Connection connection) throws SQLException {
        String sql = "UPDATE customer SET nic = ?, name = ?, phoneNo = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerDTO.getNic());
            ps.setString(2, customerDTO.getName());
            ps.setString(3, customerDTO.getPhoneNo());
            ps.setString(4, id);
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Customer updated successfully: " + customerDTO);
            } else {
                LOGGER.warning("Failed to update customer with ID: " + id);
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer with ID: " + id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteCustomer(String customerId, Connection connection) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerId);
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Customer deleted successfully with ID: " + customerId);
            } else {
                LOGGER.warning("Failed to delete customer with ID: " + customerId);
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting customer with ID: " + customerId, e);
            throw e;
        }
    }

    /*@Override
    public CustomerDTO getCustomerById(String customerId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM customer WHERE id = ?";
        CustomerDTO customer = null;

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, customerId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                customer = new CustomerDTO();
                customer.setId(rs.getString("id"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setContact(rs.getString("contact"));
            }
        }
        return customer;
    }*/

   /* private static final String GET_CUSTOMER_BY_ID = "SELECT * FROM customers WHERE id = ?";
    private static final String GET_ALL_CUSTOMERS = "SELECT * FROM customers";

    @Override
    public CustomerDTO getCustomerById(String id, Connection connection) throws SQLException {
        CustomerDTO customer = null;
        try (PreparedStatement ps = connection.prepareStatement(GET_CUSTOMER_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = new CustomerDTO();
                    customer.setId(rs.getString("id"));
                    customer.setName(rs.getString("name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setContact(rs.getString("contact"));
                }
            }
        }
        return customer;
    }

    @Override
    public List<CustomerDTO> getAllCustomers(Connection connection) throws SQLException {
        List<CustomerDTO> customers = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_ALL_CUSTOMERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CustomerDTO customer = new CustomerDTO();
                customer.setId(rs.getString("id"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setContact(rs.getString("contact"));
                customers.add(customer);
            }
        }
        return customers;
    }*/
    @Override
   public List<CustomerDTO> getAllCustomers(Connection connection) throws SQLException {
        List<CustomerDTO> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CustomerDTO customer = new CustomerDTO();
                customer.setId(rs.getString("id"));
                customer.setNic(rs.getString("nic"));
                customer.setName(rs.getString("name"));
                customer.setPhoneNo(rs.getString("phoneNo"));
                customers.add(customer);
            }
            LOGGER.info("Retrieved all customers. Total count: " + customers.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all customers", e);
            throw e;
        }
        return customers;
   }

}
