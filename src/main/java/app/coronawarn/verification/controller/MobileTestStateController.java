package app.coronawarn.verification.controller;


import app.coronawarn.verification.model.MobileTestPollingRequest;
import app.coronawarn.verification.model.MobileTestResultRequest;
import app.coronawarn.verification.model.TestResult;
import app.coronawarn.verification.monitoring.TestRequestMonitor;
import app.coronawarn.verification.service.FakeDelayService;
import app.coronawarn.verification.service.FakeRequestService;
import app.coronawarn.verification.service.TestResultServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
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

  @NonNull
  private final TestResultServerService testResultServerService;

  @NonNull
  private final FakeDelayService fakeDelayService;

  @NonNull
  private final FakeRequestService fakeRequestController;

  @NonNull
  private final TestRequestMonitor testRequestMonitor;

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

    if (mobileTestResultRequest.isFakeRequest()) {

      testRequestMonitor.incrementDummyTestRequest();
      return fakeRequestController.getTestState();

    } else {

      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      TestResult testResult = testResultServerService.pollTestResult(mobileTestResultRequest);

      if (testResult.isDummy()) {
        testRequestMonitor.incrementNonExistingTestRequest();
      } else if (testResult.isPositive()) {
        testRequestMonitor.incrementPositiveTestResponse();
      } else {
        testRequestMonitor.incrementNegativeTestResponse();
      }

      testResult.applyPadding();
      stopWatch.stop();
      fakeDelayService.updateFakeTestRequestDelay(stopWatch.getTotalTimeMillis());

      DeferredResult<ResponseEntity<TestResult>> deferredResult = new DeferredResult<>();
      deferredResult.setResult(ResponseEntity.ok(testResult));
      return deferredResult;

    }



  }

}
