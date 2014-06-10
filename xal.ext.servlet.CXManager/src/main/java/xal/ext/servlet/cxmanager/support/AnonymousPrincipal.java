package xal.ext.servlet.cxmanager.support;

import java.security.Principal;

public class AnonymousPrincipal implements Principal {

	@Override
	public String getName() {
		return "anonymous";
	}
}
