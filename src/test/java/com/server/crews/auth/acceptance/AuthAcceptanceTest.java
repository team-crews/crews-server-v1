package com.server.crews.auth.acceptance;

import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.environ.acceptance.AcceptanceTest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

// Todo: 실패 케이스 테스트 추가
public class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("[동아리 관리자] 가입하지 않은 동아리 관리자가 로그인 해 토큰을 발급 받는다.")
    void loginNotSignedUpAdmin() {
        // given
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/admin/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        AccessTokenResponse accessTokenResponse = response.as(AccessTokenResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(accessTokenResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }

    @Test
    @DisplayName("[동아리 관리자] 가입한 동아리 관리자가 로그인 해 토큰을 발급 받는다.")
    void loginSignedUpAdmin() {
        // given
        signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/admin/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        AccessTokenResponse accessTokenResponse = response.as(AccessTokenResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(accessTokenResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }

    @Test
    @DisplayName("[지원자] 가입하지 않은 지원자가 로그인 해 토큰을 발급 받는다.")
    void loginNotSignedUpApplicant() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());

        ApplicantLoginRequest applicantLoginRequest = new ApplicantLoginRequest(recruitmentDetailsResponse.code(), TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(applicantLoginRequest)
                .when().post("/auth/applicant/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        AccessTokenResponse applicantTokenResponse = response.as(AccessTokenResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicantTokenResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }
}
