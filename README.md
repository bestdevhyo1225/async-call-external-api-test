# Async Call - External API

Spring MVC`@Async` 어노테이션을 활용해서 비동기 방식으로 호출되는지 결과를 확인해보고 싶어서 테스트를 진행하게 되었다.

<br>

## 2개 이상의 메소드를 동기 방식으로 호출한 결과 (Sync)

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

## 2개 이상의 메소드를 비동기 방식으로 호출한 결과 (Async)

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

## 동기 방식에서 실행되는 스레드의 갯수와 트랜잭션 전파 여부 확인

```kotlin
@Service
@Transactional
class TransactionalHandler(
    private val transactionAService: TransactionAService,
    private val transactionBService: TransactionBService
) {
    
    fun handleSync() {
        logger.info("[ TransactionHandler ] CurrentTransactionName  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        val startTime: Long = System.currentTimeMillis()

        val aServiceResult: String = transactionAService.executeSync()
        val bServiceResult: String = transactionBService.executeSync()

        val endTime: Long = System.currentTimeMillis()

        logger.info("[ TransactionHandler ] combineResult = $aServiceResult + $bServiceResult")
        logger.info("[ TransactionHandler ] execution time = " + (endTime - startTime))
    }
}
```
```kotlin
@Service
@Transactional
class TransactionAService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun executeSync(): String {

        logger.info("[ A Service ] CurrentTransactionName  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        try {
            Thread.sleep(3000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "Success A Service!!"
    }
}
```
```kotlin
@Service
@Transactional
class TransactionBService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun executeSync(): String {

        logger.info("[ B Service ] CurrentTransactionName  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        try {
            Thread.sleep(2000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "Success B Service!!"
    }
}
```

![스크린샷 2021-03-31 오후 4 08 31](https://user-images.githubusercontent.com/23515771/113104308-57e0ad00-923b-11eb-91b5-aa41282fc6cf.png)

> 실행되는 스레드의 갯수는?

- `nio-9000-exec-1`의 이름을 가진 스레드 `1개`에서 모든 작업을 수행하고 있다.

> @Transactional 어노테이션이 부모 트랜잭션(TransactionalHandler) 에서 자식 트랜잭션(TransactionAService, TransactionBService) 으로 전파되는지?

- `Handler, AService, BSerivce` 모두 `me.hyoseok.rest.template.example.service.TransactionHandler.handleSync` 트랜잭션에서 수행되고 있음을 확인할 수 있다.

## 비동기 방식에서 실행되는 스레드의 갯수와 트랜잭션 전파 여부 확인

```kotlin
@Service
@Transactional
class TransactionHandler(
    private val transactionAService: TransactionAService,
    private val transactionBService: TransactionBService
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun handle() {

        logger.info("[ TransactionHandler ] Transaction  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        val startTime: Long = System.currentTimeMillis()

        val aServiceResult: CompletableFuture<String> = transactionAService.execute()
        val bServiceResult: CompletableFuture<String> = transactionBService.execute()

        aServiceResult.thenCombine(bServiceResult) { result1, result2 -> "$result1 + $result2" }
            .thenAccept { combineResult ->
                val endTime: Long = System.currentTimeMillis()

                logger.info("[ TransactionHandler ] combineResult = $combineResult")
                logger.info("[ TransactionHandler ] execution time = " + (endTime - startTime))
            }
    }
}
```
```kotlin
@Service
@Transactional
class TransactionAService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Async(value = "threadPoolTaskExecutor")
    fun execute(): CompletableFuture<String> {

        logger.info("[ A Service ] Transaction  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        try {
            Thread.sleep(3000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("Success A Service!!")
    }
}
```
```kotlin
@Service
@Transactional
class TransactionBService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Async(value = "threadPoolTaskExecutor")
    fun execute(): CompletableFuture<String> {

        logger.info("[ B Service ] Transaction = " + TransactionSynchronizationManager.getCurrentTransactionName())

        try {
            Thread.sleep(2000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("Success B Service!!")
    }
}
```

![스크린샷 2021-03-31 오후 4 16 39](https://user-images.githubusercontent.com/23515771/113105377-7b582780-923c-11eb-8ac5-757ba8251669.png)

> 실행되는 스레드의 갯수는?

- `Main 스레드`인 `nio-9000-exec-3` 스레드 실행

- `스레드 풀`에 존재하는 `lTaskExecutor-2` 스레드 실행

- `스레드 풀`에 존재하는 `lTaskExecutor-1` 스레드 실행

- 총 `3개`의 스레드가 별 개의 작업을 수행한다.

> @Transactional 어노테이션이 부모 트랜잭션(TransactionalHandler) 에서 자식 트랜잭션(TransactionAService, TransactionBService) 으로 전파되는지?

- 3개의 스레드가 실행되기 때문에 `3개의 트랜잭션이 별도로 수행된다.` (`Spring MVC`에서 `컨테이너`는 `스레드마다 각각의 트랜잭션을 할당한다.`)

- `TransactionalHandler`의 트랜잭션
  
    - me.hyoseok.rest.template.example.service.`TransactionHandler.handle`

- `TransactionAService`의 트랜잭션

    - me.hyoseok.rest.template.example.service.`TransactionAService.execute`

- `TransactionBService`의 트랜잭션

    - me.hyoseok.rest.template.example.service.`TransactionBService.execute`
