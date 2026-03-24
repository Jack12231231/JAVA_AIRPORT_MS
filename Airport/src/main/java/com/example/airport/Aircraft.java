package com.example.airport;
import java.io.*;

// Базовый класс
abstract class Aircraft implements Serializable {
    private String model;
    private int year;
    private int range;
    private double payload;

    public Aircraft(String model, int year, int range, double payload) {
        this.model = model;
        this.year = year;
        this.range = range;
        this.payload = payload;
    }

    // Геттеры и сеттеры
    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getPayload() {
        return payload;
    }

    public void setPayload(double payload) {
        this.payload = payload;
    }

    public abstract void introduce();
}

    // Наследник 1: Самолет
    class Plane extends Aircraft {
        private double wingSpan;

        public Plane(String model, int year, int range, double payload, double wingSpan) {
            super(model, year, range, payload);
            this.wingSpan = wingSpan;
        }

        @Override
        public void introduce() {
            System.out.printf("Самолет: %s, Год: %d, Дальность: %d, Грузоподъемность: %.1f, Размах крыльев: %.1f\n",
                    getModel(), getYear(), getRange(), getPayload(), wingSpan);
        }
    }

    // Наследник 2: Вертолет
    class Helicopter extends Aircraft {
        private double rotorDiameter;

        public Helicopter(String model, int year, int range, double payload, double rotorDiameter) {
            super(model, year, range, payload);
            this.rotorDiameter = rotorDiameter;
        }

        @Override
        public void introduce() {
            System.out.printf("Вертолет: %s, Год: %d, Дальность: %d, Грузоподъемность: %.1f, Диаметр винта: %.1f\n",
                    getModel(), getYear(), getRange(), getPayload(), rotorDiameter);
        }
    }

    // Наследник 3: Дрон
    class Drone extends Aircraft {
        private int batteryCapacity;

        public Drone(String model, int year, int range, double payload, int batteryCapacity) {
            super(model, year, range, payload);
            this.batteryCapacity = batteryCapacity;
        }

        @Override
        public void introduce() {
            System.out.printf("Дрон: %s, Год: %d, Дальность: %d, Грузоподъемность: %.1f, Батарея: %d мАч\n",
                    getModel(), getYear(), getRange(), getPayload(), batteryCapacity);
        }
    }