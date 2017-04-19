# http-stubby #

http-stubby is a tool designed to assist in testing applications that
interact over the HTTP(S) protocol. It starts a real HTTP server and supports building
responses (status code, body, headers) to be primed programmatically for different requests.


### Features ###

* Supports HTTP/HTTPS
  * The HTTPS can be initialised using an internal test certificate or alternately can be initialised by providing a JKS
* Prime responses based on request criteria
  * Multiple responses can be primed with different request criteria
  * Built-in request criteria includes: 
    * URI matching (uriEquals, uriContains, uriStartsWith, uriEndsWith)
    * HTTP method matching (GET, HEAD, POST, PUT, DELETE)
    * Header matching (headerExists, headerEqualTo, headerContains)
  * Configure responses with status code, body(JSON, XML etc) and headers
  * Configurable default response when no matching response is found
* Exposes history of all request/responses to/from to http-stubby
  
  
### How to? ###

#### Starting http-stubby ####
```java 
HttpServer httpServer = HttpServerFactory.createHttpServer(nextAvailablePortNumber());
StubbableHttpServer stubbyServer = new StubbableHttpServer(httpServer);
// start stubby with context. e.g http://{host}:{port}/api
stubbyServer.startWithContext("/api");
```

or using custom RequestResponseHandlerListener
```java 
HttpServer httpServer = HttpServerFactory.createHttpServer(nextAvailablePortNumber());
StubbableHttpServer stubbyServer = new StubbableHttpServer(httpServer);
// start stubby with context. e.g http://{host}:{port}/api
stubbyServer.startWithContext("/api", new LoggingRequestResponseHandlerListener());
```

or using HTTPS
```java 
HttpServer httpsServer = HttpServerFactory.createHttpsServer(nextAvailablePortNumber());
StubbableHttpServer stubbyServer = new StubbableHttpServer(httpsServer);
// start stubby with context. e.g https://{host}:{port}/api
stubbyServer.startWithContext("/api");
```



#### Priming responses ####

Static response:
```java
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturn(responseOf(OK, "<response><id>123</id></response>"));
```

Dynamic response:
```java
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturn(responseOf(OK, new Callable<String>() {
    private final AtomicInteger uniqueNumber = new AtomicInteger(1);

    public String call() {
        return String.valueOf(uniqueNumber.getAndIncrement());
    }
}));
```

With Latency:
```java
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturn(responseOf(OK, "<response><id>123</id></response>")
        .withLatency(Duration.ofSeconds(2))
);
```

With Default Response:
```java
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturnWhenNoResponseFound(responseOf(NOT_FOUND, "no responses match"));
```


##### Setting criteria for request to match: #####

Request Method Type (GET, POST, PUT, DELETE):
```java
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), forAGetRequest());
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), forAPostRequest());
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), forAPutRequest());
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), forADeleteRequest());
```

Request URI matching:
```java
// example uri: http://localhost:12345/api/login
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestUriMatcher.uriEqualTo("http://localhost:12345/api/login"));
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestUriMatcher.uriEqualToIgnoringCase("http://LOCALHOST:12345/API/login"));
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestUriMatcher.uriStartsWith("http://localhost:12345/api"));
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestUriMatcher.uriEndsWith("login"));
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestUriMatcher.uriContains("api"));
```

Request Header matching:
```java
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestHeaderMatcher.requestHeaderContains("Authorization", "123abc"));
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestHeaderExistsMatcher.requestHeaderExists("Authorization"));
stubbyServer.willReturn(responseOf(OK, "<response>123</response>"), RequestHeaderExistsMatcher.requestHeaderDoesNotExist("Security-Header"));
```

#### Access request/response history ####
```java 
StubbableHttpServer stubbyServer = new StubbableHttpServer(...);
stubbyServer.history().lastRequest();
stubbyServer.history().lastResponse();
```

  
