package com.server.crews.auth.acceptance;

import com.server.crews.auth.dto.request.NewApplicantRequest;
import com.server.crews.auth.dto.request.NewRecruitmentRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.environ.acceptance.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode201;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_SECRET_CODE;
import static com.server.crews.fixture.TokenFixture.RECRUITMENT_ID_AND_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지원서 양식에 대한 코드를 생성하고 토큰을 발급 받는다.")
    void createRecruitmentSecretCode() {
        // given & when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new NewRecruitmentRequest(DEFAULT_SECRET_CODE))
                .when().post("/auth/recruitment/secret-code")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        TokenResponse tokenResponse = response.as(TokenResponse.class);
        Map<String, String> cookies = response.cookies();

        // then
        assertAll(() -> {
            checkStatusCode201(response);
            assertThat(tokenResponse.accessToken()).isNotEmpty();
            assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }

    @Test
    @DisplayName("지원서(지원자)에 대한 코드를 생성하고 토큰을 발급 받는다.")
    void createApplicationSecretCode() {
        // given & when
        Long recruitmentId = RECRUITMENT_ID_AND_ACCESS_TOKEN().id();
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new NewApplicantRequest(DEFAULT_SECRET_CODE, recruitmentId))
                .when().post("/auth/applicant/secret-code")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        TokenResponse tokenResponse = response.as(TokenResponse.class);
        Map<String, String> cookies = response.cookies();

        // then
        assertAll(() -> {
            checkStatusCode201(response);
            assertThat(tokenResponse.accessToken()).isNotEmpty();
            assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }
}
