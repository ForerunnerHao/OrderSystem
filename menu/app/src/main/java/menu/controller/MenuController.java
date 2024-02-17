package menu.controller;

import menu.auth.LocalStorage;
import menu.pojo.Item;
import menu.utils.ColorfulConsole;
import menu.utils.FindListElement;

import java.util.List;

public class MenuController {

    private final static String STR_LINE = "---------------------------------------------";
    private static final MenuController INSTANCE = new MenuController();

    private MenuController() {
    }

    public static MenuController getInstance() {
        return INSTANCE;
    }

    public void showMenu() {
        List<Item> menuItems = LocalStorage.getInstance().getMenuItems();

        // no data on the menu
        if (menuItems == null){
            System.out.println(ColorfulConsole.printFailInfo("System data is not loaded correctly, please try \"sys reboot\" command to reload the data."));
            return;
        }

        System.out.println(STR_LINE);
        System.out.println("\t\t\t\tNo.514 Menu");
        System.out.println(STR_LINE);
        System.out.println("ID\tName\t\t\t\t\t\tPrice");

        String currentCategory = null;
        for (Item item : menuItems) {
            if (!item.getCategoryName().equals(currentCategory)) {
                currentCategory = item.getCategoryName();
                System.out.println(STR_LINE);
                System.out.println("\t\t\t\t" + ColorfulConsole.ANSI_GREEN + "*" + currentCategory.toUpperCase() + "*" + ColorfulConsole.ANSI_RESET);
                System.out.println(STR_LINE);
            }
            System.out.printf("%d\t%-25s\t$%.2f\n", item.getId(), item.getName(), item.getPrice());
        }
        System.out.println(STR_LINE + "\n");
    }

    public void viewMenuItem(int itemId) {
        List<Item> items = LocalStorage.getInstance().getMenuItems();
        if (items == null){
            System.out.println(ColorfulConsole.printFailInfo("System data is not loaded correctly, please try \"sys reboot\" command to reload the data."));
            return;
        }
        Item item = FindListElement.getElement(items, item1 -> item1.getId() == itemId);

        if (item != null) {
            System.out.println(STR_LINE);
            System.out.println(item);
            System.out.println(STR_LINE);
        } else {
            System.out.println("System can not find the input item ID, Please try another id.");
        }
    }
}
