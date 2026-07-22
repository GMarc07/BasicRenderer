package com.giorgio.Engine;
import com.giorgio.math.*;

class RigidBody {
    Mesh mesh;
    vector3 velocity = new vector3(0.5,0.0,0.0);
    vector3 acceleration;
    double mass;

    RigidBody(Mesh newMesh){
        this.mesh = newMesh;
    }

    
}
