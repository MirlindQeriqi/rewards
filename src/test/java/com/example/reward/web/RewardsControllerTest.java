package com.example.reward.web;

import com.example.reward.constants.PathConstants;
import com.example.reward.core.repository.TransactionRepository;
import com.example.reward.dto.RewardsDto;
import com.example.reward.dto.TransactionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RewardsControllerTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private static TestRestTemplate testRestTemplate;
    private static TransactionDto transactionDto;
    private static TransactionDto transactionDto2;
    private static final Long customerId = 50L;

    @LocalServerPort
    private int port;

    private static final String HOST = "http://localhost:";

    @AfterEach
    public void afterEach(){
        transactionRepository.deleteAll();
    }

    @BeforeAll
    static void beforeAll(){
        testRestTemplate = new TestRestTemplate();
        transactionDto = TransactionDto.builder()
                .amount(new BigDecimal(100))
                .customerId(customerId)
                .build();

        transactionDto2 = TransactionDto.builder()
                .amount(new BigDecimal(120))
                .customerId(customerId)
                .build();
    }

    @Test
    void calculatePointsWithDBData(){
        ResponseEntity<TransactionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto, TransactionDto.class);

        ResponseEntity<TransactionDto> responseEntity2 = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto2, TransactionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(responseEntity2.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity2.getBody());

        String urlWithParams = UriComponentsBuilder.fromHttpUrl(getApiUrl(PathConstants.REWARDS))
                .queryParam("customerId", customerId)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().toString())
                .toUriString();

        ResponseEntity<RewardsDto> rewardsResponseEntity = testRestTemplate.getForEntity(urlWithParams, RewardsDto.class);

        Assertions.assertEquals(rewardsResponseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(rewardsResponseEntity.getBody());
        Assertions.assertEquals(rewardsResponseEntity.getBody().getPoints(), 90);
    }

    @Test
    void calculatePointsWithDBDataWrongDateRange(){
        ResponseEntity<TransactionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto, TransactionDto.class);

        ResponseEntity<TransactionDto> responseEntity2 = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto2, TransactionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(responseEntity2.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity2.getBody());

        String urlWithParams = UriComponentsBuilder.fromHttpUrl(getApiUrl(PathConstants.REWARDS))
                .queryParam("customerId", customerId)
                .queryParam("startDate", LocalDate.now().minusDays(3).toString())
                .queryParam("endDate", LocalDate.now().minusDays(1).toString())
                .toUriString();

        ResponseEntity<RewardsDto> rewardsResponseEntity = testRestTemplate.getForEntity(urlWithParams, RewardsDto.class);

        Assertions.assertEquals(rewardsResponseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(rewardsResponseEntity.getBody());
        Assertions.assertEquals(rewardsResponseEntity.getBody().getPoints(), 0);
    }

    @Test
    void calculatePointsWithReadyData(){
        ResponseEntity<RewardsDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.REWARDS),
                List.of(transactionDto, transactionDto2), RewardsDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(responseEntity.getBody().getPoints(), 90);
    }

    @Test
    void calculatePointsWithEmptyReadyData(){
        ResponseEntity<RewardsDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.REWARDS),
                new ArrayList<>(), RewardsDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(responseEntity.getBody().getPoints(), 0);
    }

    private String getApiUrl(String uri) {
        return HOST + port + uri;
    }
}
