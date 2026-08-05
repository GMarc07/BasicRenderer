package com.giorgio.Engine.Collision;
import com.giorgio.math.*;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;
import com.giorgio.Engine.*;

public class collisionDetection{

    private List<rigidBody> rigidBodyList;
    List<Pair<rigidBody, rigidBody>> broadPhaseResult;
    List<Pair<rigidBody, rigidBody>> bruteForceResult;
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

    private Bounds computeWorldBounds(List<rigidBody> rigidBodies){
        double minX = 0.0;
        double maxX = 0.0;
        double minY = 0.0;
        double maxY = 0.0;
        double minZ = 0.0;
        double maxZ = 0.0;
        for (rigidBody rigidBody : rigidBodies){
            vector3 pos = rigidBody.getMesh().getPosition();
            if (pos.x < minX){
                minX = pos.x;
            }
            if (pos.x > maxX){
                maxX = pos.x;
            }
            if (pos.y < minY){
                minY = pos.y;
            }
            if (pos.y > maxY){
                maxY = pos.y;
            }
            if (pos.z < minZ){
                minZ = pos.z;
            }
            if (pos.z > maxZ){
                maxZ = pos.z;
            }
        
        } 

        vector3 min = new vector3(minX, minY, minZ);
        vector3 max = new vector3(maxX, maxY, maxZ);
        return new Bounds(min, max);
    }

    public void updateRigidBodyList(List<rigidBody> newList){
        rigidBodyList = newList;
    }

}