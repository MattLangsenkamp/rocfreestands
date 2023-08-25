$version: "2"

namespace com.rocfreestands.core

use alloy#simpleRestJson

@simpleRestJson
service PublicLocationsService {
    operations: [GetLocations, CreateLocation, DeleteLocation]
}

@http(method: "GET", uri: "/locations")
@readonly
operation GetLocations {
    input := {
    }
    output: Locations
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
    image: String
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
    image: String

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