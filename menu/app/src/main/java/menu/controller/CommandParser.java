package menu.controller;

import menu.auth.LocalStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommandParser {
    private static final String CHECK_COMMAND = "Input command error: Please check whether the parameter is correct.";

    private static CommandParser instance = null;
    private final Map<String, CommandHandler> commandMap;

    private CommandParser() {
        commandMap = new HashMap<>();
        commandMap.put("menu show", this::menuShow);
        commandMap.put("menu view", this::menuView);
        commandMap.put("order add", this::orderAdd);
        commandMap.put("order show", this::orderShow);
        commandMap.put("order pay", this::orderPay);
        commandMap.put("order remove", this::orderRemove);
        commandMap.put("order update", this::orderUpdate);
        commandMap.put("history show", this::historyShow);
        commandMap.put("history view", this::historyView);
        commandMap.put("sys role", this::sysRole);
        commandMap.put("sys login", this::sysLogin);
        commandMap.put("sys help", this::sysHelp);
        commandMap.put("sys reboot", this::sysReboot);
        commandMap.put("admin logout", this::adminLogout);
        commandMap.put("admin dashboard", this::adminDashboard);
        commandMap.put("admin add", this::adminAdd);
        commandMap.put("admin delete", this::adminDelete);
        commandMap.put("admin update", this::adminUpdate);
    }

    public static CommandParser getInstance() {
        if (instance == null) {
            instance = new CommandParser();
        }
        return instance;
    }

    public void execute(String input) {
        String[] parts = splitInput(input);
        if (parts != null) {
            String command = parts[0];
            String[] args = parts[1].split("\\s+");

            CommandHandler handler = commandMap.get(command);
            if (handler != null) {
                handler.handle(args);
            } else {
                System.out.println("Unknown command: " + command);
            }
        } else {
            System.out.println("Unknown command: " + input);
        }

    }

    private String[] splitInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2) {
            return new String[]{input.trim(), ""};
        } else if (parts.length == 3) {
            int lastSpaceIndex = input.lastIndexOf(" ");
            String command = input.substring(0, lastSpaceIndex).trim();
            String args = input.substring(lastSpaceIndex + 1).trim();
            return new String[]{command, args};
        } else {
            return null;
        }
    }

    private boolean isPureNumber(String str) {
//        return str.matches("^\\d+$");
        return str.matches("^[1-9]\\d*$");
    }

    private void processCommandWithArg(String[] args, Consumer<Integer> action) {
        if (isPureNumber(args[0])) {
            try {
                int itemId = Integer.parseInt(args[0]);
                action.accept(itemId);
            } catch (NumberFormatException e) {
                System.out.println("The string is not a valid integer.");
            }
        } else {
            System.out.println(CHECK_COMMAND);
        }
    }

    private void processCommandWithoutArg(String[] args, Runnable action) {
        if (args[0].isEmpty()){
            action.run();
        }else {
            System.out.println(CHECK_COMMAND);
        }
    }

    private void menuShow(String[] args) {
        processCommandWithoutArg(args, () -> MenuController.getInstance().showMenu());
    }
    private void menuView(String[] args) {
        processCommandWithArg(args, itemId -> MenuController.getInstance().viewMenuItem(itemId));
    }

    private void orderShow(String[] args){
        processCommandWithoutArg(args, () -> OrderController.getInstance().orderShow());
    }
    private void orderPay(String[] args) {
        processCommandWithoutArg(args, () ->  OrderController.getInstance().orderPay());
    }
    private void orderAdd(String[] args) {
        processCommandWithArg(args, itemId -> OrderController.getInstance().orderAdd(itemId));
    }
    private void orderRemove(String[] args) {
        processCommandWithArg(args, orderItemIndex -> OrderController.getInstance().orderRemove(orderItemIndex));
    }
    private void orderUpdate(String[] args) {
        processCommandWithArg(args, orderItemIndex -> OrderController.getInstance().orderUpdate(orderItemIndex));
    }

    private void historyShow(String[] args) {
        processCommandWithoutArg(args,() -> HistoryController.getInstance().showHistory());
    }

    private void historyView(String[] args) {
        processCommandWithArg(args,orderId -> HistoryController.getInstance().viewHistory(orderId));
    }

    private void sysRole(String[] args) {
        processCommandWithoutArg(args, ()-> SystemController.getInstance().getRole());
    }

    private void sysLogin(String[] args) {
        processCommandWithoutArg(args, ()-> SystemController.getInstance().login());
    }

    private void sysHelp(String[] args) {
        processCommandWithoutArg(args, ()-> SystemController.getInstance().getHelp());

    }

    private void sysReboot(String[] args){
        LocalStorage.getInstance().loadData(LocalStorage.LOAD_DATA_ALL);
    }

    private void adminLogout(String[] args) {
        processCommandWithoutArg(args, () -> AdminController.getInstance().logout());
    }

    private void adminDashboard(String[] args) {
        processCommandWithoutArg(args, () -> AdminController.getInstance().dashboard());
    }

    private void adminAdd(String[] args) {
        processCommandWithoutArg(args, () -> AdminController.getInstance().add());
    }

    private void adminDelete(String[] args) {
        processCommandWithoutArg(args, () -> AdminController.getInstance().delete());
    }

    private void adminUpdate(String[] args) {
        processCommandWithoutArg(args, () -> AdminController.getInstance().update());
    }

}
