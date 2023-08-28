create sequence sales.sales_seq;

create table sales.TAB_SALE(
  id INTEGER NOT NULL,
  COD_PRODUCT varchar(45),
  DESCRIPTION varchar(255),
  BRAND varchar(255),
  PRICE decimal,
  COD_USER INTEGER,
  CONSTRAINT sale_pk PRIMARY KEY (id)
);

create sequence sales.sales_detail_seq;

create table sales.TAB_SALE_DETAIL(
  id INTEGER NOT NULL,
  COD_PRODUCT varchar(45),
  STATUS varchar(25),
  ERROR varchar(500),
  CONSTRAINT sale_detail_pk PRIMARY KEY (id)
);
