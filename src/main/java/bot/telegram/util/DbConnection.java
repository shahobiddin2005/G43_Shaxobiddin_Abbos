package bot.telegram.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbConnection {
    private final  String databaseURL = "jdbc:postgresql://127.0.0.1:5432/new_project";
    private Connection connection = null;

    public  void test() {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "root123");
        try {
            connection = DriverManager.getConnection(databaseURL, props);

//            Statement statement = connection.createStatement();
//            statement.execute("update users set password = 'parol'");
//            PreparedStatement preparedStatement = connection.prepareStatement("insert into users(name,email,password,enabled) values(?,?,?,?)");
//            preparedStatement.setString(1,"ali");
//            preparedStatement.setString(2,"ali@gmail.com");
//            preparedStatement.setString(3,"root123");
//            preparedStatement.setBoolean(4,true);
//            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static DbConnection instance;

    public static DbConnection getInstance() {
        if (instance == null) {
            instance = new DbConnection();
            instance.test();
        }
        return instance;
    }
}
