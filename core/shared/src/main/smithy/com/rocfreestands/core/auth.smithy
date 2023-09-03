$version: "2"

namespace com.rocfreestands.core

use alloy#simpleRestJson

@simpleRestJson
@httpBearerAuth
service AuthService {
    operations: [Login, Refresh]
}

@http(method: "POST", uri: "/login")
operation Login {
    input : AuthRequest
    output: AuthResponse
}

@auth([httpBearerAuth])
@http(method: "POST", uri: "/refresh")
operation Refresh {
    input : AuthRefresh
    output: AuthResponse
}

structure AuthRequest {
    @required
    username: String

    @required
    password: String
}

structure AuthRefresh {}

structure AuthResponse {
    @httpHeader("Set-Cookie")
    cookie: String,
    
    @required
    message: String
}