JAR := target/money-transfer-1.0-SNAPSHOT-jar-with-dependencies.jar

run: $(JAR)
	java -jar $^

$(JAR): build

test:
	mvn test

build: clean
	mvn compile assembly:single

clean:
	mvn clean
