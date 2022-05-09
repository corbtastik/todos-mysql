package io.todos.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@SpringBootApplication
@RestController
@RequestMapping("todos")
class App(
    @Autowired @Qualifier("todosRepo") val repo: TodosRepo,
    @Value("\${todos.api.limit}") val limit: Int,
    @Value("\${todos.ids.tinyId}") val tinyId: Boolean) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    fun create(@RequestBody todo: Todo): Todo {
        if(ObjectUtils.isEmpty(todo.title)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Todo title can't be empty!")
        }

        val count = this.repo.count()
        if(count >= limit) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "todos.api.limit=$limit, todos.size=$count")
        }

        val createObject = Todo()
        if (tinyId) {
            todo.id = UUID.randomUUID().toString().substring(0, 8)
        } else {
            todo.id = UUID.randomUUID().toString()
        }

        createObject.id = todo.id
        createObject.title = todo.title
        if(!ObjectUtils.isEmpty(todo.complete)) {
            createObject.complete = todo.complete
        }
        return this.repo.save(createObject)
    }

    @GetMapping("/")
    fun retrieve(): List<Todo> {
        return this.repo.findAll().iterator().asSequence().toList()
    }

    @GetMapping("/paged")
    fun pagedRetrieve(@RequestParam("pageSize") pageSize: Int,
        @RequestParam("page") page: Int): List<Todo> {

        return this.repo.findAll(PageRequest.of(page, pageSize)).iterator().asSequence().toList()
    }

    @GetMapping("/{id}")
    fun retrieve(@PathVariable id: String): Todo {
        if (!repo.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "todo.id=$id")
        }
        return repo.findById(id).get()
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody todo: Todo): Todo {
        if (!repo.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "todo.id=$id")
        }
        val updateObject = repo.findById(id).get()
        if(!ObjectUtils.isEmpty(todo.title)) {
            updateObject.title = todo.title
        }
        if(!ObjectUtils.isEmpty(todo.complete)) {
            updateObject.complete = todo.complete
        }
        return this.repo.save(updateObject)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        repo.deleteById(id)
    }

    @DeleteMapping("/drop")
    fun drop() {
        this.repo.deleteAll()
    }

    @GetMapping("/limit")
    fun limit(): Limit {
        return Limit(this.repo.count().toInt(), this.limit)
    }

}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}




















