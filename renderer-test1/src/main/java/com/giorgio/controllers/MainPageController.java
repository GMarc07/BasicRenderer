package com.giorgio.controllers;
import javafx.scene.control.Label;
import java.util.HashSet;
import java.util.List;
import com.giorgio.Engine.camera;
import com.giorgio.Engine.sceneEngine;
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

    private WritableImage writableImage;
    private sceneEngine scene;
    private long lastFpsUpdate = 0;
    private int frameCount = 0;
    private Scene javafxScene;
    private Set <KeyCode> keys = new HashSet<>();

    @FXML
    public void initialize() {
        camera cam1 = new camera(new vector3(0.0,0.0,0.0),new vector3(0.0,0.0,0.0));
        writableImage = new WritableImage(600, 600);
        renderView.setImage(writableImage);
        this.scene = new sceneEngine(writableImage,cam1);
        this.scene.addMesh(this.scene.createTestTriangle());
        this.scene.addMesh(this.scene.createTestTriangle());
        this.scene.render();
        this.javafxScene = renderView.getScene();
        this.setMeshTriangleCountScene();

        AnimationTimer loop = new AnimationTimer() {
            private double fps;

            @Override
            public void handle(long now) { //now is the current timestamp.
                fps = calculateFps(now);
                checkKeyboardPresses();
                if (fps > 0){
                    System.out.println(fps);
                }
                scene.render();
            }
        };
        loop.start();
    }
    private void checkKeyboardPresses(){
        if (this.javafxScene == null){
            this.javafxScene = renderView.getScene();
        }
        javafxScene.setOnKeyPressed(event -> {
            System.out.println("Pressed: " + event.getCode());
        });
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

}