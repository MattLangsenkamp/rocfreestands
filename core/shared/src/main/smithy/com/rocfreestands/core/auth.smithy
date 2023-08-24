$version: "2"

namespace com.rocfreestands.core

use alloy#simpleRestJson

@simpleRestJson
service AuthService {
    operations: [Login]
}

@http(method: "POST", uri: "/login")
operation Login {
    input : AuthRequest
    output: AuthResponse
}

structure AuthRequest {
    @required
    username: String

    @required
    password: String
}

structure AuthResponse {
    @required
    jwt: String
}