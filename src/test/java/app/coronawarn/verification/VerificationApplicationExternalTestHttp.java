/*
 * Corona-Warn-App / cwa-verification
 *
 * (C) 2020, T-Systems International GmbH
 * All modifications are copyright (c) 2020 Devside SRL.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class VerificationApplicationExternalTestHttp {

  @LocalServerPort
  private int port;

  @Test
  public void testChunkedModeIsDenied() throws IOException {
    Socket socket = new Socket(InetAddress.getLocalHost(), port);
    PrintWriter writer = new PrintWriter(socket.getOutputStream());

    writer.print("POST /verification-api/version/v1/registrationToken HTTP/1.1\r\n");
    writer.print("Host: 127.0.0.1:" + port + "\r\n");
    writer.print("Content-Type: application/json\r\n");
    writer.print("Transfer-Encoding: Chunked\r\n\r\n");
    writer.flush();

    writer.print("{ \"randomBody\": 42 }");
    writer.flush();

    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    Assert.assertEquals("HTTP/1.1 406", reader.readLine().trim());

    reader.close();
    socket.close();
  }

}
