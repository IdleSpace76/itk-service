package ru.task.itk_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.task.itk_service.dto.WalletBalanceResponse;
import ru.task.itk_service.dto.WalletOperationRequest;
import ru.task.itk_service.dto.WalletOperationResponse;
import ru.task.itk_service.dto.enums.OperationType;
import ru.task.itk_service.exception.ApiErrorResponse;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author a.zharov
 */
@ActiveProfiles("test")
// Поднимает полный контекст приложения на случайном порте
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletEndpointTest {

    // Из changelog-seed.xml
    private static final UUID SEEDED_WALLET =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final BigDecimal SEED_BALANCE = new BigDecimal("1000.00");

    // Инсерт сгенерированного порта
    @LocalServerPort
    int port;

    // Шлет запросы в реальное приложение
    @Autowired
    TestRestTemplate rest;

    // для управления данными в бд
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetSeedWalletBalance() {
        // 1) Попробуем обновить
        int updated = jdbcTemplate.update(
                "UPDATE wallet SET balance = ? WHERE id = ?",
                SEED_BALANCE,
                SEEDED_WALLET
        );

        // 2) Если кошелька нет - добавим в бд
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO wallet (id, balance) VALUES (?, ?)",
                    SEEDED_WALLET,
                    SEED_BALANCE
            );
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void getBalance_seededWallet_ok() {
        ResponseEntity<WalletBalanceResponse> resp = rest.getForEntity(
                url("/api/v1/wallets/" + SEEDED_WALLET),
                WalletBalanceResponse.class
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getWalletId()).isEqualTo(SEEDED_WALLET);
        assertThat(resp.getBody().getBalance()).isNotNull();
    }

    @Test
    void postDeposit_ok_returnsOperationResponse() {
        WalletOperationRequest req = new WalletOperationRequest();
        req.setWalletId(SEEDED_WALLET);
        req.setOperationType(OperationType.DEPOSIT);
        req.setAmount(new BigDecimal("10.00"));

        ResponseEntity<WalletOperationResponse> resp =
                rest.postForEntity(url("/api/v1/wallet"), req, WalletOperationResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();

        WalletOperationResponse body = resp.getBody();
        assertThat(body.getWalletId()).isEqualTo(SEEDED_WALLET);
        assertThat(body.getOperationType()).isEqualTo(OperationType.DEPOSIT);
        assertThat(body.getAmount()).isEqualByComparingTo("10.00");
        assertThat(body.getBalanceBefore()).isNotNull();
        assertThat(body.getBalanceAfter()).isNotNull();
    }

    @Test
    void postWithdraw_insufficientFunds_conflict_returnsApiError() {
        WalletOperationRequest req = new WalletOperationRequest();
        req.setWalletId(SEEDED_WALLET);
        req.setOperationType(OperationType.WITHDRAW);
        req.setAmount(new BigDecimal("9999999.00"));

        ResponseEntity<ApiErrorResponse> resp =
                rest.postForEntity(url("/api/v1/wallet"), req, ApiErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody()).isNotNull();

        ApiErrorResponse err = resp.getBody();
        assertThat(err.getError()).isEqualTo("INSUFFICIENT_FUNDS");
        assertThat(err.getMessage()).isNotBlank();
        assertThat(err.getTimestamp()).isNotNull();
        assertThat(err.getPath()).contains("/api/v1/wallet");
    }

    @Test
    void getBalance_unknownWallet_notFound_returnsApiError() {
        UUID unknown = UUID.fromString("22222222-2222-2222-2222-222222222222");

        ResponseEntity<ApiErrorResponse> resp = rest.getForEntity(
                url("/api/v1/wallets/" + unknown),
                ApiErrorResponse.class
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isNotNull();

        ApiErrorResponse err = resp.getBody();
        assertThat(err.getError()).isEqualTo("WALLET_NOT_FOUND");
        assertThat(err.getTimestamp()).isNotNull();
        assertThat(err.getPath()).contains("/api/v1/wallets/");
    }

    @Test
    void postWallet_validationError_amountMissing_badRequest_returnsApiError() {
        WalletOperationRequest req = new WalletOperationRequest();
        req.setWalletId(SEEDED_WALLET);
        req.setOperationType(OperationType.DEPOSIT);

        ResponseEntity<ApiErrorResponse> resp =
                rest.postForEntity(url("/api/v1/wallet"), req, ApiErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getError()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void postWallet_invalidJson_badRequest_returnsApiError() {
        String rawJson = "{\"walletId\":\"" + SEEDED_WALLET + "\",\"operationType\":\"DEPOSIT\",\"amount\":}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiErrorResponse> resp = rest.exchange(
                url("/api/v1/wallet"),
                HttpMethod.POST,
                new HttpEntity<>(rawJson, headers),
                ApiErrorResponse.class
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getError()).isEqualTo("INVALID_JSON");
    }
}
