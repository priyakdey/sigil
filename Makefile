.PHONY: test bug clean

test:
	./gradlew clean test

bug:
	./gradlew spotBugMain

clean:
	./gradlew clean
