package com.giorgio.math;

public class Vertex {
    public vector3 position;
    public vector3 normal;

    Vertex(vector3 position, vector3 normal) {
        this.position = position;
        this.normal = normal;
    }
    public Vertex(vector3 position){
        this.position = position;

        double sum = Math.sqrt(position.x * position.x + position.y*position.y + position.z*position.z);
        this.normal = new vector3(position.x/sum,position.y/sum,position.z/sum);
    }
}