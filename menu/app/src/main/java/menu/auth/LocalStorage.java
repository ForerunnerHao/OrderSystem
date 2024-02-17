package menu.auth;

import menu.dao.AdminDAO;
import menu.dao.MenuDAO;
import menu.dao.OrderHistoryDAO;
import menu.pojo.Category;
import menu.pojo.Item;
import menu.pojo.Order;
import menu.pojo.OrderHistory;
import menu.utils.ColorfulConsole;

import java.util.List;

public class LocalStorage {
    public static final String LOAD_DATA_MENU = "menu";
    public static final String LOAD_DATA_ORDER_HISTORY = "order";
    public static final String LOAD_DATA_CATEGORY = "category";
    public static final String LOAD_DATA_ALL = "all";
    private List<Item> menuItems;
    private List<OrderHistory> orderHistories;
    private  List<Category> categories;
    private Order userOrder;
    private static LocalStorage INSTANCE = null;

    private LocalStorage() {
    }

    public static LocalStorage getInstance() {
        if (INSTANCE == null){
            INSTANCE = new LocalStorage();
        }
        return INSTANCE;
    }

    //menuItems
    public List<Item> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<Item> menuItems) {
        this.menuItems = menuItems;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    //userOrder
    public Order getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(Order userOrder) {
        this.userOrder = userOrder;
    }

    //orderHistories
    public List<OrderHistory> getOrderHistories(){
        return this.orderHistories;
    }

    public void setOrderHistories(List<OrderHistory> orderHistories){
        this.orderHistories = orderHistories;
    }

    public void loadData(String arg) {
        System.out.println(ColorfulConsole.ANSI_BLUE + "Loading data Please wait second..." + ColorfulConsole.ANSI_RESET);

        boolean isMenuLoaded = true;
        boolean isOrderLoaded = true;
        boolean isCategoryLoaded = true;

        switch (arg) {
            case LOAD_DATA_MENU -> isMenuLoaded = loadMenuData();
            case LOAD_DATA_ORDER_HISTORY -> isOrderLoaded = loadOrderData();
            case LOAD_DATA_CATEGORY -> isCategoryLoaded = loadCategoryData();
            case LOAD_DATA_ALL -> {
                isMenuLoaded = loadMenuData();
                isOrderLoaded = loadOrderData();
                isCategoryLoaded = loadCategoryData();
            }
            default -> {}
        }

        if (!isMenuLoaded || !isOrderLoaded || !isCategoryLoaded) {
            System.out.println(ColorfulConsole.printFailInfo("Data load fail, please try command 'sys reboot' to refresh data."));
        } else {
            System.out.println(ColorfulConsole.printSuccessInfo("System load data successfully."));
        }
    }

    private boolean loadMenuData() {
        System.out.println("Load menu item...");
        LocalStorage.getInstance().setMenuItems(MenuDAO.getInstant().getMenu());
        return LocalStorage.getInstance().getMenuItems() != null;
    }

    private boolean loadOrderData() {
        System.out.println("Load order history...");
        LocalStorage.getInstance().setOrderHistories(OrderHistoryDAO.getInstance().getHistoryShow());
        return LocalStorage.getInstance().getOrderHistories() != null;
    }

    private boolean loadCategoryData() {
        System.out.println("Load item category...");
        LocalStorage.getInstance().setCategories(AdminDAO.getInstance().getCategory());
        return LocalStorage.getInstance().getMenuItems() != null;
    }

    public void cleanData(){
        INSTANCE = null;
    }
}
