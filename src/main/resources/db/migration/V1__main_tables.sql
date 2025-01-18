CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE SCHEMA "sys";


CREATE TABLE "sys"."user" (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    username VARCHAR(30) UNIQUE NOT NULL,
    name VARCHAR(70) NOT NULL,
    password BYTEA NOT NULL,
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT user_pk PRIMARY KEY (id)
);


CREATE TABLE "sys"."refresh_token" (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    id_user UUID NOT NULL,
    token UUID UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT refresh_token_pk PRIMARY KEY (id),
    CONSTRAINT refresh_token_user_fk FOREIGN KEY (id_user) REFERENCES "sys"."user"(id)
);


CREATE SCHEMA "pub";


CREATE TABLE "pub"."article" (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    id_user UUID NOT NULL,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(350),
    text_pt TEXT,
    text_en TEXT NOT NULL,
    published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT article_pk PRIMARY KEY (id),
    CONSTRAINT article_user_fk FOREIGN KEY (id_user) REFERENCES "sys"."user"(id)
);


CREATE OR REPLACE FUNCTION att_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER att_updated_at_field
BEFORE UPDATE ON "pub"."article"
FOR EACH ROW
EXECUTE FUNCTION att_updated_at();


CREATE OR REPLACE FUNCTION att_published_at()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.published THEN
        NEW.published_at = CURRENT_TIMESTAMP;
    ELSE
        NEW.published_at = NULL;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER update_published_at
BEFORE INSERT OR UPDATE ON "pub"."article"
FOR EACH ROW
EXECUTE FUNCTION att_published_at();


CREATE TABLE "pub"."tag" (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT tag_pk PRIMARY KEY (id)
);


CREATE TABLE "pub"."article_tag" (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    id_article UUID NOT NULL,
    id_tag UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT article_tag_pk PRIMARY KEY (id), 
    CONSTRAINT article_fk FOREIGN KEY (id_article) REFERENCES "pub"."article"(id) ON DELETE CASCADE,
    CONSTRAINT tag_fk FOREIGN KEY (id_tag) REFERENCES "pub"."tag"(id) ON DELETE CASCADE
);