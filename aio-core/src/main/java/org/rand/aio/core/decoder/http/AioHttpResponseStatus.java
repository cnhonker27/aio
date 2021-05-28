/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.rand.aio.core.decoder.http;

/**
 * The response code and its description of HTTP or its derived protocols, such as
 * <a href="http://en.wikipedia.org/wiki/Real_Time_Streaming_Protocol">RTSP</a> and
 * <a href="http://en.wikipedia.org/wiki/Internet_Content_Adaptation_Protocol">ICAP</a>.
 * @apiviz.exclude
 * Netty 源码  ^_^ !
 */
public class AioHttpResponseStatus implements Comparable<AioHttpResponseStatus> {

    /**
     * 100 Continue
     */
    public static final AioHttpResponseStatus CONTINUE = new AioHttpResponseStatus(100, "Continue");

    /**
     * 101 Switching Protocols
     */
    public static final AioHttpResponseStatus SWITCHING_PROTOCOLS = new AioHttpResponseStatus(101, "Switching Protocols");

    /**
     * 102 Processing (WebDAV, RFC2518)
     */
    public static final AioHttpResponseStatus PROCESSING = new AioHttpResponseStatus(102, "Processing");

    /**
     * 200 OK
     */
    public static final AioHttpResponseStatus OK = new AioHttpResponseStatus(200, "OK");

    /**
     * 201 Created
     */
    public static final AioHttpResponseStatus CREATED = new AioHttpResponseStatus(201, "Created");

    /**
     * 202 Accepted
     */
    public static final AioHttpResponseStatus ACCEPTED = new AioHttpResponseStatus(202, "Accepted");

    /**
     * 203 Non-Authoritative Information (since HTTP/1.1)
     */
    public static final AioHttpResponseStatus NON_AUTHORITATIVE_INFORMATION =
            new AioHttpResponseStatus(203, "Non-Authoritative Information");

    /**
     * 204 No Content
     */
    public static final AioHttpResponseStatus NO_CONTENT = new AioHttpResponseStatus(204, "No Content");

    /**
     * 205 Reset Content
     */
    public static final AioHttpResponseStatus RESET_CONTENT = new AioHttpResponseStatus(205, "Reset Content");

    /**
     * 206 Partial Content
     */
    public static final AioHttpResponseStatus PARTIAL_CONTENT = new AioHttpResponseStatus(206, "Partial Content");

    /**
     * 207 Multi-Status (WebDAV, RFC2518)
     */
    public static final AioHttpResponseStatus MULTI_STATUS = new AioHttpResponseStatus(207, "Multi-Status");

    /**
     * 300 Multiple Choices
     */
    public static final AioHttpResponseStatus MULTIPLE_CHOICES = new AioHttpResponseStatus(300, "Multiple Choices");

    /**
     * 301 Moved Permanently
     */
    public static final AioHttpResponseStatus MOVED_PERMANENTLY = new AioHttpResponseStatus(301, "Moved Permanently");

    /**
     * 302 Found
     */
    public static final AioHttpResponseStatus FOUND = new AioHttpResponseStatus(302, "Found");

    /**
     * 303 See Other (since HTTP/1.1)
     */
    public static final AioHttpResponseStatus SEE_OTHER = new AioHttpResponseStatus(303, "See Other");

    /**
     * 304 Not Modified
     */
    public static final AioHttpResponseStatus NOT_MODIFIED = new AioHttpResponseStatus(304, "Not Modified");

    /**
     * 305 Use Proxy (since HTTP/1.1)
     */
    public static final AioHttpResponseStatus USE_PROXY = new AioHttpResponseStatus(305, "Use Proxy");

    /**
     * 307 Temporary Redirect (since HTTP/1.1)
     */
    public static final AioHttpResponseStatus TEMPORARY_REDIRECT = new AioHttpResponseStatus(307, "Temporary Redirect");

    /**
     * 400 Bad Request
     */
    public static final AioHttpResponseStatus BAD_REQUEST = new AioHttpResponseStatus(400, "Bad Request");

    /**
     * 401 Unauthorized
     */
    public static final AioHttpResponseStatus UNAUTHORIZED = new AioHttpResponseStatus(401, "Unauthorized");

    /**
     * 402 Payment Required
     */
    public static final AioHttpResponseStatus PAYMENT_REQUIRED = new AioHttpResponseStatus(402, "Payment Required");

    /**
     * 403 Forbidden
     */
    public static final AioHttpResponseStatus FORBIDDEN = new AioHttpResponseStatus(403, "Forbidden");

    /**
     * 404 Not Found
     */
    public static final AioHttpResponseStatus NOT_FOUND = new AioHttpResponseStatus(404, "Not Found");

    /**
     * 405 Method Not Allowed
     */
    public static final AioHttpResponseStatus METHOD_NOT_ALLOWED = new AioHttpResponseStatus(405, "Method Not Allowed");

    /**
     * 406 Not Acceptable
     */
    public static final AioHttpResponseStatus NOT_ACCEPTABLE = new AioHttpResponseStatus(406, "Not Acceptable");

    /**
     * 407 Proxy Authentication Required
     */
    public static final AioHttpResponseStatus PROXY_AUTHENTICATION_REQUIRED =
            new AioHttpResponseStatus(407, "Proxy Authentication Required");

    /**
     * 408 Request Timeout
     */
    public static final AioHttpResponseStatus REQUEST_TIMEOUT = new AioHttpResponseStatus(408, "Request Timeout");

    /**
     * 409 Conflict
     */
    public static final AioHttpResponseStatus CONFLICT = new AioHttpResponseStatus(409, "Conflict");

    /**
     * 410 Gone
     */
    public static final AioHttpResponseStatus GONE = new AioHttpResponseStatus(410, "Gone");

    /**
     * 411 Length Required
     */
    public static final AioHttpResponseStatus LENGTH_REQUIRED = new AioHttpResponseStatus(411, "Length Required");

    /**
     * 412 Precondition Failed
     */
    public static final AioHttpResponseStatus PRECONDITION_FAILED = new AioHttpResponseStatus(412, "Precondition Failed");

    /**
     * 413 Request Entity Too Large
     */
    public static final AioHttpResponseStatus REQUEST_ENTITY_TOO_LARGE =
            new AioHttpResponseStatus(413, "Request Entity Too Large");

    /**
     * 414 Request-URI Too Long
     */
    public static final AioHttpResponseStatus REQUEST_URI_TOO_LONG = new AioHttpResponseStatus(414, "Request-URI Too Long");

    /**
     * 415 Unsupported Media Type
     */
    public static final AioHttpResponseStatus UNSUPPORTED_MEDIA_TYPE =
            new AioHttpResponseStatus(415, "Unsupported Media Type");

    /**
     * 416 Requested Range Not Satisfiable
     */
    public static final AioHttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE =
            new AioHttpResponseStatus(416, "Requested Range Not Satisfiable");

    /**
     * 417 Expectation Failed
     */
    public static final AioHttpResponseStatus EXPECTATION_FAILED = new AioHttpResponseStatus(417, "Expectation Failed");

    /**
     * 422 Unprocessable Entity (WebDAV, RFC4918)
     */
    public static final AioHttpResponseStatus UNPROCESSABLE_ENTITY = new AioHttpResponseStatus(422, "Unprocessable Entity");

    /**
     * 423 Locked (WebDAV, RFC4918)
     */
    public static final AioHttpResponseStatus LOCKED = new AioHttpResponseStatus(423, "Locked");

    /**
     * 424 Failed Dependency (WebDAV, RFC4918)
     */
    public static final AioHttpResponseStatus FAILED_DEPENDENCY = new AioHttpResponseStatus(424, "Failed Dependency");

    /**
     * 425 Unordered Collection (WebDAV, RFC3648)
     */
    public static final AioHttpResponseStatus UNORDERED_COLLECTION = new AioHttpResponseStatus(425, "Unordered Collection");

    /**
     * 426 Upgrade Required (RFC2817)
     */
    public static final AioHttpResponseStatus UPGRADE_REQUIRED = new AioHttpResponseStatus(426, "Upgrade Required");

    /**
     * 500 Internal Server Error
     */
    public static final AioHttpResponseStatus INTERNAL_SERVER_ERROR =
            new AioHttpResponseStatus(500, "Internal Server Error");

    /**
     * 501 Not Implemented
     */
    public static final AioHttpResponseStatus NOT_IMPLEMENTED = new AioHttpResponseStatus(501, "Not Implemented");

    /**
     * 502 Bad Gateway
     */
    public static final AioHttpResponseStatus BAD_GATEWAY = new AioHttpResponseStatus(502, "Bad Gateway");

    /**
     * 503 Service Unavailable
     */
    public static final AioHttpResponseStatus SERVICE_UNAVAILABLE = new AioHttpResponseStatus(503, "Service Unavailable");

    /**
     * 504 Gateway Timeout
     */
    public static final AioHttpResponseStatus GATEWAY_TIMEOUT = new AioHttpResponseStatus(504, "Gateway Timeout");

    /**
     * 505 HTTP Version Not Supported
     */
    public static final AioHttpResponseStatus HTTP_VERSION_NOT_SUPPORTED =
            new AioHttpResponseStatus(505, "HTTP Version Not Supported");

    /**
     * 506 Variant Also Negotiates (RFC2295)
     */
    public static final AioHttpResponseStatus VARIANT_ALSO_NEGOTIATES =
            new AioHttpResponseStatus(506, "Variant Also Negotiates");

    /**
     * 507 Insufficient Storage (WebDAV, RFC4918)
     */
    public static final AioHttpResponseStatus INSUFFICIENT_STORAGE = new AioHttpResponseStatus(507, "Insufficient Storage");

    /**
     * 510 Not Extended (RFC2774)
     */
    public static final AioHttpResponseStatus NOT_EXTENDED = new AioHttpResponseStatus(510, "Not Extended");

    /**
     * Returns the {@link AioHttpResponseStatus} represented by the specified code.
     * If the specified code is a standard HTTP status code, a cached instance
     * will be returned.  Otherwise, a new instance will be returned.
     */
    public static AioHttpResponseStatus valueOf(int code) {
        switch (code) {
        case 100:
            return CONTINUE;
        case 101:
            return SWITCHING_PROTOCOLS;
        case 102:
            return PROCESSING;
        case 200:
            return OK;
        case 201:
            return CREATED;
        case 202:
            return ACCEPTED;
        case 203:
            return NON_AUTHORITATIVE_INFORMATION;
        case 204:
            return NO_CONTENT;
        case 205:
            return RESET_CONTENT;
        case 206:
            return PARTIAL_CONTENT;
        case 207:
            return MULTI_STATUS;
        case 300:
            return MULTIPLE_CHOICES;
        case 301:
            return MOVED_PERMANENTLY;
        case 302:
            return FOUND;
        case 303:
            return SEE_OTHER;
        case 304:
            return NOT_MODIFIED;
        case 305:
            return USE_PROXY;
        case 307:
            return TEMPORARY_REDIRECT;
        case 400:
            return BAD_REQUEST;
        case 401:
            return UNAUTHORIZED;
        case 402:
            return PAYMENT_REQUIRED;
        case 403:
            return FORBIDDEN;
        case 404:
            return NOT_FOUND;
        case 405:
            return METHOD_NOT_ALLOWED;
        case 406:
            return NOT_ACCEPTABLE;
        case 407:
            return PROXY_AUTHENTICATION_REQUIRED;
        case 408:
            return REQUEST_TIMEOUT;
        case 409:
            return CONFLICT;
        case 410:
            return GONE;
        case 411:
            return LENGTH_REQUIRED;
        case 412:
            return PRECONDITION_FAILED;
        case 413:
            return REQUEST_ENTITY_TOO_LARGE;
        case 414:
            return REQUEST_URI_TOO_LONG;
        case 415:
            return UNSUPPORTED_MEDIA_TYPE;
        case 416:
            return REQUESTED_RANGE_NOT_SATISFIABLE;
        case 417:
            return EXPECTATION_FAILED;
        case 422:
            return UNPROCESSABLE_ENTITY;
        case 423:
            return LOCKED;
        case 424:
            return FAILED_DEPENDENCY;
        case 425:
            return UNORDERED_COLLECTION;
        case 426:
            return UPGRADE_REQUIRED;
        case 500:
            return INTERNAL_SERVER_ERROR;
        case 501:
            return NOT_IMPLEMENTED;
        case 502:
            return BAD_GATEWAY;
        case 503:
            return SERVICE_UNAVAILABLE;
        case 504:
            return GATEWAY_TIMEOUT;
        case 505:
            return HTTP_VERSION_NOT_SUPPORTED;
        case 506:
            return VARIANT_ALSO_NEGOTIATES;
        case 507:
            return INSUFFICIENT_STORAGE;
        case 510:
            return NOT_EXTENDED;
        }

        final String reasonPhrase;

        if (code < 100) {
            reasonPhrase = "Unknown Status";
        } else if (code < 200) {
            reasonPhrase = "Informational";
        } else if (code < 300) {
            reasonPhrase = "Successful";
        } else if (code < 400) {
            reasonPhrase = "Redirection";
        } else if (code < 500) {
            reasonPhrase = "Client Error";
        } else if (code < 600) {
            reasonPhrase = "Server Error";
        } else {
            reasonPhrase = "Unknown Status";
        }

        return new AioHttpResponseStatus(code, reasonPhrase + " (" + code + ')');
    }

    private final int code;
    private final String reasonPhrase;
    private final String toString;
	private final byte[] bytes;

    /**
     * Creates a new instance with the specified {@code code} and its
     * {@code reasonPhrase}.
     */
    public AioHttpResponseStatus(int code, String reasonPhrase) {
        if (code < 0) {
            throw new IllegalArgumentException(
                    "code: " + code + " (expected: 0+)");
        }

        if (reasonPhrase == null) {
            throw new NullPointerException("reasonPhrase");
        }

        for (int i = 0; i < reasonPhrase.length(); i ++) {
            char c = reasonPhrase.charAt(i);
            // Check prohibited characters.
            switch (c) {
            case '\n': case '\r':
                throw new IllegalArgumentException(
                        "reasonPhrase contains one of the following prohibited characters: " +
                        "\\r\\n: " + reasonPhrase);
            }
        }

        this.code = code;
        this.reasonPhrase = reasonPhrase;
        
        String initialLine = String.valueOf(code)+(char)AioHttpCodecUtil.SP+reasonPhrase;
        this.bytes = initialLine.getBytes();
        this.toString = initialLine;
    }

    /**
     * Returns the code of this status.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the reason phrase of this status.
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public int hashCode() {
        return getCode();
    }
    
    public byte[] getBytes() {
		return bytes;
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof AioHttpResponseStatus)) {
            return false;
        }

        return getCode() == ((AioHttpResponseStatus) o).getCode();
    }

    public int compareTo(AioHttpResponseStatus o) {
        return getCode() - o.getCode();
    }

    @Override
    public String toString() {
        return toString;
    }
}
