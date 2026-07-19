package com.giorgio.controllers;
import javafx.scene.control.Label;
import java.util.HashSet;
import java.util.List;
import com.giorgio.Engine.*;
import com.giorgio.math.vector3;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import java.util.Set;

public class MainPageController {

    @FXML
    private ImageView renderView;
    @FXML
    private Button testButton;
    @FXML
    private Label statusLabelMeshCount;
    @FXML
    private Label statusLabelTriangleCount;
    @FXML
    private Label LiveFpsCount;
    @FXML
    private Label currentCamCordsLabel;

    private WritableImage writableImage;
    private sceneEngine scene;
    private long lastFpsUpdate = 0;
    private int frameCount = 0;
    private Scene javafxScene;
    private Set <KeyCode> keysPressed = new HashSet<>();
    private Set<KeyCode> validKeys = new HashSet<>(
    Set.of(
        KeyCode.W,
        KeyCode.A,
        KeyCode.S,
        KeyCode.D,
        KeyCode.R,
        KeyCode.E,
        KeyCode.Q,
        KeyCode.U, //pitch up
        KeyCode.J, //pitch down
        KeyCode.H, //Yaw left
        KeyCode.K //Yaw right
    )
    );

    @FXML
    public void initialize() {
        camera cam1 = new camera(new vector3(0.0,0.0,0.0),new vector3(0.0,0.0,0.0));
        writableImage = new WritableImage(600, 600);
        renderView.setImage(writableImage);
        this.scene = new sceneEngine(writableImage,cam1);
        this.scene.addMesh(this.scene.createTestTriangle());
        this.scene.addMesh(this.scene.createTestTriangle());
        this.scene.render();
        renderNPyramids(200);
        this.javafxScene = renderView.getScene(); //usually sets null here
        this.setMeshTriangleCountScene();

        AnimationTimer loop = new AnimationTimer() {
            private double fps;
            private long lastFrameTime = 0;
            private final long TARGET_FRAME_TIME = 1_000_000_000L / 60; 

            @Override
            public void handle(long now) { //now is the current timestamp.
                if (now - lastFrameTime < TARGET_FRAME_TIME) {
                    return; // not enough time has passed, skip this frame
                }
                lastFrameTime = now;
                fps = calculateFps(now);
                checkKeyboardPresses();
                if (fps > 0){
                    System.out.println(fps);
                    LiveFpsCount.setText("Fps: " + Math.round(fps));
                }
                scene.render();
            }
        };
        loop.start();
    }
    private void adjustCamCords(){
        vector3 currentCords = this.scene.getCamCords();
        double adjustPitch = 0.0;
        double adjustYaw = 0.0;
        if (keysPressed.contains(KeyCode.W)) {
            currentCords = currentCords.Add(new vector3(0.0, 0.5, 0.0));
        }
        if (keysPressed.contains(KeyCode.S)) {
            currentCords = currentCords.Add(new vector3(0.0, -0.5, 0.0));
        }
        if (keysPressed.contains(KeyCode.A)) {
            currentCords = currentCords.Add(new vector3(-0.5, 0.0, 0.0));
        }
        if (keysPressed.contains(KeyCode.D)) {
            currentCords = currentCords.Add(new vector3(0.5, 0.0, 0.0));
        }
        if (keysPressed.contains(KeyCode.E)) {
            currentCords = currentCords.Add(new vector3(0.0, 0.0, 0.5));
        }
        if (keysPressed.contains(KeyCode.Q)) {
            currentCords = currentCords.Add(new vector3(0.0, 0.0, -0.5));
        }
        if (keysPressed.contains(KeyCode.U)){
            adjustPitch += 0.02;
        }
        if (keysPressed.contains(KeyCode.J)){
            adjustPitch -= 0.02;
        }
        if (keysPressed.contains(KeyCode.H)){
            adjustYaw += 0.03;
        }
        if (keysPressed.contains(KeyCode.K)){
            adjustYaw -= 0.03;
        }
        if (keysPressed.contains(KeyCode.R)){
            currentCords = new vector3(0.0,0.0,0.0);
            this.scene.getCamera().setCamDirection(new vector3(0.0,0.0,0.0));
        }
    
        this.scene.getCamera().rotateCamera(adjustYaw,adjustPitch);
        this.scene.setCamCords(currentCords);
        currentCamCordsLabel.setText("Cam cords: "+ currentCords.x+ " "+ currentCords.y+ " "+currentCords.z +" ");
    }
    private void checkKeyboardPresses(){
        if (this.javafxScene == null){
            this.javafxScene = renderView.getScene();
        }
        javafxScene.setOnKeyPressed(event -> {
            if (validKeys.contains(event.getCode())){
                keysPressed.add(event.getCode());
            }
        });
        javafxScene.setOnKeyReleased(event ->{
            keysPressed.remove(event.getCode());
        }); 
        if (keysPressed.size() > 0){
            adjustCamCords();
        }
    }
    private double calculateFps(long now) {
        frameCount++;
    
        if (lastFpsUpdate == 0) {
            lastFpsUpdate = now;
            return 0;
        }
    
        long elapsed = now - lastFpsUpdate;
    
        if (elapsed >= 1_000_000_000L) {
            double fps = frameCount / (elapsed / 1_000_000_000.0);
    
            frameCount = 0;
            lastFpsUpdate = now;
    
            return fps;
        }
    
        return -1;
    }
    @FXML
    private void backToMain(){
        System.out.println("Button pressed succesfully");
    }

    @FXML
    public void setMeshTriangleCountScene(){
        List<Double> res = this.scene.getMeshAndTriangleCount();
        statusLabelMeshCount.setText("Meshes: " + res.get(0));
        statusLabelTriangleCount.setText("Triangles: " + res.get(1));
    }

    private void renderNPyramids(Integer num){
        for (int i = 0; i < num; i++) {

            double value1 = -50.0 + Math.random() * 1000.0;
            double value2 = -50.0 + Math.random() * 1000.0;
            double value3 = -50.0 + Math.random() * 1000.0;
            vector3 pos = new vector3(value1,value2,value3);
            this.scene.addMesh(meshInitialiser.createPyramid(pos));
        }
    }

}