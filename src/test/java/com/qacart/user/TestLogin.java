package com.qacart.user;


import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.UserLogin;
import pojo.UserRegister;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestLogin {

    Faker faker = new Faker();
    UserRegister user;
    UserLogin registeredUser;

    @DataProvider(name = "userDataProvider")
    public Object[][] userDataProvider() {
        return new Object[][] {
                {faker.internet().emailAddress(), "123456789", faker.name().firstName(), faker.name().lastName()}
        };
    }

    @DataProvider(name = "userLoginInvalidDataProvider")
    public Object[][] userLoginInvalidDataProvider() {
        return new Object[][] {
                {"", "123456789", "\"Email\" Field is required", 400},
                {"asf547asf@gmail.com", "123456789", "We could not find the email in the database", 400},
                {"asf547gmail.com", "123456789", "\"Email\" is invalid (Missing @)", 400},
                {user.getEmail(), "", "Please Fill a correct Password", 400},
                {user.getEmail(), "Test145698", "The email and password combination is not correct, please fill a correct email and password", 401},
                {user.getEmail(), "1234567", "Please Fill a correct Password", 400}
        };
    }

    @Test(dataProvider = "userDataProvider")
    public void userShouldBeAbleToRegisterWithValidData(String email, String password, String fName, String lName){

        user = new UserRegister(email, password, fName, lName);

        given()
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
                .assertThat().body("access_token", notNullValue());
    }

    @Test(dependsOnMethods = {"userShouldBeAbleToRegisterWithValidData"}, priority = 1)
    public void registeredUserShouldBeAbleToLogin(){

        registeredUser = new UserLogin(user.getEmail(), user.getPassword());

        given()
                .baseUri("https://todo.qacart.com")
                .contentType(ContentType.JSON)
                .body(registeredUser)
                .when()
                .post("/api/v1/users/login")
                .then()
                .log().body()
                .assertThat().statusCode(200)
                .assertThat().body("firstName", equalTo(user.getFirstName()))
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body("access_token", notNullValue());
    }

    @Test(dataProvider = "userLoginInvalidDataProvider", priority = 2)
    public void userCannotLoginWithInvalidData(String email, String password, String message, int statusCode){

        registeredUser = new UserLogin(email, password);

        given()
                .baseUri("https://todo.qacart.com")
                .contentType(ContentType.JSON)
                .body(registeredUser)
                .when()
                .post("/api/v1/users/login")
                .then()
                .log().body()
                .assertThat().statusCode(statusCode)
                .assertThat().body("message", equalTo(message))
                .assertThat().contentType(ContentType.JSON);
    }
}
