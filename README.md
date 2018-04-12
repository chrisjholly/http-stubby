# http-stubby #

http-stubby is a tool designed to assist in testing applications that
interact over the HTTP(S) protocol. It starts a real HTTP server and supports building
responses (status code, body, headers) to be primed programmatically.


### Features ###

* Supports HTTP/HTTPS
  * HTTPS can be initialised using default https configuration or by supplying an SSLContext
* Prime responses based on request criteria
  * Multiple responses can be primed with different request criteria
  * Built-in request criteria includes: 
    * URI matching (uriEquals, uriContains, uriStartsWith, uriEndsWith)
    * HTTP method matching (GET, HEAD, POST, PUT, DELETE)
    * Header matching (headerExists, headerEqualTo, headerContains)
  * Configure responses with status code, body(JSON, XML etc) and headers
  * Configurable default response when no matching response is found
* Exposes history of all request/responses to/from to http-stubby
* Ability to register custom HttpRequestResponseEventListener
  
  
### How to? ###

#### Starting http-stubby ####
```java 
HttpStubbyServer stubbyServer = HttpStubbyServer.stubbyServerWith(HttpServerFactory.httpConfiguration(nextAvailablePortNumber()));
stubbyServer.start();
```

with HTTPS:
```java 
HttpStubbyServer stubbyServer = stubbyServerWith(HttpServerFactory.httpsConfiguration(nextAvailablePortNumber()));
stubbyServer.start();

or

HttpStubbyServer stubbyServer = stubbyServerWith(HttpServerFactory.httpsConfiguration(nextAvailablePortNumber(), SSLContext.getDefault()));
stubbyServer.start();
```

with RequestResponseHandlerListener:
```java 
HttpStubbyServer stubbyServer = HttpStubbyServer.stubbyServerWith(HttpServerFactory.httpConfiguration(nextAvailablePortNumber()));
stubbyServer.registerHttpRequestResponseEventListener(new HttpRequestResponseEventListener() {
            @Override
            public void newRequest(HttpRequest httpRequest) {
                
            }

            @Override
            public void newResponse(com.staygrounded.httpstubby.server.response.HttpResponse httpResponse) {

            }
        });
stubbyServer.start();
```


#### Priming responses ####

Static response:
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK)
        .withBody("<response><id>123</id></response>"));
```

Dynamic response:
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK)
            .withBody(new Callable<String>() {
                private final AtomicInteger uniqueNumber = new AtomicInteger(1);

                public String call() {
                    return String.valueOf(uniqueNumber.getAndIncrement());
                }
            }));
```

With Delay:
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK)
        .withBody("<response><id>123</id></response>"))
        .withLatency(Duration.ofSeconds(2))
);
```

With Default Response:
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturnWhenNoResponseFound(responseOf(HttpStatus.Code.NOT_FOUND)
        .withBody("no responses match));
```


#### Using criteria to prime a response for your request: #####

Request Method Type (GET, POST, PUT, DELETE, HEAD):
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK), RequestMethodMatcher.forAGetRequest());
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK), RequestMethodMatcher.forAPostRequest());
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK), RequestMethodMatcher.forAPutRequest());
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK), RequestMethodMatcher.forADeleteRequest());
stubbyServer.willReturn(responseOf(HttpStatus.Code.OK), RequestMethodMatcher.forAHeadRequest());
```

Request URI matching:
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturn(responseOf(OK), RequestUriMatcher.uriEqualTo("http://localhost:12345/api/login"));
stubbyServer.willReturn(responseOf(OK), RequestUriMatcher.uriEqualToIgnoringCase("http://LOCALHOST:12345/API/login"));
stubbyServer.willReturn(responseOf(OK), RequestUriMatcher.uriStartsWith("http://localhost:12345/api"));
stubbyServer.willReturn(responseOf(OK), RequestUriMatcher.uriEndsWith("login"));
stubbyServer.willReturn(responseOf(OK), RequestUriMatcher.uriContains("api"));
```

Request Header matching:
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.willReturn(responseOf(OK), RequestHeaderMatcher.requestHeaderContains("Authorization", "123abc"));
stubbyServer.willReturn(responseOf(OK), RequestHeaderExistsMatcher.requestHeaderExists("Authorization"));
stubbyServer.willReturn(responseOf(OK), RequestHeaderExistsMatcher.requestHeaderDoesNotExist("Security-Header"));
```

#### Access request/response history ####
```java 
HttpStubbyServer stubbyServer = ...
stubbyServer.httpRequestResponseHistory().lastRequest();
stubbyServer.httpRequestResponseHistory().lastResponse();
```

  
