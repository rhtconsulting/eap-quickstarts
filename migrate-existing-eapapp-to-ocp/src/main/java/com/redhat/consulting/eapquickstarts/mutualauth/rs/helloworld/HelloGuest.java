package com.redhat.consulting.eapquickstarts.mutualauth.rs.helloworld;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * A simple REST service which is able to say hello to someone using
 * HelloService Please take a look at the web.xml where JAX-RS is enabled
 * 
 * @author cadjai@redhat.com
 * 
 */

@Path("/")
public class HelloGuest {

	private static Logger LOG = Logger.getLogger(HelloGuest.class.getName());

	@Inject
	HelloGuestService helloService;

	@GET
	@Path("/json")
	@Produces({ "application/json" })
	public String getHelloWorldJSON(@Context SecurityContext securityCtx, @Context HttpServletRequest req) {

		return "{\"result\":\"" + helloService.createHelloMessage(getGuessName(securityCtx, req)) + "\"}";
	}

	@GET
	@Path("/xml")
	@Produces({ "application/xml" })
	public String getHelloWorldXML(@Context SecurityContext securityCtx, @Context HttpServletRequest req) {
		return "<xml><result>" + helloService.createHelloMessage(getGuessName(securityCtx, req)) + "</result></xml>";
	}

	@GET
	@Path("/name/json")
	@Produces({ "application/json" })
	public String getMyNameJSON(@Context SecurityContext securityCtx, @Context HttpServletRequest req) {
		return "{\"result\":\"" + helloService.getMyNameMessage(getGuessName(securityCtx, req)) + "\"}";
	}

	@GET
	@Path("/name/xml")
	@Produces({ "application/xml" })
	public String getMyNameXML(@Context SecurityContext securityCtx, @Context HttpServletRequest req) {
		return "<xml><result>" + helloService.getMyNameMessage(getGuessName(securityCtx, req)) + "</result></xml>";
	}

	private String getGuessName(SecurityContext securityCtx, HttpServletRequest req) {
		logPrincipalInfo(securityCtx, req);
		String name = "Guest";
		try {
			if (securityCtx.getUserPrincipal() != null) {
				LOG.info("The Security Principal name is: " + securityCtx.getUserPrincipal().getName());
				name = securityCtx.getUserPrincipal().getName();
			}
		} catch (Exception e) {
		}

		return name;
	}

	private void logPrincipalInfo(SecurityContext securityCtx, HttpServletRequest req) {

		if (null != securityCtx) {
			LOG.info("The Auth Scheme used is: " + securityCtx.getAuthenticationScheme());
			if (null != securityCtx.getUserPrincipal()) {
				LOG.info("The Principal Name is : " + securityCtx.getUserPrincipal().getName());
			}

		}
		if (null != req) {
			LOG.info("The Request Auth Type used is: " + req.getAuthType());
			if (null != req.getRemoteUser()) {
				LOG.info("The Remote User  is : " + req.getRemoteUser());
			}
			if (null != req.getRemoteHost()) {
				LOG.info("The Remote Host  is : " + req.getRemoteHost());
			}
			if (req.getRemotePort() != 0) {
				LOG.info("The Remote Port  is : " + req.getRemotePort());
			}
			if (null != req.getRemoteAddr()) {
				LOG.info("The Remote Address  is : " + req.getRemoteAddr());
			}
			if (null != req.getContextPath()) {
				LOG.info("The Context Path  is : " + req.getContextPath());
			}

			if (null != req.getServerName()) {
				LOG.info("The Server Name is : " + req.getServerName());
			}

		}

	}

}
