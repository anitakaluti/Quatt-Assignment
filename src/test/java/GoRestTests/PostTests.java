package GoRestTests;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import GoRestTests.Models.Post;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class PostTests extends TestBase
{
	@Test
	public void GetAllPosts()
	{
		Response response = given()
	            .contentType(ContentType.JSON)
	            .when()
	            .get("/public/v2/posts")
	            .then()
	            .extract().response();
		
		Assert.assertEquals(response.statusCode(), 200);
		System.out.println(response.asPrettyString());
		List<Post> returnedPosts = Arrays.asList(response.getBody().as(Post[].class));
		System.out.println("Number of posts: " + returnedPosts.size());
	}
}
