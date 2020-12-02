package com.example.carsmodels.dataModel;


public class CarImage   {

    private int id;
    private int relationId;
    private byte[] img;

    public CarImage(){

    }

    public CarImage(int id, int relationId, byte[] img) {
        this.id = id;
        this.relationId = relationId;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
