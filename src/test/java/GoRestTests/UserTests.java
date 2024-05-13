package GoRestTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import GoRestTests.Models.ErrorMessage;
import GoRestTests.Models.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;

public class UserTests extends TestBase
{		
	@Test
	public void Get_Users_Returns_A_List_Of_Users()
	{
		//Act
		Response response = given()
	            .contentType(ContentType.JSON)
	            .when()
	            .get("/public/v2/users");
		
		//Assert that response status is successful
		Assert.assertEquals(response.statusCode(), 200);
		System.out.println(response.asPrettyString());
		
		//Assert that at least one user is returned
		List<User> returnedUsers = Arrays.asList(response.getBody().as(User[].class));
		Assert.assertTrue(returnedUsers.size() > 0);
		System.out.println("Number of users: " + returnedUsers.size());
	}
	
	@Test
	public void Create_New_User_With_Sufficient_Data_Is_Successful()
	{
		//Arrange
		User newUser = new User();
		newUser.name = "QuattUser";
		newUser.email = "QuattUser@DummyyDomain.com";
		newUser.gender = "Female";
		newUser.status = "Active";
			
		//Act
		Response createUserResponse = given()
		        .body(newUser)
		        .post("/public/v2/users");
		User createdUser = createUserResponse.getBody().as(User.class);
		System.out.println("Create User Response: \n" + createUserResponse.asPrettyString());
		
		//Assert
		Assert.assertEquals(createUserResponse.statusCode(), 201);
		Assert.assertNotNull(createdUser);
		Assert.assertEquals(createdUser.name, newUser.name);
		Assert.assertEquals(createdUser.email, newUser.email);
		Assert.assertTrue(createdUser.id > 0);
		
		//Delete the newly created user to cleanup, so that it does not complain in the next run 
		given().delete("/public/v2/users/" + createdUser.id).then().statusCode(204);
	}
	
	@Test
	public void Create_New_User_With_Insufficient_Data_Is_Unsuccessful()
	{
		//Arrange
		User newUser = new User();
		//newUser.name = "QuattUser";
		//newUser.email = "QuattUser@DummyyDomain.com";
		//newUser.gender = "Female";
		//newUser.status = "Active";
		
		//Act
		Response createUserResponse = given()
		        .body(newUser)
		        .post("/public/v2/users")
		        ;
		System.out.println("Response: \n" + createUserResponse.asPrettyString());
		
		//Assert
		Assert.assertNotEquals(createUserResponse.statusCode(), 201);
		//Convert response body into a List errorMessages of type ErrorMessage.
		List<ErrorMessage> errorMessages = Arrays.asList(createUserResponse.getBody().as(ErrorMessage[].class));
		
		//Assert that there is an error message for each missing field in the response
		for (ErrorMessage errorMessage : errorMessages) {
			switch (errorMessage.field) {
				case "name":
					Assert.assertTrue(errorMessage.message.contains("can't be blank"));
					break;
				case "email":
					Assert.assertTrue(errorMessage.message.contains("can't be blank"));
					break;
				case "gender":
					Assert.assertTrue(errorMessage.message.contains("can't be blank"));
					break;
				case "status":
					Assert.assertTrue(errorMessage.message.contains("can't be blank"));
					break;
				default:
					break;
			}	
		}
	}
	
	@Test
	public void Create_New_User_Using_Existing_Email_Is_Unsuccessful()
	{
		//Arrange. Create a user
		User newUser = new User();
		newUser.name = "New User";
		newUser.email = "Quattt@SomeDomain.com";
		newUser.gender = "Female";
		newUser.status = "active";
		
		int userId = given().body(newUser)
	    .when().post("/public/v2/users")
		.then().statusCode(201)
		.extract().path("id");
		
		//Act
		Response response = given().body(newUser).post("/public/v2/users");
		System.out.println("Response: \n" + response.asPrettyString());
		
		//Assert
		ErrorMessage[] errorMessage = response.getBody().as(ErrorMessage[].class);
		Assert.assertEquals(errorMessage[0].field, "email");
		Assert.assertEquals(errorMessage[0].message, "has already been taken");
		
		//Delete user to cleanup test data
		given().delete("/public/v2/users/" + userId).then().statusCode(204);
	}
	
	@Test
	public void CreateNewUser_And_UpdateUser_Is_Successful()
	{
		//Arrange. Create a new user.
		User newUser = new User();
		newUser.name = "New User";
		newUser.email = "AUser@SomeDomain.com";
		newUser.gender = "Female";
		newUser.status = "active";
		
		int userId = given().body(newUser)
        .when().post("/public/v2/users")
		.then().statusCode(201)
		.extract().path("id");
		
		//Update user
		User updateUser = new User();
		updateUser.name = "Updated User";
		updateUser.email = "SomeUserr@SomeOtherDomain.com";
		updateUser.gender = "Male";
		updateUser.status = "inactive";
		
		//Act
		Response response = given().body(updateUser).put("/public/v2/users/" + userId);
		System.out.println("Update User Response: \n" + response.asPrettyString());
		User returnedUser = response.getBody().as(User.class);
		
		//Assert
		Assert.assertEquals(returnedUser.id, userId);
		Assert.assertEquals(returnedUser.name, updateUser.name);
		Assert.assertEquals(returnedUser.status, updateUser.status);
		Assert.assertEquals(returnedUser.email, updateUser.email);
		
		//Delete user to cleanup test data
		given().delete("/public/v2/users/" + userId).then().statusCode(204);
		
	}
}

