.PHONY: test bug clean

test: clean
	./gradlew clean test testCodeCoverageReport

bug: clean
	./gradlew spotBugMain

clean:
	./gradlew clean


docs: clean
	./gradlew javadoc


