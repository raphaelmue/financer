-- INITIAL DB SCHEMA --

CREATE TABLE attachments
(
    id             bigint(20)   NOT NULL AUTO_INCREMENT,
    content        longblob     NOT NULL,
    name           varchar(255) NOT NULL,
    upload_date    date         NOT NULL,
    transaction_id bigint(20)   NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table categories
--

CREATE TABLE categories
(
    id             bigint(20)   NOT NULL AUTO_INCREMENT,
    category_class int(11)      NOT NULL,
    name           varchar(255) NOT NULL,
    parent_id      bigint(20) DEFAULT NULL,
    user_id        bigint(20)   NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table fixed_transactions
--

CREATE TABLE fixed_transactions
(
    id          bigint(20) NOT NULL,
    description varchar(255) DEFAULT NULL,
    vendor      varchar(255) DEFAULT NULL,
    category_id bigint(20) NOT NULL,
    amount      double     NOT NULL,
    day         int(11)      DEFAULT NULL,
    is_variable bit(1)     NOT NULL,
    product     varchar(255) DEFAULT NULL,
    end_date    date         DEFAULT NULL,
    start_date  date       NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table fixed_transactions_amounts
--

CREATE TABLE fixed_transactions_amounts
(
    id                   bigint(20) NOT NULL AUTO_INCREMENT,
    amount               double     NOT NULL,
    value_date           date       NOT NULL,
    fixed_transaction_id bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table products
--

CREATE TABLE products
(
    id                      bigint(20)  NOT NULL AUTO_INCREMENT,
    amount                  double      NOT NULL,
    name                    varchar(64) NOT NULL,
    quantity                int(11)     NOT NULL,
    variable_transaction_id bigint(20)  NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table roles
--

CREATE TABLE roles
(
    id   bigint(20)  NOT NULL AUTO_INCREMENT,
    name varchar(45) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table settings
--

CREATE TABLE settings
(
    id       bigint(20)   NOT NULL AUTO_INCREMENT,
    property varchar(255) NOT NULL,
    value    varchar(255) NOT NULL,
    user_id  bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table tokens
--

CREATE TABLE tokens
(
    id          bigint(20)   NOT NULL AUTO_INCREMENT,
    expire_date date         NOT NULL,
    ip_address  varchar(255) NOT NULL,
    system      varchar(255) DEFAULT NULL,
    token       varchar(64)  NOT NULL,
    user_id     bigint(20)   NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table transaction_id_sequence
--

CREATE TABLE transaction_id_sequence
(
    next_val bigint(20) DEFAULT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table users
--

CREATE TABLE users
(
    id         bigint(20)   NOT NULL AUTO_INCREMENT,
    birth_date date         DEFAULT NULL,
    email      varchar(128) NOT NULL,
    gender     varchar(255) DEFAULT NULL,
    name       varchar(64)  NOT NULL,
    surname    varchar(64)  NOT NULL,
    password   varchar(64)  NOT NULL,
    salt       varchar(32)  NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table users_roles
--

CREATE TABLE users_roles
(
    user_id bigint(20) NOT NULL,
    role_id bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table variable_transactions
--

CREATE TABLE variable_transactions
(
    id          bigint(20) NOT NULL,
    description varchar(255) DEFAULT NULL,
    vendor      varchar(255) DEFAULT NULL,
    category_id bigint(20) NOT NULL,
    value_date  date       NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table verification_tokens
--

CREATE TABLE verification_tokens
(
    id             bigint(20)  NOT NULL AUTO_INCREMENT,
    expire_date    date        NOT NULL,
    token          varchar(64) NOT NULL,
    verifying_date date DEFAULT NULL,
    user_id        bigint(20)  NOT NULL
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table attachments
--
ALTER TABLE attachments
    ADD PRIMARY KEY (id);

--
-- Indexes for table categories
--
ALTER TABLE categories
    ADD PRIMARY KEY (id);

--
-- Indexes for table fixed_transactions
--
ALTER TABLE fixed_transactions
    ADD PRIMARY KEY (id);

--
-- Indexes for table fixed_transactions_amounts
--
ALTER TABLE fixed_transactions_amounts
    ADD PRIMARY KEY (id);

--
-- Indexes for table products
--
ALTER TABLE products
    ADD PRIMARY KEY (id);

--
-- Indexes for table settings
--
ALTER TABLE settings
    ADD PRIMARY KEY (id);
ALTER TABLE settings
    ADD UNIQUE (user_id, property);

--
-- Indexes for table tokens
--
ALTER TABLE tokens
    ADD PRIMARY KEY (id);
ALTER TABLE tokens
    ADD UNIQUE (user_id, token);

--
-- Indexes for table users
--
ALTER TABLE users
    ADD PRIMARY KEY (id);

--
-- Indexes for table roles
--
ALTER TABLE roles
    ADD PRIMARY KEY (id);

--
-- Indexes for table users_roles
--
ALTER TABLE users_roles
    ADD UNIQUE (user_id, role_id);

--
-- Indexes for table variable_transactions
--
ALTER TABLE variable_transactions
    ADD PRIMARY KEY (id);

--
-- Indexes for table verification_tokens
--
ALTER TABLE verification_tokens
    ADD PRIMARY KEY (id);

--
-- Constraints for table categories
--
ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_USER FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_PARENT FOREIGN KEY (parent_id) REFERENCES categories (id);

--
-- Constraints for table fixed_transactions
--
ALTER TABLE fixed_transactions
    ADD CONSTRAINT FK_FIXED_TRANSACTION_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

--
-- Constraints for table fixed_transactions_amounts
--
ALTER TABLE fixed_transactions_amounts
    ADD CONSTRAINT FK_AMOUNT_FIXED_TRANSACTION FOREIGN KEY (fixed_transaction_id) REFERENCES fixed_transactions (id);

--
-- Constraints for table products
--
ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCT_VARIABLE_TRANSACTION FOREIGN KEY (variable_transaction_id) REFERENCES variable_transactions (id);

--
-- Constraints for table settings
--
ALTER TABLE settings
    ADD CONSTRAINT FK_SETTINGS_USER FOREIGN KEY (user_id) REFERENCES users (id);

--
-- Constraints for table tokens
--
ALTER TABLE tokens
    ADD CONSTRAINT FK_TOKENS_USER FOREIGN KEY (user_id) REFERENCES users (id);

--
-- Constraints for table tokens
--
ALTER TABLE users_roles
    ADD CONSTRAINT FK_USER_ROLE FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE users_roles
    ADD CONSTRAINT FK_ROLE_USER FOREIGN KEY (role_id) REFERENCES roles (id);

--
-- Constraints for table variable_transactions
--
ALTER TABLE variable_transactions
    ADD CONSTRAINT FK_VARIABLE_TRANSACTION_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

--
-- Constraints for table verification_tokens
--
ALTER TABLE verification_tokens
    ADD CONSTRAINT FK_VERIFICATION_TOKEN_USER FOREIGN KEY (user_id) REFERENCES users (id);

--
-- Default data for table transaction_id_sequence
--

INSERT INTO transaction_id_sequence (next_val)
VALUES (1);

--
-- Default data for table roles
--

INSERT INTO roles(id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

COMMIT;