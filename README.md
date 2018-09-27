# aidinsight-api

FIXME

## Usage

### Run the application locally

`lein run config.edn`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

### Opening ports

When running on port 8080, ensure the firewall is configured correctly:

sudo ufw allow 8080/tcp

https://serverfault.com/questions/238563/can-i-use-ufw-to-setup-a-port-forward

## License

Copyright ©  FIXME
