package com.novatec.studentcrmservice.shared.filter

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

@Component
class RequestLogInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {

        val headers = request.headers
        headers.add("Application-Name", "Student-CRM")
        headers.add("Content-Type", "application/json");



        return execution.execute(request, body)
    }
}
