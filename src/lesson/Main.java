package lesson;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        Human human = new Human("asdasd");

        Class<Human> humanClass = Human.class;

        Constructor<?>[] constructors = humanClass.getConstructors();

        Method[] methods = humanClass.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method);
            System.out.println(method.getName());
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                System.out.println(parameterType.getName());
            }
            System.out.println(method.getReturnType());
        }

        try {
            Field name = humanClass.getDeclaredField("name");
            System.out.println(name);

            name.setAccessible(true);

            Class<?> declaringClass = name.getDeclaringClass();

            System.out.println(declaringClass);

            Object o = name.get(human);
            System.out.println(o);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }
}