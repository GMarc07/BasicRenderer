package com.giorgio.Engine;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.giorgio.math.*;
import javafx.scene.image.PixelFormat;
import java.util.Random;

public class sceneEngine {

    WritableImage image;
    private int width;
    private int height;
    private camera setCamera;
    private List<Mesh> meshList = new ArrayList<>();
    private List<RigidBody> rigidBodyList = new ArrayList<>();
    private int[] pixelBuffer;
    private int[] clearBuffer;
    private double[] depthBuffer;
    private long lastFrameTime;
    Color flatColour = Color.GREEN;

    public sceneEngine(WritableImage imageLink, camera camera){
        this.image = imageLink;
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        this.setCamera = camera;
        pixelBuffer = new int[width * height];
        clearBuffer = new int[width * height];
        depthBuffer = new double[width * height]; 
        Arrays.fill(clearBuffer, 0xFF000000); 
    }
    public camera getCamera(){
        return this.setCamera;
    }
    public void setNewCamera(camera camera){
        this.setCamera = camera;
    }
    public vector3 getCamCords(){
        return this.setCamera.getCamCords();
    }
    public void setCamCords(vector3 newCords){
        this.setCamera.setCamCords(newCords);
    }

    public List<Double> getMeshAndTriangleCount() { 
        double meshCount = meshList.size();
        double triangleCount = 0;
        for (Mesh mesh : meshList) {
           triangleCount += mesh.getTriangles().size();
        }
        return Arrays.asList(meshCount, triangleCount);

    }
    public void render(long now){
        PixelWriter pixelWriter = this.image.getPixelWriter();
        System.arraycopy(clearBuffer, 0, pixelBuffer, 0, pixelBuffer.length);
        Arrays.fill(depthBuffer, Double.MAX_VALUE);
        vector3 camPos = setCamera.getCamCords();
        long timePassed = now - lastFrameTime;
        this.lastFrameTime = now;
        double deltaTime = timePassed / 1_000_000_000.0;
    
        for (Mesh mesh : meshList) {
            int meshColour;
            if (mesh.getColour() == -1) {
                meshColour = randomColour();
                mesh.setColour(meshColour);
            } else {
                meshColour = mesh.getColour();
            }
            renderTriangles(mesh.getTriangles(), mesh.getPosition(), meshColour, camPos);
        } 
    
        for (RigidBody rigidBody : rigidBodyList) {
            Mesh mesh = rigidBody.mesh;
    
            int meshColour;
            if (mesh.getColour() == -1) {
                meshColour = randomColour();
                mesh.setColour(meshColour);
            } else {
                meshColour = mesh.getColour();
            }
    
            if (rigidBody.velocity != null) {
                vector3 movement = rigidBody.velocity.scale(deltaTime);
                movement.print();
                vector3 newPos = movement.Add(mesh.getPosition());
                newPos.print();
                mesh.setPosition(newPos);
            }
    
            renderTriangles(mesh.getTriangles(), mesh.getPosition(), meshColour, camPos);
        } 
    
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixelBuffer, 0, width);
    }

    private double edgeFunction(int x0, int y0, int x1, int y1, int px, int py) {
        return (double)(x1 - x0) * (py - y0) - (double)(y1 - y0) * (px - x0);
    }

    public void addMesh(List<Triangle> triangleList){
        Mesh mesh = new Mesh(triangleList);
        this.meshList.add(mesh);
    }

    public void addMesh(Mesh mesh){
        this.meshList.add(mesh);
    }

    public void removeMesh(int pos){
        if (pos < 0 || pos > meshList.size()){
            return;
        }
        meshList.remove(pos);
    }


    public Mesh createTestTriangle() {
        Vertex v0 = new Vertex(new vector3(-1.0, -1.0, 0.0));
        Vertex v1 = new Vertex(new vector3( 1.0, -1.0, 0.0));
        Vertex v2 = new Vertex(new vector3( 0.0,  1.0, 0.0));
    
        Triangle triangle = new Triangle(v0, v1, v2);
    
        List<Triangle> triangles = new ArrayList<>();
        triangles.add(triangle);
    
        Mesh mesh = new Mesh(triangles);
        mesh.setPosition(new vector3(0.0, 0.0, 5.0));
        meshList.add(mesh);
        return mesh;
    }

    public static int randomColour() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public void assignMeshStaticBody(Mesh mesh){
        RigidBody newBody = new RigidBody(mesh);
        meshList.remove(mesh);
        rigidBodyList.add(newBody);

    }
    private void renderTriangles(List<Triangle> triangles, vector3 meshPos, int colour, vector3 camPos) {
        double fov = 300;
        for (Triangle triangle : triangles) {
            vector3 worldV0 = triangle.getV0().position.Add(meshPos);
            vector3 worldV1 = triangle.getV1().position.Add(meshPos);
            vector3 worldV2 = triangle.getV2().position.Add(meshPos);
    
            vector3 newPosV0 = worldV0.subtract(camPos);
            vector3 newPosV1 = worldV1.subtract(camPos);
            vector3 newPosV2 = worldV2.subtract(camPos);
    
            vector3 camV0 = vector3.applyPitch(vector3.applyYaw(newPosV0, setCamera.getYaw()), setCamera.getPitch());
            vector3 camV1 = vector3.applyPitch(vector3.applyYaw(newPosV1, setCamera.getYaw()), setCamera.getPitch());
            vector3 camV2 = vector3.applyPitch(vector3.applyYaw(newPosV2, setCamera.getYaw()), setCamera.getPitch());
    
            if (camV0.z <= 0 || camV1.z <= 0 || camV2.z <= 0) continue;
    
            double triangleDepth = (camV0.z + camV1.z + camV2.z) / 3;
    
            double z0 = camV0.z == 0 ? 0.0001 : camV0.z;
            double z1 = camV1.z == 0 ? 0.0001 : camV1.z;
            double z2 = camV2.z == 0 ? 0.0001 : camV2.z;
    
            int sx0 = (int) (camV0.x / z0 * fov + width  / 2.0);
            int sy0 = (int) (-camV0.y / z0 * fov + height / 2.0);
            int sx1 = (int) (camV1.x / z1 * fov + width  / 2.0);
            int sy1 = (int) (-camV1.y / z1 * fov + height / 2.0);
            int sx2 = (int) (camV2.x / z2 * fov + width  / 2.0);
            int sy2 = (int) (-camV2.y / z2 * fov + height / 2.0);
    
            int minX = Math.max(0, Math.min(sx0, Math.min(sx1, sx2)));
            int maxX = Math.min(width - 1, Math.max(sx0, Math.max(sx1, sx2)));
            int minY = Math.max(0, Math.min(sy0, Math.min(sy1, sy2)));
            int maxY = Math.min(height - 1, Math.max(sy0, Math.max(sy1, sy2)));
    
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    double e0 = edgeFunction(sx0, sy0, sx1, sy1, x, y);
                    double e1 = edgeFunction(sx1, sy1, sx2, sy2, x, y);
                    double e2 = edgeFunction(sx2, sy2, sx0, sy0, x, y);
    
                    if ((e0 >= 0 && e1 >= 0 && e2 >= 0) || (e0 <= 0 && e1 <= 0 && e2 <= 0)) {
                        int index = y * width + x;
                        if (triangleDepth < depthBuffer[index]) {
                            pixelBuffer[index] = colour;
                            depthBuffer[index] = triangleDepth;
                        }
                    }
                }
            }
        }
    }
}
