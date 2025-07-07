package pl.coderslab.entity;

import java.sql.SQLException;

public class MainUser {
    public static void main(String[] args) {
        User newUser = new User("magda", "magda@mail.com", "magda123");
        UserDao user = new UserDao();

        User userToUpdate = new User("bartosz", "batrosz@gmail.com", "bartosz123");
        userToUpdate.setId(11);

        try{
//            user.update(userToUpdate);

//            user.create(newUser);

//            user.delete(12);

//            User read = user.read(1);
//            System.out.println(read);

            User[] findAll = user.findAll();
            for (User u : findAll) {
                System.out.println(u);
            };
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}