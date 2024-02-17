package menu.dao;

import menu.pojo.Admin;
import menu.utils.ColorfulConsole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemDAO {
    private static final SystemDAO INSTANCE = new SystemDAO();
    private SystemDAO() {}
    public static SystemDAO getInstance() {
        return INSTANCE;
    }

    public boolean getUser(Admin admin){
        boolean isValid = false;
        try ( Connection connection = MySQLOpenHelper.getInstance().getConnection()){

            String sql = "SELECT * FROM `admin` WHERE `username` = ? AND `password` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
            preparedStatement.setString(1, admin.getUsername());
            preparedStatement.setString(2, admin.getPassword());

            ResultSet resultSet  = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                isValid = true;
            }
        }
        catch(SQLException e){
            System.out.println("Error while validating user");
            System.out.println(ColorfulConsole.printFailInfo(e.getMessage()));
        }

        return isValid;
    }
}
