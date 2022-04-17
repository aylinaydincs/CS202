create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
    );
create FUNCTION sample_trigger() RETURNS TRIGGER AS
    '
    BEGIN
        IF (SELECT value FROM sample where id = NEW.id ) > 1000
        THEN
            RAISE SQLSTATE ''23503'';
        END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER sample_value AFTER insert ON sample
    FOR EACH ROW EXECUTE PROCEDURE sample_trigger();

create table if not exists product(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description  VARCHAR(64),
    brand_name VARCHAR (64)
    );
create table if not exists zipCity(
    zip int PRIMARY KEY,
    city VARCHAR(64)
    );
create table if not exists company(
    name VARCHAR(64) PRIMARY KEY,
    country VARCHAR(64),
    zip int,
    street VARCHAR(64),
    phone VARCHAR(64) NOT NULL UNIQUE,
    FOREIGN KEY  (zip) REFERENCES zipCity(zip)
    );
create table if not exists email(
    name VARCHAR(64),
    email VARCHAR(64),
    PRIMARY KEY (name, email),
    FOREIGN KEY  (name) REFERENCES company(name)
    );
create table if not exists produce(
    produce_id serial PRIMARY KEY,
    company_name VARCHAR(64) NOT NULL,
    product_id int NOT NULL,
    capacity int,
    FOREIGN KEY  (company_name) REFERENCES company(name),
    FOREIGN KEY  (product_id) REFERENCES product(id)
    /* If the ordered transaction amounts are more
    than the production capacity, the order should not be accepted.*/
    );
create table if not exists transaction(
    transaction_id serial PRIMARY KEY,
    company_name VARCHAR(64) NOT NULL,
    product_id int NOT NULL,
    amount int,
    order_date timestamp with time zone,
    FOREIGN KEY (company_name) REFERENCES company (name),
    FOREIGN KEY (product_id) REFERENCES product (id)
    );
create FUNCTION order_trigger() RETURNS TRIGGER AS
    '
    BEGIN
        IF (SELECT SUM(amount) FROM transaction where company_name = NEW.company_name AND product_id = NEW.product_id) >
           (SELECT capacity FROM produce where company_name = NEW.company_name AND product_id = NEW.product_id)
        THEN
            RAISE SQLSTATE ''23503'';
        END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER order_capacity AFTER insert ON transaction
    FOR EACH ROW EXECUTE PROCEDURE order_trigger();

create table if not exists transaction_history(
    history_id int PRIMARY KEY,
    company VARCHAR(64) NOT NULL,
    product int NOT NULL,
    amount int,
    created_date timestamp with time zone
);


