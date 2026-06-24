package com.giorgio.controllers;

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

    private WritableImage writableImage;
    private sceneEngine scene;

    @FXML
    public void initialize() {
        writableImage = new WritableImage(600, 600);
        renderView.setImage(writableImage);
        this.scene = new sceneEngine(writableImage);
        this.scene.render();
    }
    @FXML
    private void backToMain(){
        System.out.println("Button pressed succesfully");
    }
}