package com.example.demoapp

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.Temporal
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*


@MappedSuperclass
class BaseEntity(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
        @CreatedDate @Temporal(TemporalType.TIMESTAMP) var  createdDate : Date? = null,
        @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modidiedDate : Date? = null,
        @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
        )

@Entity(name = "users")
class User(
        var fullName : String,
        @Column(length = 30, unique = true) var userName : String,
        var balance : BigDecimal = BigDecimal.ZERO,
): BaseEntity() {
}

@Entity
class UserPaymentTransaction(
        var amount: BigDecimal,
        var date: Date,
        @ManyToOne var user: User
): BaseEntity()

@Entity
class Category(
        var name : String,
        var ordered : Long,
        var description : String
): BaseEntity()

@Entity
class Product(
        var name : String,
        var count : Long,
        @ManyToOne  var category: Category
): BaseEntity()

@Entity
class Transaction(
        var date : Date?,
        var totalAmount : Long,
        @ManyToOne var user: User
) : BaseEntity()

@Entity
class TransactionItem(
        var count : Long,
        var amount : BigDecimal,
        var totalAmount: BigDecimal,
        @ManyToOne var product: Product,
        @OneToOne var transaction: Transaction
): BaseEntity()



