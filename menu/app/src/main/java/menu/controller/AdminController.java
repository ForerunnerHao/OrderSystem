package menu.controller;

import menu.App;
import menu.auth.LocalStorage;
import menu.auth.ROLE;
import menu.auth.UserRole;
import menu.dao.AdminDAO;
import menu.pojo.Category;
import menu.pojo.Item;
import menu.pojo.Message;
import menu.pojo.OrderHistory;
import menu.utils.ColorfulConsole;
import menu.utils.FindListElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AdminController {
    private static final AdminController INSTANCE = new AdminController();
    private static final String REFUSE_EXECUTE = "Permission denied";
    private static final String INPUT_ERROR = "Invalid input";
    private static Scanner scanner;
    private AdminController() {
    }

    public static AdminController getInstance() {
        return INSTANCE;
    }

    public void add(){
        if (UserRole.getInstance().isAdmin()){
            System.out.print("1. Menu Item\n2. Category\nWhich one you want to add?(only number): ");
            scanner = App.getScanner();
            if (!scanner.hasNextLine()){
                System.out.println("valid input");
                return;
            }
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()){
                System.out.println(INPUT_ERROR);
                return;
            }
            switch (input) {
                case "1", "menu item" -> {
                    Item item = new Item();

                   // Menu Item
                    System.out.print("Item name: ");
                    if (!scanner.hasNextLine()){
                        System.out.println(INPUT_ERROR);
                        return;
                    }
                    input = scanner.nextLine().trim();
                    if (input.isEmpty()){
                        System.out.println(INPUT_ERROR);
                        return;
                    }
                    item.setName(input);
                    System.out.println("The name has been set to: " +ColorfulConsole.printSuccessInfo(item.getName()));

                    // Category
                    System.out.println("Item category: ");
                    printCategory(LocalStorage.getInstance().getCategories());
                    System.out.print("Please select one(only number): ");
                    input = scanner.nextLine();
                    try{
                        int num = Integer.parseInt(input);
                        if (isOnCategory(LocalStorage.getInstance().getCategories(), num)){
                            Category ca = FindListElement.getElement(LocalStorage.getInstance().getCategories(), category -> category.getId() == num);
                            if (ca != null){
                                item.setCategoryName(ca.getName());
                                item.setCategoryId(num);
                                System.out.println("The category has been set to: " +ColorfulConsole.printSuccessInfo(item.getCategoryName()));
                            }else {
                                System.out.println(INPUT_ERROR);
                                return;
                            }
                        }else {
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                    }catch (NumberFormatException e){
                        System.out.println(INPUT_ERROR);
                        return;
                    }

                    //price
                    System.out.print("Item price: ");
                    input = scanner.nextLine();
                    try{
                        double price = Double.parseDouble(input);
                        if (price <= 0.0){
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                        item.setPrice(price);
                        System.out.println("The price has been set to: " +item.getPrice());
                    }catch (NumberFormatException | NullPointerException e){
                        System.out.println(INPUT_ERROR);
                        return;
                    }

                    //weight
                    System.out.print("Item weight: ");
                    input = scanner.nextLine();
                    try{
                        double weight = Double.parseDouble(input);
                        if (weight <= 0.0){
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                        item.setWeight(weight);
                        System.out.println("The price has been set to: " +item.getWeight());
                    }catch (NumberFormatException e){
                        System.out.println(INPUT_ERROR);
                        return;
                    }

                    // description
                    System.out.print("Item description: ");
                    input = scanner.nextLine();
                    item.setDescription(input);
                    System.out.println("The description has been set to: " + item.getDescription());

                    //ingredient
                    System.out.print("Item ingredient: ");
                    input = scanner.nextLine();
                    item.setIngredient(input);
                    System.out.println("The ingredient has been set to: " +item.getIngredient());

                    //insert menu item
                    Message message = AdminDAO.getInstance().insertMenuItem(item);
                    if (message.getCode() == Message.MESSAGE_OK){
                        System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
                    }else {
                        System.out.println(ColorfulConsole.printFailInfo(message.getMessage()));
                    }
                }
                case "2", "category" -> {
                    // Category
                    System.out.println("Please input new category name: ");
                    Category category = new Category();
                    if (!scanner.hasNextLine()){
                        System.out.println(INPUT_ERROR);
                        return;
                    }
                    input = scanner.nextLine().trim();
                    if (input.isEmpty()){
                        System.out.println(INPUT_ERROR);
                        return;
                    }
                    category.setName(input);

                    // insert category
                    Message message = AdminDAO.getInstance().insertCategory(category.getName());
                    if (message.getCode() == Message.MESSAGE_OK){
                        System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
                    }else {
                        System.out.println(ColorfulConsole.printFailInfo(message.getMessage()));
                    }
                }
                default -> System.out.println(ColorfulConsole.printFailInfo(INPUT_ERROR));
            }
            LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
        }else {
            System.out.println(REFUSE_EXECUTE);
        }
    }

    public void delete(){
        if (UserRole.getInstance().isAdmin()){
            System.out.print("1. Menu Item\n2. Category\nWhich one you want to delete?(only number): ");
            scanner = App.getScanner();
            if (!scanner.hasNextLine()){
                System.out.println("invalid input");
                return;
            }
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input){
                case "1", "menu item" ->{
                    List<Item> menuItems =  LocalStorage.getInstance().getMenuItems();
                    printMenuItem(menuItems);
                    System.out.print("Please select one(only number): ");
                    input = scanner.nextLine();
                    try{
                        int itemId = Integer.parseInt(input);
                        if (isOnMenuItem(menuItems, itemId)){

                            // delete menu item
                            Message message = AdminDAO.getInstance().deleteMenuItem(itemId);
                            if (message.getCode() == Message.MESSAGE_OK){
                                System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
                            }else {
                                System.out.println(ColorfulConsole.printFailInfo(message.getMessage()));
                            }
                        }else{
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                    }catch (NumberFormatException e){
                        System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
                    }
                }
                case "2", "category" ->{
                    List<Category> categories =  LocalStorage.getInstance().getCategories();
                    printCategory(categories);
                    System.out.print("Please select one(only number): ");
                    input = scanner.nextLine();
                    try{
                        int categoryId = Integer.parseInt(input);
                        if (isOnCategory(categories, categoryId)){

                            // delete category
                            Message message = AdminDAO.getInstance().deleteCategory(categoryId);
                            if (message.getCode() == Message.MESSAGE_OK){
                                System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
                            }else {
                                System.out.println(ColorfulConsole.printFailInfo(message.getMessage()));
                            }
                        }else{
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                    }catch (NumberFormatException e){
                        System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
                    }
                }
                default -> System.out.println(ColorfulConsole.printFailInfo(INPUT_ERROR));
            }
            LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
        }else {
            System.out.println(REFUSE_EXECUTE);
        }
    }

    public void update(){
        if (UserRole.getInstance().isAdmin()){
            System.out.print("1. Menu Item\n2. Category\nWhich one you want to update?(only number): ");
            scanner = App.getScanner();
            if (!scanner.hasNextLine()){
                System.out.println("invalid input");
                return;
            }
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input){
                case "1", "menu item" ->{
                    List<Item> items =  LocalStorage.getInstance().getMenuItems();
                    printMenuItem(items);
                    System.out.print("Please select one(only number): ");
                    if (!scanner.hasNextLine()){
                        System.out.println(INPUT_ERROR);
                        return;
                    }
                    input = scanner.nextLine().trim();
                    try {
                        int itemID = Integer.parseInt(input);
                        if (isOnMenuItem(items, itemID)){
                            Item item = FindListElement.getElement(items, item1 -> item1.getId() == itemID);
                            if (item != null){
                                // Menu Item name
                                System.out.println("Item current name: " + ColorfulConsole.printSuccessInfo(item.getName()));
                                System.out.print("Input the new name(default keep old one): ");
                                if (!scanner.hasNextLine()){
                                    System.out.println(INPUT_ERROR);
                                    return;
                                }
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()){
                                    item.setName(input);
                                    System.out.println("The name has been set to: " +ColorfulConsole.printSuccessInfo(item.getName()));
                                }else {
                                    System.out.println("Keep the old name: " +ColorfulConsole.printSuccessInfo(item.getName()));
                                }

                                // Category
                                List<Category> categories = LocalStorage.getInstance().getCategories();
                                System.out.println("Item current category: " + ColorfulConsole.printSuccessInfo(item.getCategoryName()));
                                printCategory(categories);
                                System.out.print("Please select new one(only number, default keep old one): ");
                                if (!scanner.hasNextLine()){
                                    System.out.println(INPUT_ERROR);
                                    return;
                                }
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()){
                                    try{
                                        int num = Integer.parseInt(input);
                                        if (isOnCategory(categories, num)){
                                            Category ca = FindListElement.getElement(categories, category -> category.getId() == num);
                                            if (ca != null){
                                                item.setCategoryName(ca.getName());
                                                item.setCategoryId(num);
                                                System.out.println("The category has been set to: " +ColorfulConsole.printSuccessInfo(item.getCategoryName()));
                                            }else {
                                                System.out.println(INPUT_ERROR);
                                                return;
                                            }
                                        }else {
                                            System.out.println(INPUT_ERROR);
                                            return;
                                        }
                                    }catch (NumberFormatException e){
                                        System.out.println(INPUT_ERROR);
                                        return;
                                    }
                                }
                                else {
                                    Category ca = FindListElement.getElement(categories, category -> category.getName().equals(item.getCategoryName()) );
                                    if (ca == null){
                                        System.out.println(ColorfulConsole.printFailInfo("Can not find the old category's id"));
                                        return;
                                    }else {
                                        item.setCategoryId(ca.getId());
                                    }
                                    System.out.println("Keep the old category: " +ColorfulConsole.printSuccessInfo(item.getCategoryName()));
                                }

                                //price
                                System.out.println("Item current price: " + ColorfulConsole.printSuccessInfo(String.valueOf(item.getPrice())));
                                System.out.print("Input the new price(default keep old one): ");
                                if (!scanner.hasNextLine()){
                                    System.out.println(INPUT_ERROR);
                                    return;
                                }
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()){
                                    try{
                                        double price = Double.parseDouble(input);
                                        if (price <= 0.0){
                                            System.out.println(INPUT_ERROR);
                                            return;
                                        }
                                        item.setPrice(price);
                                        System.out.println("The price has been set to: " +item.getPrice());
                                    }catch (NumberFormatException | NullPointerException e){
                                        System.out.println(INPUT_ERROR);
                                        return;
                                    }
                                }else {
                                    System.out.println("Keep the old price: " +ColorfulConsole.printSuccessInfo(String.valueOf(item.getPrice())));
                                }

                                //weight
                                System.out.println("Item current weight: " + ColorfulConsole.printSuccessInfo(String.valueOf(item.getWeight())));
                                System.out.print("Input the new weight(default keep old one): ");
                                if (!scanner.hasNextLine()){
                                    System.out.println(INPUT_ERROR);
                                    return;
                                }
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()){
                                    try{
                                        double weight = Double.parseDouble(input);
                                        if (weight <= 0.0){
                                            System.out.println(INPUT_ERROR);
                                            return;
                                        }
                                        item.setWeight(weight);
                                        System.out.println("The price has been set to: " +item.getWeight());
                                    }catch (NumberFormatException e){
                                        System.out.println(INPUT_ERROR);
                                        return;
                                    }
                                }
                                else {
                                    System.out.println("Keep the old price: " +ColorfulConsole.printSuccessInfo(String.valueOf(item.getWeight())));
                                }

                                // description
                                System.out.println("Item current description: " + ColorfulConsole.printSuccessInfo(item.getDescription()));
                                System.out.print("Input the new description(default keep old one): ");
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()){
                                    item.setDescription(input);
                                    System.out.println("The description has been set to: " + item.getDescription());
                                }else {
                                    System.out.println("Keep the old description: " +ColorfulConsole.printSuccessInfo(item.getDescription()));
                                }

                                //ingredient
                                System.out.println("Item current ingredient: " + ColorfulConsole.printSuccessInfo(item.getIngredient()));
                                System.out.print("Input the new ingredient(default keep old one): ");
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()){
                                    item.setIngredient(input);
                                    System.out.println("The ingredient has been set to: " + item.getIngredient());
                                }else {
                                    System.out.println("Keep the old ingredient: " +ColorfulConsole.printSuccessInfo(item.getIngredient()));
                                }

                                // update menu item
                                Message message = AdminDAO.getInstance().updateMenuItem(item);
                                if (message.getCode() == Message.MESSAGE_OK){
                                    System.out.println(message.getMessage());
                                }else {
                                    System.out.println(message.getMessage());
                                }
                            }else {
                                System.out.println(INPUT_ERROR);
                                return;
                            }
                        }else {
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                    }catch (NumberFormatException e){
                        System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
                        return;
                    }


                }
                case "2", "category" ->{
                    // Category
                    List<Category> categories =  LocalStorage.getInstance().getCategories();
                    printCategory(categories);
                    System.out.print("Please select one(only number): ");
                    if (!scanner.hasNextLine()){
                        System.out.println(INPUT_ERROR);
                        return;
                    }
                    input = scanner.nextLine();
                    try{
                        int categoryId = Integer.parseInt(input);
                        if (isOnCategory(categories, categoryId)){
                            Category category = FindListElement.getElement(categories, category1 -> category1.getId() == categoryId);
                            if (category == null){
                                System.out.println(INPUT_ERROR);
                                return;
                            }

                            // update category
                            System.out.println("Please input the new name: ");
                            String categoryName = scanner.nextLine().trim();
                            if (categoryName.isEmpty()){
                                System.out.println("Keep the old name :" + ColorfulConsole.printSuccessInfo(category.getName()));
                            }else {
                                System.out.println("The new name is" + ColorfulConsole.printSuccessInfo(categoryName));
                                Message message = AdminDAO.getInstance().updateCategory(categoryName,categoryId);
                                if (message.getCode() == Message.MESSAGE_OK){
                                    System.out.println(ColorfulConsole.printSuccessInfo(message.getMessage()));
                                }else {
                                    System.out.println(ColorfulConsole.printFailInfo(message.getMessage()));
                                }
                            }
                        }else{
                            System.out.println(INPUT_ERROR);
                            return;
                        }
                    }catch (NumberFormatException e){
                        System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
                    }
                }
                default -> System.out.println(ColorfulConsole.printFailInfo(INPUT_ERROR));
            }
            LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
        }else {
            System.out.println(REFUSE_EXECUTE);
        }
    }

    public void dashboard(){
        if (UserRole.getInstance().isAdmin()){

            List<OrderHistory> orderHistories = LocalStorage.getInstance().getOrderHistories();
            int[] dashboardInfo = {0,0,0,0,0};
            for (OrderHistory history:orderHistories) {
                String orderStatus = history.getOrderStatus().name().toLowerCase();
                String orderMethod = history.getOrderMethod().name().toLowerCase();
                switch (orderStatus){
                    case "pending" -> dashboardInfo[0]++;
                    case "paid" -> dashboardInfo[1]++;
                    case "completed" -> dashboardInfo[2]++;
                }
                switch (orderMethod){
                    case "delivery" -> dashboardInfo[3]++;
                    case "pick_up" -> dashboardInfo[4]++;
                }
            }

            // Time now
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();

            // Print data
            System.out.println(" --------------------------------------------");
            System.out.println("      Welcome to Admin Dashboard");
            System.out.println(" --------------------------------------------");
            System.out.printf(" Current Time: %s%n", dateFormat.format(date));
            System.out.println(" ---------------------------------------------");
            System.out.printf(" Pending Order:          %d%n", dashboardInfo[0]);
            System.out.printf(" Paid Order:             %d%n", dashboardInfo[1]);
            System.out.printf(" Completed Order:        %d%n", dashboardInfo[2]);
            System.out.println(" --------------------------------------------");
            System.out.printf(" Pick-up Order:          %d%n", dashboardInfo[4]);
            System.out.printf(" Delivery Order:         %d%n", dashboardInfo[3]);
            System.out.println(" ___________________________________________");
            System.out.println(" admin add       \"Add new item or category;\"");
            System.out.println(" admin delete    \"Delete item or category\"");
            System.out.println(" admin update    \"Update item or category\"");
            System.out.println(" admin dashboard \"Show admin dashboard\"");

        }else {
            System.out.println(REFUSE_EXECUTE);
        }
    }

    public void logout(){
        if (UserRole.getInstance().isAdmin()){
            UserRole.getInstance().setRole(ROLE.CUSTOMER);
            System.out.println("Your are " + ROLE.CUSTOMER.name() + " now");
        }else {
            System.out.println(REFUSE_EXECUTE);
        }
    }

    private void printCategory(List<Category> categories){
        for (Category category:categories) {
            System.out.printf("%d. %s\n", category.getId(), category.getName());
        }
    }

    private boolean isOnCategory(List<Category> categories, int categoryId){
        for (Category category:categories) {
            if (category.getId() == categoryId){
                return true;
            }
        }
        return false;
    }

    private void printMenuItem(List<Item> items){
        for (Item item: items) {
            System.out.printf("%d: %s\n", item.getId(), item.getName());
        }
    }

    private boolean isOnMenuItem(List<Item> items, int itemId){
        for (Item item:items) {
            if (item.getId() == itemId){
                return true;
            }
        }
        return false;
    }
}
