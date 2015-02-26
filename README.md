# Java Client for Cloud Foundry UAA

The Cloud Foundry User Account and Authentication (UAA) service is a set of JSON
APIs to manage user accounts, user groups, and OAuth2 clients. This project aims
to be an easy-to-use and feature complete Java library for these APIs.

## Quick Start

```
$ git clone https://github.com/ECSTeam/uaa-java-client.git
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
import org.cloudfoundry.identity.uaa.api.common.model.PagedResult;
import org.cloudfoundry.identity.uaa.api.common.model.UaaCredentials;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.user.UaaUserOperations;
import org.cloudfoundry.identity.uaa.api.user.model.UaaUser;

public class GetUsersNamedJohn {
  public static void main(String[] args) throws Exception {
    UaaCredentials credentials =
      new UaaCredentials("app", "appclientsecret", "myuser", "mypassword");

    URL uaaHost = new URL("http://localhost:8080/uaa");
    UaaConnection connection = UaaConnectionFactory.getConnection(uaaHost, credentials);
    UaaUserOperations operations = connection.userOperations();

    FilterRequestBuilder builder = new FilterRequestBuilder();
    builder.startsWith("username", "john.");

    PagedResult<UaaUser> results = operations.getUsers(builder.build());
    for (UaaUser user : results.getResources()) {
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
