package org.hilel14.archie.beeri.ws.tools;

import io.jsonwebtoken.Jwts;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private Configuration config;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    //private static final String AUTHENTICATION_SCHEME = "Basic"; // Bearer
    private static final Response ACCESS_DENIED
            = Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this resource").build();
    private static final Response ACCESS_FORBIDDEN
            = Response.status(Response.Status.FORBIDDEN)
                    .entity("Access blocked for all users !!").build();

    @Override
    public void filter(ContainerRequestContext requestContext) {

        Method method = resourceInfo.getResourceMethod();

        //Access allowed for all
        if (!method.isAnnotationPresent(PermitAll.class)) {
            //Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

            //If no authorization information present; block access
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }

            // get token and extract role
            Key key = (Key) config.getProperty("jwt.key");
            String token = authorization.get(0);
            String role = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();

            //Verify user access
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
                if (!rolesSet.contains(role)) {
                    requestContext.abortWith(ACCESS_DENIED);
                }
            }

        }
    }
}
