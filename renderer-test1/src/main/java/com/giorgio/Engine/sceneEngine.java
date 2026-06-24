package com.giorgio.Engine;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class sceneEngine {

    WritableImage image;
    public sceneEngine(WritableImage imageLink){
        this.image = imageLink;
    }

    public void render(){
        PixelWriter pixelWriter = this.image.getPixelWriter();

        pixelWriter.setColor(300, 300, Color.RED);
        for (int x= 0; x<10; x++){
            for (int i =0; i<150; i++){
                pixelWriter.setColor(i+300, 300+x, Color.RED);
            }
        }
    }
}
