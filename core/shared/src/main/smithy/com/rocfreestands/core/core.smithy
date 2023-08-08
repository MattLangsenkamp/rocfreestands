$version: "2"

namespace com.rocfreestands.core

use alloy#simpleRestJson

@simpleRestJson
service LocationsService {
    operations: [GetLocations, CreateLocation, DeleteLocation]
}

@http(method: "GET", uri: "/locations")
@readonly
operation GetLocations {
    input := {
    }
    output: Locations
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

structure LocationInput {
    @required
    address: String

    @required
    name: String

    @required
    description: String

    @required
    latitude: Double

    @required
    longitude: Double

    @required
    image: Blob
}

structure Location {
    @required
    uuid: String

    @required
    address: String

    @required
    name: String

    @required
    description: String

    @required
    latitude: Double

    @required
    longitude: Double

    @required
    image: Blob

    @required
    creationDateTime: Timestamp
}

list LocationList {
    member: Location
}

structure Locations {
    @required
    locations: LocationList
}