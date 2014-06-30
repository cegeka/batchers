# Spring and WebSockets

Historically sending messages from the server to the browser has been hard for developers and servers as well. Usually we had to resort to some sort of periodic polling and stressing the server with requests that do not carry any data. Today we have WebSockets and best of all, Spring integration for WebSockets!

For the [Batchers](https://github.com/cegeka/batchers) project we needed to send progress events for the [job result](http://localhost:9090/taxcalculator/#/jobResults) page. In order to do this we needed a subscriber on the client side, a publisher on the server and we needed to decide on a protocol for sending our data. We chose the simple text oriented messaging protocol ([Stomp](http://stomp.github.io))  which has great support for the client side in [SockJS](http://sockjs.org) and in [Spring-WebSockets](https://github.com/spring-projects/spring-framework/blob/master/spring-websocket/src/main/java/org/springframework/web/socket/config/annotation/WebSocketMessageBrokerConfigurer.java#L26-L41) for the backend.

## Configuration

All the configuration was quick and clean.
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/jobinfo-updates");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/jobinfo").withSockJS();
    }

}
```

## The Controller
The [controller](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-presentation/src/main/java/be/cegeka/batchers/taxcalculator/presentation/websockets/WebSocketController.java#L25-L28) actually sends messages using a [MessageSendingOperations](https://github.com/spring-projects/spring-framework/blob/master/spring-messaging/src/main/java/org/springframework/messaging/core/MessageSendingOperations.java). The [@Subscribe](https://github.com/Noia/guava-libraries/blob/master/guava/src/com/google/common/eventbus/Subscribe.java) annotation is part of Google's EventBus and we use it to pass messages internally in our application:

```Java
@Controller
public class WebSocketController {

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Subscribe
    public void onJobProgressEvent(JobProgressEvent jobProgressEvent) {
        messagingTemplate.convertAndSend("/jobinfo-updates", jobProgressEvent);
    }

}
```

## The client side
The [client side](https://github.com/cegeka/batchers/blob/master/taxcalculator/taxcalculator-presentation/src/main/webapp/resources/js/jobresult/jobresult-controllers.js#L43-L76) simply creates a SockJS client and listens for messages

```JavaScript
      var socket = new SockJS('/taxcalculator/rest/jobinfo');
      var client = Stomp.over(socket);

      client.connect({}, function (frame) {
        console.log("connected");
        $scope.model.connected = true;
        $scope.model.transport = "sockjs";
        $scope.$apply();

        client.subscribe("/jobinfo-updates", function (message) {
          var message = angular.fromJson(message.body);
          alert('new message ' + message);
          //change ancular $scope with the new message
          //and notify angular of the changes
          $scope.$apply();
        });
      });

```

And that's it! That's how you send messages from the server to the client over web sockets. This was very fast to develop, easy to understand. We were pleasantly surprised to see how quick creating a WebSockets connection was for our application. Check out [our project](https://github.com/cegeka/batchers) on github and see the rest of our blog posts.