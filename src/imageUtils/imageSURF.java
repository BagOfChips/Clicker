package imageUtils;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.*;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.*;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_TM_CCORR_NORMED;
import static org.bytedeco.javacpp.opencv_imgproc.cvMatchTemplate;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;

/**
 * Reference: http://dummyscodes.blogspot.ca/2015/12/using-siftsurf-for-object-recognition.html
 */
public class imageSURF{

    private snapshot snapshotUtils = new snapshot();
    private Mat snapshotMat = null;
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
    public Mat bufferedImageToMat(BufferedImage image){
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
    public void loadSnapshotIntoMat(){
        System.out.println("loadIntoMats()");
        snapshotUtils.takeSnapshot();
        //snapshotUtils.loadBufferedImages(); // targetted images in ./pictures - todo: shouldnt need anymore

        BufferedImage snapshot = snapshotUtils.getSnapshot();
        snapshotMat = bufferedImageToMat(snapshot);
    }

    public void loadSnapshotIntoMat(corners corners){
        System.out.println("loadIntoMats()");
        snapshotUtils.takeSnapshot(corners);
        BufferedImage snapshot = snapshotUtils.getSnapshot();
        snapshotMat = bufferedImageToMat(snapshot);

    }

    /**
     * Use OpenCV implementation of SURF to search for targettedImage
     * Set the coordinates of the found polygon
     * @return true if found targettedImage
     */
    public boolean SURF(Mat targettedImage, int FeatureDetectorAlgo, int DescriptorExtractorAlgo, int DescriptorMatcherAlgo, float nndrRatio, int goodMatchesCriteria){
        System.out.println("SURF()");
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetectorAlgo);

        // detect key points - targettedImage
        featureDetector.detect(targettedImage, objectKeyPoints);
        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractorAlgo);
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
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcherAlgo);
        // match images
        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);
        // good match list
        LinkedList<DMatch> goodMatchesList = new LinkedList<>();

        //float nndrRatio = 0.7f;
        for(MatOfDMatch matofDMatch: matches){
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if(m1.distance <= m2.distance * nndrRatio){
                goodMatchesList.addLast(m1);
            }
        }

        if(goodMatchesList.size() >= goodMatchesCriteria){
            System.out.println("  targettedImage found");
            System.out.println("  goodMatchesList.size(): " + goodMatchesList.size());

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

            /** // uncomment to store image
             Mat img = snapshotMat;
             Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
             Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
             Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
             Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
             Highgui.imwrite("pictures//img.jpg", img);
             */
            return true;
        }else{
            // not found - log message
            System.out.println("  targettedImage not found");
            System.out.println("  goodMatchesList.size(): " + goodMatchesList.size());
            return false;
        }
    }

    /**
     * Alternative to SURF()
     * Should work for smaller targettedImage
     * Reference: https://stackoverflow.com/questions/23451721/how-to-detect-a-subset-image-in-a-picture-where-the-position-of-the-picture-may
     *
     * @param targettedImageMat
     * @return
     */
    public boolean templateMatch(Mat targettedImageMat, double accuracy){
        System.out.println("templateMatch()");

        // turn both snapshotMat and targettedImageMat into IplImage objects
        IplImage snapshotIpl = openCVMatToIplImage(snapshotMat);
        IplImage targettedIpl = openCVMatToIplImage(targettedImageMat);
        IplImage result = cvCreateImage(
                cvSize(
                        snapshotIpl.width() - targettedIpl.width() + 1,
                        snapshotIpl.height() - targettedIpl.height() + 1),
                IPL_DEPTH_32F,
                1
        );
        cvZero(result);

        // match template
        cvMatchTemplate(snapshotIpl, targettedIpl, result, CV_TM_CCORR_NORMED);

        double[] min_val = new double[2];
        double[] max_val = new double[2];
        int[] minLoc = new int[2];
        int[] maxLoc = new int[2];

        cvMinMaxLoc(result, min_val, max_val, minLoc, maxLoc, null);
        System.out.println("  " + java.util.Arrays.toString(min_val));
        System.out.println("  " + java.util.Arrays.toString(max_val));

        if(max_val[0] > accuracy){
            // draw rectangle for matched region
            int[] point = new int[2];
            point[0] = maxLoc[0] + targettedIpl.width();
            point[1] = maxLoc[1] + targettedIpl.height();

            // store found image as a corners object
            foundImageCorners = new corners(
                    maxLoc,
                    new int[]{maxLoc[0] + targettedIpl.width(), maxLoc[1]},
                    point,
                    new int[]{maxLoc[0], maxLoc[1] + targettedIpl.height()}
            );

            /** // uncomment to see found image
             cvRectangle(snapshotIpl, maxLoc, point, CvScalar.WHITE, 2, 8, 0);
             cvShowImage("found targettedImage", snapshotIpl);
             cvWaitKey(0);
             cvReleaseImage(snapshotIpl);
             cvReleaseImage(targettedIpl);
             cvReleaseImage(result);
            */
            return true;
        }

        // not found
        System.out.println("  not found");
        return false;
    }

    /**
     * Converts org.opencv.core.Mat to java.awt.image.BufferedImage
     *
     * @param mat
     * @return
     */
    private BufferedImage openCVMatToBufferedImage(Mat mat){
        int type = 0;
        if(mat.channels() == 1){
            type = BufferedImage.TYPE_BYTE_GRAY;
        }else if(mat.channels() == 3){
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBufferByte = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBufferByte.getData();
        mat.get(0, 0, data);
        return image;
    }

    /**
     * Converts BufferedImage to JavaCV Mat
     *
     * @param image
     * @return
     */
    private opencv_core.Mat bufferedImageToJavaCVMat(BufferedImage image){
        OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat();
        return cv.convertToMat(new Java2DFrameConverter().convert(image));
    }

    private IplImage openCVMatToIplImage(Mat mat){
        BufferedImage image = openCVMatToBufferedImage(mat);
        opencv_core.Mat javaCVMat = bufferedImageToJavaCVMat(image);
        return new IplImage(javaCVMat);
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