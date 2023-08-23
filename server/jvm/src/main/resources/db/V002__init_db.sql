create TABLE if NOT exists location(
    uuid VARCHAR(40) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    description VARCHAR(765) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL
        CONSTRAINT lat_top_bound CHECK (latitude<=90),
        CONSTRAINT lat_bottom_bound CHECK (latitude>=-90),
    longitude DOUBLE PRECISION NOT NULL
        CONSTRAINT long_top_bound CHECK (latitude<=180),
        CONSTRAINT long_bottom_bound CHECK (latitude>=-180),
    image varchar(255) NOT NULL,
    creation_date_time VARCHAR(255) NOT NULL
);