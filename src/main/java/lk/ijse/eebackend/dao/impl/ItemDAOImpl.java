package lk.ijse.eebackend.dao.impl;

import lk.ijse.eebackend.dao.ItemDAO;
import lk.ijse.eebackend.dto.ItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDAOImpl implements ItemDAO {

    private static final Logger LOGGER = Logger.getLogger(ItemDAOImpl.class.getName());
    @Override
    public boolean saveItem(ItemDTO item, Connection connection) throws SQLException {
        String sql = "INSERT INTO item (code, name, price, qty) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, item.getCode());
            pst.setString(2, item.getName());
            pst.setString(3, item.getPrice());
            pst.setString(4, item.getQty());
            boolean result = pst.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Item saved successfully: " + item);
            } else {
                LOGGER.warning("Failed to save item: " + item);
            }
            return result;
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving item: " + item, e);
            throw e;
        }
    }

    @Override
    public boolean updateItem(String code, ItemDTO itemDTO, Connection connection) throws SQLException {
        String sql = "UPDATE item SET name = ?, price = ?, qty = ? WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemDTO.getName());
            ps.setString(2, itemDTO.getPrice());
            ps.setString(3, itemDTO.getQty());
            ps.setString(4, code);
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Item updated successfully: " + itemDTO);
            } else {
                LOGGER.warning("Failed to update item with code: " + code);
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item with code: " + code, e);
            throw e;
        }
    }

    @Override
    public boolean deleteItem(String code, Connection connection) throws SQLException {
        String sql = "DELETE FROM item WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                LOGGER.info("Item deleted successfully with code: " + code);
            } else {
                LOGGER.warning("Failed to delete item with code: " + code);
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting item with code: " + code, e);
            throw e;
        }
    }

    @Override
    public List<ItemDTO> getAllItems(Connection connection) throws SQLException {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM item";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setCode(rs.getString("code"));
                itemDTO.setName(rs.getString("name"));
                itemDTO.setPrice(rs.getString("price"));
                itemDTO.setQty(rs.getString("qty"));
                items.add(itemDTO);
            }
            LOGGER.info("Retrieved all items. Total count: " + items.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all items", e);
            throw e;
        }
        return items;
    }

}
