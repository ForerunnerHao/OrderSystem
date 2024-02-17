package menu.pojo;

public class OrderHistory {
    private int orderId;
    private java.sql.Timestamp dateTime; //Timestamp type to match with datetime type in MySQL
    private ORDER_STATUS orderStatus;

    private ORDER_METHOD orderMethod;

    public ORDER_METHOD getOrderMethod() {
        return orderMethod;
    }

    public void setOrderMethod(ORDER_METHOD orderMethod) {
        this.orderMethod = orderMethod;
    }

    public void setOrderId(int id) {
        this.orderId = id;
    }
    public void setDateTime(java.sql.Timestamp dateTime) {
        this.dateTime = dateTime;
    }
    public void setOrderStatus(ORDER_STATUS orderStatus){
        this.orderStatus = orderStatus;
    }


    public int getOrderId(){
        return orderId;
    }

    public java.sql.Timestamp getDateTime() {
        return dateTime;
    }

    public ORDER_STATUS getOrderStatus() {
        return orderStatus;
    }
}