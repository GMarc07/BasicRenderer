package com.giorgio.controllers;
import javafx.scene.control.Label;
import java.util.HashSet;
import java.util.List;
import com.giorgio.Engine.*;
import com.giorgio.math.Mesh;
import com.giorgio.math.vector3;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import java.util.Set;
import java.nio.file.Path;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

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
    @FXML
    private Label currentCollisionsCountLabel;
    @FXML
    private Button importObject;

    private boolean frameCap = true;
    private WritableImage writableImage;
    private Path objectPath;
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
    private vector3 initCamDir = new vector3(0.0,0.0,1.0);
    private vector3 initCamPos = new vector3(0.0,0.0,0.0);
    @FXML
    public void initialize() {
        camera cam1 = new camera(initCamPos,initCamDir);
        writableImage = new WritableImage(600, 600);
        renderView.setImage(writableImage);
        this.scene = new sceneEngine(writableImage,cam1);

        //place object creation for the scene here

        this.scene.addMesh(meshInitialiser.createPyramid(new vector3(0.0,0.0,10.0)));

        this.javafxScene = renderView.getScene(); //usually sets null here
        this.setMeshTriangleCountScene();

        AnimationTimer loop = new AnimationTimer() {
            private double fps;
            private long lastFrameTime = 0;
            private boolean firstFrame = true;
            private final long TARGET_FRAME_TIME = 1_000_000_000L / 60; 

            @Override
            public void handle(long now) { //now is the current timestamp.
                if (firstFrame) {
                    firstFrame = false;
                    scene.setReady(now); // pass now so lastFrameTime is set correctly
                    return;
                }
                if (frameCap == true){
                    if (now - lastFrameTime < TARGET_FRAME_TIME) {
                    return; // not enough time has passed, skip this frame
                    }
                }   
                lastFrameTime = now;
                fps = calculateFps(now);
                checkKeyboardPresses();
                if (fps > 0){
                    System.out.println(fps);
                    LiveFpsCount.setText("Fps: " + Math.round(fps));
                }
                scene.render(now);
                currentCollisionsCountLabel.setText("Collisions: " + scene.getCollisionCount());
            }
        };
        loop.start();
    }
    private void adjustCamCords(){
        vector3 currentCords = this.scene.getCamCords();
        vector3 currentDirection = this.scene.getCamera().getCamDirection();
        currentDirection.print();
        double adjustPitch = 0.0;
        double adjustYaw = 0.0;


        if (keysPressed.contains(KeyCode.W)) {
            //Where its currently facing, + add more to it?
            vector3 movement = this.scene.getCamera().findDownVector();
            currentCords = currentCords.Add(movement);
        }
        if (keysPressed.contains(KeyCode.S)) {
            vector3 movement = this.scene.getCamera().findUpVector();
            currentCords = currentCords.Add(movement);
        }
        if (keysPressed.contains(KeyCode.A)) {
            vector3 movement = this.scene.getCamera().findLeftVector();
            currentCords = currentCords.Add(movement);
        }
        if (keysPressed.contains(KeyCode.D)) {
            vector3 movement = this.scene.getCamera().findRightVector();
            currentCords = currentCords.Add(movement);
        }
        if (keysPressed.contains(KeyCode.E)) {
            vector3 movement = this.scene.getCamera().findForwardVector();
            currentCords = currentCords.Add(movement);
        }
        if (keysPressed.contains(KeyCode.Q)) {
            vector3 movement = this.scene.getCamera().findBackwardVector();
            currentCords = currentCords.Add(movement);
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
            currentCords = initCamPos;
            this.scene.getCamera().setCamDirection(initCamDir);
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

    @FXML
    public void importObject(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a 3D Model");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("OBJ Files", "*.obj")
        );

        File selectedFile = fileChooser.showOpenDialog(importObject.getScene().getWindow());

        if (selectedFile != null) {
            System.out.println(selectedFile.getAbsolutePath());
            objectPath = selectedFile.toPath();
            objLoader objLoader = new objLoader();
            objLoader.setModelPath(objectPath);

            try {
                //should initialise at 0,0,0
                Mesh importedMesh = objLoader.createMeshFromFile();
                this.scene.addMesh(importedMesh);
            } catch (IOException e) {
                return;
            }

        }
    }


    private void renderNRigidBodies(Integer num){
        for (int i = 0; i < num; i++) {
            double value1 = -50.0 + Math.random() * 1000.0;
            double value2 = -50.0 + Math.random() * 1000.0;
            double value3 = -50.0 + Math.random() * 1000.0;
            vector3 pos = new vector3(value1,value2,value3);
            this.scene.createRigidBodyPyramid(pos);
        } 
    }
    private void renderNPyramids(Integer num){
        this.scene.addMesh(meshInitialiser.createPyramid(new vector3(0.0,0.0,0.0)));
        for (int i = 0; i < num-1; i++) {

            double value1 = 0 + Math.random() * 10.0;
            double value2 = 0 + Math.random() * 10.0;
            double value3 = 0 + Math.random() * 10.0;
            vector3 pos = new vector3(value1,value2,value3);
            this.scene.addMesh(meshInitialiser.createPyramid(pos));
        }
    }
    private void testMovement(){
        this.scene.assignMeshStaticBody(this.scene.createTestTriangle());
        //add velocity to the triangle.
    }

    private void checkBroadPhaseCollision(){
        vector3 p1 = new vector3(0, 0, 0);
        vector3 p2 = new vector3(0.5, 0, 0);
        vector3 p3 = new vector3(50, 50, 50);
        vector3 p4 = new vector3(-50, -50, -50);
        this.scene.createRigidBodyPyramid(p1);
        this.scene.createRigidBodyPyramid(p2);
        this.scene.createRigidBodyPyramid(p3);
        this.scene.createRigidBodyPyramid(p4);

        /*for (int i = 0; i < 20; i++) {
            double value1 = -50.0 + Math.random() * 100.0;
            double value2 = -50.0 + Math.random() * 100.0;
            double value3 = -50.0 + Math.random() * 100.0;
            vector3 pos = new vector3(value1,value2,value3);
            this.scene.createRigidBodyPyramid(pos);
        }
        */
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

}