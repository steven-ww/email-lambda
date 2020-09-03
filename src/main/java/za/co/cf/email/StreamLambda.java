package za.co.cf.email;

import javax.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.inject.Inject;

@Named("stream")
public class StreamLambda implements RequestStreamHandler {

    @Inject
    ProcessingService service;

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        service.process(inputStream, context);
    }
}
