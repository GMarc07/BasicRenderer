package com.giorgio.math;

public class generalEquations {
    public vector3 scale(vector3 vector,Double a){
        double rx = vector.x * a;
        double ry = vector.y * a;
        double rz = vector.z * a;
        return new vector3(rx, ry, rz);
    }
    public vector3 normalise(vector3 vector){
        double sum = vector.x + vector.y +vector.z;
        return new vector3(vector.x/sum,vector.y/sum,vector.z/sum);
    }
    public vector3 cross(vector3 vector,vector3 other){        
        // i 
        double res1 =( vector.y * other.z ) - (other.y * vector.z);
        // j 
        double res2 = -1*((vector.x * other.z)-(other.x*vector.z));
        //k 
        double res3 = ((vector.x *other.y)-(other.x*vector.y));
        return new vector3(res1,res2,res3);
    }

    public double edgeFunc(vector3 vecA, vector3 vecB, vector3 point){

        return 0.0;
    }
}
