package ca.ulaval.ima.mp.api

inline fun <T> createHandler(crossinline call: (result: APIService.Result<T>) -> Unit): APIService.ResponseHandler<T> {
        return object : APIService.ResponseHandler<T> {
            override fun onResult(result: APIService.Result<T>) {
                return call(result)
            }
        }
}
