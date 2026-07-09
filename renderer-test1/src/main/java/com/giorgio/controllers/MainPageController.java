package com.giorgio.controllers;

import javafx.scene.control.Label;
import java.util.List;
import com.giorgio.Engine.sceneEngine;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

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

    @FXML
    public void initialize() {
        writableImage = new WritableImage(600, 600);
        renderView.setImage(writableImage);
        this.scene = new sceneEngine(writableImage);
        this.scene.addMesh(this.scene.createTestTriangle());
        this.scene.addMesh(this.scene.createTestTriangle());
        this.scene.render();
        this.setMeshTriangleCountScene();
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