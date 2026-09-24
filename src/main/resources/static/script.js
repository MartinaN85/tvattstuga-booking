const loginPage = document.getElementById("loginPage");
const bookingPage = document.getElementById("bookingPage");
const loginButton = document.getElementById("loginButton");
const logoutButton = document.getElementById("logoutButton");
const bookingList = document.getElementById("bookingList");
const welcomeText = document.getElementById("welcomeText");
const calendar = document.getElementById("calendar");

const previousWeekButton = document.getElementById("previousWeek");
const nextWeekButton = document.getElementById("nextWeek");
const weekLabel = document.getElementById("weekLabel");

const timeSlots = [
    "08-11",
    "11-14",
    "14-17",
    "17-20"
];

const weekdayNames = [
    "Mån",
    "Tis",
    "Ons",
    "Tor",
    "Fre",
    "Lör",
    "Sön"
];

let currentWeekOffset = 0;


/* ---------------- LOGIN ---------------- */

checkLogin();

loginButton.addEventListener("click", async () => {

    const email =
        document.getElementById("userId").value.trim();

    if (email === "") {
        alert("Ange e-post");
        return;
    }

    try {

        const response = await fetch("/api/users/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email
            })
        });

        if (!response.ok) {

            const errorText =
                await response.text();

            console.error(
                "Login failed:",
                response.status,
                errorText
            );

            alert(
                "Ange en giltig e-postadress som slutar på .se, .com eller .nu"
            );

            return;
        }

        const user =
            await response.json();

        localStorage.setItem(
            "loggedInUser",
            user.email
        );

        await checkLogin();

    } catch (error) {

        console.error(
            "Login failed:",
            error
        );

        alert(
            "Kunde inte ansluta till servern"
        );
    }
});


logoutButton.addEventListener("click", () => {

    localStorage.removeItem(
        "loggedInUser"
    );

    location.reload();
});


async function checkLogin() {

    const user =
        localStorage.getItem(
            "loggedInUser"
        );

    if (!user) {

        loginPage.classList.remove(
            "hidden"
        );

        bookingPage.classList.add(
            "hidden"
        );

        return;
    }

    loginPage.classList.add(
        "hidden"
    );

    bookingPage.classList.remove(
        "hidden"
    );

    welcomeText.textContent =
        `Inloggad som: ${user}`;

    await refreshBookings();
}


/* ---------------- VECKONAVIGERING ---------------- */

previousWeekButton.addEventListener(
    "click",
    async () => {

        currentWeekOffset--;

        await createCalendar();
    }
);


nextWeekButton.addEventListener(
    "click",
    async () => {

        currentWeekOffset++;

        await createCalendar();
    }
);


/* ---------------- DATUM ---------------- */

function getMonday() {

    const today =
        new Date();

    const currentDay =
        today.getDay();

    const distanceToMonday =
        currentDay === 0
            ? -6
            : 1 - currentDay;

    const monday =
        new Date(today);

    monday.setHours(
        0,
        0,
        0,
        0
    );

    monday.setDate(
        today.getDate()
        + distanceToMonday
        + currentWeekOffset * 7
    );

    return monday;
}


function getWeekDays() {

    const monday =
        getMonday();

    const weekDays = [];

    for (let i = 0; i < 7; i++) {

        const date =
            new Date(monday);

        date.setDate(
            monday.getDate() + i
        );

        weekDays.push({
            name: weekdayNames[i],
            date: formatDate(date),
            displayDate:
                `${date.getDate()}/${date.getMonth() + 1}`
        });
    }

    return weekDays;
}


function formatDate(date) {

    const year =
        date.getFullYear();

    const month =
        String(
            date.getMonth() + 1
        ).padStart(2, "0");

    const day =
        String(
            date.getDate()
        ).padStart(2, "0");

    return `${year}-${month}-${day}`;
}


function formatBookingDate(dateString) {

    const date =
        new Date(
            dateString + "T00:00:00"
        );

    const weekday =
        weekdayNames[
            (date.getDay() + 6) % 7
        ];

    return `${weekday} ${date.getDate()}/${date.getMonth() + 1}`;
}


/* ---------------- API ---------------- */

async function getAllBookings() {

    try {

        const response =
            await fetch(
                "/api/bookings"
            );

        if (!response.ok) {

            console.error(
                "Could not load bookings:",
                response.status
            );

            return [];
        }

        return await response.json();

    } catch (error) {

        console.error(
            "Could not load bookings:",
            error
        );

        return [];
    }
}


async function getMyBookings() {

    const email =
        localStorage.getItem(
            "loggedInUser"
        );

    if (!email) {
        return [];
    }

    try {

        const response =
            await fetch(
                `/api/bookings/user?email=${encodeURIComponent(email)}`
            );

        if (!response.ok) {

            console.error(
                "Could not load user bookings:",
                response.status
            );

            return [];
        }

        return await response.json();

    } catch (error) {

        console.error(
            "Could not load user bookings:",
            error
        );

        return [];
    }
}


/* ---------------- KALENDER ---------------- */

async function createCalendar() {

    calendar.innerHTML = "";

    const bookings =
        await getAllBookings();

    const weekDays =
        getWeekDays();

    updateWeekLabel(
        weekDays
    );

    addHeader("Tid");

    weekDays.forEach(day => {

        addHeader(
            `${day.name} ${day.displayDate}`
        );
    });


    timeSlots.forEach(slot => {

        const timeCell =
            document.createElement(
                "div"
            );

        timeCell.className =
            "time";

        timeCell.textContent =
            slot;

        calendar.appendChild(
            timeCell
        );


        weekDays.forEach(day => {

            const cell =
                document.createElement(
                    "div"
                );

            cell.classList.add(
                "slot",
                "available"
            );

            cell.dataset.date =
                day.date;

            cell.dataset.slot =
                slot;


            const alreadyBooked =
                bookings.some(
                    booking =>
                        booking.date === day.date &&
                        booking.timeSlot === slot
                );


            if (alreadyBooked) {

                cell.classList.remove(
                    "available"
                );

                cell.classList.add(
                    "booked"
                );
            }


            cell.addEventListener(
                "click",
                async () => {

                    if (
                        cell.classList.contains(
                            "booked"
                        )
                    ) {
                        return;
                    }

                    await bookTime(
                        day.date,
                        slot
                    );
                }
            );

            calendar.appendChild(
                cell
            );
        });
    });
}


function addHeader(text) {

    const header =
        document.createElement(
            "div"
        );

    header.className =
        "header";

    header.textContent =
        text;

    calendar.appendChild(
        header
    );
}


function updateWeekLabel(weekDays) {

    const first =
        weekDays[0];

    const last =
        weekDays[6];

    weekLabel.textContent =
        `${first.displayDate} - ${last.displayDate}`;
}


/* ---------------- BOKA ---------------- */

async function bookTime(
    date,
    timeSlot
) {

    const email =
        localStorage.getItem(
            "loggedInUser"
        );

    if (!email) {

        alert(
            "Du måste vara inloggad"
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/bookings",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        date: date,
                        timeSlot: timeSlot,
                        userEmail: email
                    })
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            console.error(
                "Booking failed:",
                response.status,
                errorText
            );

            alert(
                "Bokningen kunde inte genomföras. Tiden kan redan vara bokad eller så har du redan två bokningar."
            );

            return;
        }


        await refreshBookings();

    } catch (error) {

        console.error(
            "Booking failed:",
            error
        );

        alert(
            "Kunde inte genomföra bokningen"
        );
    }
}


/* ---------------- MINA BOKNINGAR ---------------- */

async function updateBookings() {

    bookingList.innerHTML = "";

    const bookings =
        await getMyBookings();


    bookings.forEach(booking => {

        const li =
            document.createElement(
                "li"
            );

        const bookingText =
            document.createElement(
                "span"
            );

        bookingText.textContent =
            `${formatBookingDate(booking.date)} ${booking.timeSlot}`;


        const deleteButton =
            document.createElement(
                "button"
            );

        deleteButton.textContent =
            "Avboka";


        deleteButton.addEventListener(
            "click",
            async () => {

                await cancelBooking(
                    booking.id
                );
            }
        );


        li.appendChild(
            bookingText
        );

        li.appendChild(
            deleteButton
        );

        bookingList.appendChild(
            li
        );
    });
}


/* ---------------- AVBOKA ---------------- */

async function cancelBooking(id) {

    try {

        const response =
            await fetch(
                `/api/bookings/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            console.error(
                "Cancel booking failed:",
                response.status
            );

            alert(
                "Bokningen kunde inte avbokas"
            );

            return;
        }


        await refreshBookings();

    } catch (error) {

        console.error(
            "Cancel booking failed:",
            error
        );

        alert(
            "Kunde inte avboka bokningen"
        );
    }
}


/* ---------------- UPPDATERA SIDAN ---------------- */

async function refreshBookings() {

    await createCalendar();

    await updateBookings();
}