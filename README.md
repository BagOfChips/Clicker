![search + click](https://i.imgur.com/VnXq5gz.png)  <br />
*A customizable auto-clicker that finds and clicks wherever you want* <br />
Handy for automating simple (and complex) day to day desktop tasks

## features
* Desktop wide fast *image detection* with minimal CPU and memory usage
* Single, double and multi clicking
* Randomized timers (e.g. random generated intervals between each search and click)
* Curved *mouse movements* w/ randomized offsets
* Simple and customizable configuration settings

## demo
* For larger images with defined features, OpenCV has implemented the FAST [feature detection](https://docs.opencv.org/trunk/d7/d66/tutorial_feature_detection.html) algorithm
    * Given my desktop, here is the result of finding [pictures/nmz/nmz_inventory.png](https://i.imgur.com/J5nBQ9k.png)
    ![FAST demo](https://i.imgur.com/C53yluA.png)  <br />
* For smaller images, OpenCV has implemented various [template matching](https://docs.opencv.org/trunk/d7/d66/tutorial_feature_detection.html) solutions
    * Given the *returned image* above, we can search within this new image for [pictures/nmz/absorp4.png](https://i.imgur.com/xIfisVt.png)
    ![templateMatching demo](https://i.imgur.com/L03FC6t.png)  <br />
* Experiment with custom timers and multiple images to create a repeating sequence of clicks

## dependencies
* Java 8
* [OpenCV 2.4.11](https://opencv.org/) - Feature detection
* [JavaCV 1.3.3](https://github.com/bytedeco/javacv) - Template Matching

## future plans
* Implement more modules
* Develop a GUI + CLI (and better documentation)
* Text detection and reading
