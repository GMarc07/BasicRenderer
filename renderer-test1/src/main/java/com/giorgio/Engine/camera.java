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
    public double getPitch() {
        return Math.atan2(
            camDirection.y,
            Math.sqrt(camDirection.x * camDirection.x +
                      camDirection.z * camDirection.z)
        );
    }
    public double getYaw() {
        return Math.atan2(camDirection.z, camDirection.x);
    }

    public void rotateCamera(double yawDelta, double pitchDelta) {
        double newYaw = this.getYaw() + yawDelta;
        double newPitch = this.getPitch() + pitchDelta;
    
        newPitch = Math.max(-Math.PI / 2 + 0.01, Math.min(Math.PI / 2 - 0.01, newPitch));
    
        // reconstruct direction vector from angles
        double x = Math.cos(newPitch) * Math.cos(newYaw);
        double y = Math.sin(newPitch);
        double z = Math.cos(newPitch) * Math.sin(newYaw);
    
        this.setCamDirection(new vector3(x, y, z));
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
