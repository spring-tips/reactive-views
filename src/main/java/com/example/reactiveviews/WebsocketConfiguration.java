package com.example.reactiveviews;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;


@Configuration
class WebsocketConfiguration {

	@Bean
	SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler wsh) {
		return new SimpleUrlHandlerMapping() {
			{
				setOrder(10);
				setUrlMap(Map.of("/ws/greetings", wsh));
			}
		};
	}

	@Bean
	WebSocketHandlerAdapter webSocketHandlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

	@Bean
	WebSocketHandler webSocketHandler(GreetingProducer producer) {
		return session -> {

			var greetings = session
				.receive()
				.map(WebSocketMessage::getPayloadAsText)
				.flatMap(producer::greet)
				.map(Greeting::getMessage)
				.map(session::textMessage);

			return session.send(greetings);
		};
	}
}
