package menu.pojo;

public class Message {
    public static final int MESSAGE_OK = 200;
    public static final int MESSAGE_FAIL = 404;

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public Message(){
        code = MESSAGE_FAIL;
        message = "";
    }

    public Message(String msg) {
        code = MESSAGE_FAIL;
        message = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



