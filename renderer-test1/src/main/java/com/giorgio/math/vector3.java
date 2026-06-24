package com.giorgio.math;

public class vector3 {
    Double x,y,z;

    public vector3(Double x,Double y, Double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public vector3(){
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public vector3 Add(vector3 otherVector){
        double rx = this.x + otherVector.x;
        double ry = this.y + otherVector.y;
        double rz = this.z + otherVector.z;

        vector3 returnVector = new vector3(rx,ry,rz);
        return returnVector;
    }
    public vector3 subtract(vector3 other) {
        double rx = this.x - other.x;
        double ry = this.y - other.y;
        double rz = this.z - other.z;
        return new vector3(rx, ry, rz);
    }
    public vector3 Multiply(vector3 otherVector){
        double rx = this.x * otherVector.x;
        double ry = this.y * otherVector.y;
        double rz = this.z * otherVector.z;

        vector3 returnVector = new vector3(rx,ry,rz);
        return returnVector;
    }
    public double dotProduct(vector3 otherVector){
        double px = this.x * otherVector.x;
        double py = this.y * otherVector.y;
        double pz = this.z * otherVector.z;

        double num = px + py + pz;
        return num;
    }
    public vector3 scale(Double a){
        double rx = this.x * a;
        double ry = this.y * a;
        double rz = this.z * a;
        return new vector3(rx, ry, rz);
    }
    public vector3 normalise(){
        double sum = this.x + this.y +this.z;
        return new vector3(this.x/sum,this.y/sum,this.z/sum);
    }
    public vector3 cross(vector3 other){        
        // i 
        double res1 =( this.y * other.z ) - (other.y * this.z);
        // j 
        double res2 = -1*((this.x * other.z)-(other.x*this.z));
        //k 
        double res3 = ((this.x *other.y)-(other.x*this.y));
        return new vector3(res1,res2,res3);
    }
}