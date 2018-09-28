# aidinsight-api

Aid inSight API for coordination of humanitarian relief.

## Usage

### Configuration

Copy template-config.edn to config.edn and populate the values.

For the Natural Language Classifier, visit https://console.bluemix.net/dashboard/apps and find your 
natural language classifier instance. Selecting that will give access to your credentials.

Contact your Discourse admin to request the API key.

(Note: for the Call for Code submission, configuration details will be provided separately.)

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
