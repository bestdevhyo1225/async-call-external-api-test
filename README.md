# Async Call - External API

Spring MVC`@Async` 어노테이션을 활용해서 비동기 방식으로 호출되는지 결과를 확인해보고 싶어서 테스트를 진행하게 되었다.

<br>

## 동기 방식 호출 결과 (Sync)

가장 먼저 동기 방식의 호출 테스트를 진행했고, 코드는 다음과 같다.

```kotlin
@Service
class SyncService {

    private val logger: Logger = LoggerFactory.getLogger(ApiController::class.java)

    fun callSyncFirst(): String {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(2000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Sync ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "callSyncFirst"
    }

    fun callSyncSecond(): String {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(3000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Sync ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "callSyncSecond"
    }
}
```

그리고 이를 `Controller`에서 호출한다.

```kotlin
@RestController
@RequestMapping(value = ["api"])
class ApiController(
    private val productService: ProductService,
    private val asyncService: AsyncService,
    private val syncService: SyncService
) {
    @GetMapping(value = ["/call-sync"])
    fun callSync() {
        val startTime: Long = System.currentTimeMillis()

        val firstResult: String = syncService.callSyncFirst()
        val secondResult: String = syncService.callSyncSecond()

        val endTime: Long = System.currentTimeMillis()

        val s = firstResult + secondResult

        logger.info("[ Sync Result ] value $s")
        logger.info("[ Sync Result ] execution time = " + (endTime - startTime))
    }
}
```

위의 코드를 예상해보자면, `callSyncFirst()` 호출하는데 `2초`가 걸리고, 그 다음 `callSyncSecond()`를 호출하는데 `3초`가 걸린다는 것을 예상할 수 있다. 결과를 바로 확인할 수
있는데 `하나의 스레드`에서 `동기`방식으로 처리된 것을 확인할 수 있고, 총 `5초`의 수행시간이 걸렸음을 확인할 수 있다.

![스크린샷 2021-03-23 오전 9 57 08](https://user-images.githubusercontent.com/23515771/112076644-23774c00-8bbe-11eb-9470-245379b519c0.png)

<br>

### 비동기 호출 방식 테스트 결과 (Async)

비동기 호출 방식을 테스트 하기 위한 코드는 아래와 같다. `@Async` 어노테이션과 `ThreadPoolTaskExecutor`를 활용했다. `ThreadPoolTaskExecutor`를 사용하지 않으면, 비동기
작업을 `Thread Pool`에서 처리하는 것이 아니라 새로운 스레드를 매번 생성해서 작업을 하기때문에 굉장히 비효율적이다.

```kotlin
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
```

```kotlin
@Service
class AsyncService {

    private val logger: Logger = LoggerFactory.getLogger(ApiController::class.java)

    @Async(value = "threadPoolTaskExecutor")
    fun callAsyncFirst(): CompletableFuture<String> {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(2000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Async ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("callAsyncFirst")
    }

    @Async(value = "threadPoolTaskExecutor")
    fun callAsyncSecond(): CompletableFuture<String> {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(3000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Async ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("callAsyncSecond")
    }
}
```

```kotlin
@RestController
@RequestMapping(value = ["api"])
class ApiController(
    private val productService: ProductService,
    private val asyncService: AsyncService,
    private val syncService: SyncService
) {
    @GetMapping(value = ["/call-async"])
    fun callAsync() {
        val startTime: Long = System.currentTimeMillis()

        val firstResult: CompletableFuture<String> = asyncService.callAsyncFirst()
        val secondResult: CompletableFuture<String> = asyncService.callAsyncSecond()

        firstResult.thenCombine(secondResult) { s1, s2 -> "$s1 + $s2" }
            .thenAccept { s ->
                val endTime: Long = System.currentTimeMillis()
                logger.info("[ Async Result ] value $s")
                logger.info("[ Async Result ] execution time = " + (endTime - startTime))
            }
    }
}
```

위의 코드를 통해 결과를 예상했을때, `비동기` 방식으로 수행되기 때문에 총 `3초`의 수행시간이 걸린다는 것을 짐작할 수 있으며, 결과 또한 `3초`가 걸린다는 것을 확인할 수 있다.

![스크린샷 2021-03-23 오전 10 09 43](https://user-images.githubusercontent.com/23515771/112077455-e613be00-8bbf-11eb-87d9-8d7c1192bc26.png)

총 수행시간이 `3초`인 이유는 `callAsyncFirst()` 메소드를 호출할 때 `lTaskExecutor-1` 스레드에서 처리되며, `callAsyncSecond()` 메소드를 호출할
때 `lTaskExecutor-2` 처리된다. 즉, `2개의 메소드`는 `별개의 스레드`에서 `각각 처리`되기 때문에 `callAsyncSecond()` 메소드는 `callAsyncFirst()` 메소드의 결과를 기다릴 필요가 없다.
