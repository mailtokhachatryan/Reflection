package oop;

public class Student extends Human {

    private Car car;

    public Student() {
    }

    public Student(Car car) {
        this.car = car;
    }

    @Override
    public void method() {
        super.method();
    }
}
