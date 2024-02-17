package menu.controller;

import menu.auth.LocalStorage;
import menu.dao.OrderHistoryDAO;
import menu.pojo.Item;
import menu.pojo.Order;
import menu.pojo.OrderHistory;

import java.util.List;
import java.util.Map;

public class HistoryController {
    private final static String STR_LINE = "------------------------------------------------------------";
    private static final HistoryController INSTANCE = new HistoryController();

    private HistoryController() {}

    public static HistoryController getInstance() {
        return INSTANCE;
    }
    /**
     * Print out list of order history */
    public void showHistory() {
        List<OrderHistory> orderHistoriesItems = LocalStorage.getInstance().getOrderHistories();
        System.out.println("There were " + orderHistoriesItems.size() +" order(s) made.");
        System.out.println(STR_LINE);
        System.out.println("\t\t\t\t Order History");
        System.out.println(STR_LINE);
        System.out.println("ORDER ID\tCreate Time\t\t\t\t\tMethod\t\tStatus");
        System.out.println(STR_LINE);
        for (OrderHistory oh : orderHistoriesItems) {

            System.out.printf("%d\t\t\t%-27s\t%-10s\t%-10s\n", oh.getOrderId(), oh.getDateTime().toString(),oh.getOrderMethod(), oh.getOrderStatus());
        }
        System.out.println(STR_LINE + "\n");
    }

    public void viewHistory(int orderId){
        if (isOnHistory(orderId)){
            System.out.println("The system is searching, please wait...");
            Map<Item, Integer> items = OrderHistoryDAO.getInstance().getHistoryView(orderId);
            if (!items.isEmpty()){
                Order order = new Order();
                order.setOrderId(orderId);
                order.setOrderItems(items);
                OrderController.getInstance().printOrder(order);
            }else {
                System.out.println("The order is empty.");
            }
        }else {
            System.out.println("Your input orderId do not exist, Please try again.");
        }
    }

    private boolean isOnHistory(int orderId){
        for (OrderHistory oh: LocalStorage.getInstance().getOrderHistories()
        ) {
            if(oh.getOrderId() == orderId){
                return true;
            }
        }
        return false;
    }

}
