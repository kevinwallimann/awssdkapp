package com.github.kevinwallimann

import org.slf4j.LoggerFactory
import scopt.OptionParser


object MyAwsSdkApp extends App {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  private case class CmdConfig(
    demoApp: String = "",
    s3DemoAppConfig: S3DemoAppConfig = S3DemoAppConfig(),
    emrDemoAppConfig: EmrDemoAppConfig = EmrDemoAppConfig(),
    useV1: Boolean = true
  )

  case class S3DemoAppConfig(
    bucket: String = ""
  )

  case class EmrDemoAppConfig(
    jobFlowId: String = "",
    mainClass: String = "",
    jar: String = "",
    stepName: String = ""
  )
  
  private class CmdParser(programName: String) extends OptionParser[CmdConfig](programName) {
    head("Usage: java -jar awssdkapp-0.1.0-SNAPSHOT.jar (s3|emr)")
    opt[Boolean]("use-v2").optional().action((_, config) => {
      config.copy(useV1 = false)
    })
    cmd("s3")
      .action((_, conf) => conf.copy(demoApp = "s3"))
      .children(
        opt[String]('b', "bucket").required().action((value, config) => {
          config.copy(s3DemoAppConfig = config.s3DemoAppConfig.copy(bucket = value))
        }),
      )
    cmd("emr")
      .action((_, conf) => conf.copy(demoApp = "emr"))
      .children(
        opt[String]('j', "job-flow-id").required().action((value, config) => {
          config.copy(emrDemoAppConfig = config.emrDemoAppConfig.copy(jobFlowId = value))
        }),
        opt[String]('m', "main-class").required().action((value, config) => {
          config.copy(emrDemoAppConfig = config.emrDemoAppConfig.copy(mainClass = value))
        }),
        opt[String](name = "jar").required().action((value, config) => {
          config.copy(emrDemoAppConfig = config.emrDemoAppConfig.copy(jar = value))
        }),
        opt[String](name = "step-name").required().action((value, config) => {
          config.copy(emrDemoAppConfig = config.emrDemoAppConfig.copy(stepName = value))
        })
      )
  }

  private def getCmdLineArguments(args: Array[String]): CmdConfig = {
    val parser = new CmdParser("./MyAwsSdkApp.jar")

    val optionCmd = parser.parse(args, CmdConfig())
    if (optionCmd.isEmpty) {
      // Wrong arguments provided, the message is already displayed
      System.exit(1)
    }
    optionCmd.get
  }


  override def main(args: Array[String]): Unit = {

    val cmd: CmdConfig = getCmdLineArguments(args)
    cmd.demoApp match {
      case "s3" if cmd.useV1 => S3DemoAppV1.execute(cmd.s3DemoAppConfig)
      case "s3" if !cmd.useV1 => S3DemoAppV2.execute(cmd.s3DemoAppConfig)
      case "emr" if cmd.useV1 => EmrDemoAppV1.execute(cmd.emrDemoAppConfig)
      case "emr" if !cmd.useV1 => EmrDemoAppV2.execute(cmd.emrDemoAppConfig)
      case _ => logger.error("Unknown demo app")
    }
  }
}
