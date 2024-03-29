DROP TABLE IF EXISTS todos;
CREATE TABLE todos (
  id        VARCHAR(64) NOT NULL,
  title     VARCHAR(255) DEFAULT NULL,
  complete  BOOL DEFAULT FALSE,
  PRIMARY KEY (id),
  INDEX (title)
);
