package com.giorgio.Engine.Collision;
import com.giorgio.math.*;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.giorgio.Engine.*;

public class collisionDetection{

    private List<rigidBody> rigidBodyList;
    List<Pair<rigidBody, rigidBody>> broadPhaseResult;
    List<Pair<rigidBody, rigidBody>> bruteForceResult;
    List<Pair<rigidBody, rigidBody>> narrowPhaseResult;
    public record Bounds(vector3 min, vector3 max) {

    }
    
    public collisionDetection(List<rigidBody> theBodyList){
        this.rigidBodyList = theBodyList;

    }
    public void runDetection(){
        this.broadPhaseResult =  this.broadPhase();
        System.out.println("Detected " + broadPhaseResult.size()+" possible collisions");
        this.narrowPhaseResult = narrowPhase();
        System.out.println("Detected " + narrowPhaseResult.size() + " collisions");
    }

    //Broad Phase
    //This phase aims at quickly finding objects or parts of objects for which it can be quickly 
    //determined that no further collision test is needed.
    
    private List<Pair<rigidBody, rigidBody>> broadPhase(){
        // Step 1: Determine world bounds
        // Either use a fixed known world size, or compute dynamically
        // by scanning all rigidBodies once and taking the min/max extents.
        Bounds worldBounds = computeWorldBounds(rigidBodyList);   

        // ---- Step 2: Build the octree fresh ----
        Octree octree = new Octree(worldBounds);

        int size = rigidBodyList.size();

        Bounds[] buffer = new Bounds[size];

        IntStream.range(0,size).parallel().forEach(i ->{
            Bounds boundMesh = rigidBodyList.get(i).getMesh().getAABB();
            buffer[i] = boundMesh;
        });
        
        for (int i = 0; i < size; i++){
            octree.insert(rigidBodyList.get(i), buffer[i]);
        }

        // Step 3: Traverse tree, collect candidate pairs 
  
        List<Pair<rigidBody, rigidBody>> candidatePairs = octree.getPotentialCollisions();
        //bruteForceResult = octree.bruteForce();

        return candidatePairs;
    }
    private List<Pair<rigidBody, rigidBody>> narrowPhase() {
        return this.broadPhaseResult.parallelStream()
            .filter(pair -> {
                rigidBody objA = pair.getKey();
                rigidBody objB = pair.getValue();
    
                // 1. Check axes from objA
                if (hasSeparatingAxis(objA, objB, getAxes(objA))) {
                    return false; // Separated, exclude from results
                }
    
                // 2. Check axes from objB
                if (hasSeparatingAxis(objA, objB, getAxes(objB))) {
                    return false; // Separated, exclude from results
                }
    
                return true; // Overlaps on all axes therefore colliding
            })
            .collect(Collectors.toList());
    }

    private boolean hasSeparatingAxis(rigidBody objA, rigidBody objB, List<vector3> axes) {
        for (vector3 axis : axes) {
            Pair<Double, Double> minMaxObjA = projectObjectOnAxis(objA, axis);
            Pair<Double, Double> minMaxObjB = projectObjectOnAxis(objB, axis);
    
            boolean overlaps = !(minMaxObjA.getValue() < minMaxObjB.getKey() 
                               || minMaxObjB.getValue() < minMaxObjA.getKey());
    
            if (!overlaps) {
                return true; // Found a separating axis
            }
        }
        return false;
    }
    // loop over all the vertices, performing the dot product with the axis and storing the minimum and maximum.
    private Pair<Double,Double> projectObjectOnAxis(rigidBody object,vector3 axis){
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        Mesh mesh = object.getMesh();
        vector3 meshPos = mesh.getPosition();
        for (Triangle triangle : mesh.getTriangles()){
            List<Vertex> vertices = Arrays.asList(triangle.getV0(),triangle.getV1(),triangle.getV2());
            for (Vertex vertex : vertices) {
                double p = vertex.position.Add(meshPos).dotProduct(axis);
        
                if (p < min) min = p;
                if (p > max) max = p;
            }
        }
        return new Pair<>(min, max);
    }
    private List<vector3> getAxes(rigidBody body){

        List<vector3> resultsList = new ArrayList<vector3>();
        Mesh mesh = body.getMesh();

        for (Triangle triangle : mesh.getTriangles()){
            vector3 v0 = triangle.getV0().position;
            vector3 v1 = triangle.getV1().position;
            vector3 v2 = triangle.getV2().position;

            vector3 normal = generalEquations.cross(v1.subtract(v0), v2.subtract(v0));
            resultsList.add(normal);

        }
        return resultsList;
    }
    private Bounds computeWorldBounds(List<rigidBody> rigidBodies){
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (rigidBody rigidBody : rigidBodies) {
            Bounds bodyAABB = rigidBody.getMesh().getAABB();

            if (bodyAABB.min().x < minX) minX = bodyAABB.min().x;
            if (bodyAABB.max().x > maxX) maxX = bodyAABB.max().x;

            if (bodyAABB.min().y < minY) minY = bodyAABB.min().y;
            if (bodyAABB.max().y > maxY) maxY = bodyAABB.max().y;

            if (bodyAABB.min().z < minZ) minZ = bodyAABB.min().z;
            if (bodyAABB.max().z > maxZ) maxZ = bodyAABB.max().z;
        }

        vector3 min = new vector3(minX, minY, minZ);
        vector3 max = new vector3(maxX, maxY, maxZ);
        return new Bounds(min, max);
    }

    public void updateRigidBodyList(List<rigidBody> newList){
        rigidBodyList = newList;
    }

}