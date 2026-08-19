package com.giorgio.Engine;
import com.giorgio.math.*;

public class rigidBody {
    Mesh mesh;
    vector3 velocity = new vector3(0.0,0.0,0.0);
    vector3 acceleration;
    double mass;

    rigidBody(Mesh newMesh){
        this.mesh = newMesh;
    }

    public Mesh getMesh(){
        return mesh;
    }

}
