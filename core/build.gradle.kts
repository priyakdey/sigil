plugins {
    java
}

dependencies {
    testImplementation(Deps.BOUNCY_CASTLE)
}

spotbugs {
    excludeFilter = file("spotbugs-exclude.xml")
}