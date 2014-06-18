package org.epics.pvmanager.websockets;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class GatewayEndpoint extends Endpoint {

	private static class GatewayMessageHandler implements MessageHandler.Whole<String> {

		private Session session;

		public GatewayMessageHandler(Session session) {
			this.session = session;
		}

		@Override
		public void onMessage(String message) {
			System.out.println(session.getRequestURI() + ": MESSAGE: " + message);
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(new GatewayMessageHandler(session));
		System.out.println(session.getRequestURI() + ": OPEN");
	}

	@Override
	public void onClose(Session session, CloseReason reason) {
		System.out.println(session.getRequestURI() + ": CLOSE: " + reason);
	}

	@Override
	public void onError(Session session, Throwable cause) {
		System.out.println(session.getRequestURI() + ": ERROR");
		cause.printStackTrace();
	}
}