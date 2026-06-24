package com.giorgio.math;

class Triangle {
    Vertex v0, v1, v2;

    Triangle(Vertex v0, Vertex v1, Vertex v2) {
        this.v0 = v0; this.v1 = v1; this.v2 = v2;
    }

    public vector3 faceNormal() {
        vector3 edge1 = v1.position.subtract(v0.position);
        vector3 edge2 = v2.position.subtract(v0.position);
        return edge1.cross(edge2).normalise();
    }
}