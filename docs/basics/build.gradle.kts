// Once tasks are created they can be accessed via an API. For instance,
// you could use this to dynamically add dependencies to a task, at runtime.
// Ant doesn’t allow anything like this.
repeat(4) { counter ->
    tasks.register("task$counter") {
        doLast {
            println("I'm task number $counter")
        }
    }
}
tasks.named("task0") { dependsOn("task2", "task3") }


// You can add your own properties to a task. To add a property named myProperty,
// set ext.myProperty to an initial value. From that point on, the property can be
// read and set like a predefined task property.
tasks.register("myTask") {
    extra["myProperty"] = "myValue"
}

tasks.register("printTaskProperties") {
    doLast {
        println(tasks["myTask"].extra["myProperty"])
    }
}

// Gradle scales in how you can organize your build logic. The first level of
// organizing your build logic for the example above, is extracting a method.
tasks.register("printFilesInDocuments") {
    doLast {
        fileList("/Users/popovbodya/Documents").forEach { file -> println("$file.name") }
    }
}

tasks.register("printDirsInDocuments") {
  doLast {
    dirList("/Users/popovbodya/Documents").forEach { file -> println("$file") }
  }
}

fun fileList(dir: String): List<File> =
    file(dir).listFiles { file: File -> file.isFile }.sorted()

fun dirList(dir: String): List<File> =
    file(dir).listFiles { file: File -> file.isDirectory}.sorted()


// Gradle allows you to define one or more default tasks that are executed if no other tasks are specified.
defaultTasks("clean", "run")

tasks.register("clean") {
    doLast {
        println("Default Cleaning!")
    }
}

tasks.register("run") {
    doLast {
      println("Default Running!")
    }
}

tasks.register("other") {
    doLast {
      println("I'm not a default task!")
    }
}


// Gradle has a configuration phase and an execution phase. After the configuration phase,
// Gradle knows all tasks that should be executed. Gradle offers you a hook to make
// use of this information. A use-case for this would be to check if the release
// task is among the tasks to be executed. Depending on this, you can assign different
// values to some variables.
tasks.register("distribution") {
    doLast {
        println("We build the zip with version=$version")
    }
}

tasks.register("release") {
    dependsOn("distribution")
    doLast {
        println("We release now")
    }
}

gradle.taskGraph.whenReady {
    version =
        if (hasTask(":release")) "1.0"
        else "1.0-SNAPSHOT"
}
