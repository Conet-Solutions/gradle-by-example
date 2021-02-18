import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("org.javafreedom.verification.jacoco-consumer-conventions")
    id("io.gitlab.arturbosch.detekt")
    id("org.sonarqube")
}

// right now, there is no real aggregation of detekt, therefor we are just adding all
// sources to an aggregation tasks and generate the reports all in one
val aggregateDetektTask = tasks.register<Detekt>("aggregateDetekt") {
    buildUponDefaultConfig = false
    ignoreFailures = true

    reports {
        html.enabled = true
        xml.enabled = true
        txt.enabled = false
        sarif.enabled = false
    }

    source(
        subprojects.flatMap { subproject ->
            subproject.tasks.filterIsInstance<Detekt>().map { task ->
                task.source
            }
        }
    )
}

subprojects {
    if (this.name != "documentation") {
        val sonarSource = this.projectDir.resolve("src/main").absolutePath
        val reportsDir = this.buildDir.resolve("reports/detekt/detekt.xml").absolutePath

        sonarqube {
            properties {
                property("sonar.sources", sonarSource)
                property("sonar.kotlin.detekt.reportPaths", reportsDir)
            }
        }
    }
}
