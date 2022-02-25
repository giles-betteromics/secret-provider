/*
 *
 *  * Copyright 2017-2020 Lenses.io Ltd
 *
 */

package io.lenses.connect.secrets.providers

import java.time.OffsetDateTime
import java.util

import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator
import io.lenses.connect.secrets.config.{AWSProviderConfig, AWSProviderSettings}
import io.lenses.connect.secrets.connect.getSecretsAndExpiry
import org.apache.kafka.common.config.ConfigData
import org.apache.kafka.common.config.provider.ConfigProvider
import org.apache.kafka.connect.errors.ConnectException

import scala.collection.JavaConverters._

class AWSRdsTokenProvider() extends ConfigProvider with AWSHelper {

  var client: Option[RdsIamAuthTokenGenerator] = None
  var hostname: String = ""
  var port: Integer = 0
  var region: String = ""
  var username: String = ""

  override def get(path: String): ConfigData =
    new ConfigData(Map.empty[String, String].asJava)

  // path is expected to be the name of the AWS secret
  // keys are expect to be the keys in the payload
  override def get(path: String, keys: util.Set[String]): ConfigData = {

    client match {
      case Some(awsClient) =>
        //aws client caches so we don't need to check here
        val (expiry, data) = getSecretsAndExpiry(
          getSecrets(awsClient, hostname, port, region, username, keys.asScala.toSet))
        expiry.foreach(exp =>
          logger.info(s"Min expiry for TTL set to [${exp.toString}]"))
        data

      case None => throw new ConnectException("AWS client is not set.")
    }
  }

  override def close(): Unit = {}

  override def configure(configs: util.Map[String, _]): Unit = {
    val settings = AWSProviderSettings(AWSProviderConfig(props = configs))
    hostname = settings.hostname
    port = settings.port
    region = settings.region
    username = settings.username
    client = Some(createRDSClient(settings))
  }

  def getSecrets(
      awsClient: RdsIamAuthTokenGenerator,
      hostname: String,
      port: Integer,
      region: String,
      username: String,
      keys: Set[String]): Map[String, (String, Option[OffsetDateTime])] = {
    keys.map { key =>
      logger.info(s"Looking up value at [$hostname, $port, $username] for key [$key]")
      val (value, expiry) = getRdsSecretValue(awsClient, hostname, port, region, username)
      (key, (value, expiry))
    }.toMap
  }
}
