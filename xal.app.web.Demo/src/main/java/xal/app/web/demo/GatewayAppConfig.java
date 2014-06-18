package xal.app.web.demo;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.epics.pvmanager.websockets.AnnotatedGateway;
import org.epics.pvmanager.websockets.GatewayEndpoint;

public class GatewayAppConfig implements ServerApplicationConfig {

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> arg0) {
		Set<Class<?>> annotatedEndpointClasses = new HashSet<Class<?>>();
		annotatedEndpointClasses.add(AnnotatedGateway.class);
		return annotatedEndpointClasses;
	}

	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> arg0) {
		Set<ServerEndpointConfig> endpointConfigs = new HashSet<ServerEndpointConfig>();
		endpointConfigs.add(ServerEndpointConfig.Builder.create(GatewayEndpoint.class, "/gateway2").build());
		return endpointConfigs;
	}
}
