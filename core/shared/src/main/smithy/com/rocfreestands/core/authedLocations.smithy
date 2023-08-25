$version: "2"

namespace com.rocfreestands.core

use alloy#simpleRestJson

@simpleRestJson
service AuthedLocationsService {
    operations: [CreateLocation, DeleteLocation]
}


@http(method: "POST", uri: "/location")
operation CreateLocation {

    input: LocationInput

    output: Location
}

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

