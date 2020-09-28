-- INITIAL DB SCHEMA --

CREATE TABLE financer.attachments (
  id bigint(20) NOT NULL,
  content longblob NOT NULL,
  name varchar(255) NOT NULL,
  upload_date date NOT NULL,
  transaction_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table categories
--

CREATE TABLE categories (
  id bigint(20) NOT NULL,
  category_class int(11) NOT NULL,
  name varchar(255) NOT NULL,
  parent_id bigint(20) DEFAULT NULL,
  user_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table fixed_transactions
--

CREATE TABLE fixed_transactions (
  id bigint(20) NOT NULL,
  description varchar(255) DEFAULT NULL,
  vendor varchar(255) DEFAULT NULL,
  category_id bigint(20) NOT NULL,
  amount double NOT NULL,
  day int(11) DEFAULT NULL,
  is_variable bit(1) NOT NULL,
  product varchar(255) DEFAULT NULL,
  end_date date DEFAULT NULL,
  start_date date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table fixed_transactions_amounts
--

CREATE TABLE fixed_transactions_amounts (
  id bigint(20) NOT NULL,
  amount double NOT NULL,
  value_date date NOT NULL,
  fixedTransaction_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table products
--

CREATE TABLE products (
  id bigint(20) NOT NULL,
  amount double NOT NULL,
  name varchar(64) NOT NULL,
  quantity int(11) NOT NULL,
  transaction_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table settings
--

CREATE TABLE settings (
  id bigint(20) NOT NULL,
  property varchar(255) NOT NULL,
  value varchar(255) NOT NULL,
  user_id bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table tokens
--

CREATE TABLE tokens (
  id bigint(20) NOT NULL,
  expire_date date NOT NULL,
  ip_address varchar(255) NOT NULL,
  system varchar(255) DEFAULT NULL,
  token varchar(64) NOT NULL,
  user_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table transaction_id_sequence
--

CREATE TABLE transaction_id_sequence (
  next_val bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table transaction_id_sequence
--

INSERT INTO transaction_id_sequence (next_val) VALUES
(1);

-- --------------------------------------------------------

--
-- Table structure for table users
--

CREATE TABLE users (
  id bigint(20) NOT NULL,
  birth_date date DEFAULT NULL,
  email varchar(128) NOT NULL,
  gender varchar(255) DEFAULT NULL,
  name varchar(64) NOT NULL,
  surname varchar(64) NOT NULL,
  password varchar(64) NOT NULL,
  salt varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table variable_transactions
--

CREATE TABLE variable_transactions (
  id bigint(20) NOT NULL,
  description varchar(255) DEFAULT NULL,
  vendor varchar(255) DEFAULT NULL,
  category_id bigint(20) NOT NULL,
  value_date date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table verification_tokens
--

CREATE TABLE verification_tokens (
  id bigint(20) NOT NULL,
  expire_date date NOT NULL,
  token varchar(64) NOT NULL,
  verifying_date date DEFAULT NULL,
  user_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  ADD PRIMARY KEY (id),
  ADD KEY FKsaok720gsu4u2wrgbk10b5n8d (parent_id),
  ADD KEY FKghuylkwuedgl2qahxjt8g41kb (user_id);

--
-- Indexes for table fixed_transactions
--
ALTER TABLE fixed_transactions
  ADD PRIMARY KEY (id),
  ADD KEY FK_1rhn3uamvuyn4r27yt55mqtmn (category_id);

--
-- Indexes for table fixed_transactions_amounts
--
ALTER TABLE fixed_transactions_amounts
  ADD PRIMARY KEY (id),
  ADD KEY FKsnf2wqrwf9xw95mwy761hjb4o (fixedTransaction_id);

--
-- Indexes for table products
--
ALTER TABLE products
  ADD PRIMARY KEY (id),
  ADD KEY FK9vm5xxoumt4ce37r2n1okehf3 (transaction_id);

--
-- Indexes for table settings
--
ALTER TABLE settings
  ADD PRIMARY KEY (id),
  ADD UNIQUE KEY UKmk883ffd5trjmcrxy77vxcyw6 (user_id,property);

--
-- Indexes for table tokens
--
ALTER TABLE tokens
  ADD PRIMARY KEY (id),
  ADD UNIQUE KEY UKo2tqsmy69vta1pod3a3u8fmlw (user_id,token);

--
-- Indexes for table users
--
ALTER TABLE users
  ADD PRIMARY KEY (id);

--
-- Indexes for table variable_transactions
--
ALTER TABLE variable_transactions
  ADD PRIMARY KEY (id),
  ADD KEY FK_8k5hyxudq76cixg5bqwcck4mc (category_id);

--
-- Indexes for table verification_tokens
--
ALTER TABLE verification_tokens
  ADD PRIMARY KEY (id),
  ADD UNIQUE KEY UK_dqp95ggn6gvm865km5muba2o5 (user_id),
  ADD UNIQUE KEY UKpnrfi6owpku606078l9ealbmf (user_id,token);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table attachments
--
ALTER TABLE attachments
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table categories
--
ALTER TABLE categories
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table fixed_transactions_amounts
--
ALTER TABLE fixed_transactions_amounts
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table products
--
ALTER TABLE products
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table settings
--
ALTER TABLE settings
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table tokens
--
ALTER TABLE tokens
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table users
--
ALTER TABLE users
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table verification_tokens
--
ALTER TABLE verification_tokens
  MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table categories
--
ALTER TABLE categories
  ADD CONSTRAINT FKghuylkwuedgl2qahxjt8g41kb FOREIGN KEY (user_id) REFERENCES users (id),
  ADD CONSTRAINT FKsaok720gsu4u2wrgbk10b5n8d FOREIGN KEY (parent_id) REFERENCES categories (id);

--
-- Constraints for table fixed_transactions
--
ALTER TABLE fixed_transactions
  ADD CONSTRAINT FK_1rhn3uamvuyn4r27yt55mqtmn FOREIGN KEY (category_id) REFERENCES categories (id);

--
-- Constraints for table fixed_transactions_amounts
--
ALTER TABLE fixed_transactions_amounts
  ADD CONSTRAINT FKsnf2wqrwf9xw95mwy761hjb4o FOREIGN KEY (fixedTransaction_id) REFERENCES fixed_transactions (id);

--
-- Constraints for table products
--
ALTER TABLE products
  ADD CONSTRAINT FK9vm5xxoumt4ce37r2n1okehf3 FOREIGN KEY (transaction_id) REFERENCES variable_transactions (id);

--
-- Constraints for table settings
--
ALTER TABLE settings
  ADD CONSTRAINT FKf585xxww5h8b9up0ninp57tjs FOREIGN KEY (user_id) REFERENCES users (id);

--
-- Constraints for table tokens
--
ALTER TABLE tokens
  ADD CONSTRAINT FK2dylsfo39lgjyqml2tbe0b0ss FOREIGN KEY (user_id) REFERENCES users (id);

--
-- Constraints for table variable_transactions
--
ALTER TABLE variable_transactions
  ADD CONSTRAINT FK_8k5hyxudq76cixg5bqwcck4mc FOREIGN KEY (category_id) REFERENCES categories (id);

--
-- Constraints for table verification_tokens
--
ALTER TABLE verification_tokens
  ADD CONSTRAINT FK54y8mqsnq1rtyf581sfmrbp4f FOREIGN KEY (user_id) REFERENCES users (id);
COMMIT;