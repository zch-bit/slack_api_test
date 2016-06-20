/**
 * Created by zhixichen on 6/19/16.
 */
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

public class SlackAPIRequest {

    String baseURL = "https://slack.com/api/";

    public RequestSpecification getRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(baseURL).build();
    }
}