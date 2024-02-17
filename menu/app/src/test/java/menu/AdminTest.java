package menu;

import menu.auth.LocalStorage;
import menu.auth.ROLE;
import menu.auth.UserRole;
import menu.controller.AdminController;
import menu.controller.CommandParser;
import menu.controller.SystemController;
import menu.pojo.Category;
import menu.pojo.Item;
import menu.pojo.Message;
import menu.utils.FindListElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminTest {
    private static final String STR_LINE = "---------------------------------------------------------------------------------";
    private static final String REFUSE_EXECUTE = "Permission denied";
    private static int testNo = 0;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    public void restoreStreams() {
        System.setOut(originalOut);
    }
    public void setUserInput(String simulatedUserInput) {
        simulatedUserInput += "\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        App.setScanner(mockScanner);
    }

    private void printTestInfo(int code, int index, String message){

        if (code == Message.MESSAGE_OK){
            System.out.println(STR_LINE + "\nadminAddTest["+ index +"]: Pass {" + message + "}\n" + STR_LINE);
        }else {
            System.out.println(STR_LINE + "\nadminAddTest["+ index +"]: Failed\n" + STR_LINE);
        }
    }
    @BeforeAll
    static void loadData(){
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
    }

    @Test
    void dashBoardTest(){

        //print dashboard
        AdminController.getInstance().dashboard();

        //Command
        CommandParser.getInstance().execute("admin dashboard");
    }

    private final List<Category> createCategoryId = new ArrayList<>();
    private final List<Item> createMenuItemId = new ArrayList<>();
    @Test
    @Order(1)
    void adminAddTest(){

        //0. user not admin - add new category
        setUpStreams();
        setUserInput("2\nuser not admin");
        AdminController.getInstance().add();
        assertEquals(REFUSE_EXECUTE, outContent.toString().trim());
        restoreStreams();
        printTestInfo(Message.MESSAGE_OK, testNo++, "user not admin - add new category");

        //1. user is admin - add new category
        setUpStreams();
        UserRole.getInstance().setRole(ROLE.ADMIN);
        setUserInput("2\n1. user is admin - add new category");
        AdminController.getInstance().add();
        restoreStreams();
        List<Category> categories = LocalStorage.getInstance().getCategories();
        if (!categories.isEmpty()){
            Category category = categories.get(categories.size()-1);
            if (category != null){
                assertEquals("1. user is admin - add new category", category.getName());
                createCategoryId.add(category);
                printTestInfo(Message.MESSAGE_OK, testNo++, "add new category: {id:" + category.getId() + " name:" + category.getName() + "}");
            }else {
                printTestInfo(Message.MESSAGE_FAIL, testNo++, "user is admin - add new category");
            }
        }else {
            printTestInfo(Message.MESSAGE_FAIL, testNo++, "user is admin - add new category");
        }

        //2. user is admin - add new category with invalid input ("a")
        setUpStreams();
        setUserInput("a\nuser is admin - choose change option with invalid input (\"a\")");
        int oldNumber = categories.size();
        AdminController.getInstance().add();
        restoreStreams();
        assertEquals(oldNumber, categories.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "user is admin - choose change option with invalid input (\"a\")");

        //3. user is admin - add new category with invalid input ("")
        setUserInput("\nuser is admin - choose change option with invalid input (\"\")");
        setUpStreams();
        oldNumber = categories.size();
        AdminController.getInstance().add();
        restoreStreams();
        assertEquals(oldNumber, categories.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "user is admin - choose change option with invalid input (\"\")");

        //4. user is admin - choose change option with invalid input (" ")
        setUserInput(" \nchoose change option with invalid input (\" \")");
        setUpStreams();
        oldNumber = categories.size();
        AdminController.getInstance().add();
        restoreStreams();
        assertEquals(oldNumber, categories.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "user is admin - choose change option with invalid input (\" \")");

        //5. user is admin - add new category with invalid input (" ")
        setUserInput("2\n ");
        setUpStreams();
        oldNumber = categories.size();
        AdminController.getInstance().add();
        restoreStreams();
        assertEquals(oldNumber, categories.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "user is admin - add new category with invalid input (\" \")");

        //6. user is admin - add new category with invalid input ("")
        setUserInput("2\n");
        setUpStreams();
        oldNumber = categories.size();
        AdminController.getInstance().add();
        restoreStreams();
        assertEquals(oldNumber, categories.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "user is admin - add new category with invalid input (\"\")");

        //7. user no admin - add new menu item
        List<Item> items = LocalStorage.getInstance().getMenuItems();
        if (items.isEmpty()){
            printTestInfo(Message.MESSAGE_FAIL, testNo++, "user no admin - add new menu item");
        }
        AdminController.getInstance().logout();
        setUpStreams();
        int oldMenuItemNumber = items.size();
        setUserInput("1\nuser no admin - add new menu item\n110\n99\n999\nhaha\nhaha");
        AdminController.getInstance().add();

        restoreStreams();
        assertEquals(oldMenuItemNumber,items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "user no admin - add new menu item");


        //8. user is admin - add new menu item
        UserRole.getInstance().setRole(ROLE.ADMIN);
        oldMenuItemNumber = items.size();
        setUserInput("1\nTest[8]\n1\n16.8\n2\ntest1\ntest1");
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        Item newItem = FindListElement.getElement(items, item -> item.getName().equals("Test[8]"));
        assertEquals(oldMenuItemNumber+1, items.size());
        createMenuItemId.add(newItem);
        printTestInfo(Message.MESSAGE_OK, testNo++, "user is admin - add new menu item: {id: "+ newItem.getId() +" name: "+ newItem.getName() +"}");


        //9. user is admin - add new menu item - category - with invalid input (" ")
        setUserInput("1\nTest[9]\n \n16.8\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - category - with invalid input (\" \")");

        //10. user is admin - add new menu item - category - with invalid input (null)
        setUserInput("1\nTest[10]\n\n16.8\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - category - with invalid input (null)");

        //11. user is admin - add new menu item - category - with invalid input (999999)
        setUserInput("1\nTest[11]\n999999\n16.8\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - category - with invalid input (999999)");

        //12. user is admin - add new menu item - category - with invalid input (test12)
        setUserInput("1\nTest[12]\ntest12\n16.8\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - category - with invalid input (test12)");

        //13. user is admin - add new menu item - category - with invalid input (56789.6425)
        setUserInput("1\nTest[13]\n56789.6425\n16.8\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - category - with invalid input (56789.6425)");

        //14. user is admin - add new menu item - price - with invalid input (n092)
        setUserInput("1\nTest["+ testNo +"]\n1\nn092\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - price - with invalid input (n092)");

        //15. user is admin - add new menu item - price - with invalid input (number)
        setUserInput("1\nTest["+ testNo +"]\n1\nnumber\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - price - with invalid input (number)");

        //16. user is admin - add new menu item - price - with invalid input ( )
        setUserInput("1\nTest["+ testNo +"]\n1\n \n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - price - with invalid input ( )");

        //17. user is admin - add new menu item - price - with invalid input null
        setUserInput("1\nTest["+ testNo +"]\n1\n\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - price - with invalid input (null)");

        //17. user is admin - add new menu item - weight - with invalid input null
        setUserInput("1\nTest["+ testNo +"]\n1\n\n2\ntest1\ntest1");
        oldMenuItemNumber = items.size();
        setUpStreams();
        AdminController.getInstance().add();
        items = LocalStorage.getInstance().getMenuItems();
        restoreStreams();
        assertEquals(oldMenuItemNumber, items.size());
        printTestInfo(Message.MESSAGE_OK, testNo++, "add new menu item - price - with invalid input (null)");

    }

    @Test
    @Order(2)
    void adminLogoutTest(){

        // user
        SystemController.getInstance().getRole();
        AdminController.getInstance().logout();

        UserRole.getInstance().setRole(ROLE.ADMIN);
        SystemController.getInstance().getRole();

        AdminController.getInstance().logout();
    }
    int newCategoryId;
    int newMenuItemId;
    @Test
    @Order(3)
    void adminUpdateTest() {
        newCategoryId = FindListElement.getElement(LocalStorage.getInstance().getCategories(), category -> category.getName().equals("1. user is admin - add new category")).getId();
        newMenuItemId = FindListElement.getElement(LocalStorage.getInstance().getMenuItems(),item -> item.getName().equals("Test[8]")).getId();

        // user not admin - update new category
        setUserInput("1\nnew category\n");
        AdminController.getInstance().update();

        // user is admin - add update category
        UserRole.getInstance().setRole(ROLE.ADMIN);
        setUserInput("2\n" + newCategoryId + "\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput(" \n3\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("\n3\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("ci\n3\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("-5\n3\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("2\n1465895\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("2\n \nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("2\n\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("2\nlll\nupdate new category");
        AdminController.getInstance().update();

        // user is admin - update new category with invalid input
        setUserInput("2\n-3\nupdate new category");
        AdminController.getInstance().update();

        // user not admin - update new item
        AdminController.getInstance().logout();
        setUserInput("1\n" + newMenuItemId + "\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item - name
        UserRole.getInstance().setRole(ROLE.ADMIN);
        setUserInput("1\n" + newMenuItemId + "\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name with invalid input
        setUserInput("1\n2999\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name with invalid input
        setUserInput("1\n*-\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name with invalid input
        setUserInput("1\na\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name with invalid input
        setUserInput("1\n \nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name with invalid input
        setUserInput("1\n\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name
        setUserInput("1\n" + newMenuItemId + "\nupdate new item name\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - update new item name with default input
        setUserInput("1\n" + newMenuItemId + "\n\n8\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new item with invalid input
        setUserInput("1\n" + newMenuItemId + "\n \n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new category with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n5\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new category with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new category with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n \n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new category with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n9999\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new category with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\nnull\n56\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new price
        setUserInput("1\n" + newMenuItemId + "\n\n\n9999.9999\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new price with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new price with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\naaa\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new price with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n-999.99\n95\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new weight
        setUserInput("1\n" + newMenuItemId + "\n\n\n\n999.999\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new weight with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n\n\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new weight with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n\n \ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new weight with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n\naaa\ngood\nsausage");
        AdminController.getInstance().update();

        // user is admin - add new weight with invalid input
        setUserInput("1\n" + newMenuItemId + "\n\n\n\n-999.888\ngood\nsausage");
        AdminController.getInstance().update();
    }

    @Test
    @Order(4)
    void adminDeleteTest(){
//        int newCategoryId = FindListElement.getElement(LocalStorage.getInstance().getCategories(), category -> category.getName().equals("update new category")).getId();
//        int newMenuItemId = FindListElement.getElement(LocalStorage.getInstance().getMenuItems(),item -> item.getName().equals("update new item name")).getId();
        // user not admin - delete new category
        setUserInput("2\n2");
        AdminController.getInstance().delete();

        // user is admin - delete new category
        UserRole.getInstance().setRole(ROLE.ADMIN);
        setUserInput("2\n" + newCategoryId);
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("0\n"+ newCategoryId);
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput(" \n" + newCategoryId);
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("\n" + newCategoryId);
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("-1\n" + newCategoryId);
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("aaa\n" + newCategoryId);
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("2\n9999");
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("2\n-1");
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("2\naa");
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("2\n ");
        AdminController.getInstance().delete();

        // user is admin - delete new category
        setUserInput("2\n");
        AdminController.getInstance().delete();

        // user not admin - delete new item
        AdminController.getInstance().logout();
        setUserInput("1\n" + newMenuItemId);
        AdminController.getInstance().delete();

        // user is admin - delete new item
        UserRole.getInstance().setRole(ROLE.ADMIN);
        setUserInput("1\n" + newMenuItemId);
        AdminController.getInstance().delete();

        // user is admin - delete item
        setUserInput("1\n555555");
        AdminController.getInstance().delete();

        // user is admin - delete item
        setUserInput("1\n-1");
        AdminController.getInstance().delete();

        // user is admin - delete item
        setUserInput("1\naaa");
        AdminController.getInstance().delete();

        // user is admin - delete item
        setUserInput("1\n**");
        AdminController.getInstance().delete();

        // user is admin - delete item
        setUserInput("1\n");
        AdminController.getInstance().delete();

        // user is admin - delete item
        setUserInput("1\n ");
        AdminController.getInstance().delete();

    }
}
