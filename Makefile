.PHONY: test bug clean

test: clean
	./gradlew clean test

bug: clean
	./gradlew spotBugMain

clean:
	./gradlew clean


docs: clean
	./gradlew javadoc


