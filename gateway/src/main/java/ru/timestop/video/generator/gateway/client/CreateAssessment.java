package ru.timestop.video.generator.gateway.client;

import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceSettings;
import com.google.cloud.recaptchaenterprise.v1.stub.RecaptchaEnterpriseServiceStub;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.Event;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;

import java.io.IOException;

public class CreateAssessment {

    public void main(String[] args) throws IOException {
        // TODO: Replace the token and reCAPTCHA action variables before running the sample.
        String projectID = "copper-guide-356420";
        String recaptchaKey = "6LfSRTspAAAAAGhDxYA5DbWyRMlzeubFflXKjh3c";
        String token = "03AFcWeA4umEF8p89ErRGaQy9mA7GyF90Ujp-1GRyVEPNF54PCJuQF84vlnyHQGSgkDmYBPmg69_pxXAWCvviEAspklbS-RC1PJj2vMgq26mdYyuqupFwTf--mZfKKSAnwt7cukUVCmyxF94CYvQHVsVCh7yFcfazPY0_eDOxIk85JpEKwTbK9AKObR1vyRcrZKJsX6vFKcafhT6hQVlDSW5-3YdetpI8bH69botx3yZQIePjLe85lELyhRNuFGKzBkwB1ODJ9OXLv9_wzPkwljiRMJ_pQkcp75LD2Df0rqRJM99ty0mCWdnK6IxVEfwydD6eMyWqIDWD9MT9LZaexXskNz3y7w1elbctvEgJwx3SYuLS4v5XdYyUtDA9-jUmrW_BFUFTY_23bCPPaUIULMaYBzdLCI77XcBGhEX8AJjQV9DTDcsc6D-BgnFu6vV41ticF3a1g5Hzucljh-CYDfL36BXdEcqNvW4TMVbo6OjTFYJsRNqGmYlK9bR_gU-gNlwch5VamK5OLY4Hi-g-sa-Zj2rzz402pop-Fh-iok2Ca9IogYRySo0F1rQNcK8nC3vPwMCH60Igm6uv46uA4hrkcg0SztmDlUzOdbvwYHA5c6V_omcYfUhMm2bSVFK57f9ZxgyBfCoNi2tInwPDAo8PM912Q_9FhEpQAdY6kMTEkDdUgVQmYJC7ueZFi5gSE6IrS1QBtVNW6wKxEfckImXZhxYWUsqD1_TK1HKx0o6yTypbWMsdGwxOaKTEJrR0rvQtqpzWCWjq6Xbspf3gLHc7L-0gss9n8FbYIE_wd6tnceknxH8BdwAzGsiqK0HNs75xCxiWYZHU1r3TKPQCZaTXFvXR0TneZsNWN51SrT_wDNrsEkg8MYxndcuyACvDGKf5O0CA33ZLjdzpPw0Z0u5AaeSCXBVpp-rZpq3LGgCkfAYK0JyaplCkZVj7I6M3LhrOCNr4p-156dpMlIUgBXAAS9Tv4n44bjqqgqTUnmYVHKu-s8Q-Cki7lZuiPTZrBATFXwxzchsUVuQgJaup15aHc8hPxW3-YDHp3Hm3o7j9riHhSS0AMNjpVcTXqYuxw6Ldv8QwlCJFuf21ikkZPCRDBDa_S1qslUvyzjxXDGiysvKqNj6Xk7L37VwHTsHgX6eKlNEJEUeZ81b7GmnIMjuoPN0zFlqvh3brLgEYyFCeNNLkwUfX9g-4msk-F-ZicI5jWPOvyvlHAoNXl40zYyYIaukKEHBnEDXYaxS3RtCFNubLxFz8_yckHBnXjbxMbZe7ZW9_JMW-OAkl1cImk4SunyDfuZe9qyTcBiS-ydE6Y7tlsgssNCPVcKsWoe3bBW5AL3sAjilQP5VA-dbwhrdxNDsfrfHHS_FCfvR1nE9z3NdJVBV5GNFOvyEGNtGkAhQReg5Y9mxaLi-vv7qub0dJFfj2CUZH8VDZ31OHFCZriRxweSkUWi6zN8wE-n1REvtBQvEMkOkE4o18KHEPcJuWfgHK5QK0K8Bdazh1VyWoQrEgrqM7nqC6xez6vFSDx9OCt02B9Ja7L6YyrialsByfrasmMoxUjnMr-i9qXLNH-d4GqyKuJxvDdGbzqwLlxWOdYvCRW0Kiu";
        String recaptchaAction = "LOGIN";

        createAssessment(projectID, recaptchaKey, token, recaptchaAction);
    }

    /**
     * Create an assessment to analyze the risk of a UI action.
     *
     * @param projectID       : Your Google Cloud Project ID.
     * @param recaptchaKey    : The reCAPTCHA key associated with the site/app
     * @param token           : The generated token obtained from the client.
     * @param recaptchaAction : Action name corresponding to the token.
     */
    public static void createAssessment(
            String projectID, String recaptchaKey, String token, String recaptchaAction)
            throws IOException {
        // Create the reCAPTCHA client.
        // TODO: Cache the client generation code (recommended) or call client.close() before exiting the method.
        try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {

            // Set the properties of the event to be tracked.
            Event event = Event.newBuilder().setSiteKey(recaptchaKey).setToken(token).build();

            // Build the assessment request.
            CreateAssessmentRequest createAssessmentRequest =
                    CreateAssessmentRequest.newBuilder()
                            .setParent(ProjectName.of(projectID).toString())
                            .setAssessment(Assessment.newBuilder().setEvent(event).build())
                            .build();

            Assessment response = client.createAssessment(createAssessmentRequest);

            // Check if the token is valid.
            if (!response.getTokenProperties().getValid()) {
                System.out.println(
                        "The CreateAssessment call failed because the token was: "
                                + response.getTokenProperties().getInvalidReason().name());
                return;
            }

            // Check if the expected action was executed.
            if (!response.getTokenProperties().getAction().equals(recaptchaAction)) {
                System.out.println(
                        "The action attribute in reCAPTCHA tag is: "
                                + response.getTokenProperties().getAction());
                System.out.println(
                        "The action attribute in the reCAPTCHA tag "
                                + "does not match the action ("
                                + recaptchaAction
                                + ") you are expecting to score");
                return;
            }

            // Get the risk score and the reason(s).
            // For more information on interpreting the assessment, see:
            // https://cloud.google.com/recaptcha-enterprise/docs/interpret-assessment
            for (ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
                System.out.println(reason);
            }

            float recaptchaScore = response.getRiskAnalysis().getScore();
            System.out.println("The reCAPTCHA score is: " + recaptchaScore);

            // Get the assessment name (id). Use this to annotate the assessment.
            String assessmentName = response.getName();
            System.out.println(
                    "Assessment name: " + assessmentName.substring(assessmentName.lastIndexOf("/") + 1));
        }
    }
    /**
     The reCAPTCHA score is: 0.9
     Assessment name: 80f29943d2000000
     */
}