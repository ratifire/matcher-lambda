
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

data class Output(val message: String)

class Main : RequestHandler<Map<String, Any>, Output> {

    override fun handleRequest(input: Map<String, Any>, context: Context): Output {
        return Output(message = "Hello, Oleksandr")
    }
}