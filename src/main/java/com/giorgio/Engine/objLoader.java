package com.giorgio.Engine;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import com.giorgio.math.Mesh;
import com.giorgio.math.Triangle;
import com.giorgio.math.Vertex;
import com.giorgio.math.vector3;


public class objLoader {
    
    Path modelPath;

    public objLoader(){ 

    }

    public void setModelPath(Path newPath){
        modelPath = newPath;
    }

    public Mesh createMeshFromFile() throws IOException{
        if (modelPath.equals(null)){
            return null; 
        }

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Triangle> triangles = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(modelPath)) {
            
            String line;
        
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");

                if (parts[0].equals("v")) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    vertices.add(new Vertex(new vector3(x,y,z)));
                }
                else if (parts[0].equals("f")){
                    String[] faceParts = line.trim().split("\\s+");

                    int[] faceVertices = new int[faceParts.length - 1];

                    for (int i = 1; i < faceParts.length; i++) {
                        faceVertices[i - 1] = Integer.parseInt(faceParts[i]) - 1;
                    }

                    //assumes theres a maximum of 4 vertices per face.
                    //Converts them into triangles

                    if (faceVertices.length == 3){
                        triangles.add(new Triangle(vertices.get(faceVertices[0]), vertices.get(faceVertices[1]), vertices.get(faceVertices[2])));   
                    }
                    else if(faceVertices.length == 4){
                        triangles.add(new Triangle(vertices.get(faceVertices[0]), vertices.get(faceVertices[1]), vertices.get(faceVertices[2])));
                        triangles.add(new Triangle(vertices.get(faceVertices[0]), vertices.get(faceVertices[2]), vertices.get(faceVertices[3])));
                    }
                }
            }

        }

        if (triangles.size() > 0){
            Mesh newMesh = new Mesh(triangles);
            return newMesh;
        }
        else{
            return null;
        }
    }
}
