package com.example.wheretostudy;

public class Classroom {
    private long available;
    private long capacity;
    private String name;


    public Classroom() {
    }

    public Classroom(long available, long capacity, String name) {
        this.available = available;
        this.capacity = capacity;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "name='" + name + '\'' +
                ", available=" + available +
                ", capacity=" + capacity +
                '}';
    }
}
