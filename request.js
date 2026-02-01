fetch("http://localhost/dev-api/$ware/purchases", {
    "headers": {
        "accept": "application/json, text/plain, */*",
        "accept-language": "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
        "authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImxvZ2luX3VzZXJfa2V5IjoiNWNlNDE4MzgtMjFkMS00YTMxLTg0MmItNTI5ZTY2MjZlNDEyIn0.CqO6_wBgxXmooN61qCljhPfIuGway-vWIdm_C0bq03RoJK-sCyjKX_NYgzJtUSXy8LdCASITDFSZocywogmnsQ",
        "cache-control": "no-cache",
        "content-type": "application/json;charset=UTF-8",
        "pragma": "no-cache",
        "sec-ch-ua": "\"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Microsoft Edge\";v=\"144\"",
        "sec-ch-ua-mobile": "?1",
        "sec-ch-ua-platform": "\"Android\"",
        "sec-fetch-dest": "empty",
        "sec-fetch-mode": "cors",
        "sec-fetch-site": "same-origin",
        "cookie": "Idea-3092be33=9e7b4db9-b0da-46db-83a1-2f96cd29dcbe; username=admin; rememberMe=true; password=jGhKAXtCQU+6wO6wyTqTfpkYpM3Lw8yZzUpwzyU/kmBpLtzEy1+yIqV38/cJjFXEOnDaFeJR6FwOB0Kk+yty1w==; Admin-Token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImxvZ2luX3VzZXJfa2V5IjoiNWNlNDE4MzgtMjFkMS00YTMxLTg0MmItNTI5ZTY2MjZlNDEyIn0.CqO6_wBgxXmooN61qCljhPfIuGway-vWIdm_C0bq03RoJK-sCyjKX_NYgzJtUSXy8LdCASITDFSZocywogmnsQ; sidebarStatus=0",
        "Referer": "http://localhost/ware/procurement/purchase"
    },
    "body": "{\"assigneeId\":2,\"assigneeName\":\"若依\",\"phone\":\"15666666666\",\"priority\":1,\"status\":0,\"wareId\":\"2017864540725407746\",\"amount\":100,\"createTime\":\"2026-02-01\"}",
    "method": "POST"
});