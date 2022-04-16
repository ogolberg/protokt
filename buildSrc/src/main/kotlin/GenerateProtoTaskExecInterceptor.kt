import groovy.lang.DelegatingMetaClass
import groovy.lang.MetaClass
import org.codehaus.groovy.runtime.ProcessGroovyMethods
import org.gradle.api.GradleException

class GenerateProtoTaskExecInterceptor(
    delegate: MetaClass,
    private val agentParams: String) : DelegatingMetaClass(delegate) {
    // this commits a Groovy crime to intercept
    // GenerateProtoTask.compileFiles(cmd: List<String>)
    override fun invokeMethod(
        sender: Class<*>,
        receiver: Any,
        methodName: String,
        arguments: Array<out Any>,
        isCallToSuper: Boolean,
        fromInsideClass: Boolean
    ): Any? {
        if (methodName == "compileFiles") {
            val cmd = (arguments[0] as List<Any>).map {
                when (it) {
                    is String -> it
                    else -> "$it" // because groovy strings are sometimes GStringImpl
                }
            }

            val process = ProcessBuilder(cmd).apply {
                environment()["PROTOC_GEN_PROTOKT_OPTS"] = "-javaagent:${agentParams}"
            }.start()

            val stdout = StringBuffer()
            val stderr = StringBuffer()

            ProcessGroovyMethods.waitForProcessOutput(process, stdout, stderr)

            val output = "protoc: stdout: ${stdout}. stderr: ${stderr}"

            if (process.exitValue() == 0) {
                //
            } else {
                throw GradleException(output)
            }

            return null
        }

        return super.invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass)
    }
}