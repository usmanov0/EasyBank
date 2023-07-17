package com.example.demoapp

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

sealed class DemoException(message: String? = null) : RuntimeException(message) {

    abstract fun errorType(): ENUM

    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource, vararg array: Any?): BaseMessage {
        return BaseMessage(
                errorType().code,
                errorMessageSource.getMessage(
                        errorType().toString(),
                        array,
                        Locale(LocaleContextHolder.getLocale().language)
                )
        )
    }
}

class MyCustomException(message: String) : Exception(message)



class UserNameExistsException(val userName: String) : DemoException() {
    override fun errorType() = ENUM.USER_NAME_EXISTS
}


class UserNotFoundException(val id: Long) : DemoException() {
    override fun errorType() = ENUM.USER_NOT_FOUND
}

class ProductNotFoundException(val id: Long): DemoException(){
    override fun errorType() = ENUM.PRODUCT_NOT_FOUND
}

class CategoryNotFoundException(val id: Long): DemoException(){
    override fun errorType() = ENUM.CATEGORY_NOT_FOUND
}


class UserPaymentTransactionNotFoundException(val id: Long) : DemoException() {
    override fun errorType() = ENUM.USER_PAYMENT_TRANSACTION_NOT_FOUND
}

class TransactionNotFoundException(val id: Long) :DemoException() {
    override fun errorType() = ENUM.TRANSACTION_NOT_FOUND
}

class TransactionItemNotFoundException(val id: Long): DemoException(){
    override fun errorType() = ENUM.TRANSACTION_ITEM_NOT_FOUND

}
