Company (name, phone number, zip number, country, street info)

Product (id, name, description, brand name)

Produce (company, product, produce id, capacity)

Transaction (company, product, transaction id, amount, order date)

ZipCity(zip, city)

Email(name,email)

Transaction History(history_id, company, product, amount, created_date)



PRIMARY KEY (Company) = < name >

PRIMARY KEY (Product) = < id >

PRIMARY KEY (Produce) = < produce id >

PRIMARY KEY (Transaction) = < transaction id >

PRIMARY KEY (ZipCity) = < zip >

PRIMARY KEY (Email) = <name, email>

PRIMARY KEY (Email) = <history_id>

FOREIGN KEY Produce (Company) REFERENCES Company (name)

FOREIGN KEY Produce (Product) REFERENCES Product (id)

FOREIGN KEY Transaction (Company) REFERENCES Company (name)

FOREIGN KEY Transaction (Product) REFERENCES Product (id)

FOREIGN KEY ZipCity (Company) REFERENCES Company (name)


//AYLİN AYDIN
