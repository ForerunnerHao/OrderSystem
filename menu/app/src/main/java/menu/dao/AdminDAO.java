package menu.dao;

import menu.pojo.Category;
import menu.pojo.Item;
import menu.pojo.Message;
import menu.utils.ColorfulConsole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    private static final AdminDAO INSTANCE = new AdminDAO();
    private static final String SQL_ERROR = "SQL execute failed, please check SQL statement and try again later";
    private static final String SELECT_ALL_CATEGORY = "SELECT category.id, category.name FROM `category`";
    private static final String INSERT_A_NEW_CATEGORY = "INSERT INTO `category` (category.name) VALUES (?)";
    private static final String INSERT_A_NEW_MENU_ITEM = "INSERT INTO `item` (`name`, `description`, `ingredient`, `weight`, `price`, `category`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_A_CATEGORY = "UPDATE `category` SET `category`.name = ? WHERE `category`.id = ?";
    private static final String UPDATE_A_MENU_ITEM = "UPDATE assignment1.item " +
            "SET item.name = ?, item.description = ?, item.ingredient = ?, item.weight = ?, item.price = ?, item.category = ? " +
            "WHERE item.id = ?";
    private static final String DELETE_A_MENU_ITEM = "DELETE FROM `item` Where item.id = ?";
    private static final String DELETE_A_CATEGORY = "DELETE FROM `category` Where category.id = ?";

    private AdminDAO() {
    }

    public static AdminDAO getInstance() {
        return INSTANCE;
    }

    public List<Category> getCategory() {
        List<Category> categories = new ArrayList<>();
        try (
                Connection connection = MySQLOpenHelper.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CATEGORY)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(new Category(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return categories;
    }

    public Message insertCategory(String categoryName) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_A_NEW_CATEGORY)) {
            statement.setString(1, categoryName);
            return executeUpdate(statement);
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message deleteCategory(int categoryId) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_A_CATEGORY)) {
            statement.setInt(1, categoryId);
            return executeUpdate(statement);
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message updateCategory(String categoryName, int categoryId) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_A_CATEGORY)) {
            statement.setString(1, categoryName);
            statement.setInt(2, categoryId);
            return executeUpdate(statement);
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message insertMenuItem(Item item) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_A_NEW_MENU_ITEM)) {
            statement.setString(1, item.getName());
            statement.setString(2, item.getDescription());
            statement.setString(3, item.getIngredient());
            statement.setDouble(4, item.getWeight());
            statement.setDouble(5, item.getPrice());
            statement.setInt(6, item.getCategoryId());
            return executeUpdate(statement);
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message deleteMenuItem(int itemId) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_A_MENU_ITEM)) {
            statement.setInt(1, itemId);
            return executeUpdate(statement);
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message updateMenuItem(Item item) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_A_MENU_ITEM)) {
            statement.setDouble(4, item.getWeight());
            statement.setDouble(5, item.getPrice());
            statement.setInt(6, item.getCategoryId());
            statement.setInt(7, item.getId());
            statement.setString(1, item.getName());
            statement.setString(2, item.getDescription());
            statement.setString(3, item.getIngredient());
            return executeUpdate(statement);
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    private Message executeUpdate(PreparedStatement statement) {
        Message message = new Message(SQL_ERROR);
        try {
            int result = statement.executeUpdate();
            if (result > 0) {
                message.setCode(Message.MESSAGE_OK);
                message.setMessage("Operation successful" + " Affected rows: " + result + ".");
            } else {
                message.setCode(Message.MESSAGE_FAIL);
                message.setMessage("No rows affected. Operation might not be successful.");
            }
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return message;
    }

}
