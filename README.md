# Exchange Rate API

## Description
**Exchange Rate API** is a REST API for working with currency exchange rates. It provides functionalities to fetch exchange rates, convert values between currencies, and more. Data is sourced from [exchangerate.host](https://exchangerate.host) or [fixer.io](https://fixer.io).

## Features

### API Endpoints
1. **Get exchange rate between two currencies**
    - `GET /api/exchange-rates/{from}/{to}`
    - Example: `/api/exchange-rates/EUR/UAH`

2. **Get all exchange rates for a currency**
    - `GET /api/exchange-rates/{from}`
    - Example: `/api/exchange-rates/EUR`

3. **Convert value between two currencies**
    - `GET /api/exchange-rates/{from}/{to}/conversion?amount={value}`
    - Example: `/api/exchange-rates/EUR/UAH/conversion?amount=100`

4. **Convert value to multiple currencies**
    - `GET /api/exchange-rates/{from}/conversion?amount={value}&currencies={currencyList}`
    - Example: `/api/exchange-rates/EUR/conversion?amount=100&currencies=UAH,GBP,USD`


### Steps to Run
1. **Clone the repository**
   ```bash  
   git clone https://github.com/Mivmagul/exchange-rate.git
   ```

2. **Build and run the application**
   ```bash
   ./gradlew clean build
   docker-compose up --build

3. **Access Swagger UI**
   <p>Swagger documentation is available at:</p>
   
   ```bash
   http://localhost:8081/swagger-ui/index.html
