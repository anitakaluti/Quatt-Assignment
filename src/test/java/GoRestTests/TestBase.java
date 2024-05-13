package GoRestTests;

import org.testng.annotations.BeforeTest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

//Base class for all test classes
public class TestBase 
{
	//Setup all arrangements once for all tests
	@BeforeTest
	public static void setup() {
		String token = "b91ea8d3623d36775f4ba46f08b84ad75016ff28cddff77ede99cbd7a618a7a7";
		RestAssured.baseURI = "https://gorest.co.in";
	    RestAssured.requestSpecification = new RequestSpecBuilder()
	    		.setContentType(ContentType.JSON)
	    		.addHeader("Authorization", "Bearer " + token)
	    		.build();
	}
}
