
package com.github.kevinwallimann

import com.github.kevinwallimann.MyAwsSdkApp.EmrDemoAppConfig
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.emr.EmrClient
import software.amazon.awssdk.services.emr.model.{ActionOnFailure, AddJobFlowStepsRequest, HadoopJarStepConfig, StepConfig}

object EmrDemoAppV2 {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  def execute(config: EmrDemoAppConfig): Unit = {
    import scala.collection.JavaConverters._
    val stepConfig = StepConfig.builder()
      .hadoopJarStep(HadoopJarStepConfig.builder()
        .jar("command-runner.jar")
        .args("spark-submit","--executor-memory","1g","--class",s"${config.mainClass}",s"${config.jar}","10")
        .build())
      .actionOnFailure(ActionOnFailure.CONTINUE)
      .name(config.stepName)
      .build()
    val jobFlowStepsRequest = AddJobFlowStepsRequest.builder()
      .jobFlowId(config.jobFlowId)
      .steps(Seq(stepConfig).asJava)
      .build()
    val emrClient = EmrClient.builder().build()
    val response = emrClient.addJobFlowSteps(jobFlowStepsRequest)
    logger.info(response.toString)
  }
}
