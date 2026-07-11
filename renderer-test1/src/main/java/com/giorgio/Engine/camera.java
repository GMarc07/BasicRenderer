package com.giorgio.Engine;

import com.giorgio.math.vector3;

public class camera {
    private vector3 camCords;
    private vector3 camDirection;

    public camera(){
        this.camCords = new vector3(0.0,0.0,0.0);
        this.camDirection = new vector3(0.0,0.0,0.0);
    }
    public camera(vector3 camCords, vector3 Camdirection){
        this.camCords = camCords;
        this.camDirection = Camdirection;
    }

    public void setCamCords(vector3 newCords){
        this.camCords = newCords;
    }
    public void setCamDirection(vector3 newDirection){
        this.camDirection = newDirection;
    }
    public vector3 getCamCords(){
        return this.camCords;
    }
    public vector3 getCamDirection(){
        return this.camDirection;
    }
}
