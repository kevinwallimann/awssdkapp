
package com.github.kevinwallimann

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder
import com.amazonaws.services.elasticmapreduce.model.{ActionOnFailure, AddJobFlowStepsRequest, HadoopJarStepConfig, StepConfig}
import com.github.kevinwallimann.MyAwsSdkApp.EmrDemoAppConfig
import org.slf4j.LoggerFactory

object EmrDemoAppV1 {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  def execute(config: EmrDemoAppConfig): Unit = {
    import scala.collection.JavaConverters._
    val stepConfig = new StepConfig()
      .withHadoopJarStep(new HadoopJarStepConfig()
        .withJar("command-runner.jar")
        .withArgs("spark-submit","--executor-memory","1g","--class",s"${config.mainClass}",s"${config.jar}","10")
      )
      .withActionOnFailure(ActionOnFailure.CONTINUE)
      .withName(config.stepName)

    val jobFlowStepsRequest = new AddJobFlowStepsRequest()
      .withJobFlowId(config.jobFlowId)
      .withSteps(Seq(stepConfig).asJava)

    val emr = AmazonElasticMapReduceClientBuilder.standard().build()
    val response = emr.addJobFlowSteps(jobFlowStepsRequest)
    logger.info(response.toString)
  }
}
