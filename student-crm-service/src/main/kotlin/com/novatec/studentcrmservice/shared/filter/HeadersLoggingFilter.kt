package com.novatec.studentcrmservice.shared.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.UnsupportedEncodingException


//@Component
//class HeadersLoggingFilter : Filter {
//
//    private val logger = LoggerFactory.getLogger(this.javaClass)
//
//
//
//    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
//
//
//        val uniqueId = UUID.randomUUID().toString()
//        val req = request as HttpServletRequest
//        val requestWrapper = ContentCachingRequestWrapper(req)
//
//            MDC.put("X-Tracking-Id", uniqueId)
//
//        logger.info(
//            "Request is fired: {}",
//            requestWrapper.contentAsString
//            )
//
//        val httpServletResponse = response as HttpServletResponse
//        val responseWrapper = ContentCachingResponseWrapper(httpServletResponse)
//
//
//        chain.doFilter(request, responseWrapper)
//        responseWrapper.setHeader("X-Tracking-Id", uniqueId);
//        responseWrapper.copyBodyToResponse();
//        logger.info("Response header is set Trace-ID: {}", responseWrapper.getHeader("X-Tracking-Id"))
//    }
//}

@Component
class HttpLoggingFilter : OncePerRequestFilter() {

    companion object {
        private val logger = LoggerFactory.getLogger(this.javaClass)
    }

    private fun getStringValue(contentAsByteArray: ByteArray, characterEncoding: String): String {
        try {
            return String(contentAsByteArray, 0, contentAsByteArray.size, charset(characterEncoding))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)

        val startTime = System.currentTimeMillis()
        filterChain.doFilter(requestWrapper, responseWrapper)
        val timeTaken = System.currentTimeMillis() - startTime

        val requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding())
        val responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding())

        val trackingId = request.getHeader("trackingId")
        val actingUserId = request.getHeader("actingUserId")

        MDC.put("trackingId", trackingId)
        MDC.put("actingUserId", actingUserId)

        try {
            //filterChain.doFilter(requestWrapper, responseWrapper)

            logger.info("Request: ${request.method} ${request.requestURI} trackingId = $trackingId actingUserId = $actingUserId \n" + requestBody)
            logger.info("Response: ${response.status} trackingId = $trackingId actingUserId = $actingUserId \n" + responseBody + " Execution Time: " + timeTaken + "ms")
            responseWrapper.copyBodyToResponse()
        } finally {
            MDC.remove("trackingId")
            MDC.remove("actingUserId")
        }
    }
}
