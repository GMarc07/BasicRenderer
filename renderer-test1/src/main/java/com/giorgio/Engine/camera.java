package com.giorgio.Engine;

import com.giorgio.math.vector3;

public class camera {
    vector3 camCords;
    public camera(){
        this.camCords = new vector3(0.0,0.0,0.0);
    }
    public camera(Double x, Double y, Double z){
        this.camCords = new vector3(x,y,z);
    }

    public void setCamCords(Double x, Double y,Double z){
        camCords = new vector3(x,y,z);
    }
}
