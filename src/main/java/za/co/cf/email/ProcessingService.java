package za.co.cf.email;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@ApplicationScoped
public class ProcessingService {

    @Inject
    S3Client s3;

    @Inject
    EmailService emailService;

    public void process(InputStream input, Context context) {
        LambdaLogger logger = context.getLogger();

        JsonReader jsonReader = Json.createReader(input);
        JsonObject jobj = jsonReader.readObject();

        logger.log(jobj.toString());

        JsonArray records = jobj.getJsonArray("Records");
        for (int i = 0; i < records.size(); i++) {
            JsonObject record = records.getJsonObject(i);
            JsonObject s3 = record.getJsonObject("s3");
            JsonObject bucket = s3.getJsonObject("bucket");
            JsonObject object = s3.getJsonObject("object");
            ByteArrayOutputStream baos = getObject(bucket.getString("name"), object.getString("key"));
            emailService.sendEmail(baos, context);
        }

    }

    private ByteArrayOutputStream getObject(String bucket, String key) {

        // Create a GetObject request
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GetObjectResponse objectResponse = s3.getObject(getObjectRequest, ResponseTransformer.toOutputStream(baos));

        return baos;
    }

}
