package za.co.cf.email;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@ApplicationScoped
public class EmailService {

    @Inject
    SesClient ses;

    @ConfigProperty(name = "mail.from")
    String from;

    @ConfigProperty(name = "mail.to")
    String to;

    public void sendEmail(ByteArrayOutputStream baos, Context context) {

        String rawString = new String(baos.toByteArray());

        // Remove the Return-Path header.
        String pattern = "^Return-Path:[\\t ]?(.*)\\r?\\n";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(rawString);
        rawString = m.replaceAll("");

        // Remove Sender header.
        pattern = "^sender:[\\t ]?(.*)\\r?\\n";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("");

        // Remove Message-ID header.
        pattern = "^Message-ID:[\\t ]?(.*)\\r?\\n";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("");

        // Remove all DKIM-Signature headers to prevent triggering an
        pattern = "^DKIM-Signature:[\\t ]?.*\\r?\\n(\\s+.*\\r?\\n)*";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("");

        pattern = "^X-SES-DKIM-SIGNATURE:[\\t ]?.*\\r?\\n(\\s+.*\\r?\\n)*";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("");

        pattern = "^X-SES-RECEIPT:[\\t ]?.*\\r?\\n(\\s+.*\\r?\\n)*";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("");

        // SES does not allow sending messages from an unverified address,
        // so replace the message's "From:" header with the injected address
        // This must be on the verified domain.
        pattern = "^From:[\\t ]?(.*(?:\\r?\\n\\s+.*)*)";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("From: " + from);

        // Replace original 'To' header with a manually defined one
        pattern = "^To:[\\t ]?.*\\r?\\n(\\s+.*\\r?\\n)*";
        r = Pattern.compile(pattern, Pattern.MULTILINE);
        m = r.matcher(rawString);
        rawString = m.replaceAll("To:" + to + "\r\n");

        RawMessage rawMessage = RawMessage.builder().data(SdkBytes.fromUtf8String(rawString)).build();

        SendRawEmailRequest sendRawEmailRequest = SendRawEmailRequest.builder()
                .rawMessage(rawMessage).build();

        ses.sendRawEmail(sendRawEmailRequest);

    }

}
