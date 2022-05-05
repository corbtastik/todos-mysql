## Todo(s) MySQL

Todo(s) data api using Spring Boot.

```bash
# Run a MySQL container
podman run --name todos-mysql -d \
  -e MYSQL_USER=user1 \
  -e MYSQL_PASSWORD=mysql123! \
  -e MYSQL_DATABASE=todos \
  -e MYSQL_ROOT_PASSWORD=mysql123! \
  registry.redhat.io/rhel8/mysql-80

# build and run app
mvn clean package
java -jar -Dspring.profiles.active=mysql ./target/todos-mysql-1.0.0.SNAP.jar \
  --MYSQL_USER=user1 \
  --MYSQL_PASSWORD=mysql123! \
  --MYSQL_DATABASE=todos
  
# add some data with your fave http client
http :8080/ title="Make bacon pancakes"
http :8080/ title="Eat bacon pancakes"
http :8080/ title="Clean kitchen"
```
