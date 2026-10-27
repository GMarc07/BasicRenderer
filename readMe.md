# 3D Renderer

## Running the Project

To run the application, execute the following command from the project's root directory:

```bash
mvn javafx:run
```

Camera Controls; (Changeable in MainPageController.java)
W; Up
S; Down
A; Left
D; Right
E; Forwards
Q; Backwards
R; Reset to starting angles and position
U; Rotate Up
J; Rotate Down
H; Rotate Left
K; Rotate Right

## Importing OBJ Files

OBJ file importing currently has the following limitations:

- Each face must contain either 3 or 4 vertices (triangles or quadrilaterals). For example, `f 1 2 3` or `f 1 2 4 5`.
- Vertices must be defined using the format `v x y z`, for example, `v 1 1 0`.
- The file must not contain additional information at the beginning that the current parser does not support.

See `cube.obj` and `stepped_temple.obj` for examples of supported files.

## How the Renderer Works

`MainPageController.java` manages the application window and handles interactions between the UI and the rendering engine. It controls settings such as the frame rate cap, initialises `sceneEngine`, and adds meshes and rigid bodies to the scene.

`sceneEngine.java` handles the rendering mathematics. It transforms vertices relative to the camera's position and orientation, then uses perspective projection to map the vertices onto the 2D screen. An edge function determines which pixels lie inside each projected triangle, allowing the renderer to fill the triangles with colour. A depth buffer ensures that closer triangles are rendered in front of further ones.

## Physics and Collision Detection

Rigid bodies can be assigned velocity and acceleration to control their movement. Collision detection is implemented, but collision response is not yet supported.

The collision detection system uses a two-phase algorithm to reduce the number of collision checks required compared with brute-force pairwise testing.

### Broad Phase

An octree spatial partitioning structure divides 3D space into smaller regions to identify potential collision pairs based on their spatial locations. This reduces the number of pairs that need to undergo more expensive collision tests.

### Narrow Phase

Potential collision pairs are checked using the Separating Axis Theorem (SAT). If no separating axis exists between the tested shapes, they are considered to be intersecting.

Collision response, such as resolving overlaps or changing velocities after an impact, has not yet been implemented.