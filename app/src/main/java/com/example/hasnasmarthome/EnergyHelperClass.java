package com.example.hasnasmarthome;

public class EnergyHelperClass {
    double seconds;
    double consumption;

    private static final EnergyHelperClass ourInstance = new EnergyHelperClass();
    public static EnergyHelperClass getInstance() {
        return ourInstance;
    }

    public EnergyHelperClass() {
    }

    public EnergyHelperClass(double seconds, double consumption) {
        this.seconds = seconds;
        this.consumption = consumption;
    }

    public Double getSeconds() {
        return seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public Double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

}
