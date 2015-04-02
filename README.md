# Java Client for Cloud Foundry UAA

The Cloud Foundry User Account and Authentication (UAA) service is a set of JSON
APIs to manage user accounts, user groups, and OAuth2 clients. This project aims
to be an easy-to-use and feature complete Java library for these APIs.

## Quick Start

Note: this library only works with UAA 2.1.0 and later. Versions prior to 2.1.0
should use the `1.0.0-RELEASE` tag, which has been tested with 2.0 and later, but
should work with 1.9 and later. Be warned that the object model has changed
significantly from the `1.0.0-RELEASE` tag to use existing objects rather than
custom POJOs.

```
$ git clone https://github.com/cloudfoundry-incubator/uaa-java-client.git
$ mvn package
```

## Running Tests

While the `test` goal of the Maven project should always complete successfully,
certain tests do require a local UAA server to be running for them to pass. If
a UAA server is not running on port 8080, the tests will be skipped. To run a
copy of the UAA on port 8080, follow the [Quick Start](https://github.com/cloudfoundry/uaa/blob/master/README.md) guide for the
UAA. The tests expect a clean UAA database as bootstrapped by the server, so if
a test fails, simply restart the UAA server and you should be back in a clean
state.

## Getting Started in code

```java
import java.net.URL;

import org.cloudfoundry.identity.uaa.api.UaaConnectionFactory;
import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.user.UaaUserOperations;
import org.cloudfoundry.identity.uaa.api.user.model.ScimUsers;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

public class GetUsersNamedJohn {
  public static void main(String[] args) throws Exception {

    ResourceOwnerPasswordResourceDetails credentials = new ResourceOwnerPasswordResourceDetails();
    credentials.setAccessTokenUri("http://localhost:8080/uaa/oauth/token");
    credentials.setClientAuthenticationScheme(AuthenticationScheme.header);
    credentials.setClientId("app");
    credentials.setClientSecret("appclientsecret");
    credentials.setUsername("myuser");
    credentials.setPassword("mypassword");

    URL uaaHost = new URL("http://localhost:8080/uaa");
    UaaConnection connection = UaaConnectionFactory.getConnection(uaaHost, credentials);
    UaaUserOperations operations = connection.userOperations();

    FilterRequestBuilder builder = new FilterRequestBuilder();
    builder.startsWith("username", "john.");

    ScimUsers results = operations.getUsers(builder.build());
    for (ScimUser user : results.getResources()) {
      System.out.println(user.getId());
    }
  }
}
```

## More Information

Javadoc is available by building the code (or by running `mvn javadoc:javadoc`).
Authoratative information about the UAA is located [here](https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst)

## Contributing

Pull requests are welcome! Current to-dos including writing test cases for group
mapping APIs, as well as completing the model objects more fully. I'm also considering
taking out the existing Spring depedencies to make the client more standalone,
but that's a very low priority.
