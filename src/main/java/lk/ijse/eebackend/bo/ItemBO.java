package lk.ijse.eebackend.bo;

import lk.ijse.eebackend.dto.ItemDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ItemBO {
    boolean saveItem(ItemDTO item, Connection connection) throws SQLException;

    boolean updateItem(String code, ItemDTO item, Connection connection) throws SQLException;

    boolean deleteItem(String code, Connection connection) throws SQLException;

    List<ItemDTO> getAllItems(Connection connection) throws SQLException;
}
