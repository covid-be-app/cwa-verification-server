/*
 * Corona-Warn-App / cwa-testresult-server
 *
 * (C) 2020, T-Systems International GmbH
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

package app.coronawarn.verification.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Request model of the test result.
 */
@Schema(
  description = "The test result request model."
)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobileTestResultRequest {

  @NotNull
  @Pattern(regexp = "^[0-9]{15}")
  private String mobileTestId;

  @NotNull
  private LocalDate datePatientInfectious;

  /**
   * Converts a MobileTestPollingRequest into a MobileTestResultRequest.
   *
   * @param mobileTestPollingRequest the request containing the token
   * @return
   */
  public static MobileTestResultRequest fromMobileTestPollingRequest(
    MobileTestPollingRequest mobileTestPollingRequest) {

    String token = mobileTestPollingRequest.getTestResultPollingToken();
    return new MobileTestResultRequest(
      token.split("\\|")[0],
      LocalDate.parse(token.split("\\|")[1])
    );

  }

}
