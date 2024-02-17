package menu;

import menu.auth.LocalStorage;
import menu.controller.CommandParser;
import menu.controller.HistoryController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class OrderHistoryTest {

    private static final int TEST_ORDER_ID_ON_HISTORY = 115;
    private static final int TEST_ORDER_ID_NOT_ON_HISTORY = 9999;
    @BeforeAll
    static void loadData(){
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
    }

    @Test
    void historyShowTest(){
        // printout all order history
        HistoryController.getInstance().showHistory();

        //Command
        CommandParser.getInstance().execute("history show");
    }

    @Test
    void historyViewTest(){

        // Printout detail of specify order id on history
        HistoryController.getInstance().viewHistory(TEST_ORDER_ID_ON_HISTORY);

        // Not on history
        HistoryController.getInstance().viewHistory(TEST_ORDER_ID_NOT_ON_HISTORY);

        // Command
        CommandParser.getInstance().execute("history view "+ TEST_ORDER_ID_ON_HISTORY);

        // Command invalid input ('order view ')
        CommandParser.getInstance().execute("history view ");

        // Command invalid input ('order view 121')
        CommandParser.getInstance().execute("history view " + TEST_ORDER_ID_ON_HISTORY + " ");

        // Command invalid input ('order view 01')
        CommandParser.getInstance().execute("history view 01");

        // Command invalid input ('order view 1a')
        CommandParser.getInstance().execute("history view 1a");

        // Command invalid input ('order view aa')
        CommandParser.getInstance().execute("history view aa");

        // Command invalid input ('order view a121')
        CommandParser.getInstance().execute("history view a121");

    }
}
