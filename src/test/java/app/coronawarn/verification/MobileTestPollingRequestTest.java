/*
 * Coronalert / cwa-verification
 *
 * (c) 2020 Devside SRL
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
