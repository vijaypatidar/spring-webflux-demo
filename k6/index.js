import http from 'k6/http';
import { Rate, Counter } from 'k6/metrics';
import { check } from 'k6';
import { API_AUTH_TOKEN, API_ROOT_URL, NR_AUTH_TOKEN } from './config.js';
import { events } from './events.js';

const errorRate = new Rate("error_rate")
const httpReqCount = new Counter("http_req_count")
const httpFailedReqCount = new Counter("http_failed_req_count")

export const options = {
  insecureSkipTLSVerify: true,
  ext: {
    loadimpact: {
      apm: [
        {
          provider: 'prometheus',
          remoteWriteURL:
            'https://metric-api.newrelic.com/prometheus/v1/write?prometheus_server=k6-demo-loadtest',
          credentials: {
            token: NR_AUTH_TOKEN,
          },
          metrics: ['http_req_sending', 'error_rate', 'http_req_count', 'http_failed_req_count'],
          includeDefaultMetrics: true,
          includeTestRunId: true,
        },
      ],
    },
  },
};


export default function () {

  events.forEach((e)=>{
    const res = e.event();
    const isSuccess = check(res, {
      "Status is 2xx": (r) => 200 <= r.status && r.status <= 299
    })
    errorRate.add(!isSuccess);
    if (!isSuccess) {
      httpFailedReqCount.add(1);
    }
    httpReqCount.add(1);
  })

}
