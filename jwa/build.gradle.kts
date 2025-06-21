plugins {
    java
}

dependencies {
    implementation(project(":core"))

    testImplementation(Deps.BOUNCY_CASTLE)
}