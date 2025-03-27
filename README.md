exchange-rate

samples for local testing:
curl 'http://localhost:8081/api/exchange/rates/EUR'
curl 'http://localhost:8081/api/exchange/rate/EUR/UAH'
curl 'http://localhost:8081/api/exchange/convert/EUR/UAH?amount=100'
curl 'http://localhost:8081/api/exchange/convert/EUR?amount=100&currencies=USD,UAH,GBP'