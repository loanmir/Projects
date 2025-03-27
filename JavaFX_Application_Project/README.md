## Mandelbrot Set Renderer

This project visualizes the Mandelbrot Set using JavaFX, implementing three different computation approaches: **Sequential**, **Parallel**, and **Distributed**. It showcases the performance diffferences and scalability of each approach

### Features
  - Sequential Rendering:  Computes the Mandelbrot Set pixel by pixel on a single thread.
  - Parallel Rendering:  Multi-threading to speed up computration.
  - Distributed Rendering: Computation is distributed across multiple machines or processes for enhanced performance.
  - Interactive UI:
    - Navigation:  Move in all directions using the arrow keys.
    - Zoom:  Zoom in and out using the + and - keys.
    - Resizable Window:   The rendering dynamically adjusts when resizing the window.
   
### Technologies USed
  - Device running Ubuntu on VB.
  - 4 available cores
  - JavaFX
  - MPJ Express (dedicated library used for the distributed version)
