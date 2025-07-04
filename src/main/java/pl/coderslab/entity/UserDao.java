package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";

    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

    private static final String READ_USER_QUERY =
            "SELECT username FROM users where id = ?";

    private static final String READ_ALL_USERS_QUERY =
            "SELECT username FROM users";

    private static final String REMOVE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";


    public User create(User user) throws SQLException{
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }

    public User read(int id) throws SQLException{
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement =
                    conn.prepareStatement(READ_USER_QUERY,  Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            ResultSet resultSet = statement.getGeneratedKeys();
            User user= new User();
            while(resultSet.next()){
                user.setUserName(resultSet.getNString("username"));
                user.setEmail(resultSet.getNString("email"));
                user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }


    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}