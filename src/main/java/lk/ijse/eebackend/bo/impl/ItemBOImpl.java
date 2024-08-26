package lk.ijse.eebackend.bo.impl;

import lk.ijse.eebackend.bo.ItemBO;
import lk.ijse.eebackend.dao.ItemDAO;
import lk.ijse.eebackend.dao.impl.ItemDAOImpl;
import lk.ijse.eebackend.dto.ItemDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ItemBOImpl implements ItemBO {
    private final ItemDAO itemDAO = new ItemDAOImpl();

    @Override
    public boolean saveItem(ItemDTO itemDTO, Connection connection) throws SQLException {
        try {
            return itemDAO.saveItem(itemDTO, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save item", e);
        }
    }

    @Override
    public boolean updateItem(String code, ItemDTO itemDTO, Connection connection) throws SQLException {
        try {
            return itemDAO.updateItem(code, itemDTO, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update item", e);
        }
    }

    @Override
    public boolean deleteItem(String code, Connection connection) throws SQLException {
        return itemDAO.deleteItem(code, connection);
    }

    @Override
    public List<ItemDTO> getAllItems(Connection connection) throws SQLException {
        return itemDAO.getAllItems(connection);
    }
}
