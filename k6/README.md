# K6 load test

```shell
k6 run index.js -e API_AUTH_TOKEN="Bearer" --vus 10 --duration 2m
```