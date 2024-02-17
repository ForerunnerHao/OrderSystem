package menu.pojo;

import java.sql.Timestamp;
import java.util.Map;

public class Order {
    private int orderId;
    private ORDER_STATUS orderStatus;
    private  ORDER_METHOD orderMethod;
    private double orderTotalPrice;
    private static final double GST = 0.1;

    public static double getGST() {
        return GST;
    }

    private Map<Item, Integer> orderItems;
    private Timestamp dateTime;

    public Map<Item, Integer> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Map<Item, Integer> orderItems) {
        this.orderItems = orderItems;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public ORDER_STATUS getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(ORDER_STATUS orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(double orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public ORDER_METHOD getOrderMethod() {
        return orderMethod;
    }

    public void setOrderMethod(ORDER_METHOD orderMethod) {
        this.orderMethod = orderMethod;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

}

