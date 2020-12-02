package com.example.carsmodels.dataModel;

public class CarColor extends Color{
    private int realtionId;
    private int carId;
    public CarColor(String colorHexCode) {
        super(colorHexCode);
    }

    public CarColor(int realtionId,int carId,int id, String colorHexCode) {
        super(id, colorHexCode);
        this.realtionId=realtionId;
        this.carId=carId;

    }

    public int getRealtionId() {
        return realtionId;
    }

    public void setRealtionId(int realtionId) {
        this.realtionId = realtionId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }
}
