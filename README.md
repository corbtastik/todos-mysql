## Todo(s) MySQL

Todo(s) data api using Spring Boot.

```bash
# Run a MySQL container
podman run --name todos-db -d \
  -p 3306:3306 \
  -e MYSQL_USER=user1 \
  -e MYSQL_PASSWORD=mysql123 \
  -e MYSQL_DATABASE=todos \
  -e MYSQL_ROOT_PASSWORD=mysql123 \
  registry.redhat.io/rhel8/mysql-80

# build and run app
mvn clean package
java -jar -Dspring.profiles.active=mysql ./target/todos-mysql-1.0.0.SNAP.jar \
  --MYSQL_USER=user1 \
  --MYSQL_PASSWORD=mysql123 \
  --MYSQL_DATABASE=todos

podman exec -it todos-db /bin/bash -c 'mysql -uuser1 -pmysql123 -e "INSERT INTO todos.todos(id, title, complete) VALUE (1003, \"Learn podman networking\", TRUE);"'
podman exec -it todos-db /bin/bash -c 'mysql -uuser1 -pmysql123 -e "SELECT * FROM todos.todos;"'

http :8080/ title="Learn podman"
# add some data with your fave http client
http :8080/ title="Make bacon pancakes"
http :8080/ title="Eat bacon pancakes"
http :8080/ title="Clean kitchen"
```
