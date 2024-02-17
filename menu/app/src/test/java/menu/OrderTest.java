package menu;

import menu.auth.LocalStorage;
import menu.controller.CommandParser;
import menu.controller.OrderController;
import menu.dao.OrderDAO;
import menu.pojo.Item;
import menu.pojo.Message;
import menu.utils.ColorfulConsole;
import menu.utils.FindListElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    private static final int ORDER_ITEM_NUM = 4;
    private static final int ORDER_REMOVE_INDEX = 1;
    public void setUserInput(String simulatedUserInput) {
        simulatedUserInput += "\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        App.setScanner(mockScanner);
    }
    @BeforeAll
    static void loadData(){
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
    }
    @Test
    @Order(1)
    void addOrderItemTest(){
        // Add item
        OrderController.getInstance().orderAdd(14);
        OrderController.getInstance().orderAdd(14);
        OrderController.getInstance().orderAdd(14);
        OrderController.getInstance().orderAdd(15);
        OrderController.getInstance().orderAdd(16);
        OrderController.getInstance().orderAdd(17);
        CommandParser.getInstance().execute("order show");
        assertEquals(ORDER_ITEM_NUM, LocalStorage.getInstance().getUserOrder().getOrderItems().size());

        // Invalid input (999)
        CommandParser.getInstance().execute("order add 999");

        // Invalid input (015)
        CommandParser.getInstance().execute("order add 015");

        // Invalid input ( )
        CommandParser.getInstance().execute("order add ");

        // Invalid input (a15)
        CommandParser.getInstance().execute("order add a15");

        // Invalid input (15a)
        CommandParser.getInstance().execute("order add 15a");

        // Invalid input (aaa)
        CommandParser.getInstance().execute("order add aaa");
    }

    @Test
    @Order(2)
    void  updateOrderItemTest(){
        // update order item (index = 1, itemId = 14) set 99
        setUserInput("99");
        OrderController.getInstance().orderUpdate(1);
        Item item = FindListElement.getElement(LocalStorage.getInstance().getMenuItems(), item1 -> item1.getId() == 14);
        int updateNum = LocalStorage.getInstance().getUserOrder().getOrderItems().get(item);
        assertEquals(99, updateNum);

        // add more item on (index = 1, itemId = 14), max is 99 so number not change
        OrderController.getInstance().orderAdd(14);
        updateNum = LocalStorage.getInstance().getUserOrder().getOrderItems().get(item);
        assertEquals(99, updateNum);

        // update order item (index = 1, itemId = 14) set 0 = remove it
        setUserInput("0");
        OrderController.getInstance().orderUpdate(1);
        int leftNum = LocalStorage.getInstance().getUserOrder().getOrderItems().size();
        assertEquals(3, leftNum);

        //Invalid input ()
        setUserInput("");
        OrderController.getInstance().orderUpdate(1);


        //Invalid input ( )
        setUserInput(" ");
        OrderController.getInstance().orderUpdate(1);


        //Invalid input (Null)
        setUserInput(null);
        OrderController.getInstance().orderUpdate(1);

        //Invalid input (012)
        setUserInput("012");
        OrderController.getInstance().orderUpdate(1);

        //Invalid input (-1)
        setUserInput("-1");
        OrderController.getInstance().orderUpdate(1);

        //Invalid input (a12)
        setUserInput("a12");
        OrderController.getInstance().orderUpdate(1);

        //Invalid input (aa)
        setUserInput("aaa");
        CommandParser.getInstance().execute("order update 1");
    }

    @Test
    @Order(3)
    void removeOrderItemTest(){

        // Remove the first element
        CommandParser.getInstance().execute("order remove " + ORDER_REMOVE_INDEX);
        assertEquals(2, LocalStorage.getInstance().getUserOrder().getOrderItems().size());

        // invalid order index (999)
        CommandParser.getInstance().execute("order remove 999");

        // invalid input (01)
        CommandParser.getInstance().execute("order remove 01");

        // invalid input (1a)
        CommandParser.getInstance().execute("order remove 1a");

        // invalid input (aa)
        CommandParser.getInstance().execute("order remove aa");
    }

    @Test
    @Order(4)
    void  orderPayTest(){
        // Pay order - delivery - cancel
        setUserInput("1\nc");
        OrderController.getInstance().orderPay();
        // change order method (1. delivery, 2. pick_up [only number])
        // Confirm order check out
        String method = LocalStorage.getInstance().getUserOrder().getOrderMethod().name().toLowerCase();
        assertEquals("delivery", method);


        // Pay order - pick_up - cancel
        setUserInput("2\nc");
        OrderController.getInstance().orderPay();
        // change order method (1. delivery, 2. pick_up [only number])
        // Confirm order check out
        method = LocalStorage.getInstance().getUserOrder().getOrderMethod().name().toLowerCase();
        assertEquals("pick_up", method);

        // Pay order - order method - invalid input ()
        setUserInput("");
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input ( )
        setUserInput(" ");
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input ( )
        setUserInput(null);
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input (0)
        setUserInput("0");
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input (01)
        setUserInput("01");
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input (1a)
        setUserInput("1a");
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input (a1)
        setUserInput("a1");
        OrderController.getInstance().orderPay();


        // Pay order - order method - invalid input (aa)
        setUserInput("aa");
        OrderController.getInstance().orderPay();

        // Pay order - confirm order - invalid input ( )
        setUserInput("1\n ");
        OrderController.getInstance().orderPay();


        // Pay order - confirm order - invalid input (1)
        setUserInput("1\n1");
        OrderController.getInstance().orderPay();

        // Pay order - confirm order - invalid input (01)
        setUserInput("1\n01");
        OrderController.getInstance().orderPay();

        // Pay order - confirm order - invalid input (aa)
        setUserInput("1\naa");
        OrderController.getInstance().orderPay();

        // Pay order - successful
        setUserInput("1\ny");
        OrderController.getInstance().orderPay();

        // Pay no exist order
        setUserInput("1\ny");
        OrderController.getInstance().orderPay();

        // Pay empty order
        menu.pojo.Order order = new menu.pojo.Order();
        order.setOrderId(22);
        LocalStorage.getInstance().setUserOrder(order);
        setUserInput("1\ny");
        CommandParser.getInstance().execute("order pay");
    }

    @Test
    @Order(5)
    void  orderShowTest(){

            // Printout order info
            OrderController.getInstance().orderShow();

            //Command
            CommandParser.getInstance().execute("order show");
    }

    @AfterAll
    static void deleteTestOrder(){
        menu.pojo.Order order = LocalStorage.getInstance().getUserOrder();
        if (order != null){
            Message message = OrderDAO.getInstance().deleteOrder(order.getOrderId());
            if (message.getCode() == Message.MESSAGE_OK){
                System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
            }else {
                System.out.println(ColorfulConsole.printFailInfo(message.getMessage()));
            }
        }else {
            System.out.println("Order is not exist");
        }
    }
}
