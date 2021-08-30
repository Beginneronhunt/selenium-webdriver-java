/*
 * (C) Copyright 2021 Boni Garcia (https://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.webdriver.jupiter.ch5.caps.push_notifications;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;

import io.github.bonigarcia.wdm.WebDriverManager;

class PushNotificationsEdgeJupiterTest {

    static final Logger log = getLogger(lookup().lookupClass());

    WebDriver driver;

    @BeforeEach
    void setup() {
        EdgeOptions options = new EdgeOptions();
        options.setExperimentalOption("prefs", Map
                .of("profile.default_content_setting_values.notifications", 1));

        driver = WebDriverManager.edgedriver().capabilities(options).create();
    }

    @AfterEach
    void teardown() throws InterruptedException {
        // FIXME: pause for manual browser inspection
        Thread.sleep(Duration.ofSeconds(3).toMillis());

        driver.quit();
    }
    @Test
    void testPushNotifications() {
        driver.get(
                "https://bonigarcia.dev/selenium-webdriver-java/push-notifications.html");
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = String.join("\n",
                "const callback = arguments[arguments.length - 1];",
                "const originalNotification = window.Notification;",
                "window.Notification = function(title, options) {",
                "    const newNotification = new originalNotification(title, options);",
                "    newNotification.onshow = function() {",
                "        window.Notification = originalNotification;",
                "        callback(newNotification.body);", "    }", "}",
                "document.getElementById('notify-me').click();");
        log.debug("Executing the following script asynchronously:\n{}", script);

        var notificationBody = js.executeAsyncScript(script);
        assertThat(notificationBody).isEqualTo("Hey there!");
    }

}