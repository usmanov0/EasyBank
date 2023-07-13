package com.example.demoapp

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.findByIdOrNull

interface BaseRepository<T: BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T>{
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>
}

class BaseRepositoryImpl<T: BaseEntity>(
        entityInformation: JpaEntityInformation<T, Long>, entityManager: EntityManager,): SimpleJpaRepository<T,Long>(
                entityInformation,entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run {
        if (deleted) null else this}


    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run{
        deleted = true
        save(this)
    }

    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)

    override fun findAllNotDeleted(pageable: Pageable): Page<T> = findAll(isNotDeletedSpecification, pageable)




}


interface UserRepository : BaseRepository<User>{
    fun existsByUserName(username: String): Boolean
    fun existsByIdAndDeletedFalse(id: Long): Boolean

}

interface UserPaymentTransactionRepository: BaseRepository<UserPaymentTransaction>{
    fun existsByIdAndDeletedFalse(id: Long): Boolean



}
interface  CategoryRepository: BaseRepository<Category>{
    fun existsCategoriesByOrder(categoryName: String): Boolean
    fun existsByIdAndDeletedFalse(id: Category): Boolean

}
interface ProductRepository: BaseRepository<Product>{
    fun existsByIdAndDeletedFalse(id: Long): Boolean

}

interface TransactionRepository: BaseRepository<Transaction>{
    fun existsByIdAndDeletedFalse(id: Long): Boolean

}
interface TransactionItemRepository: BaseRepository<TransactionItem>{
    fun existsByIdAndDeletedFalse(id: Long): Boolean

}
