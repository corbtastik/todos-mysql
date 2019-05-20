package io.todos

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.persistence.*

@Entity
@Table(name="todos")
data class TodoEntity(var title: String, var completed: Boolean) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
    constructor() : this("",false)
}

data class Limit(val size: Int, val limit: Int)

class Todo {
    var title: String? = null
    var completed: Boolean? = null
}

@Repository("todos")
interface TodosRepo : CrudRepository<TodoEntity, Int> { }

@SpringBootApplication
@RestController
class App(
        @Autowired val repo: TodosRepo,
        @Value("\${todos.mysql.limit}") val limit: Int) {

    companion object { val LOG = LoggerFactory.getLogger(App::class.java.name) }

    @PostMapping("/")
    fun create(@RequestBody todo: Todo): TodoEntity {
        val count = this.repo.count()
        if(count >= limit) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "todos.mysql.limit=$limit, todos.size=$count")
        }
        val createObject = TodoEntity()
        if(!ObjectUtils.isEmpty(todo.title)) {
            createObject.title = todo.title!!
        }
        if(!ObjectUtils.isEmpty(todo.completed)) {
            createObject.completed = todo.completed!!
        }
        return this.repo.save(createObject)
    }

    @GetMapping("/")
    fun retrieve(): List<TodoEntity> {
        return this.repo.findAll().iterator().asSequence().toList()
    }

    @GetMapping("/{id}")
    fun retrieve(@PathVariable id: Int): TodoEntity {
        if (!repo.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "todo.id=$id")
        }
        return repo.findById(id).get()
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody todo: Todo): TodoEntity {
        if (!repo.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "todo.id=$id")
        }
        val updateObject = repo.findById(id).get()
        if(!ObjectUtils.isEmpty(todo.title)) {
            updateObject.title = todo.title!!
        }
        if(!ObjectUtils.isEmpty(todo.completed)) {
            updateObject.completed = todo.completed!!
        }
        return this.repo.save(updateObject)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        repo.deleteById(id)
    }

    @GetMapping("/limit")
    fun limit(): Limit {
        return Limit(this.repo.count().toInt(), this.limit)
    }

    @PostMapping("/load")
    fun load(@RequestBody options: Map<String,Any>) {
        val size: Int = options.get("size") as Int
        for(i in 1..size) {
            this.repo.save(TodoEntity("Todo num ${i}", false))
        }
    }

    @PostMapping("/drop")
    fun drop() {
        this.repo.deleteAll()
    }

    @GetMapping("/dump")
    fun dump(): List<TodoEntity> {
        return this.repo.findAll().iterator().asSequence().toList()
    }
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}




















