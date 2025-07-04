package pl.coderslab.entity;

import java.sql.SQLException;

public class MainUser {
    public static void main(String[] args) {
        User newUser = new User("Sara", "sara@gmail.com", "sara123");
        UserDao user = new UserDao();


        try{
//            user.create(newUser);

            User read = user.read(1);
            System.out.println(read);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}