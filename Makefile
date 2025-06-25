.PHONY: test bug clean

check: clean test bug docs

test: clean
	./gradlew test 

cov: test
	./gradlew testCodeCoverageReport

bug: test
	./gradlew spotBugMain

clean:
	./gradlew clean


docs: clean
	./gradlew javadoc


