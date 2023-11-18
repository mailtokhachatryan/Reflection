package anotation;

import java.lang.reflect.Field;

public class Main {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        User user = new User("adsa");

        Class<User> userClass = User.class;
        Field age = userClass.getDeclaredField("age");
        age.setAccessible(true);
        age.setInt(user, 58);
        System.out.println(user.getAge());
    }
}
