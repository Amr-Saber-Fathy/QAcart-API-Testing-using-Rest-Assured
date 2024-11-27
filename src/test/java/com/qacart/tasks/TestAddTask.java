package com.qacart.tasks;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.Task;
import pojo.UserRegister;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestAddTask {

    Faker faker = new Faker();
    String authKey;
    String userID;
    UserRegister user;
    Task task;

    @DataProvider(name = "taskDataProvider")
    public Object[][] taskDataProvider() {
        //emailForDup = faker.internet().emailAddress();
        return new Object[][] {
                {"Automation", false, null},
                {"", false, "\"item\" is not allowed to be empty"},
                {"Au", false, "\"item\" length must be at least 3 characters long"},
                {null, false, "\"item\" is required"},
                {5, false, "\"item\" must be a string"},
                {"   ", false, "\"item\" is required"},
                {"Automation", null, "\"isCompleted\" is required"},
                {"Automation", 0, "\"isCompleted\" must be a boolean"}
        };
    }

    @BeforeClass
    public void CreateAccountForAuth(){

        user = new UserRegister(faker.internet().emailAddress(), "123456789", faker.name().firstName(), faker.name().lastName());

        Response response = given()
                .baseUri("https://todo.qacart.com")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/users/register")
                .then()
                .log().body()
                .assertThat().statusCode(201)
                .assertThat().body("firstName", equalTo(user.getFirstName()))
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body("access_token", notNullValue())
                .extract().response();

        authKey = response.jsonPath().getString("access_token");
        userID = response.jsonPath().getString("userID");
    }

    @Test(dataProvider = "taskDataProvider")
    public void RegisteredUserCanAddTaskWithValidData(Object item, Object isCompleted, String message){

        task = new Task();
        if(item != null){
            task.setItem(item);
        }
        if(isCompleted != null){
            task.setIsCompleted(isCompleted);
        }

        if(message == null){
            given()
                    .baseUri("https://todo.qacart.com")
                    .contentType(ContentType.JSON)
                    .auth().oauth2(authKey)
                    .body(task)
                    .when()
                    .post("/api/v1/tasks")
                    .then()
                    .log().body()
                    .assertThat().statusCode(201)
                    .assertThat().body("userID", equalTo(userID))
                    .assertThat().body("item", equalTo(item))
                    .assertThat().body("isCompleted", equalTo(isCompleted))
                    .assertThat().contentType(ContentType.JSON);
        }
        else{
            given()
                    .baseUri("https://todo.qacart.com")
                    .contentType(ContentType.JSON)
                    .auth().oauth2(authKey)
                    .body(task)
                    .when()
                    .post("/api/v1/tasks")
                    .then()
                    .log().body()
                    .assertThat().statusCode(400)
                    .assertThat().body("message", equalTo(message))
                    .assertThat().contentType(ContentType.JSON);
        }

    }
}
