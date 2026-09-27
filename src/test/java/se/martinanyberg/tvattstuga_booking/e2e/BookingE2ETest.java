package se.martinanyberg.tvattstuga_booking.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class BookingE2ETest {

    private static final String BASE_URL =
            "http://localhost:8080";

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;


    /* ---------------- SETUP ---------------- */

    @BeforeAll
    static void launchBrowser() {

        playwright =
                Playwright.create();

        browser =
                playwright
                        .chromium()
                        .launch();
    }


    @BeforeEach
    void createContextAndPage() {

        context =
                browser.newContext();

        page =
                context.newPage();

        page.navigate(BASE_URL);
    }


    /* ---------------- CLEANUP ---------------- */

    @AfterEach
    void closeContext() {

        context.close();
    }


    @AfterAll
    static void closeBrowser() {

        browser.close();
        playwright.close();
    }


    /* ---------------- LOGIN-SIDA ---------------- */

    @Test
    void loginPage_isDisplayed() {

        assertThat(
                page.locator("#loginPage")
        ).isVisible();
    }


    /* ---------------- LOGIN ---------------- */

    @Test
    void user_canLogin() {

        login(
                page,
                "e2elogin@test.se"
        );

        assertThat(
                page.locator("#bookingPage")
        ).isVisible();

        assertThat(
                page.locator("#welcomeText")
        ).containsText(
                "e2elogin@test.se"
        );
    }


    /* ---------------- BOKA + AVBOKA ---------------- */

    @Test
    void user_canBookAndCancelBooking() {

        login(
                page,
                "e2ebooking@test.se"
        );


        Locator availableSlot =
                page.locator(
                        ".slot.available"
                ).first();


        assertThat(
                availableSlot
        ).isVisible();


        availableSlot.click();


        Locator bookingList =
                page.locator(
                        "#bookingList"
                );


        assertThat(
                bookingList.locator("li")
        ).hasCount(1);


        Locator cancelButton =
                bookingList
                        .locator("button")
                        .first();


        assertThat(
                cancelButton
        ).isVisible();


        cancelButton.click();


        assertThat(
                bookingList.locator("li")
        ).hasCount(0);
    }


    /* ---------------- MAX TVÅ BOKNINGAR ---------------- */

    @Test
    void user_cannotBookMoreThanTwoSlots() {

        login(
                page,
                "e2emaxtwo@test.se"
        );


        Locator firstAvailableSlot =
                page.locator(
                        ".slot.available"
                ).first();

        assertThat(
                firstAvailableSlot
        ).isVisible();

        firstAvailableSlot.click();


        Locator bookingList =
                page.locator(
                        "#bookingList"
                );

        assertThat(
                bookingList.locator("li")
        ).hasCount(1);


        Locator secondAvailableSlot =
                page.locator(
                        ".slot.available"
                ).first();

        assertThat(
                secondAvailableSlot
        ).isVisible();

        secondAvailableSlot.click();


        assertThat(
                bookingList.locator("li")
        ).hasCount(2);


        Locator thirdAvailableSlot =
                page.locator(
                        ".slot.available"
                ).first();

        assertThat(
                thirdAvailableSlot
        ).isVisible();


        thirdAvailableSlot.click();


        assertThat(
                bookingList.locator("li")
        ).hasCount(2);
    }


    /* ---------------- UPPTAGEN TID ---------------- */

    @Test
    void bookedSlot_isUnavailableForAnotherUser() {

        /*
         * ANVÄNDARE A
         */

        login(
                page,
                "e2eusera@test.se"
        );


        Locator availableSlot =
                page.locator(
                        ".slot.available"
                ).first();


        assertThat(
                availableSlot
        ).isVisible();


        String bookedDate =
                availableSlot.getAttribute(
                        "data-date"
                );

        String bookedTime =
                availableSlot.getAttribute(
                        "data-slot"
                );


        availableSlot.click();


        assertThat(
                page.locator("#bookingList li")
        ).hasCount(1);


        /*
         * ANVÄNDARE B
         *
         * Ny BrowserContext betyder ny localStorage
         * och därmed en separat inloggad användare.
         */

        try (
                BrowserContext secondContext =
                        browser.newContext()
        ) {

            Page secondPage =
                    secondContext.newPage();

            secondPage.navigate(
                    BASE_URL
            );


            login(
                    secondPage,
                    "e2euserb@test.se"
            );


            Locator bookedSlot =
                    secondPage.locator(
                            ".slot"
                                    + "[data-date='"
                                    + bookedDate
                                    + "']"
                                    + "[data-slot='"
                                    + bookedTime
                                    + "']"
                    );


            assertThat(
                    bookedSlot
            ).isVisible();


            assertThat(
                    bookedSlot
            ).containsClass("booked");


            assertThat(
                    secondPage.locator(
                            "#bookingList li"
                    )
            ).hasCount(0);


            bookedSlot.click();


            assertThat(
                    secondPage.locator(
                            "#bookingList li"
                    )
            ).hasCount(0);
        }
    }


    /* ---------------- HJÄLPMETOD LOGIN ---------------- */

    private void login(
            Page targetPage,
            String email
    ) {

        targetPage
                .locator("#userId")
                .fill(email);

        targetPage
                .locator("#loginButton")
                .click();

        assertThat(
                targetPage.locator(
                        "#bookingPage"
                )
        ).isVisible();

        assertThat(
                targetPage.locator(
                        "#welcomeText"
                )
        ).containsText(email);
    }
}