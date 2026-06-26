package com.giorgio.Engine;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.List;
import com.giorgio.math.*;

public class sceneEngine {

    WritableImage image;
    List<Mesh> meshList;

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
    public void addMesh(List<Triangle> triangleList){
        Mesh mesh = new Mesh(triangleList);
        this.meshList.add(mesh);
    }
    public void addMesh(Mesh mesh){
        this.meshList.add(mesh);
    }
}
