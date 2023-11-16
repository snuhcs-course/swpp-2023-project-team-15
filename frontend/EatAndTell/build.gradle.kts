// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false

    jacoco
    id("io.github.gmazzo.test.aggregation.coverage") version "2.1.1"
    id("io.github.gmazzo.test.aggregation.results") version "2.1.1"
    /*
    to get report:
    - run `./gradlew jacocoAggregatedReport`
    - open `build/reports/jacoco/jacocoAggregatedReport/html/index.html` in brower
      - (On Mac, run `open build/reports/jacoco/jacocoAggregatedReport/html/index.html`)
     */
}
