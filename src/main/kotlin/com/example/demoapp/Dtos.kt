package com.example.demoapp

import jakarta.persistence.EntityManager
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class BaseMessage(val code : Int, val message: String?)

data class UserCreateDto(
        val fullName : String,
        val userName : String
){
    fun toEntity() = User(
            fullName,
            userName,
    )
}
data class UserUpdateDto(
        val fullName: String?,
        val userName: String?,
)

data class GetOneUserDto(
        val fullName: String,
        val userName: String,
        val balance : BigDecimal
){
    companion object{
        fun toDto(user: User): GetOneUserDto{
            return user.run {
                GetOneUserDto(fullName,userName,balance)
            }
        }
    }
}


data class UserPaymentTransactionCreateDto(
        val amount: BigDecimal,
        val date : Date,
        val userId : Long? = null
){
    fun toEntity(entityManager: EntityManager) = UserPaymentTransaction(
            amount = amount,
            date = date,
            user = entityManager.getReference(User::class.java, userId)
    )
}
data class UserPaymentUpdateDto(
        val amount: BigDecimal,
        val date: LocalDate,
        val userId: User
)
data class GetOneUserPaymentTransactionDto(
        val amount: BigDecimal,
        val date: Date,
        val user: User
)
{
    companion object{
        fun toDto(userPaymentTransaction: UserPaymentTransaction) : GetOneUserPaymentTransactionDto{
            return userPaymentTransaction.run {
                GetOneUserPaymentTransactionDto(amount,date,user)
            }
        }
    }
}


data class CategoryCreateDto(  //Admin
        val name : String,
        val order : Long,
        val description : String
)
{
    fun toEntity() = Category(
            name,
            order,
            description
    )
}
data class UpdateCategoryDto(
        val name: String?,
        val order: Long?,
        val description: String?
)
data class GetOneCategoryDto(
        val name: String,
        val order: Long,
        val description: String
)
{
    companion object{
        fun toDto(category: Category) : GetOneCategoryDto{
            return category.run {
                GetOneCategoryDto(name,order,description)
            }
        }
    }
}

data class ProductCreateDto(
        val name: String,
        val count : Long,
        val categoryId: Category
)
{
    fun toEntity(entityManager: EntityManager) = Product(
            name,
            count,
            category = entityManager.getReference(Category::class.java, categoryId)
    )
}
data class ProductUpdateDto(
        val name: String?,
        val count: Long?,
        val categoryId: Category?
)
data class GetOneProductDto(
        val name: String,
        val count: Long,
        val categoryId: Category
)
{
    companion object{
        fun toDto(product: Product) : GetOneProductDto{
            return product.run {
                GetOneProductDto(name,count,category)
            }
        }
    }
}

data class TransactionCreateDto(
        val totalAmount : Long,
        val date : LocalDate,
        val userId : User
){
    fun toEntity(entityManager: EntityManager) = Transaction(
            totalAmount,
            date,
            user = entityManager.getReference(User::class.java, userId)
    )
}
data class TransactionUpdateDto(
        val totalAmount: Long,
        val date: LocalDate,
        val userId: User
)
data class GetOneTransactionDto(
        val totalAmount: Long,
        val date: LocalDate,
        val userId: User
)
{
    companion object{
        fun toDto(transaction: Transaction) : GetOneTransactionDto{
            return transaction.run{
                GetOneTransactionDto(totalAmount,date,user)
            }
        }
    }
}

data class TransactionItemCreateDto(
        val productId: Product,
        val count: Long,
        val amount: BigDecimal,
        val totalAmount: BigDecimal,
        val transactionId: Transaction
)
{
    fun toEntity(entityManager: EntityManager) = TransactionItem(
            count,
            amount,
            totalAmount,
            product = entityManager.getReference(Product::class.java, productId),
            transaction = entityManager.getReference(Transaction::class.java,transactionId)
    )
}
data class TransactionItemUpdateDto(
        val productId: Product,
        val count: Long,
        val amount: BigDecimal,
        val totalAmount: Long,
        val transactionId: Transaction
)
data class GetOneTransactionItem(
        val productId: Product,
        val count: Long,
        val amount: BigDecimal,
        val totalAmount: BigDecimal,
        val transactionId: Transaction
)
{
    companion object{
        fun toDto(transactionItem: TransactionItem) :GetOneTransactionItem{
            return transactionItem.run {
                GetOneTransactionItem(product,count,amount,totalAmount,transaction)
            }
        }
    }
}


