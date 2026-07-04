package com.giorgio.math;
import java.util.List;

public class Mesh {
    private List<Triangle> triangles;

    private vector3 position; // this is the position of the mesh in the 3d space.

    public Mesh(List<Triangle> newMesh){
        this.triangles = newMesh;
        this.position = new vector3(0.0,0.0,0.0);
    }

    public Mesh(List<Triangle> newMesh, vector3 pos){
        this.triangles = newMesh;
        this.position = pos;
    }


    public void setPosition(vector3 newPos){
        this.position = newPos;
    }

    public vector3 getPosition(){
        return this.position;
    }

    public List<Triangle> getTriangles(){
        return this.triangles;
    }
}
