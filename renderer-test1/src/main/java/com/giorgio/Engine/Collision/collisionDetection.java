package com.giorgio.Engine.Collision;
import com.giorgio.math.*;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.giorgio.Engine.*;

public class collisionDetection{

    private List<rigidBody> rigidBodyList;
    List<Pair<rigidBody, rigidBody>> broadPhaseResult;
    List<Pair<rigidBody, rigidBody>> bruteForceResult;
    List<rigidBody> narrowPhaseResult;
    public record Bounds(vector3 min, vector3 max) {

    }
    
    public collisionDetection(List<rigidBody> theBodyList){
        this.rigidBodyList = theBodyList;

    }
    public void runDetection(){
        this.broadPhaseResult =  this.broadPhase();
        System.out.println("OCTREE RESULT");
        System.out.println(broadPhaseResult);
        System.out.println("BRUTE FORCE RESULT");
        System.out.println(bruteForceResult);
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

        for (rigidBody rigidBody : rigidBodyList){
            Bounds boundMesh = rigidBody.getMesh().getAABB();
            octree.insert(rigidBody,boundMesh);
        }

        // ---- Step 3: Traverse tree, collect candidate pairs ----
  
        List<Pair<rigidBody, rigidBody>> candidatePairs = octree.getPotentialCollisions();
        bruteForceResult = octree.bruteForce();
        // ---- Step 4: Return pairs for narrow phase to check ----
        //RETURN candidatePairs
        return candidatePairs;
    }
    private List<Pair<rigidBody, rigidBody>> narrowPhase(){
        List<Pair<rigidBody, rigidBody>> latestBroadPhaseResults = this.broadPhaseResult;
        List<Pair<rigidBody, rigidBody>> narrowresults = new ArrayList<>();
        //need to get every axis to test from the 2 objects and go through each one and check if theres any overlap.

        for(Pair<rigidBody, rigidBody> pair : latestBroadPhaseResults){
            rigidBody objA = pair.getKey();
            rigidBody objB = pair.getValue();
            boolean separated = false;
            List<vector3> axes = getAxes(objA);
            axes.addAll(getAxes(objB));

            //project both objects on these axes.
            for (vector3 axis : axes){
                Pair<Double,Double> minMaxObjA = projectObjectOnAxis(objA, axis);
                Pair<Double,Double> minMaxObjB = projectObjectOnAxis(objB, axis);


                if (!(minMaxObjA.getValue()< minMaxObjB.getKey() || minMaxObjB.getValue() < minMaxObjA.getKey())) {
                    separated = true;
                    break;
                }
            }
            if (!separated) {
                narrowresults.add(pair);
            }
        }
        return narrowresults;
    }
    // loop over all the vertices, performing the dot product with the axis and storing the minimum and maximum.
    private Pair<Double,Double> projectObjectOnAxis(rigidBody object,vector3 axis){
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        Mesh mesh = object.getMesh();
        for (Triangle triangle : mesh.getTriangles()){
            List<Vertex> vertices = Arrays.asList(triangle.getV0(),triangle.getV1(),triangle.getV2());
            for (Vertex vertex : vertices) {
                double p = vertex.position.dotProduct(axis);
        
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

            vector3 normal = generalEquations.cross(v0,v1);
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