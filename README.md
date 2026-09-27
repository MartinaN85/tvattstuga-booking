# Tvättstuga Booking

Tvättstuga Booking är en webbapplikation för bokning av tvättider.

Applikationen är utvecklad med Java och Spring Boot på backend samt HTML, CSS och JavaScript på frontend. Användaren kan logga in med sin e-postadress, se tillgängliga tvättider, boka tider, se sina bokningar och avboka befintliga bokningar.

Projektet innehåller automatiserade tester på flera nivåer samt en CI/CD-pipeline med GitHub Actions. Applikationen driftsätts i separata utvecklings- och produktionsmiljöer på Render.

## Live-miljöer

### Produktion

Den produktionssatta versionen av applikationen finns här:

[Öppna Tvättstuga Booking Production](https://tvattstuga-booking-prod.onrender.com/)

### Utvecklingsmiljö

Utvecklingsversionen av applikationen finns här:

[Öppna Tvättstuga Booking Development](https://tvattstuga-booking-dev.onrender.com)

## Funktionalitet

Applikationen innehåller bland annat följande funktioner:

- Inloggning med e-postadress
- Validering av e-postadress
- Visning av tillgängliga tvättider
- Bokning av lediga tider
- Visning av användarens bokningar
- Avbokning av bokningar
- Förhindrande av dubbelbokningar
- Max två aktiva bokningar per användare
- Bokade tider visas som upptagna för andra användare

## Tekniker

Projektet använder bland annat:

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- REST API
- H2 Database
- Maven

### Frontend

- HTML
- CSS
- JavaScript

### Testning

- JUnit
- Mockito
- Spring Boot Test
- MockMvc
- Playwright
- Chromium

### CI/CD och deployment

- Git
- GitHub
- GitHub Actions
- Docker
- Render

## Köra applikationen lokalt

### Förutsättningar

För att köra projektet lokalt behövs Java 17.

Klona repositoryt och gå till projektets rot.

Starta Spring Boot-applikationen med:

```bash
./mvnw spring-boot:run
```

När applikationen har startat öppnas den lokalt på:

`http://localhost:8080`

## Tester

Projektet innehåller tester på flera nivåer.

### Enhetstester

Enhetstester används för att testa backend-logiken isolerat.

Exempel på testade delar är:

- Bokningslogik
- Användarlogik
- Begränsning av antal bokningar
- Hantering av upptagna tider

### Integrationstester

Integrationstester verifierar samspelet mellan flera delar av backend, bland annat:

- REST API
- Controller
- Service
- Repository
- H2-databas
- Validering

### Köra enhets- och integrationstester

Kör:

```bash
./mvnw test
```

### End-to-End-tester

Projektet innehåller E2E-tester med Playwright och Chromium.

E2E-testerna verifierar användarflödet genom hela applikationen:

```text
Webbläsare
    ↓
HTML / JavaScript
    ↓
REST API
    ↓
Spring Boot
    ↓
Databas
```

Bland annat testas:

- Att login-sidan visas
- Att en användare kan logga in
- Att en användare kan boka en ledig tid
- Att bokningen visas under Mina bokningar
- Att bokningen kan avbokas
- Att en användare inte kan ha fler än två bokningar
- Att en redan bokad tid är upptagen för en annan användare

För att köra E2E-testerna lokalt måste applikationen först vara igång.

Starta applikationen i en terminal:

```bash
./mvnw spring-boot:run
```

Kör sedan E2E-testerna i en annan terminal:

```bash
./mvnw -Dtest=BookingE2E test
```

## CI/CD

Projektet använder GitHub Actions för Continuous Integration och Continuous Deployment.

Workflows finns i:

```text
.github/workflows/
```

Projektet har separata workflows för CI, utvecklingsdeployment och produktionsdeployment.

### Continuous Integration

När en Pull Request skapas mot `dev` kör GitHub Actions automatiserade kontroller.

CI-flödet innehåller:

```text
Pull Request mot dev
        ↓
Build
        ↓
Enhetstester
        ↓
Integrationstester
        ↓
E2E-tester
        ↓
Godkänd eller underkänd pipeline
```

De vanliga testerna byggs och körs med Maven.

E2E-jobbet:

1. Bygger applikationen.
2. Installerar Playwright och Chromium.
3. Startar Spring Boot-applikationen.
4. Väntar tills applikationen svarar.
5. Kör de automatiserade E2E-testerna med Playwright.

Kod kan därefter mergas till `dev` när kontrollerna är godkända.

## Development deployment

Branch `dev` används som utvecklingsbranch.

När kod mergas till `dev` triggas GitHub Actions-workflowen för utvecklingsmiljön:

```text
Feature branch
      ↓
Pull Request
      ↓
CI + tester
      ↓
dev
      ↓
GitHub Actions
      ↓
development environment
      ↓
Render Deploy Hook
      ↓
Render Development
```

Utvecklingsmiljön:

[Öppna Development](https://tvattstuga-booking-dev.onrender.com)

GitHub-environmentet `development` är kopplat till utvecklingsdeploymenten. En Render Deploy Hook lagras som en GitHub Environment Secret och används av GitHub Actions för att trigga deploymenten.

## Production deployment

Branch `main` används som produktionsbranch och är projektets default branch.

När färdig och testad kod förs från `dev` till `main` triggas produktionsdeploymenten:

```text
dev
 ↓
Pull Request
 ↓
main
 ↓
GitHub Actions
 ↓
production environment
 ↓
Render Deploy Hook
 ↓
Render Production
```

Produktionsmiljön:

[Öppna Production](https://tvattstuga-booking-prod.onrender.com/)

GitHub-environmentet `production` använder en separat Render Deploy Hook för produktionsmiljön.

## Branch-strategi

Projektet använder ett branchbaserat arbetsflöde.

```text
feature/*
    ↓
Pull Request
    ↓
dev
    ↓
Development deployment
    ↓
Pull Request
    ↓
main
    ↓
Production deployment
```

Ny funktionalitet och ändringar utvecklas på separata feature branches.

Exempel:

```text
feature/e2e-tests
feature/ci-cd
feature/deployment
feature/deploy-dev
feature/deploy-prod
```

Ändringar granskas och testas genom Pull Requests innan de förs vidare.

`dev` används för utvecklingsmiljön och `main` används för den produktionssatta versionen.

## Docker

Applikationen använder Docker för deployment på Render.

Dockerfilen bygger Spring Boot-applikationen med Java 17 och Maven och startar därefter den paketerade JAR-filen.

```text
Source code
    ↓
Docker build
    ↓
Maven package
    ↓
Spring Boot JAR
    ↓
Docker container
    ↓
Render
```

## Projektstruktur

En förenklad bild av projektets struktur:

```text
tvattstuga-booking/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       ├── deploy-dev.yml
│       └── deploy-prod.yml
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── static/
│   │
│   └── test/
│
├── Dockerfile
├── pom.xml
├── mvnw
└── README.md
```

## Sammanfattning av CI/CD-flödet

Det kompletta arbetsflödet ser ut så här:

```text
Feature branch
      ↓
Pull Request → dev
      ↓
Build
      ↓
Unit tests
      ↓
Integration tests
      ↓
Automatiserade E2E-tester
      ↓
Merge → dev
      ↓
Automatisk development deployment
      ↓
Render DEV
      ↓
Pull Request dev → main
      ↓
Merge → main
      ↓
Automatisk production deployment
      ↓
Render PROD
```

På detta sätt kontrolleras applikationen genom automatiserade tester innan koden förs vidare, samtidigt som utvecklings- och produktionsmiljöerna hålls separerade.# tvattstuga-booking