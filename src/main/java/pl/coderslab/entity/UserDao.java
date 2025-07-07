package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";

    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

    private static final String READ_USER_QUERY =
            "SELECT id, username, email, password FROM users where id = ?";

    private static final String FIND_ALL_USERS_QUERY =
            "SELECT id, username, email, password FROM users";

    private static final String REMOVE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";

    private static final String CHECK_USER_ID_QUERY=
            "SELECT 1 FROM users WHERE id = ?";

    public void update(User user) throws SQLException {
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            System.out.println("User name is not valid");
            return;
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty() || user.getPassword().length() < 6) {
            System.out.println("Password is not valid");
            return;
        }
        if (user.getEmail() == null || !user.getEmail().contains("mail.com")) {
            System.out.println("Email is not valid");
            return;
        }

        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(CHECK_USER_ID_QUERY);
            checkStmt.setInt(1, user.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("User with id " + user.getId() + " not exist.");
                return;
            }
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.setInt(4, user.getId());
            statement.executeUpdate();
            System.out.println("User has been changed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(CHECK_USER_ID_QUERY);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("User with id " + id + " not exist.");
                return;
            }
            PreparedStatement statement = conn.prepareStatement(REMOVE_USER_QUERY);
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("User has been removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User[] findAll() throws SQLException {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(FIND_ALL_USERS_QUERY);
            ResultSet resultSet = statement.executeQuery();
            User[] users = new User[0];
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                users = addToArray(user, users);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User create(User user) throws SQLException{
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            System.out.println("User name is not valid");
            return null;
        }
        if (user.getEmail() == null || !user.getEmail().contains("mail.com")) {
            System.out.println("Email is not valid");
            return null;
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty() || user.getPassword().length() < 6) {
            System.out.println("Password is not valid");
            return null;
        }
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            while (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                System.out.println("User has been added.");
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User read(int id) throws SQLException {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(CHECK_USER_ID_QUERY);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("User with id " + id + " not exist.");
                return null;
            }
            PreparedStatement statement =
                    conn.prepareStatement(READ_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            User user = new User();
            while (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private User[] addToArray(User u, User[]users){
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }


    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}