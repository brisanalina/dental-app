package com.example.demo.service;



import com.google.cloud.aiplatform.util.ValueConverter;
import com.google.cloud.aiplatform.v1.*;
import com.google.cloud.aiplatform.v1.schema.predict.prediction.TabularClassificationPredictionResult;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class GoogleCloudPredictionService {

    static { OpenCV.loadShared(); }
    private final PredictionServiceClient predictionServiceClient;

    String projectId = "69478067968";
    String location = "europe-west4";
    String endpointId = "2878692965331501056";

    public byte[] getPrediciton(byte[] arr) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        //byte[] arr = readImageBytes();
        String arrEncoded = Base64.getEncoder().encodeToString(arr);
        String instance = "[{ \"content\": \"" + arrEncoded + "\" }]";
        return predictTabularClassification(instance, projectId, endpointId,arr);

    }

    byte[] predictTabularClassification(String instance, String project, String endpointId,byte[] arr)
            throws IOException {

             String location = "europe-west4";
             EndpointName endpointName = EndpointName.of(project, location, endpointId);

             ListValue.Builder listValue = ListValue.newBuilder();
             JsonFormat.parser().merge(instance, listValue);
             List<Value> instanceList = listValue.getValuesList();

             Value parameters = Value.newBuilder().setListValue(listValue).build();
             PredictResponse predictResponse =
                     predictionServiceClient.predict(endpointName, instanceList, parameters);
             System.out.println("Predict Tabular Classification Response");
             System.out.format("\tDeployed Model Id: %s\n", predictResponse.getDeployedModelId());
             System.out.println("Predictions");
             //extractAndPrintPredictions(predictResponse);
             Mat image = byteArrayToMat(arr);
             return drawBoundingBoxes(image,predictResponse);


     }

    private  byte[] readImageBytes() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/caria-dentara.jpg");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toByteArray();
    }



    private byte[] drawBoundingBoxes(Mat image, PredictResponse predictResponse) {
        // Read the image using OpenCV

        // Process each prediction
        for (Value predictionResult : predictResponse.getPredictionsList()) {
            if (predictionResult.hasStructValue()) {
                Struct struct = predictionResult.getStructValue();
                Value bboxes = struct.getFieldsOrThrow("bboxes").getListValue().getValuesList().get(0);
                Value confidences = struct.getFieldsOrThrow("confidences").getListValue().getValuesList().get(0);
                Value displayNames = struct.getFieldsOrThrow("displayNames").getListValue().getValuesList().get(0);


                    List<Value> bbox = bboxes.getListValue().getValuesList();
                    double yMin = bbox.get(0).getNumberValue();
                    double xMax = bbox.get(1).getNumberValue();
                    double xMin = bbox.get(2).getNumberValue();
                    double yMax = bbox.get(3).getNumberValue();
                    float score = (float) confidences.getNumberValue();
                    String label = displayNames.getStringValue();
                    System.out.println("yMin: " + yMin + ", xMin: " + xMin + ", right: " + yMax + ", bottom: " + xMax);
                    // Draw bounding box if score is above threshold
                    //if (score >= 0.5) {
                        int left = (int) (xMin * image.width());
                        int bottom = (int) (yMin * image.height());
                        int right = (int) (xMax * image.width());
                        int top = (int) (yMax * image.height());
                System.out.println("left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);
                        // Draw rectangle and label
                        Imgproc.rectangle(image, new Point(left, top), new Point(right, bottom), new Scalar(0, 255, 0), 1);
                        Imgproc.putText(image, String.format("%s: %.2f", label, score), new Point(left, top - 10),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
                    //}

            }
        }
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", image, matOfByte);
        return matOfByte.toArray();

    }
    private static Mat byteArrayToMat(byte[] byteArray) {
        Mat mat = Imgcodecs.imdecode(new MatOfByte(byteArray), Imgcodecs.IMREAD_UNCHANGED);
        return mat;
    }
}
