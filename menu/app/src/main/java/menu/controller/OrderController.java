package menu.controller;

import menu.App;
import menu.auth.LocalStorage;
import menu.dao.OrderDAO;
import menu.pojo.*;
import menu.utils.ColorfulConsole;
import menu.utils.FindListElement;

import java.util.Map;
import java.util.Scanner;

public class OrderController {
    private final static String STR_LINE = "-----------------------------------------------------";
    private static final OrderController INSTANCE = new OrderController();

    private OrderController() {
    }

    public static OrderController getInstance() {
        return INSTANCE;
    }

    public void orderAdd(int itemId) {
        Order userOrder = LocalStorage.getInstance().getUserOrder();
        if (userOrder == null) {
            System.out.println("Creating a new order, please wait a second...");
            LocalStorage.getInstance().setUserOrder(OrderDAO.getInstance().createOrder());
            userOrder = LocalStorage.getInstance().getUserOrder();
            if (userOrder != null) {
                System.out.println(ColorfulConsole.printSuccessInfo("You create a new order, the order ID is #" + userOrder.getOrderId()));
            } else {
                System.out.println(ColorfulConsole.printFailInfo("System create order failed, please try again..."));
                return;
            }
        }
        Item item = FindListElement.getElement(LocalStorage.getInstance().getMenuItems(), item1 -> item1.getId() == itemId);
        if (item != null) {
            if (userOrder.getOrderItems().get(item) == null) {
                System.out.println("Add a new item: " + ColorfulConsole.ANSI_GREEN + item.getName() + ColorfulConsole.ANSI_RESET);
                userOrder.getOrderItems().put(item, 1);
            } else {
                int newQuantity = userOrder.getOrderItems().get(item);
                newQuantity++;
                if (newQuantity >= 100) {
                    System.out.println("You can not add more.");
                } else {
                    userOrder.getOrderItems().put(item, newQuantity);
                    System.out.println(ColorfulConsole.ANSI_GREEN + item.getName() + ColorfulConsole.ANSI_RESET + " plus one, current number is " + newQuantity);
                }
            }
            userOrder.setOrderTotalPrice(userOrder.getOrderTotalPrice() + item.getPrice());

            //print edited order
            printOrder(userOrder);
        } else {
            System.out.println("System can not find the input item ID, Please try a another id.");
        }
    }

    public void orderRemove(int orderItemIndex) {
        if (isOrderExist()) {
            Item item = getOrderItemByIndex(LocalStorage.getInstance().getUserOrder(), orderItemIndex);
            if (item != null) {
                LocalStorage.getInstance().getUserOrder().getOrderItems().remove(item);
                System.out.println("Remove " + ColorfulConsole.ANSI_GREEN + item.getName() + ColorfulConsole.ANSI_RESET + " successfully!");

                // print edited order
                printOrder(LocalStorage.getInstance().getUserOrder());
            }
        }
    }

    public void orderUpdate(int orderItemIndex) {
        if (isOrderExist()) {
            Item item = getOrderItemByIndex(LocalStorage.getInstance().getUserOrder(), orderItemIndex);
            if (item != null) {
                Scanner scanner = App.getScanner();
                System.out.print(ColorfulConsole.ANSI_GREEN + item.getName() + ColorfulConsole.ANSI_RESET + " new quantity is (0~99): ");
                if (!scanner.hasNextLine()) {
                    System.out.println("Please input a correct number.");
                    return;
                }
                String input = scanner.nextLine().trim();
                int num;
                try {
                    num = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
                    System.out.println("Please input a correct number.");
                    return;
                }

                if (num >= 0 && num <= 99) {
                    if (num == 0) {
                        orderRemove(orderItemIndex);
                    } else {
                        LocalStorage.getInstance().getUserOrder().getOrderItems().put(item, num);
                        System.out.println("Update successfully!");

                        // print edited order
                        printOrder(LocalStorage.getInstance().getUserOrder());
                    }
                } else {
                    System.out.println("Please input a correct number.");
                }
            }
        }
    }

    public void orderShow() {
        if (isOrderExist()) {
            Order order = LocalStorage.getInstance().getUserOrder();
            printOrder(order);
        }
    }

    public void printOrder(Order order) {
        System.out.println("\nOrder id:" + order.getOrderId());
        System.out.println(STR_LINE);
        System.out.println("No\tName\t\t\t\t\t\tNum\t\tTotalPrice");
        System.out.println(STR_LINE);
        int[] index = {1};
        double[] total = {0};
        order.getOrderItems().forEach((item, quantity) -> {
                    System.out.printf("%d\t" + ColorfulConsole.ANSI_GREEN + "%-25s" + ColorfulConsole.ANSI_RESET + "\t%d\t\t$%.2f\n",
                            index[0]++, "#" + item.getId() + " " + item.getName(), quantity, item.getPrice() * quantity);
                    total[0] += item.getPrice() * quantity;
                }
        );
        order.setOrderTotalPrice(total[0]);
        System.out.printf("\n%-40s$%.2f\n", "GST:", order.getOrderTotalPrice() * Order.getGST());
        System.out.println(STR_LINE);
        System.out.printf(ColorfulConsole.ANSI_RED + "%-40s$%.2f\n" + ColorfulConsole.ANSI_RESET, "Total:", order.getOrderTotalPrice() * (1 + Order.getGST()));
        System.out.println(STR_LINE);

    }

    public void orderPay() {
        Order order;
        if (isOrderExist()) {
            order = LocalStorage.getInstance().getUserOrder();
            if (isOrderEmpty(order)) {
                System.out.println("Your order is empty, please add some items...");
                return;
            }
            orderShow();
            Scanner scanner = App.getScanner();
            while (true) {
                System.out.print("1. Delivery\n2. Pickup\nPlease choose order get method (default is Pickup):");
                if (!scanner.hasNextLine()) {
                    System.out.println("Invalid input. Please try again.");
                    return;
                }
                String input = scanner.nextLine().trim().toLowerCase();

                // Change order method
                if ("1".equals(input) || "delivery".equals(input)) {
                    System.out.println("Changing order method...");
                    Message result = OrderDAO.getInstance().updateOrderMethod(order.getOrderId(), ORDER_METHOD.DELIVERY);
                    if (result.getCode() == Message.MESSAGE_OK) {
                        System.out.println(ColorfulConsole.printSuccessInfo(result.getMessage()));
                        order.setOrderMethod(ORDER_METHOD.DELIVERY);
                    } else {
                        System.out.println(ColorfulConsole.printFailInfo(result.getMessage()));
                        return;
                    }
                    break;
                } else if (input.isEmpty() || "2".equals(input) || "pickup".equals(input)) {
                    System.out.println("Changing order method...");
                    Message result = OrderDAO.getInstance().updateOrderMethod(order.getOrderId(), ORDER_METHOD.PICK_UP);
                    if (result.getCode() == Message.MESSAGE_OK) {
                        System.out.println(ColorfulConsole.printSuccessInfo(result.getMessage()));
                        order.setOrderMethod(ORDER_METHOD.PICK_UP);
                    } else {
                        System.out.println(ColorfulConsole.printFailInfo(result.getMessage()));
                        return;
                    }
                    break;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }

            System.out.print(
                    ColorfulConsole.ANSI_RED +
                            "\n" + STR_LINE +
                            "\nOrder checkout" +
                            "\nOrder id: #" + order.getOrderId() +
                            "\nOrder status: " + order.getOrderStatus().name() +
                            "\nOrder method: " + order.getOrderMethod().name() +
                            "\nTotal price(include GST): $" + String.format("%.2f", order.getOrderTotalPrice() * (1 + Order.getGST())) +
                            "\n" + STR_LINE +
                            ColorfulConsole.ANSI_RESET +
                            "\n\nDo you want to pay the order? 'yes'(y) or 'cancel'(c): ");

            if (!scanner.hasNextLine()) {
                System.out.println("Invalid input, order checkout was terminated.");
                return;
            }
            String input = scanner.nextLine().trim();

            if (input.equals("yes") || input.equals("y")) {
                System.out.println("Order paying, please wait...");
                Message message = OrderDAO.getInstance().orderPay(order);

                // Payment successfully
                if (message.getCode() == Message.MESSAGE_OK) {
                    System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
                    System.out.println("Changing order status, please wait...");

                    // Change the order status
                    Message result = OrderDAO.getInstance().updateOrderStatus(order.getOrderId(), ORDER_STATUS.PAID);

                    // Status update successfully
                    if (result.getCode() == Message.MESSAGE_OK) {
                        System.out.println(ColorfulConsole.printSuccessInfo(result.getMessage()));
                        System.out.println(ColorfulConsole.printSuccessInfo("Payment successfully!!!"));
                        // Destroy the successful payment order
                        LocalStorage.getInstance().setUserOrder(null);
                        // Reload order history data
                        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ORDER_HISTORY);
                    } else {
                        System.out.println(ColorfulConsole.printFailInfo("Failed: " + result.getMessage()));
                    }
                }

                // Payment failed
                else if (message.getCode() == Message.MESSAGE_FAIL) {
                    System.out.println(ColorfulConsole.printFailInfo(message.getMessage() + "\nPayment failed!!!"));
                }
            } else if (input.equals("cancel") || input.equals("c")) {
                System.out.println("Order checkout was canceled.");
            } else {
                System.out.println("Invalid input, order checkout was terminated.");
            }
        }
    }

    private boolean isOrderExist() {
        Order order = LocalStorage.getInstance().getUserOrder();
        if (order != null && order.getOrderItems() != null) {
            return true;
        } else {
            System.out.println("Your order do not exist, please add some items...");
            return false;
        }
    }

    private boolean isOrderEmpty(Order order) {
        if (order != null && order.getOrderItems() != null) {
            return order.getOrderItems().isEmpty();
        }
        return false;
    }

    private Item getOrderItemByIndex(Order order, int mapIndex) {
        int i = 1;
        Map<Item, Integer> linkedHashMap = order.getOrderItems();
        for (Map.Entry<Item, Integer> entry : linkedHashMap.entrySet()) {
            if (mapIndex == i++) {
                return entry.getKey();
            }
        }
        System.out.println("Order do not exist the item the input index");
        return null;
    }
}
