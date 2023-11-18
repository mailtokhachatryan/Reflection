package anotation.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        User user = new User("Aram", "Badalyan");

        System.out.println(user);
//        Class<? extends User> aClass = user.getClass();
//        Field[] declaredFields = aClass.getDeclaredFields();
//
//        List<Field> fieldList = new ArrayList<>();
//
//        for (Field declaredField : declaredFields) {
//            if (declaredField.isAnnotationPresent(Value.class)) {
//                fieldList.add(declaredField);
//            }
//        }
//
//        for (Field field : fieldList) {
//            Value declaredAnnotation = field.getDeclaredAnnotation(Value.class);
//            RoleEnum value = declaredAnnotation.value();
//            field.setAccessible(true);
//            try {
//                field.set(user, value.name());
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//            field.setAccessible(false);
//        }


//        Arrays.stream(user.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(Value.class))
//                .forEach(field -> {
//                    field.setAccessible(true);
//                    try {
//                        field.set(user, field.getDeclaredAnnotation(Value.class).value().name());
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    }
//                    field.setAccessible(false);
//                });

        System.out.println(user.getRole());

        setAge(user);
        System.out.println(user);

    }


    public static void setAge(User user) throws NoSuchFieldException, IllegalAccessException {

        if (user.getClass().isAnnotationPresent(ClassLevelAnnotation.class)) {
            Class<User> userClass = User.class;
            Field[] declaredFields = userClass.getDeclaredFields();

            List<String> fieldNames = Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(SetValue.class))
                    .map(Field::getName)
                    .toList();

            Arrays.stream(userClass.getDeclaredMethods())
                    .filter(method -> method.getName().startsWith("set"))
                    .peek(method -> {
                        fieldNames.forEach(s ->
                        {
                            String s1 = "set" + s.substring(0, 1).toUpperCase() + s.substring(1);
                            if (method.getName().equals(s1)) {
                                try {
                                    method.invoke(user, 5);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    })
                    .collect(Collectors.toList());

        }

    }


}

