package com.giorgio.Engine.Collision;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.giorgio.Engine.rigidBody;
import com.giorgio.Engine.Collision.collisionDetection.Bounds;
import com.giorgio.math.vector3;

import javafx.util.Pair;

public class Octree {

    private static final int MAX_OBJECTS = 8;
    private static final int MAX_DEPTH = 8;

    private final Bounds worldBounds;
    private final vector3 center;
    private final int depth;

    private final List<rigidBody> objects = new ArrayList<>();
    private Octree[] children = null;

    public Octree(Bounds bounds) {
        this(bounds, 0);
    }

    private Octree(Bounds bounds, int depth) {
        this.worldBounds = bounds;
        this.depth = depth;
        this.center = bounds.min().Add(bounds.max()).scale(0.5);
    }

    public void insert(rigidBody object, Bounds aabb) {

        if (children != null) {
            int index = getChildIndex(aabb);
    
            if (index != -1) {
                children[index].insert(object, aabb);
                return;
            }
        }
    
        objects.add(object);
    
        if (objects.size() > MAX_OBJECTS && depth < MAX_DEPTH) {
    
            if (children == null)
                subdivide();
    
            Iterator<rigidBody> iterator = objects.iterator();
    
            while (iterator.hasNext()) {
                rigidBody obj = iterator.next();
                Bounds objBounds = obj.getMesh().getAABB();
    
                int index = getChildIndex(objBounds);
    
                if (index != -1) {
                    children[index].insert(obj, objBounds);
                    iterator.remove();
                }
            }
        }
    }

    private void subdivide() {

        children = new Octree[8];

        vector3 min = worldBounds.min();
        vector3 max = worldBounds.max();
        vector3 center = min.Add(max).scale(0.5);

        children[0] = new Octree(new Bounds(
                new vector3(min.x, min.y, min.z),
                new vector3(center.x, center.y, center.z)), depth + 1);

        children[1] = new Octree(new Bounds(
                new vector3(center.x, min.y, min.z),
                new vector3(max.x, center.y, center.z)), depth + 1);

        children[2] = new Octree(new Bounds(
                new vector3(min.x, center.y, min.z),
                new vector3(center.x, max.y, center.z)), depth + 1);

        children[3] = new Octree(new Bounds(
                new vector3(center.x, center.y, min.z),
                new vector3(max.x, max.y, center.z)), depth + 1);

        children[4] = new Octree(new Bounds(
                new vector3(min.x, min.y, center.z),
                new vector3(center.x, center.y, max.z)), depth + 1);

        children[5] = new Octree(new Bounds(
                new vector3(center.x, min.y, center.z),
                new vector3(max.x, center.y, max.z)), depth + 1);

        children[6] = new Octree(new Bounds(
                new vector3(min.x, center.y, center.z),
                new vector3(center.x, max.y, max.z)), depth + 1);

        children[7] = new Octree(new Bounds(
                new vector3(center.x, center.y, center.z),
                new vector3(max.x, max.y, max.z)), depth + 1);
    }

    private int getChildIndex(Bounds aabb) {

        vector3 center = this.center;
    
        int index = 0;
    
        // X axis
        if (aabb.max().x < center.x) {
            // left
        } else if (aabb.min().x >= center.x) {
            index |= 1; // right
        } else {
            return -1; // crosses X plane
        }
    
        // Y axis
        if (aabb.max().y < center.y) {
            // bottom
        } else if (aabb.min().y >= center.y) {
            index |= 2; // top
        } else {
            return -1; // crosses Y plane
        }
    
        // Z axis
        if (aabb.max().z < center.z) {
            // back
        } else if (aabb.min().z >= center.z) {
            index |= 4; // front
        } else {
            return -1; // crosses Z plane
        }
    
        return index;
    }


    public List<Pair<rigidBody, rigidBody>> getPotentialCollisions(){
        List<Pair<rigidBody, rigidBody>> pairs = new ArrayList<>();
        collectPairs(new ArrayList<>(), pairs);
        return pairs;
    }

    private void collectPairs(List<rigidBody> ancestors, List<Pair<rigidBody, rigidBody>> pairs) {

        // 1. Compare every object in this node with ancestors
        for (rigidBody current : objects) {
            for (rigidBody ancestor : ancestors) {
                pairs.add(new Pair<>(current, ancestor));
            }
        }

        // 2. Compare objects stored in this node with each other
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                pairs.add(new Pair<>(objects.get(i), objects.get(j)));
            }
        }

        // 3. Descendants should compare against these objects too
        List<rigidBody> nextAncestors = new ArrayList<>(ancestors);
        nextAncestors.addAll(objects);

        if (children != null) {
            for (Octree child : children) {
                child.collectPairs(nextAncestors, pairs);
            }
        }
    }

    public List<Pair<rigidBody, rigidBody>> bruteForce(){
        List<Pair<rigidBody, rigidBody>> bruteForce = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                bruteForce.add(new Pair<>(objects.get(i), objects.get(j)));
            }
        }
        return bruteForce;
    }
}