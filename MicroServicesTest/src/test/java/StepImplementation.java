import com.thoughtworks.gauge.Step;

import java.net.HttpURLConnection;
import java.net.URL;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;

public class StepImplementation {


    @Step("Wait for the services to be up and running")
    public void waitForServices() throws InterruptedException {
        while(true) {
            try {
                System.out.println("This is before checking.....");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/api/get").openConnection();
                connection.setRequestMethod("HEAD");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("Service is up and running....");
                    break;
                }
            } catch(Exception e) {
                System.out.println("trying again...");
                Thread.sleep(10000);// waiting 10 seconds before next try

            }
        }
    }

    @Step("Request to upstream service is successful")
    public void verifyUpstreamAPI() throws InterruptedException {
        when().
                get("http://localhost:8080/api/get").
                then().
                statusCode(200).
                body("modifiedKey1", equalTo("value1"),
                        "modifiedKey2", equalTo("value2"));
    }


}
