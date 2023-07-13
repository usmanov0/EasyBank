package com.example.demoapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
class DemoAppApplication

fun main(args: Array<String>) {
	runApplication<DemoAppApplication>(*args)
}
