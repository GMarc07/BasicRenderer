package com.giorgio.Engine;
import java.util.ArrayList;
import java.util.List;
import com.giorgio.math.*;

public class meshInitialiser {

    public static Mesh createTriangle(){
        Vertex v0 = new Vertex(new vector3(-1.0, -1.0, 0.0));
        Vertex v1 = new Vertex(new vector3( 1.0, -1.0, 0.0));
        Vertex v2 = new Vertex(new vector3( 0.0,  1.0, 0.0));
    
        Triangle triangle = new Triangle(v0, v1, v2);
    
        List<Triangle> triangles = new ArrayList<>();
        triangles.add(triangle);
    
        Mesh mesh = new Mesh(triangles);
        mesh.setPosition(new vector3(0.0, 0.0, 5.0));
    
        return mesh;
    }

    public static Mesh createPyramid() {
        // base vertices
        Vertex v0 = new Vertex(new vector3(-1.0, -1.0,  1.0)); // front left
        Vertex v1 = new Vertex(new vector3( 1.0, -1.0,  1.0)); // front right
        Vertex v2 = new Vertex(new vector3( 1.0, -1.0, -1.0)); // back right
        Vertex v3 = new Vertex(new vector3(-1.0, -1.0, -1.0)); // back left
        Vertex apex = new Vertex(new vector3( 0.0,  1.0,  0.0)); // top point
    
        List<Triangle> triangles = new ArrayList<>();
    
        // base (two triangles forming a quad)
        triangles.add(new Triangle(v0, v1, v2));
        triangles.add(new Triangle(v0, v2, v3));
    
        // four side faces
        triangles.add(new Triangle(v0, v1, apex)); // front
        triangles.add(new Triangle(v1, v2, apex)); // right
        triangles.add(new Triangle(v2, v3, apex)); // back
        triangles.add(new Triangle(v3, v0, apex)); // left
    
        Mesh mesh = new Mesh(triangles);
        mesh.setPosition(new vector3(0.0, 0.0, 0.0));
    
        return mesh;
    }
    public static Mesh createPyramid(vector3 Pos) {
        // base vertices
        Vertex v0 = new Vertex(new vector3(-1.0, -1.0,  1.0)); // front left
        Vertex v1 = new Vertex(new vector3( 1.0, -1.0,  1.0)); // front right
        Vertex v2 = new Vertex(new vector3( 1.0, -1.0, -1.0)); // back right
        Vertex v3 = new Vertex(new vector3(-1.0, -1.0, -1.0)); // back left
        Vertex apex = new Vertex(new vector3( 0.0,  1.0,  0.0)); // top point
    
        List<Triangle> triangles = new ArrayList<>();
    
        // base (two triangles forming a quad)
        triangles.add(new Triangle(v0, v1, v2));
        triangles.add(new Triangle(v0, v2, v3));
    
        // four side faces
        triangles.add(new Triangle(v0, v1, apex)); // front
        triangles.add(new Triangle(v1, v2, apex)); // right
        triangles.add(new Triangle(v2, v3, apex)); // back
        triangles.add(new Triangle(v3, v0, apex)); // left
    
        Mesh mesh = new Mesh(triangles);
        mesh.setPosition(Pos);
    
        return mesh;
    }
}
