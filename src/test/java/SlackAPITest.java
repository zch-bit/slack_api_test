/**
 * Created by zhixichen on 6/19/16.
 */

import com.jayway.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;


public class SlackAPITest {

    public String getPath() {
        String currentDirectory;
        File file = new File("");

        currentDirectory = file.getAbsolutePath() + "/src/main/test_img.png";
        return currentDirectory;
    }

    @Test
    public void uploadFilesTest() {
        String fileName = "APIUploadPng";
        String filePath = getPath();
        String token = "xoxp-52263253139-52322452210-52306300182-e7c9c273cf";

        String thumbnailsFileName = fileName.toLowerCase();

        RequestSpecification requestSpec = new SlackAPIRequest().getRequestSpecification();

        given().
                spec(requestSpec).
                param("token", token).
                param("filename", fileName).
                multiPart(new File(filePath)).
                when().
                post("files.upload").
                then().log().ifValidationFails().
                statusCode(200).
                body("ok", equalTo(true)).
                body("file.id", notNullValue()).
                body("file.name", equalTo(fileName)).
                body("file.thumb_64", containsString(thumbnailsFileName + "_64.png")).
                body("file.thumb_80", containsString(thumbnailsFileName + "_80.png")).
                body("file.thumb_360", containsString(thumbnailsFileName + "_360.png")).
                body("file.thumb_480", containsString(thumbnailsFileName + "_480.png")).
                body("file.thumb_160", containsString(thumbnailsFileName + "_160.png"));
    }

    @Test
    public void filesListTest() throws Exception {
        String fileName = "APIUploadPng";
        String filePath = getPath();
        String token = "xoxp-52263253139-52322452210-52306300182-e7c9c273cf";

        RequestSpecification requestSpec = new SlackAPIRequest().getRequestSpecification();

        String fileId =
                given().
                        spec(requestSpec).
                        param("token", token).
                        param("filename", fileName).
                        multiPart(new File(filePath)).
                        when().
                        post("files.upload").
                        then().
                        statusCode(200).
                        extract().
                        path("file.id");
        System.out.print("File uploaded:" + fileId);
        Thread.sleep(5000);

        given().
                spec(requestSpec).
                param("token", token).
                when().
                get("files.list").
                then().log().ifValidationFails().
                statusCode(200).
                body("ok", equalTo(true)).
                body("files[0].id", equalTo(fileId)).
                body("files[0].name", equalTo(fileName));
    }

    @Test
    public void fileDeleteTest() throws Exception {
        String fileName = "APIUploadPng";
        String filePath = getPath();
        String token = "xoxp-52263253139-52322452210-52306300182-e7c9c273cf";

        RequestSpecification requestSpec = new SlackAPIRequest().getRequestSpecification();

        String fileId =
                given().
                        spec(requestSpec).
                        param("token", token).
                        param("filename", fileName).
                        multiPart(new File(filePath)).
                        when().
                        post("files.upload").
                        then().
                        statusCode(200).
                        extract().
                        path("file.id");
        /*
         * Wait CloudFront to cache this new file.
         */
        Thread.sleep(10000);

        given().
                spec(requestSpec).
                param("token", token).
                param("file", fileId).
                when().
                get("files.delete").
                then().log().ifValidationFails().
                statusCode(200).
                body("ok", equalTo(true));

        given().
                spec(requestSpec).
                param("token", token).
                param("file", fileId).
                when().
                get("files.delete").
                then().log().ifValidationFails().
                statusCode(200).
                body("ok", equalTo(false)).
                body("error", equalTo("file_deleted"));
    }
}