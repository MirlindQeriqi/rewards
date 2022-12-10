package com.example.reward.web;

import com.example.reward.constants.PathConstants;
import com.example.reward.core.repository.TransactionRepository;
import com.example.reward.dto.TransactionDto;
import com.example.reward.exceptions.dto.MessageExceptionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private static TestRestTemplate testRestTemplate;
    private static TransactionDto transactionDto;
    private static TransactionDto transactionDto2;
    private static Long customerId = 50L;

    @LocalServerPort
    private int port;

    private static final String HOST = "http://localhost:";

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

    @AfterEach
    public void afterEach(){
        transactionRepository.deleteAll();
    }

    @Test
    void testCreateTransaction(){
        ResponseEntity<TransactionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto, TransactionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(transactionDto.getCustomerId(), responseEntity.getBody().getCustomerId());
        Assertions.assertEquals(transactionDto.getAmount(), responseEntity.getBody().getAmount());
    }

    @Test
    void testCreateTransactionWithNullBody(){
        ResponseEntity<MessageExceptionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                null, MessageExceptionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().getMessage());
    }

    @Test
    void testCreateTransactionWithEmptyFields(){
        ResponseEntity<MessageExceptionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                new TransactionDto(), MessageExceptionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().getMessage());
    }

    @Test
    void testGetTransactionById(){
        ResponseEntity<TransactionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto, TransactionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());

        ResponseEntity<TransactionDto> getResponseEntity = testRestTemplate.getForEntity(
                getApiUrl(PathConstants.TRANSACTIONS + "/" + responseEntity.getBody().getId()), TransactionDto.class);

        Assertions.assertEquals(getResponseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(getResponseEntity.getBody());
        Assertions.assertEquals(getResponseEntity.getBody().getCustomerId(), responseEntity.getBody().getCustomerId());
    }

    @Test
    void testGetTransactionByIdNotFound(){
        ResponseEntity<MessageExceptionDto> getResponseEntity = testRestTemplate.getForEntity(
                getApiUrl(PathConstants.TRANSACTIONS + "/" + customerId), MessageExceptionDto.class);

        Assertions.assertEquals(getResponseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(getResponseEntity.getBody());
        Assertions.assertNotNull(getResponseEntity.getBody().getMessage());
    }

    @Test
    void testDeleteTransaction(){
        ResponseEntity<TransactionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto, TransactionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(transactionDto.getCustomerId(), responseEntity.getBody().getCustomerId());
        Assertions.assertEquals(transactionDto.getAmount(), responseEntity.getBody().getAmount());

        testRestTemplate.delete(getApiUrl(PathConstants.TRANSACTIONS + "/" + responseEntity.getBody().getId()));

        Assertions.assertTrue(transactionRepository.findById(responseEntity.getBody().getId()).isEmpty());
    }

    @Test
    void testDeleteTransactionNotFound(){
        ResponseEntity<MessageExceptionDto> responseEntity = testRestTemplate.exchange(
                getApiUrl(PathConstants.TRANSACTIONS + "/" + customerId),
                HttpMethod.DELETE,
                new HttpEntity<>(new HttpHeaders()),
                MessageExceptionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().getMessage());
    }

    @Test
    void testGetTransactionsByCustomerIdAndDateRange(){
        ResponseEntity<TransactionDto> responseEntity = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto, TransactionDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity.getBody());

        ResponseEntity<TransactionDto> responseEntity2 = testRestTemplate.postForEntity(getApiUrl(PathConstants.TRANSACTIONS),
                transactionDto2, TransactionDto.class);

        Assertions.assertEquals(responseEntity2.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseEntity2.getBody());

        String urlWithParams = UriComponentsBuilder.fromHttpUrl(getApiUrl(PathConstants.TRANSACTIONS))
                .queryParam("customerId", customerId)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().toString())
                .toUriString();

        ResponseEntity<TransactionDto[]> getResponseEntity = testRestTemplate.getForEntity(urlWithParams, TransactionDto[].class);

        Assertions.assertEquals(getResponseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(getResponseEntity.getBody());

        List<TransactionDto> transactionDtoList = List.of(getResponseEntity.getBody());

        Assertions.assertEquals(transactionDtoList.size(), 2);
    }

    private String getApiUrl(String uri) {
        return HOST + port + uri;
    }
}
