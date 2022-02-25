/*
 *
 *  * Copyright 2017-2020 Lenses.io Ltd
 *
 */

package io.lenses.connect.secrets.config

import java.util

import io.lenses.connect.secrets.connect.{AuthMode, _}
import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

object AWSProviderConfig {

  val AWS_REGION: String = "aws.region"
  val AWS_ACCESS_KEY: String = "aws.access.key"
  val AWS_SECRET_KEY: String = "aws.secret.key"
  val AUTH_METHOD: String = "aws.auth.method"
  val HOST_NAME: String = "aws.rds.hostname"
  val PORT: String = "aws.rds.port"
  val USER_NAME: String = "aws.rds.username"

  val config: ConfigDef = new ConfigDef()
    .define(
      AWS_REGION,
      Type.STRING,
      Importance.HIGH,
      "AWS region the Secrets manager is in"
    )
    .define(
      AWS_ACCESS_KEY,
      Type.STRING,
      null,
      Importance.HIGH,
      "AWS access key"
    )
    .define(
      AWS_SECRET_KEY,
      Type.PASSWORD,
      null,
      Importance.HIGH,
      "AWS password key"
    )
    .define(
      AUTH_METHOD,
      Type.STRING,
      AuthMode.CREDENTIALS.toString,
      Importance.HIGH,
      """
        | AWS authenticate method, 'credentials' to use the provided credentials 
        | or 'default' for the standard AWS provider chain.
        | Default is 'credentials'
        |""".stripMargin
    )
    .define(
      FILE_DIR,
      Type.STRING,
      "",
      Importance.MEDIUM,
      FILE_DIR_DESC
    )
    .define(
      HOST_NAME,
      Type.STRING,
      "",
      Importance.MEDIUM,
      "Hostname of RDS Instance"
    )
    .define(
      PORT,
      Type.INT,
      0,
      Importance.MEDIUM,
      "Port number of RDS Instance"
    )
    .define(
      USER_NAME,
      Type.STRING,
      "",
      Importance.MEDIUM,
      "Username to connect to RDS database"
    )
}

case class AWSProviderConfig(props: util.Map[String, _])
    extends AbstractConfig(AWSProviderConfig.config, props)
