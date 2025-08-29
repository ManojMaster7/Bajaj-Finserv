package com.bajaj.javaqualifier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class JavaQualifierApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(JavaQualifierApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // Step 1: Generate Webhook
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> body = new HashMap<>();
        body.put("name", "Your Name Here");
        body.put("regNo", "22BCE3839");
        body.put("email", "your_email@vitstudent.ac.in");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        // Step 2: Final SQL Query
        String finalQuery =
            "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
            "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME " +
            "FROM PAYMENTS p " +
            "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
            "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
            "ORDER BY p.AMOUNT DESC LIMIT 1;";

        Map<String, String> finalBody = new HashMap<>();
        finalBody.put("finalQuery", finalQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(finalBody, headers);

        restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
        System.out.println("Final query submitted successfully!");
    }
}
