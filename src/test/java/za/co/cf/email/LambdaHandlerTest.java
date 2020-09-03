package za.co.cf.email;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;

//@QuarkusTest
public class LambdaHandlerTest {

    @Test
    public void testSimpleLambdaSuccess() throws Exception {
        
        
        //S3EventNotification notification = S3EventNotification.parseJson(loadJsonFromFile("payload.json"));
        //S3Event event = new S3Event(notification.getRecords());

        //S3Event s3event = new S3Event(S3Event.parseJson(loadJsonFromFile("payload.json")));
        
        //String out = LambdaClient.invoke(String.class, s3event);
        //Assertions.assertEquals("Ok", out);
        //Assertions.assertTrue(out.getRequestId().matches("aws-request-\\d"), "Expected requestId as 'aws-request-<number>'");
    }

}
