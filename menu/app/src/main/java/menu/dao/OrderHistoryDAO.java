package menu.dao;

import menu.pojo.*;
import menu.utils.ColorfulConsole;

import java.sql.*;
import java.util.*;

public class OrderHistoryDAO {
    private static final String GET_HISTORY_SHOW = "SELECT * from `order_history`" ;

    //private static SQLHelper = null;
    private static OrderHistoryDAO INSTANCE = null;

    private OrderHistoryDAO() {
    }

    public static OrderHistoryDAO getInstance() {
        if (INSTANCE == null){
            INSTANCE = new OrderHistoryDAO();
        }
        return INSTANCE;
    }

    /*Reference from MenuDAO*/
    public List<OrderHistory> getHistoryShow(){
        List<OrderHistory> result = new ArrayList<>();
        try (
                Connection connection = MySQLOpenHelper.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_HISTORY_SHOW);
             ResultSet resultSet = statement.executeQuery()){

             while(resultSet.next()){
                OrderHistory oh = new OrderHistory();

                //parse ID
                oh.setOrderId(resultSet.getInt("id"));

                //if else to validate parsing status(varchar) into enum(ORDER_STATUS)
                 try {
                     ORDER_STATUS status = ORDER_STATUS.valueOf(resultSet.getString("status").toUpperCase());
                     oh.setOrderStatus(status);

                     ORDER_METHOD method = ORDER_METHOD.valueOf(resultSet.getString("method").toUpperCase());
                     oh.setOrderMethod(method);
                 }
                 catch (IllegalArgumentException e){
                     System.out.println(ColorfulConsole.printFailInfo("IllegalArgumentException: " + e.getMessage()));
                 }

                //parsing DateTime(java.sql.Timestamp)
                oh.setDateTime(resultSet.getTimestamp("create_time"));
                //add instance to result
                result.add(oh);
             }
        } catch (SQLException e) {
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));

        }
        return result;
    }

    public Map<Item, Integer> getHistoryView(int orderId){
        Map<Item, Integer> items = null;
        String GET_HISTORY_VIEW = "SELECT item.id AS id, item.name AS name , `order`.quantity AS quantity, item.price AS price " +
                "FROM `order` " +
                "JOIN item on `order`.item_id = item.id " +
                "WHERE `order`.order_id = ?";

        try(
                Connection connection = MySQLOpenHelper.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_HISTORY_VIEW)){
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            items = new HashMap<>();
            while (resultSet.next()){
                Item item = new Item();
                item.setId(resultSet.getInt("id"));
                item.setName(resultSet.getString("name"));
                item.setPrice(resultSet.getDouble("price"));
                items.put(item, resultSet.getInt("quantity"));
            }
        }catch (SQLException e){
            System.out.println(ColorfulConsole.printFailInfo("SQL error: " + e.getMessage()));

        }

        return items;
    }



}
