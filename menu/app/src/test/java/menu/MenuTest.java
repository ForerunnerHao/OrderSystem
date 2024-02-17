package menu;

import menu.auth.LocalStorage;
import menu.controller.CommandParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuTest {
    private static final int ON_MENU_ITEM_ID = 17;
    private static final int NOT_ON_MENU_ITEM_ID = 199;

    @BeforeAll
    static void getData(){
        // Storing data from database to local
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_MENU);
    }

    @Test
    void menuItemCheck(){

        // Check the number of menu item
//        List<Item> items = LocalStorage.getInstance().getMenuItems();
//        assertEquals(19, items.size());

        // Show on screen
        CommandParser.getInstance().execute("menu show");

        // Delete local data show again
        LocalStorage.getInstance().setMenuItems(null);
        CommandParser.getInstance().execute("menu show");

        // Reload data and test again
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_MENU);
        CommandParser.getInstance().execute("menu show");
    }

    @Test
    void commandParserMenuViewTest(){

        // get detail of menu item (on menu)
        CommandParser.getInstance().execute(
                "menu view " + ON_MENU_ITEM_ID
        );

        // get detail of menu item (not on menu)
        CommandParser.getInstance().execute(
                "menu view " + NOT_ON_MENU_ITEM_ID
        );

        // get detail of menu item (invalid input: '017')
        CommandParser.getInstance().execute(
                "menu view 0" + ON_MENU_ITEM_ID
        );

        // get detail of menu item (invalid input: 'a17')
        CommandParser.getInstance().execute(
                "menu view a" + ON_MENU_ITEM_ID
        );

        // get detail of menu item (invalid input: ' ')
        CommandParser.getInstance().execute(
                "menu view " + " "
        );

        // get detail of menu item (invalid input: 'aaa')
        CommandParser.getInstance().execute(
                "menu view " + "aaa"
        );

        // Delete local data show again (on menu)
        LocalStorage.getInstance().setMenuItems(null);
        CommandParser.getInstance().execute(
                "menu view " + ON_MENU_ITEM_ID
        );

        // Reload data and test again (on menu)
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_MENU);
        CommandParser.getInstance().execute(
                "menu view " + ON_MENU_ITEM_ID
        );
    }

}
