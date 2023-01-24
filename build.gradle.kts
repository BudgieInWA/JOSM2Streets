plugins {
    id("java")
    id("org.openstreetmap.josm").version("0.8.2")
}

group = "org.osm2streets"
version = "0.1-DEV"

repositories {
    mavenCentral()

//    flatDir {
//        dirs("lib") // TODO put the osm2streets-0.1.0.jar in lib/
//    }
}

josm {
    pluginName = "Streets"
    debugPort = 2121
    josmCompileVersion = "18004"
    manifest {
        description = "Visualise streets, intersections and lanes with the power of osm2streets."
        mainClass = "org.openstreetmap.josm.plugins.osm2streets.JOSM2Streets"
        minJosmVersion = "10580"
        author = "Ben Ritter"
        canLoadAtRuntime = true
        iconPath = "images/JOSM2Streets.png"
        // loadEarly = false
        // loadPriority = 50
        // pluginDependencies += setOf("apache-commons", "apache-http")
        // website = java.net.URL("https://example.org")
        // oldVersionDownloadLink(123, "v1.2.0", java.net.URL("https://example.org/download/v1.2.0/MyAwesomePlugin.jar"))
        // oldVersionDownloadLink( 42, "v1.0.0", java.net.URL("https://example.org/download/v1.0.0/MyAwesomePlugin.jar"))

        // to populate the 'Class-Path' attribute in the JOSM plugin manifest invoke
        // the function 'classpath', i.e.
        //   classpath "foo.jar"
        //   classpath "sub/dir/bar.jar"
        // This results in 'Class-Path: foo.jar sub/dir/bar.jar' in the
        // manifest file. Added class path entries must not contain blanks.
    }
    // i18n {
    //   bugReportEmail = "me@example.com"
    //   copyrightHolder = "John Doe"
    // }
}

dependencies {
    implementation(files("lib/StreetNetwork.jar"))

//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

//tasks.getByName<Test>("test") {
//    useJUnitPlatform()
//}