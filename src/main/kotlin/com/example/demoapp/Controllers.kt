package com.example.demoapp

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@ControllerAdvice
class ExceptionHandler(
        private val errorMessage: ResourceBundleMessageSource
) {
    @ExceptionHandler(DemoException::class)
    fun handleException(exception: DemoException): ResponseEntity<*> {
        return when (exception) {
            is UserNameExistsException -> ResponseEntity.badRequest()
                    .body(exception.getErrorMessage(errorMessage, exception.userName))

            is UserNotFoundException -> ResponseEntity.badRequest()
                    .body(exception.getErrorMessage(errorMessage, exception.id))

            is ProductNotFoundException -> ResponseEntity.badRequest()
                    .body(exception.getErrorMessage(errorMessage, exception.id))

            is CategoryNotFoundException -> ResponseEntity.badRequest()
                    .body(exception.getErrorMessage(errorMessage, exception.id))

            is UserPaymentTransactionNotFoundException -> ResponseEntity.badRequest()
                    .body(exception.getErrorMessage(errorMessage, exception.id))

            is TransactionNotFoundException -> ResponseEntity.badRequest()
                    .body(exception.getErrorMessage(errorMessage, exception.id))

            is TransactionItemNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessage,exception.id))
        }
    }
}

@RestController
@RequestMapping("user")
class UserController(private val service: UserService){
    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneUserDto> = service.getAll(pageable)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneUserDto = service.getOne(id)

    @PostMapping("{userId}/payments")
    fun makePayment(@PathVariable userId: Long,@RequestParam amount: BigDecimal) : ResponseEntity<Unit> {
        val user = service.makePayment(userId,amount)
        return ResponseEntity.ok(user)
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UserUpdateDto) = service.update(id,dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}


@RestController
@RequestMapping("UserPaymentTransaction")
class UserPaymentTransactionController(private val service: UserPaymentTransactionService) {
    @PostMapping
    fun create(@RequestBody dto: UserPaymentTransactionCreateDto) = service.create(dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneTransactionItem = service.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneTransactionItem> = service.getAll(pageable)
}

@RestController
@RequestMapping("category")
class CategoryController(private val service: CategoryService){
    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneCategoryDto> = service.getAll(pageable)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneCategoryDto = service.getOne(id)

    @PostMapping
    fun create(@RequestBody dto: CategoryCreateDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UpdateCategoryDto) = service.update(id,dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}

@RestController
@RequestMapping("product")
class ProductController(private val service: ProductService){
    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneProductDto> = service.getAll(pageable)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneProductDto = service.getOne(id)

    @PostMapping
    fun create(@RequestBody dto: ProductCreateDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ProductUpdateDto) = service.update(id,dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}

@RestController
@RequestMapping("transaction")
class TransactionController(private val service: TransactionService){
    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneTransactionDto> = service.getAll(pageable)
}


