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
let myBookings = [];


/* ---------------- LOGIN ---------------- */

checkLogin();

loginButton.addEventListener("click", () => {

    const userId = document.getElementById("userId").value.trim();

    if (userId === "") {
        alert("Ange e-post");
        return;
    }

    localStorage.setItem("loggedInUser", userId);

    checkLogin();
});


logoutButton.addEventListener("click", () => {

    localStorage.removeItem("loggedInUser");

    location.reload();
});


function checkLogin() {

    const user = localStorage.getItem("loggedInUser");

    if (!user) {
        loginPage.classList.remove("hidden");
        bookingPage.classList.add("hidden");
        return;
    }

    loginPage.classList.add("hidden");
    bookingPage.classList.remove("hidden");

    welcomeText.textContent = `Inloggad som: ${user}`;

    createCalendar();
    updateBookings();
}


/* ---------------- VECKONAVIGERING ---------------- */

previousWeekButton.addEventListener("click", () => {

    currentWeekOffset--;

    createCalendar();
});


nextWeekButton.addEventListener("click", () => {

    currentWeekOffset++;

    createCalendar();
});


/* ---------------- DATUM ---------------- */

function getMonday() {

    const today = new Date();

    const currentDay = today.getDay();

    const distanceToMonday =
        currentDay === 0
            ? -6
            : 1 - currentDay;

    const monday = new Date(today);

    monday.setHours(0, 0, 0, 0);

    monday.setDate(
        today.getDate()
        + distanceToMonday
        + currentWeekOffset * 7
    );

    return monday;
}


function getWeekDays() {

    const monday = getMonday();

    const weekDays = [];

    for (let i = 0; i < 7; i++) {

        const date = new Date(monday);

        date.setDate(monday.getDate() + i);

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

    const year = date.getFullYear();

    const month =
        String(date.getMonth() + 1)
            .padStart(2, "0");

    const day =
        String(date.getDate())
            .padStart(2, "0");

    return `${year}-${month}-${day}`;
}


/* ---------------- KALENDER ---------------- */

function createCalendar() {

    calendar.innerHTML = "";

    const weekDays = getWeekDays();

    updateWeekLabel(weekDays);

    addHeader("Tid");

    weekDays.forEach(day => {

        addHeader(
            `${day.name} ${day.displayDate}`
        );

    });


    timeSlots.forEach(slot => {

        const timeCell =
            document.createElement("div");

        timeCell.className = "time";
        timeCell.textContent = slot;

        calendar.appendChild(timeCell);


        weekDays.forEach(day => {

            const cell =
                document.createElement("div");

            cell.classList.add(
                "slot",
                "available"
            );

            cell.dataset.date = day.date;
            cell.dataset.day = day.name;
            cell.dataset.displayDate = day.displayDate;
            cell.dataset.slot = slot;


            const alreadyBooked =
                myBookings.some(booking =>
                    booking.date === day.date &&
                    booking.slot === slot
                );


            if (alreadyBooked) {
                cell.classList.remove("available");
                cell.classList.add("booked");
            }


            cell.addEventListener("click", () => {

                if (cell.classList.contains("booked")) {
                    return;
                }

                myBookings.push({
                    day: day.name,
                    date: day.date,
                    displayDate: day.displayDate,
                    slot: slot
                });

                createCalendar();

                updateBookings();
            });


            calendar.appendChild(cell);
        });
    });
}


function addHeader(text) {

    const header =
        document.createElement("div");

    header.className = "header";

    header.textContent = text;

    calendar.appendChild(header);
}


function updateWeekLabel(weekDays) {

    const first = weekDays[0];
    const last = weekDays[6];

    weekLabel.textContent =
        `${first.displayDate} - ${last.displayDate}`;
}


/* ---------------- MINA BOKNINGAR ---------------- */

function updateBookings() {

    bookingList.innerHTML = "";

    myBookings.forEach((booking, index) => {

        const li =
            document.createElement("li");

        const bookingText =
            document.createElement("span");

        bookingText.textContent =
            `${booking.day} ${booking.displayDate} ${booking.slot}`;


        const deleteButton =
            document.createElement("button");

        deleteButton.textContent = "Avboka";


        deleteButton.addEventListener("click", () => {

            myBookings.splice(index, 1);

            createCalendar();

            updateBookings();
        });


        li.appendChild(bookingText);

        li.appendChild(deleteButton);

        bookingList.appendChild(li);
    });
}
