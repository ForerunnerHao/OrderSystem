package menu.dao;

import menu.pojo.Item;
import menu.utils.ColorfulConsole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    private static MenuDAO instance = null;

    private MenuDAO() {
    }

    public static MenuDAO getInstant() {
        if (instance == null) {
            instance = new MenuDAO();
        }
        return instance;
    }

    private static final String SELECT_ALL_MENU_ITEM =
            "SELECT item.id, item.`name`, item.description, item.ingredient, item.weight, item.price, category.`name` AS category_name " +
                    "FROM item " +
                    "JOIN category ON item.category = category.id " +
                    "ORDER BY category.`id` DESC, item.`name` DESC;";

    public List<Item> getMenu() {
        List<Item> menu = new ArrayList<>();
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_MENU_ITEM);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Item item = new Item();
                item.setId(resultSet.getInt("id"));
                item.setName(resultSet.getString("name"));
                item.setDescription(resultSet.getString("description"));
                item.setIngredient(resultSet.getString("ingredient"));
                item.setWeight(resultSet.getDouble("weight"));
                item.setPrice(resultSet.getDouble("price"));
                item.setCategoryName(resultSet.getString("category_name"));

                menu.add(item);
            }

        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
        }
        return menu;
    }


}
