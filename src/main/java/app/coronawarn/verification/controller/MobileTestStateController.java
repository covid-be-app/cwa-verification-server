package app.coronawarn.verification.controller;


import app.coronawarn.verification.model.MobileTestPollingRequest;
import app.coronawarn.verification.model.MobileTestResultRequest;
import app.coronawarn.verification.model.TestResult;
import app.coronawarn.verification.service.TestResultServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * This class represents the rest controller for requests regarding test states.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/version/v1")
@Validated
@Profile("external")
public class MobileTestStateController {

  /**
   * The route to the test status of the COVID-19 test endpoint.
   */
  public static final String TESTRESULT_POLL = "/testresult/poll";

  public static final Integer RESPONSE_PADDING_LENGTH = 45;

  @NonNull
  private final TestResultServerService testResultServerService;

  /**
   * Returns the test status of the COVID-19 test.
   *
   * @param mobileTestPollingRequest mobileTestId with datePatientInfectious{@link MobileTestPollingRequest}
x   * @return result of the test, which can be POSITIVE, NEGATIVE, INVALID, PENDING or FAILED will POSITIVE for TeleTan
   */
  @Operation(
    summary = "COVID-19 test result for given RegistrationToken",
    description = "Gets the result of COVID-19 Test. "
      + "If the RegistrationToken belongs to a TeleTan the result is always positive"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Testresult retrieved")})
  @PostMapping(value = TESTRESULT_POLL,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public DeferredResult<ResponseEntity<TestResult>> getTestState(
    @Valid @RequestBody MobileTestPollingRequest mobileTestPollingRequest) {

    MobileTestResultRequest mobileTestResultRequest = MobileTestResultRequest
      .fromMobileTestPollingRequest(mobileTestPollingRequest);

    TestResult testResult = null;

    if (mobileTestResultRequest.isFakeRequest()) {
      testResult = TestResult.dummyTestResult();
    } else {
      testResult = testResultServerService.pollTestResult(mobileTestResultRequest);
    }

    testResult.setResponsePadding(RandomStringUtils.randomAlphanumeric(RESPONSE_PADDING_LENGTH));

    log.debug("Result {}",testResult);
    log.info("The result for registration token based on hashed Guid will be returned.");

    DeferredResult<ResponseEntity<TestResult>> deferredResult = new DeferredResult<>();
    deferredResult.setResult(ResponseEntity.ok(testResult));
    return deferredResult;

  }

}
