package GoRestTests;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import GoRestTests.Models.Comment;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CommentTests extends TestBase
{
	@Test
	public void GetAllComments()
	{
		Response response = given()
	            .contentType(ContentType.JSON)
	            .when()
	            .get("/public/v2/comments")
	            .then()
	            .extract().response();
		
		Assert.assertEquals(response.statusCode(), 200);
		System.out.println(response.asPrettyString());
		List<Comment> returnedComments = Arrays.asList(response.getBody().as(Comment[].class));
		System.out.println("Number of Comments: " + returnedComments.size());
		
	}
}
