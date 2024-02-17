package menu;

import menu.dao.*;
import menu.pojo.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DaoTest {
    public static void setUserInput(String simulatedUserInput) {
        simulatedUserInput += "\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        App.setScanner(mockScanner);
    }

    @BeforeAll
    static void disconnectPool(){
        // Close connection pool
        setUserInput("1\nexit");
        App.main(new String[] {"none arg"});
    }

    @Test
    void menuDaoTest(){
        //get all menu item
        MenuDAO.getInstant().getMenu();
    }

    @Test
    void orderDaoTest(){

        // delete
        OrderDAO.getInstance().deleteOrder(100);

        // add order
        OrderDAO.getInstance().createOrder();

        // update method
        OrderDAO.getInstance().updateOrderMethod(100, ORDER_METHOD.PICK_UP);

        // update status
        OrderDAO.getInstance().updateOrderStatus(100, ORDER_STATUS.PAID);

        // order pay
        Order order = new Order();
        Map<Item,Integer> map = new LinkedHashMap<>();
        map.put(new Item(), 11);
        order.setOrderItems(map);
        OrderDAO.getInstance().orderPay(order);
    }

    @Test
    void systemDaoTest(){

        // verify user input
        SystemDAO.getInstance().getUser(new Admin());
    }

    @Test
    void HistoryDaoTest(){

        //show History
        OrderHistoryDAO.getInstance().getHistoryShow();

        // history view
        OrderHistoryDAO.getInstance().getHistoryView(100);
    }

    @Test
    void adminDaoTest(){

        // delete category
        AdminDAO.getInstance().deleteCategory(11);

        //delete menu item
        AdminDAO.getInstance().deleteMenuItem(11);

        //update category
        AdminDAO.getInstance().updateCategory("haha", 11);

        //update menu item
        AdminDAO.getInstance().updateMenuItem(new Item());

        //add new category
        AdminDAO.getInstance().insertCategory("haha");

        //add new menu item
        AdminDAO.getInstance().insertMenuItem(new Item());
    }

    @AfterAll
    static void reconnectPool(){
        MySQLOpenHelper.getInstance().restartConnectionPool();
    }
}
