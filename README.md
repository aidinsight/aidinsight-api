# aidinsight-api

Aid inSight API for coordination of humanitarian relief.

## Usage

### Opening ports

By default, the server runs on port 8080. To change this, set the PORT environment variable.

When running on port 8080, ensure the firewall is configured correctly:

(Ubuntu)
```sudo ufw allow 8080/tcp```

### Run the application locally

```lein run config.edn```

Browse to port 8080.

## License

Copyright © 2018 Andrew Whitehouse

Distributed under the Apache 2.0 License https://www.apache.org/licenses/LICENSE-2.0
