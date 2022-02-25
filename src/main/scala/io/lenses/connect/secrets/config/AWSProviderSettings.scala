/*
 *
 *  * Copyright 2017-2020 Lenses.io Ltd
 *
 */

package io.lenses.connect.secrets.config

import io.lenses.connect.secrets.connect.AuthMode.AuthMode
import io.lenses.connect.secrets.connect._
import org.apache.kafka.common.config.types.Password
import org.apache.kafka.connect.errors.ConnectException

case class AWSProviderSettings(
    region: String,
    accessKey: String,
    secretKey: Password,
    authMode: AuthMode,
    fileDir: String,
    hostname: String,
    port: Integer,
    username: String
)

import AbstractConfigExtensions._
object AWSProviderSettings {
  def apply(configs: AWSProviderConfig): AWSProviderSettings = {
    val region = configs.getStringOrThrowOnNull(AWSProviderConfig.AWS_REGION)
    val accessKey =
      configs.getStringOrThrowOnNull(AWSProviderConfig.AWS_ACCESS_KEY)
    val secretKey =
      configs.getPasswordOrThrowOnNull(AWSProviderConfig.AWS_SECRET_KEY)

    val authMode =
      getAuthenticationMethod(configs.getString(AWSProviderConfig.AUTH_METHOD))

    if (authMode == AuthMode.CREDENTIALS) {
      if (accessKey.isEmpty)
        throw new ConnectException(
          s"${AWSProviderConfig.AWS_ACCESS_KEY} not set"
        )
      if (secretKey.value().isEmpty)
        throw new ConnectException(
          s"${AWSProviderConfig.AWS_SECRET_KEY} not set"
        )
    }
    val fileDir = configs.getString(FILE_DIR)
    val hostname = configs.getString(AWSProviderConfig.HOST_NAME)
    val port = configs.getInt(AWSProviderConfig.PORT)
    val username = configs.getString(AWSProviderConfig.USER_NAME)

    new AWSProviderSettings(
      region = region,
      accessKey = accessKey,
      secretKey = secretKey,
      authMode = authMode,
      fileDir = fileDir,
      hostname = hostname,
      port = port,
      username = username
    )
  }
}
