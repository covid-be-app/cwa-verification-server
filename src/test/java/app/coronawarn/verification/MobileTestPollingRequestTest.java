package app.coronawarn.verification;

import app.coronawarn.verification.model.MobileTestPollingRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class MobileTestPollingRequestTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

  }

  @Test
  public void requestContainsValidPayload() {
    MobileTestPollingRequest mobileTestPollingRequest = new MobileTestPollingRequest();
    mobileTestPollingRequest.setTestResultPollingToken("123456789012345|2020-05-04");
    Set<ConstraintViolation<MobileTestPollingRequest>> constraintViolations = validator.validate(mobileTestPollingRequest);
    assertThat(constraintViolations).hasSize(0);
  }

  @Test
  public void requestContainsInValidDatePart() {
    MobileTestPollingRequest mobileTestPollingRequest = new MobileTestPollingRequest();
    mobileTestPollingRequest.setTestResultPollingToken("123456789012345|2020-0504");
    Set<ConstraintViolation<MobileTestPollingRequest>> constraintViolations = validator.validate(mobileTestPollingRequest);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  public void requestContainsInValidMobileTestIdPart() {
    MobileTestPollingRequest mobileTestPollingRequest = new MobileTestPollingRequest();
    mobileTestPollingRequest.setTestResultPollingToken("1234567890123|2020-05-04");
    Set<ConstraintViolation<MobileTestPollingRequest>> constraintViolations = validator.validate(mobileTestPollingRequest);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  public void requestContainsCharactersInMobileTestIdPart() {
    MobileTestPollingRequest mobileTestPollingRequest = new MobileTestPollingRequest();
    mobileTestPollingRequest.setTestResultPollingToken("abcdefghijklmno|2020-05-04");
    Set<ConstraintViolation<MobileTestPollingRequest>> constraintViolations = validator.validate(mobileTestPollingRequest);
    assertThat(constraintViolations).hasSize(1);
  }

}
