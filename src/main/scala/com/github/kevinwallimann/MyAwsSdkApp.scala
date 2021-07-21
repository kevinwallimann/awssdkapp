/*
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.kevinwallimann

import org.slf4j.LoggerFactory
import scopt.OptionParser


object MyAwsSdkApp extends App {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  private case class CmdConfig(
    demoApp: String = "",
    s3DemoAppConfig: Option[S3DemoAppConfig] = None
  )

  case class S3DemoAppConfig(
    bucket: String
  )

  private class CmdParser(programName: String) extends OptionParser[CmdConfig](programName) {
    head("Usage: java -jar awssdkapp-0.1.0-SNAPSHOT.jar (s3|emr)")
    cmd("s3")
      .action((_, conf) => conf.copy(demoApp = "s3"))
      .children(
        opt[String]('b', "bucket").required().action((value, config) => {
          config.copy(s3DemoAppConfig = config.s3DemoAppConfig match {
            case Some(s3DemoAppConfigValue) => Some(s3DemoAppConfigValue.copy(bucket = value))
            case None => Some(S3DemoAppConfig(bucket = value))
          })
        })
      )
    cmd("emr")
      .action((_, conf) => conf.copy(demoApp = "emr"))
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
      case "s3" => S3DemoApp.execute(cmd.s3DemoAppConfig.get)
      case "emr" =>
      case _ => logger.error("Unknown demo app")
    }
  }
}
