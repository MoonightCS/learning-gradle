import javax.inject.Inject

// Defining tasks using strings for task names
val simpleTask: TaskProvider<Task> = tasks.register("simple") {
    doLast {
        println("simple")
    }
}

//  Defining tasks using a DSL specific syntax
val hello: TaskProvider<Task> by tasks.registering {
    doLast {
        println("hello")
    }
}

// Access tasks using Kotlin delegated properties
task("dslTask")
val dslTask: Task by tasks.getting
println(dslTask.name)
println(tasks["dslTask"].name)
println(tasks.named("dslTask").get().name)


// Configure task using Kotlin delegated properties and a lambda
tasks.register<Copy>("myCopy")
val myCopy: TaskProvider<Copy> by tasks.existing(Copy::class) {
    from("resources")
    into("target")
}
myCopy { include("**/*.txt", "**/*.xml", "**/*.properties") }


// Creating a task with constructor arguments using TaskContainer
open class CustomTask @Inject constructor(
        val message: String,
        val number: Int
) : DefaultTask()

tasks.register<CustomTask>("myCustomTask", "hello", 42)
val myCustomTask: CustomTask by tasks.getting(CustomTask::class)
println(myCustomTask.message)
println(myCustomTask.number)


// define a task dependency using a lazy block. When evaluated, the block is passed the task whose dependencies are being
// calculated. The lazy block should return a single Task or collection of Task objects, which are then treated as dependencies
// of the task. The following example adds a dependency from taskX to all the tasks in the project whose name starts with lib:
val taskX: TaskProvider<Task> by tasks.registering {
    doLast {
        println("taskX")
    }
}

// Using a Gradle Provider
taskX {
    dependsOn(provider {
        println("lazy provider")
        tasks.filter { task -> task.name.startsWith("lib") }
    })
}

tasks.register("lib1") {
    doLast {
        println("lib1")
    }
}

tasks.register("lib2") {
    doLast {
        println("lib2")
    }
}

tasks.register("notALib") {
    doLast {
        println("notALib")
    }
}


// In some cases it is useful to control the order in which 2 tasks will execute, without introducing an explicit dependency
// between those tasks
val taskFirst by tasks.registering {
    doLast {
        println("taskFirst")
    }
}
val taskSecond by tasks.registering {
    mustRunAfter(taskFirst)
    doLast {
        println("taskSecond")
    }
}
