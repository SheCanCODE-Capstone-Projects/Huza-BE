@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${MAIL_FROM}")
    private String fromEmail;


    public void sendVerificationEmail(String toEmail, String otp) {

        String json = """
        {
          "sender": {
            "name": "Huza",
            "email": "%s"
          },
          "to": [
            {
              "email": "%s"
            }
          ],
          "subject": "Huza Verification Code",
          "htmlContent": "<h2>Your OTP code is: %s</h2><p>This code expires in 10 minutes.</p>"
        }
        """.formatted(fromEmail, toEmail, otp);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.brevo.com/v3/smtp/email")
                .addHeader(
                        "api-key",
                        brevoApiKey
                )
                .addHeader(
                        "Content-Type",
                        "application/json"
                )
                .post(
                        RequestBody.create(
                                json,
                                MediaType.parse("application/json")
                        )
                )
                .build();


        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Brevo email failed: " + response.body().string()
                );
            }

            System.out.println("Email sent successfully");

        } catch(Exception e) {
            throw new RuntimeException(
                    "Unable to send email",
                    e
            );
        }
    }
}