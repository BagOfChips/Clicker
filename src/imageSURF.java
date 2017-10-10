import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

/**
 * Reference: http://dummyscodes.blogspot.ca/2015/12/using-siftsurf-for-object-recognition.html
 */
public class imageSURF{

    private snapshot snapshotUtils = new snapshot();
    private Mat snapshotMat = null;
    private Mat[] targettedImagesMat = null;
    private corners foundImageCorners = null;

    public imageSURF() throws AWTException{}

    public corners getFoundImageCorners(){
        return foundImageCorners;
    }

    /**
     * Converts BufferedImage to Mat
     * Flow:
     * 1. load images in snapshot.java
     * 2. convert to Mat in imageSURF.java
     *
     * @param image
     * @return
     */
    private Mat bufferedImageToMat(BufferedImage image){
        image = convertBufferedImageType(image, BufferedImage.TYPE_3BYTE_BGR);
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    /**
     * We have to change the BufferedImage type first
     * Otherwise (DataBufferByte) typecast wont work
     *
     * @param image
     * @param type
     * @return
     */
    private BufferedImage convertBufferedImageType(BufferedImage image, int type){
        if(image.getType() == type){
            return image;
        }

        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
        Graphics2D graphics2D = newImage.createGraphics();
        try{
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.drawImage(image, 0, 0, null);
        }finally{
            graphics2D.dispose();
        }

        return newImage;
    }

    /**
     * Initialize snapshotMat + targettedImagesMat
     */
    public void loadIntoMats(){
        System.out.println("loadIntoMats()");
        snapshotUtils.takeSnapshot();
        snapshotUtils.loadBufferedImages(); // targetted images in ./pictures

        BufferedImage snapshot = snapshotUtils.getSnapshot();
        snapshotMat = bufferedImageToMat(snapshot);

        BufferedImage[] targettedImages = snapshotUtils.getTargettedImages();
        targettedImagesMat = new Mat[targettedImages.length];
        for(int i = 0; i < targettedImages.length; i++){
            targettedImagesMat[i] = bufferedImageToMat(targettedImages[i]);
        }
    }

    /**
     * Use OpenCV implementation of SURF to search for targettedImage
     * Want to get the coordinates of the
     */
    // todo: assume only 1 targetted image for now - targettedImagesMat[0]
    // todo: add a input parameter of ONE targettedImage
    public void SURF(Mat targettedImage){
        System.out.println("SURF()");
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
        //Mat targettedImage = targettedImagesMat[0]; // todo: implement multiple targettedImages later

        // detect key points - targettedImage
        featureDetector.detect(targettedImage, objectKeyPoints);
        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        // compute descriptors - targettedImage
        descriptorExtractor.compute(targettedImage, objectKeyPoints, objectDescriptors);
        // find targettedImage in snapshot
        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
        // detect key points in snapshot
        featureDetector.detect(snapshotMat, sceneKeyPoints);
        // compute descriptors - snapshot
        descriptorExtractor.compute(snapshotMat, sceneKeyPoints, sceneDescriptors);
        List<MatOfDMatch> matches = new LinkedList<>();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        // match images
        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);
        // good match list
        LinkedList<DMatch> goodMatchesList = new LinkedList<>();

        float nndrRatio = 0.7f;
        for(MatOfDMatch matofDMatch: matches){
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if(m1.distance <= m2.distance * nndrRatio){
                goodMatchesList.addLast(m1);
            }
        }

        if(goodMatchesList.size() >= 7){
            System.out.println("  targettedImage found");
            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

            LinkedList<Point> objectPoints = new LinkedList<>();
            LinkedList<Point> scenePoints = new LinkedList<>();

            for(DMatch aGoodMatchesList: goodMatchesList){
                objectPoints.addLast(objKeypointlist.get(aGoodMatchesList.queryIdx).pt);
                scenePoints.addLast(scnKeypointlist.get(aGoodMatchesList.trainIdx).pt);
            }

            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
            objMatOfPoint2f.fromList(objectPoints);
            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
            scnMatOfPoint2f.fromList(scenePoints);

            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

            obj_corners.put(0, 0, 0, 0);
            obj_corners.put(1, 0, targettedImage.cols(), 0);
            obj_corners.put(2, 0, targettedImage.cols(), targettedImage.rows());
            obj_corners.put(3, 0, 0, targettedImage.rows());

            // transform targettedImage corners into snapshot corners
            Core.perspectiveTransform(obj_corners, scene_corners, homography);

            double[] topLeft = scene_corners.get(0, 0);
            int[] topLeftCorner = doubleArrayToIntArray(topLeft);
            double[] topRight = scene_corners.get(1, 0);
            int[] topRightCorner = doubleArrayToIntArray(topRight);
            double[] bottomRight = scene_corners.get(2, 0);
            int[] bottomRightCorner = doubleArrayToIntArray(bottomRight);
            double[] bottomLeft = scene_corners.get(3, 0);
            int[] bottomLeftCorner = doubleArrayToIntArray(bottomLeft);
            foundImageCorners = new corners(topLeftCorner, topRightCorner, bottomRightCorner, bottomLeftCorner);
            foundImageCorners.printAllCorners();

            // uncomment to store image
            /*Mat img = snapshotMat;
            Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
            Highgui.imwrite("pictures//img.jpg", img);*/
        }else{
            // not found - log message
            System.out.println("  targettedImage not found");
            System.out.println("  goodMatchesList.size(): " + goodMatchesList.size());
        }
    }


    /**
     * Converts a double[] to a int[]
     *
     * @param array
     * @return
     */
    private int[] doubleArrayToIntArray(double[] array){
        int[] intArray = new int[array.length];
        for(int i = 0; i < array.length; i++){
            intArray[i] = (int) array[i];
        }
        return intArray;
    }

    /**
     * Load OpenCV Core library automatically
     */
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}