package com.example.demoapp

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.Date

interface UserService{
    fun makePayment(id: Long, amount: BigDecimal)
    fun update(id: Long, dto: UserUpdateDto)

    fun getOne(id: Long) : GetOneUserDto
    fun getAll(pageable: Pageable) : Page<GetOneUserDto>
    fun delete(id:Long)
    fun addBalance(id: Long, sum: BigDecimal)
    fun userHistory(id: Long): List<GetOneUserDto>
}

interface UserPaymentTransactionService{
    fun create(dto: UserPaymentTransactionCreateDto)
    fun getOne(id: Long) : GetOneTransactionItem
    fun getAll(pageable: Pageable) : Page<GetOneTransactionItem>
}

interface CategoryService{
    fun create(dto: CategoryCreateDto)
    fun update(id: Long, dto: UpdateCategoryDto)
    fun getOne(id: Long) : GetOneCategoryDto
    fun getAll(pageable: Pageable) : Page<GetOneCategoryDto>
    fun delete(id: Long)
}

interface ProductService{
    fun create(dto: ProductCreateDto)
    fun update(id:Long, dto: ProductUpdateDto)
    fun getOne(id: Long) : GetOneProductDto
    fun getAll(pageable: Pageable) : Page<GetOneProductDto>
    fun delete(id: Long)
}

interface TransactionService{
    fun getAll(pageable: Pageable) : Page<GetOneTransactionDto>

}

interface TransactionItemService{
    fun create(dto: TransactionItemCreateDto)
    fun getOne(id: Long) : GetOneTransactionItem
    fun getAll(pageable: Pageable) : Page<GetOneTransactionItem>
}



@Service
class UserServiceImpl(
        private val userRepository: UserRepository,
        private val userPaymentTransactionRepository: UserPaymentTransactionRepository,

):UserService {

        override fun makePayment(id: Long, amount: BigDecimal) {
            val user = userRepository.findById(id).orElseThrow { throw UserNotFoundException(id) }
            if (user.balance >= amount) {
                user.balance -= amount
                userRepository.save(user)

                val paymentTransaction = UserPaymentTransaction(amount = amount, date = Date(), user = user)
                userPaymentTransactionRepository.save(paymentTransaction)
            } else {
                throw IllegalArgumentException("Insufficent balance")
            }

        }

        override fun update(id: Long, dto: UserUpdateDto) {
            val user = userRepository.findByIdAndDeletedFalse(id)
                    ?: throw UserNotFoundException(id)
            dto.run {
                fullName?.let { user.fullName = it }
                userName?.let { user.userName = it }
            }

        }

        override fun getOne(id: Long) = userRepository.findByIdAndDeletedFalse(id)?.let { GetOneUserDto.toDto(it) }
                ?: throw UserNotFoundException(id)


        override fun getAll(pageable: Pageable) = userRepository.findAllNotDeleted(pageable).map { GetOneUserDto.toDto(it) }

        override fun delete(id: Long) {
            userRepository.trash(id) ?: throw UserNotFoundException(id)
        }

    override fun addBalance(id: Long, sum: BigDecimal) {
        val user = userRepository.findByIdAndDeletedFalse(id)?: throw UserNotFoundException(id)

        if (sum.toDouble() < 0){
            throw UserNotFoundException(id)
        }
        user.balance += sum
        userRepository.save(user)
    }

    override fun userHistory(id: Long):  List<GetOneUserDto> {
        val  userPayment = userPaymentTransactionRepository.findByUserID(id)
        return userPayment.map{
            UserPaymentDto.toDto(it)
        }

    }

}

@Service
class UserPaymentTransactionServiceImpl(
        private val userPaymentRepository: UserPaymentTransactionRepository,
        private val userRepository: UserRepository,
        private val entityManager: EntityManager
): UserPaymentTransactionService {
        override fun create(dto: UserPaymentTransactionCreateDto) {
            dto.run {
                val user = userId?.let {
                    userRepository.existsByIdAndDeletedFalse(it).runIfFalse { throw UserNotFoundException(it) }
                    entityManager.getReference(User::class.java, it)
                }
                dto.run {
                    amount?.let { user?.balance = user?.balance?.plus(it)!! }
                }
//                userPaymentRepository.save(toEntity())
            }
        }
    override fun getOne(id: Long) = userPaymentRepository.findByIdAndDeletedFalse(id)?. let { GetOneTransactionItem.toDto(it) }
            ?: throw UserPaymentTransactionNotFoundException(id)

    override fun getAll(pageable: Pageable) = userPaymentRepository.findAllNotDeleted(pageable).map { GetOneTransactionItem.toDto(it) }
}

@Service
class CategoryServiceImpl(
        private val categoryRepository: CategoryRepository
): CategoryService{
    override fun create(dto: CategoryCreateDto) {
        dto.run {
            categoryRepository.save(toEntity())
        }
    }

    override fun update(id: Long, dto: UpdateCategoryDto) {
        val category = categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException(id)
        dto.run {
            name?.let { category.name = it }
            order?.let { category.ordered = it }
            description?.let { category.description = it }
        }
    }


    override fun getOne(id: Long) = categoryRepository.findByIdAndDeletedFalse(id)?. let { GetOneCategoryDto.toDto(it) }
            ?:throw CategoryNotFoundException(id)


    override fun getAll(pageable: Pageable) = categoryRepository.findAllNotDeleted(pageable).map { GetOneCategoryDto.toDto(it) }


    override fun delete(id: Long) {
        categoryRepository.trash(id) ?: throw CategoryNotFoundException(id)
    }
}

@Service
class ProductServiceImpl(
        private val productRepository: ProductRepository,
        private val categoryRepository: CategoryRepository,
        private val entityManager: EntityManager,
        private val transactionItemRepository: TransactionItemRepository,

):ProductService{
    override fun create(dto: ProductCreateDto) {
        dto.run {
            productRepository.save(toEntity(entityManager))
        }
    }

    override fun update(id: Long, dto: ProductUpdateDto) {
        val product = productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException(id)
        dto.run {
            name?. let { product.name = it }
            count?. let{product.count = it}
        }

    }

    override fun getOne(id: Long) = productRepository.findByIdAndDeletedFalse(id)?. let { GetOneProductDto.toDto(it) }
            ?:throw ProductNotFoundException(id)


    override fun getAll(pageable: Pageable) = productRepository.findAllNotDeleted(pageable).map { GetOneProductDto.toDto(it) }


    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw ProductNotFoundException(id)
    }
}



@Service
class TransactionServiceImpl(private val transactionRepository: TransactionRepository):TransactionService {
    override fun getAll(pageable: Pageable): Page<GetOneTransactionDto> =
        transactionRepository.findAllNotDeleted(pageable).map {
            it.user.id?.let { userId -> GetOneTransactionDto(it.createdDate, it.totalAmount,userId) }
        }
}

@Service
class TransactionItemServiceImpl(
    private val entityManager: EntityManager,
    private val productRepository: ProductRepository,
    private val transactionItemRepository: TransactionItemRepository

):TransactionItemService
{
    override fun create(dto: TransactionItemCreateDto) {
            dto.run {
                val product = productId.let {
                    productRepository.existsByIdAndDeletedFalse(it).runIfFalse { throw ProductNotFoundException(it) }
                    entityManager.getReference(Product::class.java, it)}
                }
            }


    override fun getOne(id: Long): GetOneTransactionItem {
        return transactionItemRepository.findByIdAndDeletedFalse(id)?.let {GetOneTransactionItem.toDto(it)  }
            ?: throw TransactionItemNotFoundException(id)
    }

    override fun getAll(pageable: Pageable): Page<GetOneTransactionItem> {
        return transactionItemRepository.findAllNotDeleted(pageable).map { GetOneTransactionItem.toDto(it) }
    }


}





