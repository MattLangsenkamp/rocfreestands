$version: "2"

namespace hello

use alloy#simpleRestJson

@simpleRestJson
service LocationsService {
    operations: [GetLocations, CreateLocation]
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
}

structure Location {
    @required
    id: Integer

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
    creationDateTime: Timestamp
}

list LocationList {
    member: Location
}

structure Locations {
    @required
    locations: LocationList
}