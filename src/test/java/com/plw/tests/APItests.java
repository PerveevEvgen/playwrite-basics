package com.plw.tests;

import com.api.data.User;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HttpHeader;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

public class APItests {
    private static final String BASE_URL = "https://alexqa.netlify.app/.netlify/";
    private static final String API_TOKEN ="Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMDIzNTExMjE1NzYzOTEzODQ0NTkiLCJpYXQiOjE3MzEzNDAzMTQsImV4cCI6MTczMTM0MzkxNH0.SQhg3YQnzEDn-Ty0doF1JWUUAPIDcsZkBA66DpdtIIQ";

    private String userId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Playwright playwright;
    private APIRequestContext request;

    @BeforeClass
    public void setUp() {
        createPlaywright();
        createAPIRequestContext();
    }

    @AfterClass
    public void tearDown() {
        disposeAPIRequestContext();
        closePlaywright();
    }

    private void createPlaywright() {
        playwright = Playwright.create();
    }

    private void createAPIRequestContext() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", API_TOKEN);

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(BASE_URL)
                .setExtraHTTPHeaders(headers));
    }

    @Test(priority = 1)
    public void createUser() {
        User user = User.builder()
                .name("Evgen")
                .email("pevgen@gmail.com")
                .age(56)
                .phoneNumber("+12345678")
                .address("Sex street 45")
                .role("user")
                .referralCode("ABCDEFGH").build();

        APIResponse response = request.post("functions/createUser",
                RequestOptions.create().setData(user));

        JsonObject jsonResponse = JsonParser.parseString(response.text()).getAsJsonObject();
        userId = jsonResponse.get("id").getAsString();


        assertTrue(response.ok(), "User creation failed. Status code: " + response.status());
        System.out.println("Status code: " + response.status());
        System.out.println("status text: " + response.statusText());
        System.out.println("User ID: " + userId);

    }

    @Test(priority = 2)
    private void getUserById() {
        APIResponse response = request.get("functions/getUser/" + userId);

        assertNotNull(response, "User not found");
        System.out.println("Status code: " + response.status());
        System.out.println("status text: " + response.statusText());
        assertTrue(response.ok());
    }

    @Test(priority = 3)
    private void updateUser() throws Exception {
        User user = User.builder()
                .name("Valentine")
                .email("pvalentine@gmail.com")
                .age(56)
                .phoneNumber("+12345678")
                .address("Sex street 45")
                .role("user")
                .referralCode("ABCDEFGH")
                .build();

        APIResponse updResp = request.put("functions/updateUser/" + userId,
                RequestOptions.create()
                        .setData(user));

        Assert.assertEquals(updResp.status(), 200);

        APIResponse getResp = request.get("functions/getUser/" + userId);

            User updatedUser = objectMapper.readValue(getResp.text(), User.class);

        Assert.assertEquals(updatedUser.getName(), user.getName());
        Assert.assertEquals(updatedUser.getEmail(), user.getEmail());
        Assert.assertEquals(updatedUser.getAge(), user.getAge());
        Assert.assertEquals(updatedUser.getPhoneNumber(), user.getPhoneNumber());
        Assert.assertEquals(updatedUser.getAddress(), user.getAddress());
        Assert.assertEquals(updatedUser.getRole(), user.getRole());
        Assert.assertEquals(updatedUser.getReferralCode(), user.getReferralCode());
    }

    @Test(priority = 4)
    private void deleteUser() {
        APIResponse response = request.delete("functions/deleteUser/" + userId,
                RequestOptions.create().setHeader("Access", "admin"));
        assertTrue(response.ok());
        System.out.println(response.statusText());
        System.out.println(response.text());
    }

    private void disposeAPIRequestContext() {
        if (request != null) {
            request.dispose();
            request = null;
        }
    }

    private void closePlaywright() {
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}