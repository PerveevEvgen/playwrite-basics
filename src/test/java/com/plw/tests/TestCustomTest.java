package com.plw.tests;

import com.microsoft.playwright.*;

import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class TestCustomTest {
    @Test
    public void test() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.google.com/");
            page.getByLabel("Найти").click();
            page.getByLabel("Найти").fill("playwright");
            page.getByLabel("Поиск в Google").first().click();
            assertThat(page.locator("#rso")).containsText("https://playwright.dev");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Java").setExact(true)).click();
            assertThat(page.getByRole(AriaRole.CONTENTINFO)).containsText("Copyright © 2024 Microsoft");
        }
    }

    }