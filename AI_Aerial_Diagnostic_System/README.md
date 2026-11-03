
## KestrelEye — AI & Drone Imaging for Precision Viticulture

**Repository:** [loanmir/KestrelEye-vision](https://github.com/loanmir/KestrelEye-vision)

KestrelEye is an ongoing project to build a data-driven diagnostic service for vineyards. It combines multispectral drone surveys, in-field IoT sensors and computer vision to detect vine stress and diseases early and show exactly where to intervene.

**Implemented so far**
- **Vine detection** with **YOLO** on drone imagery
- **Disease classification** with **ResNet50** on the detected vines

**Work in progress**
- Real-time analysis of the drone's live video during flight, plus detailed post-flight analysis of multispectral imagery to map vine health
- Separating the vine canopy from the grass between rows, so measurements reflect the vines only
- LandBox, our own network of in-field sensors that tracks soil moisture and microclimate and sends the data over LoRa
- A web platform where clients can explore vineyard maps, view detected issues and download reports

**Tech:** Python · YOLO · ResNet50 · OpenCV · FastAPI · PostGIS · Leaflet.js · Docker

**Team**
- **Lucas Lorenzo Jakin** — Computer Engineering MSc student; leads computer vision, ML pipelines and software development
- **Davide Borsi** — Drone and robotic systems engineering student; leads hardware, electronics and flight operations

**See the [full repository](https://github.com/loanmir/KestrelEye-vision)** for the complete project description, architecture and roadmap.
