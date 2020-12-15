package me.hyoseok.rest.template.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class AsyncConfiguration {
    private val ASYNC_THREAD_POOL_SIZE = 5

    @Bean
    fun threadPoolTaskExecutor(): Executor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = ASYNC_THREAD_POOL_SIZE
        taskExecutor.maxPoolSize = ASYNC_THREAD_POOL_SIZE
        taskExecutor.setQueueCapacity(Int.MAX_VALUE)
        taskExecutor.setThreadNamePrefix("threadPoolTaskExecutor-")
        taskExecutor.initialize()
        return taskExecutor
    }
}
