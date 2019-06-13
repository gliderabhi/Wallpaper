package com.example.abhishekhsharma.wallpaper;

public class Count {

    private int celeb;
    private int cars;
    private int building;
    private int nature;
    private int space;
    private int ocean;

    public Count(){

    }
    public Count(int celeb,int cars,int building,int nature,int space,int ocean){
        this.celeb=celeb;
        this.cars=cars;
        this.building=building;
        this.nature=nature;
        this.space=space;
        this.ocean=ocean;
    }

    public int getCeleb(){
        return celeb;
    }
    public void setCeleb(int Celeb){
        this.celeb=Celeb;
    }
    public int getCars(){
        return cars;
    }
    public void setCars(int Celeb){
        this.cars=Celeb;
    }
    public int getBuilding(){
        return building;
    }
    public void setBuilding(int Celeb){
        this.building=Celeb;
    }
    public int getNature(){
        return nature;
    }
    public void setNature(int Celeb){
        this.nature=Celeb;
    } public int getSpace(){
        return space;
    }
    public void setSpace(int Celeb){
        this.space=Celeb;
    }
    public int getOcean(){
        return ocean;
    }
    public void setOcean(int Celeb){
        this.ocean=Celeb;
    }
}
