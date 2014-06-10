package xal.ext.servlet.cxmanager.support;

import java.security.Principal;
import java.util.UUID;

import org.springframework.messaging.simp.user.DestinationUserNameProvider;

public class UUIDDestinationUserProviderPrincipal implements Principal, DestinationUserNameProvider {

	private String uuid = UUID.randomUUID().toString();
	
	private Principal principal;
	
	public UUIDDestinationUserProviderPrincipal(Principal principal) {
		if( principal == null ) {
			throw new NullPointerException("Principal must not be Null.");
		}
		this.principal = principal;
	}
	
	public Principal getPrincipal() {
		return principal;
	}
	
	@Override
	public String getName() {
		return getPrincipal().getName();
	}

	@Override
	public String getDestinationUserName() {
		return getName() + "-" + uuid;
	}
}
