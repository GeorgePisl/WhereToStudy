package com.example.wheretostudy;

public class Classroom {
    private long available;
    private long capacity;
    private String name;
    Boolean air, sock, lib, vm;

    public Classroom(long available, long capacity, String name, Boolean air, Boolean sock, Boolean lib, Boolean vm) {
        this.available = available;
        this.capacity = capacity;
        this.name = name;
        this.air = air;
        this.sock = sock;
        this.lib = lib;
        this.vm = vm;
    }

    public Boolean getAir() {
        return air;
    }

    public void setAir(Boolean air) {
        this.air = air;
    }

    public Boolean getSock() {
        return sock;
    }

    public void setSock(Boolean sock) {
        this.sock = sock;
    }

    public Boolean getLib() {
        return lib;
    }

    public void setLib(Boolean lib) {
        this.lib = lib;
    }

    public Boolean getVm() {
        return vm;
    }

    public void setVm(Boolean vm) {
        this.vm = vm;
    }

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
