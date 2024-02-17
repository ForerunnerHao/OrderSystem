package menu.dao;

import menu.pojo.*;
import menu.utils.ColorfulConsole;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderDAO {
    private static OrderDAO INSTANCE = null;

    private OrderDAO() {}

    public static OrderDAO getInstance() {
        if (INSTANCE == null){
            INSTANCE = new OrderDAO();
        }
        return INSTANCE;
    }

    private static final String SQL_ERROR = "SQL execute failed, please check SQL statement and try again later";
    private static final String CREATE_A_NEW_ORDER = "INSERT INTO `order_history` (`create_time`) VALUES (NOW())";
    private static final String UPDATE_AN_ORDER_STATUS = "UPDATE order_history SET order_history.status = ? WHERE order_history.id = ?";
    private static final String INSERT_AN_ORDER_INFO = "INSERT INTO `order` (`order_id`, `item_id`, `quantity`) VALUES (?, ?, ?)";
    private static final String DELETE_AN_ORDER_HISTORY = "DELETE FROM order_history WHERE id = ?";
    private static final String UPDATE_AN_ORDER_METHOD = "UPDATE order_history SET order_history.method = ? WHERE order_history.id = ?";

    public Order createOrder() {
        Order order = null;
        try (
              Connection  connection = MySQLOpenHelper.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_A_NEW_ORDER, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            int newOrderId;

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newOrderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // Get new order history info from database
            String QUERY_NEW_ORDER = "SELECT * FROM `order_history` WHERE `id` = ?";

            order = new Order();
            try (PreparedStatement newStatement = connection.prepareStatement(QUERY_NEW_ORDER)) {
                newStatement.setInt(1, newOrderId);
                ResultSet resultSet = newStatement.executeQuery();
                if (resultSet.next()) {
                    order.setOrderId(newOrderId);
                    order.setOrderStatus(ORDER_STATUS.valueOf(resultSet.getString("status").toUpperCase()));
                    order.setOrderMethod(ORDER_METHOD.valueOf(resultSet.getString("method").toUpperCase()));
                    order.setDateTime(resultSet.getTimestamp("create_time"));
                    order.setOrderItems(new LinkedHashMap<>());
                }
            }
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }

        return order;
    }

    public Message orderPay(Order order){
        int orderItemNum = order.getOrderItems().size();
        int feedbackNum = 0;
        Message message = new Message(SQL_ERROR);
        for (Map.Entry<Item, Integer> entry : order.getOrderItems().entrySet()) {
            try (
                    Connection connection = MySQLOpenHelper.getInstance().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AN_ORDER_INFO)) {
                // Set values for the placeholders
                preparedStatement.setInt(1, order.getOrderId());
                preparedStatement.setInt(2, entry.getKey().getId());
                preparedStatement.setInt(3, entry.getValue());

                // Execute the statement
                feedbackNum += preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
            }
        }

        if (feedbackNum == orderItemNum){
            message.setCode(Message.MESSAGE_OK);
            message.setMessage("Insert successfully, insert all " + feedbackNum +" statements");
        }else {
            message.setCode(Message.MESSAGE_FAIL);
            message.setMessage("Insert failed, only insert " + feedbackNum +" statement(s)");
        }
        return message;
    }

    public Message updateOrderStatus(int orderId, ORDER_STATUS orderStatus) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_AN_ORDER_STATUS)) {
            statement.setString(1, orderStatus.name().toUpperCase());
            statement.setInt(2, orderId);
            return executeUpdate(statement, "Status update successful", "No rows affected. Update might not be successful.");
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message deleteOrder(int orderId) {
        try (Connection connection = MySQLOpenHelper.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_AN_ORDER_HISTORY)) {
            statement.setInt(1, orderId);
            return executeUpdate(statement, "Delete order successfully", "Delete order failed!");
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    public Message updateOrderMethod(int orderId, ORDER_METHOD orderMethod) {
        try (
                Connection connection = MySQLOpenHelper.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_AN_ORDER_METHOD)) {
            statement.setString(1, orderMethod.name().toLowerCase());
            statement.setInt(2, orderId);
            return executeUpdate(statement, "Status update successful", "No rows affected. Update might not be successful.");
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return new Message(SQL_ERROR);
    }

    private Message executeUpdate(PreparedStatement statement, String successMessage, String failMessage) {
        Message message = new Message(SQL_ERROR);
        try {
            int result = statement.executeUpdate();
            if (result > 0) {
                message.setCode(Message.MESSAGE_OK);
                message.setMessage(successMessage + " Affected rows: " + result + ".");
            } else {
                message.setCode(Message.MESSAGE_FAIL);
                message.setMessage(failMessage);
            }
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));
        }
        return message;
    }

}
