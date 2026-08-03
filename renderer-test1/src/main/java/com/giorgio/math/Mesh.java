package com.giorgio.math;
import java.util.List;
import com.giorgio.Engine.Collision.collisionDetection.Bounds;

public class Mesh {
    private List<Triangle> triangles;
    private int colour = -1;


    private vector3 position; // this is the position of the mesh in the 3d space.

    public Mesh(List<Triangle> newMesh){
        this.triangles = newMesh;
        this.position = new vector3(0.0,0.0,0.0);
    }
    
    public Mesh(List<Triangle> newMesh, vector3 pos){
        this.triangles = newMesh;
        this.position = pos;
    }

    public Bounds getAABB(){
        if (triangles == null || triangles.isEmpty()) {
            return new Bounds(position, position);
        }   
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
    
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Triangle triangle : triangles){

            vector3[] vertices = {triangle.getV0().position, triangle.getV1().position, triangle.getV2().position};

            for (vector3 v : vertices) {
                minX = Math.min(minX, v.x+position.x);
                minY = Math.min(minY, v.y+position.y);
                minZ = Math.min(minZ, v.z+position.z);

                maxX = Math.max(maxX, v.x+position.x);
                maxY = Math.max(maxY, v.y+position.y);
                maxZ = Math.max(maxZ, v.z+position.z);
            }
        }

        return  new Bounds(new vector3(minX,minY,minZ),new vector3(maxX,maxY,maxZ));

    }
        
    public int getColour(){
        return colour;
    }
    public void setColour(int newColour){
        this.colour =newColour;
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
