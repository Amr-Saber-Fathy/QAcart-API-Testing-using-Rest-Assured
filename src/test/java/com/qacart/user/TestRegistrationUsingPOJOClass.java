package com.qacart.user;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.UserRegister;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestRegistrationUsingPOJOClass {

    Faker faker = new Faker();
    //String emailForDup;
    UserRegister user;

    @DataProvider(name = "userDataProvider")
    public Object[][] userDataProvider() {
        //emailForDup = faker.internet().emailAddress();
        return new Object[][] {
                {faker.internet().emailAddress(), "123456789", faker.name().firstName(), faker.name().lastName()}
        };
    }

    @DataProvider(name = "invalidDataProvider")
    public Object[][] invalidDataProvider() {
        //emailForDup = faker.internet().emailAddress();
        return new Object[][] {
                {"", "123456789", faker.name().firstName(), faker.name().lastName(), "\"email\" is not allowed to be empty"},
                {"ali13Mohamedail.com", "123456789", faker.name().firstName(), faker.name().lastName(), "\"email\" must be a valid email"},
                {faker.internet().emailAddress(), "", faker.name().firstName(), faker.name().lastName(), "\"password\" is not allowed to be empty"},
                {faker.internet().emailAddress(), "1234567", faker.name().firstName(), faker.name().lastName(), "\"password\" length must be at least 8 characters long"},
                {faker.internet().emailAddress(), "123456789", "", faker.name().lastName(), "\"firstName\" is not allowed to be empty"},
                {faker.internet().emailAddress(), "123456789", "A", faker.name().lastName(), "\"firstName\" length must be at least 2 characters long"},
                {faker.internet().emailAddress(), "123456789", faker.name().firstName(), "", "\"lastName\" is not allowed to be empty"},
                {faker.internet().emailAddress(), "123456789", faker.name().firstName(), "M", "\"lastName\" length must be at least 2 characters long"}
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
    public void userCannotRegisterWithPreRegisteredEmail(){

        given()
                .baseUri("https://todo.qacart.com")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/users/register")
                .then()
                .log().body()
                .assertThat().statusCode(400)
                .assertThat().body("message", equalTo("Email is already exists in the Database"))
                .assertThat().contentType(ContentType.JSON);
    }

    @Test(dataProvider = "invalidDataProvider", priority = 2)
    public void userCannotRegisterWithInvalidData(String email, String password, String fName, String lName, String message){

        user = new UserRegister(email, password, fName, lName);

        given()
                .baseUri("https://todo.qacart.com")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/users/register")
                .then()
                .log().body()
                .assertThat().statusCode(400)
                .assertThat().body("message", equalTo(message))
                .assertThat().contentType(ContentType.JSON);
    }
}
