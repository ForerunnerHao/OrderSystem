package menu;

import menu.auth.LocalStorage;
import menu.auth.UserRole;
import menu.controller.AdminController;
import menu.controller.CommandParser;
import menu.controller.SystemController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SystemTest {
    @BeforeAll
    static void loadData(){
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
    }
    @Test
    void sysHelpTest(){
        // print system help information
        CommandParser.getInstance().execute("sys help");

        // invalid input ('sys  help')
        CommandParser.getInstance().execute("sys  help");

        // invalid input ('sys help ')
        CommandParser.getInstance().execute("sys help ");

        // invalid input ('sys help 111')
        CommandParser.getInstance().execute("sys help 111");

        // invalid input ('sys help 12a')
        CommandParser.getInstance().execute("sys help 12a");

        // invalid input ('sys help a#@!')
        CommandParser.getInstance().execute("sys help a#@!");
    }

    private final static String USERNAME = "admin\n";
    private final static String PASSWORD = "111111\n";
    public void setUpLoginInput(String username, String password) {
        String simulatedUserInput = username + password;
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        App.setScanner(mockScanner);
    }

    @Test
    void sysLoginTest() {

        // Correct username and password: Customer -> Admin
        setUpLoginInput(USERNAME, PASSWORD);
        SystemController.getInstance().login();
        assertTrue(UserRole.getInstance().isAdmin());

        //Login again Admin
        CommandParser.getInstance().execute("sys login");
        assertTrue(UserRole.getInstance().isAdmin());

        //Logout Admin -> Customer
        AdminController.getInstance().logout();
        assertFalse(UserRole.getInstance().isAdmin());

        // Incorrect username and password: Customer -> Customer
        setUpLoginInput("incorrect_username\n", "incorrect_password\n");
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        // Incorrect password
        setUpLoginInput(USERNAME, "incorrect_password\n");
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        // Incorrect username
        setUpLoginInput("incorrect_username\n", PASSWORD);
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        // Empty username and password
        setUpLoginInput("\n", "\n");
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        //Empty username
        setUpLoginInput("\n", PASSWORD);
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        //Empty password
        setUpLoginInput(USERNAME, "\n");
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        //Null password
        setUpLoginInput(USERNAME, null);
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());

        //Null username
        setUpLoginInput(null, PASSWORD);
        SystemController.getInstance().login();
        assertFalse(UserRole.getInstance().isAdmin());
    }

    @Test
    void sysRebootTest(){
        // reboot system reload all data from database
        CommandParser.getInstance().execute("sys reboot");

        // reload all menu items
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_MENU);

        //reload all order history
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ORDER_HISTORY);

        //reload all category
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_CATEGORY);
    }

    @Test
    void sysGetRoleTest(){
        SystemController.getInstance().getRole();
        CommandParser.getInstance().execute("sys role");
    }

}

