package menu.auth;

public class UserRole {
    ROLE role;
    private static final UserRole INSTANCE = new UserRole();

    private UserRole() {
        role = ROLE.CUSTOMER;
    }

    public static UserRole getInstance() {
        return INSTANCE;
    }

    public boolean isAdmin(){
        return role == ROLE.ADMIN;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }
}
