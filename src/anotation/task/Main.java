package anotation.task;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        Car car = new Car("Toyota", "Camry", "Red");

        String format = generate(car);

        System.out.println(format);
    }

    private static String generate(Car car) throws IllegalAccessException, InvocationTargetException {
        Class<? extends Car> carClass = car.getClass();
        Table annotation = carClass.getAnnotation(Table.class);
        String scheme = annotation.scheme();
        String table = annotation.table();

        Map<String, Method> hashMap = new HashMap<>();

        Method[] declaredMethods = carClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            String methodName = declaredMethod.getName();
            if (methodName.startsWith("get")
                    && methodName.length() > 3
                    && declaredMethod.getParameterTypes().length == 0) {
                //getBrand -> brand
                String field = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                hashMap.put(field, declaredMethod);
            }
        }

        List<String> columnNames = new ArrayList<>();
        List<String> columnValues = new ArrayList<>();


        Field[] declaredFields = Arrays.stream(carClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Column.class)).toList().toArray(new Field[0]);
        for (Field declaredField : declaredFields) {
            Column column = declaredField.getAnnotation(Column.class);
            columnNames.add(column.value().isEmpty() ? declaredField.getName() : column.value());
            columnValues.add((String) hashMap.get(declaredField.getName()).invoke(car));
        }

        String collectTables = String.join(", ", columnNames);

        List<String> collect = columnValues.stream().map(s -> "'" + s + "'").collect(Collectors.toList());

        String collectedValues = String.join(", ", collect);

        return String.format("INSERT INTO %s.%s (%s) VALUES (%s);", scheme, table, collectTables, collectedValues);
    }


    private static String generateInsert(Car car) {
        String template = "INSERT INTO %s.%s (%s) VALUES (%s);";
        Table table = car.getClass().getAnnotation(Table.class);
        Field[] fields = car.getClass().getDeclaredFields();

        String fieldNames = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Column.class))
                .sorted(Comparator.comparing(Field::getName))
                .map(field -> field.getAnnotation(Column.class))
                .map(Column::value)
                .collect(Collectors.joining(", "));

        String fieldValues = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Column.class))
                .sorted(Comparator.comparing(Field::getName))
                .map(field -> getMethodName(car, field))
                .map(method -> {
                    try {
                        return method.invoke(car);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(value -> "'" + value + "'")
                .collect(Collectors.joining(", "));

        return String.format(template, table.scheme(), table.table(), fieldNames, fieldValues);
    }

    public static Method getMethodName(Car car, Field field) {
        String name = field.getName();
        try {
            return car.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
