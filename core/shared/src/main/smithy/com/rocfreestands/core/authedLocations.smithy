$version: "2"

namespace com.rocfreestands.core

use alloy#simpleRestJson

@simpleRestJson
@httpBearerAuth
service AuthedLocationsService {
    operations: [CreateLocation, DeleteLocation]
}

@auth([httpBearerAuth])
@http(method: "POST", uri: "/location")
operation CreateLocation {

    input: LocationInput

    output: Location
}

@auth([httpBearerAuth])
@http(method: "DELETE", uri: "/location/{uuid}")
operation DeleteLocation {

    input:= {
        @required
        @httpLabel
        uuid: String
    }

    output:= {
        @required
        message: String
    }
}

