# Todo(s) MySQL

Todo(s) data api using Spring Boot.

* [Spring Boot](https://start.spring.io)
* [Podman](https://github.com/containers/podman)
* [cURL](https://curl.se/)
* [HTTPie](https://httpie.io/)

> I'm not a fan of the maven wrapper (aka mvnw) so you'll need maven.
> [SDK Man](https://sdkman.io/) is your friend.

## Build

```bash
git clone https://github.com/corbtastik/todos-mysql.git && cd todos-mysql
# build spring boot jar
mvn clean package
# build OCI image via podman plugin (bound to install phase)
mvn clean install
# or build OCI image via podman cli
podman build -t todos-mysql .
```

## Run MySQL via podman

First you need access to a MySQL instance. Podman is great for local development :wink:.

```bash
# publish and map hostPort 3306 to containerPort 3306
podman run --name todos-db -d \
  -p 3306:3306 \
  -e MYSQL_USER=user1 \
  -e MYSQL_PASSWORD=mysql123 \
  -e MYSQL_DATABASE=todos \
  -e MYSQL_ROOT_PASSWORD=mysql123 \
  registry.redhat.io/rhel8/mysql-80
```

## Run via Java
 
```bash
java -jar ./target/todos-mysql-1.0.0.SNAP.jar \
  --server.port=8081 \
  --spring.profiles.active=mysql \
  --MYSQL_USER=user1 \
  --MYSQL_PASSWORD=mysql123 \
  --MYSQL_DATABASE=todos
```

### Or

## Run via Podman

```bash
# First get the IP of your host (not localhost)
# macOS
HOST_IP=$(ipconfig getifaddr en0) ; echo $HOST_IP
# RHEL/Fedora/Centos - your interface may be different (e.g. VMware ens160 is typical)
HOST_IP=$(ip -o -4 addr list eth0 | awk '{print $4}' | cut -d/ -f1); echo $HOST_IP
# Run todos-mysql container, setting env vars accordingly, especially MYSQL_HOST=${HOST_IP}
podman run --name todos-mysql -d -p 8081:8081 \
  -e "SERVER_PORT=8081" \
  -e "SPRING_PROFILES_ACTIVE=mysql" \
  -e "MYSQL_USER=user1" \
  -e "MYSQL_PASSWORD=mysql123" \
  -e "MYSQL_HOST=${HOST_IP}" \
  -e "MYSQL_DATABASE=todos" \
  todos-mysql
```

## Grok

* Get all, create, get, update, get complete, delete, get all

### cURL flavor

```bash
# Get all
curl http://localhost:8081/todos/
# Create todo
curl -X POST http://localhost:8081/todos/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{"id":"1003", "title":"Learn podman"}'
# Get todo
curl http://localhost:8081/todos/1003
# Update todo complete
curl -X PATCH http://localhost:8081/todos/1003 \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{"id":"1003", "complete":true}'
# Get complete todos
curl http://localhost:8081/todos/complete
# Delete todo
curl -X DELETE http://localhost:8081/todos/1003
# Get all
curl http://localhost:8081/todos/
```

### HTTPie flavor

```bash
# Get all
http :8081/todos/
# Create todo
http POST :8081/todos/ id="1004" title="Learn podman pods"
# Get todo
http :8081/todos/1004
# Update todo complete
http PATCH :8081/todos/1004 complete:=true
# Get complete todos
http :8081/todos/complete
# Delete todo
http DELETE :8081/todos/1004
# Get all
http :8081/todos/
```

### Podman flavor

* Execute SQL against todos-db container in podman.

```bash
# Select all
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "SELECT * FROM todos.todos;"'
# Insert todo
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "INSERT INTO todos.todos(id, title, complete) VALUE (1005, \"Learn podman exec\", FALSE);"'
# Select todo
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "SELECT * FROM todos.todos WHERE id=1005;"'
# Update todo
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "UPDATE todos.todos SET complete=TRUE WHERE id=1005;"'
# Select complete todos
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "SELECT * FROM todos.todos WHERE complete=TRUE;"'
# Delete todo
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "DELETE FROM todos.todos WHERE id=1005;"'
# Select all
podman exec -it todos-db /bin/bash \
  -c 'mysql -uuser1 -pmysql123 -e "SELECT * FROM todos.todos;"'
```

## Next Steps

The [Todos-MySQL](https://github.com/corbtastik/todos-mysql) app can be used as a backend for [Todos-WebUI]
(https://github.com/corbtastik/todos-webui). Next steps could be running the two together so Todo(s) are saved.