package com.rocfreestands.server.config

case class ServerConfig(
    username: String,
    password: String,
    psqlUsername: String,
    psqlPassword: String,
    picturePath: String,
    port: String
)
