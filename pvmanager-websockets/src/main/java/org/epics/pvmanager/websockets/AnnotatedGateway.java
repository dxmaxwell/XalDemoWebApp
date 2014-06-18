package org.epics.pvmanager.websockets;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/gateway1")
public class AnnotatedGateway {

	@OnMessage
	public void onMessage(Session session, String message) {
		System.out.println(session.getRequestURI() + ": MESSAGE: " + message);
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println(session.getRequestURI() + ": OPEN");
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		System.out.println(session.getRequestURI() + ": CLOSE: " + reason);
	}

	@OnError
	public void onError(Session session, Throwable cause) {
		System.out.println(session.getRequestURI() + ": ERROR");
		cause.printStackTrace();
	}
}
