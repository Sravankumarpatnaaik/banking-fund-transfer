CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(16) NOT NULL,
    holder_name VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(20) NULL,
    password_hash VARCHAR(160) NOT NULL,
    account_type VARCHAR(16) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (account_number),
    UNIQUE KEY uq_accounts_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    txn_ref VARCHAR(32) NOT NULL,
    account_number VARCHAR(16) NOT NULL,
    txn_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    counterparty VARCHAR(16) NULL,
    remarks VARCHAR(120) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_transactions_ref_account (txn_ref, account_number),
    KEY ix_transactions_account_time (account_number, created_at),
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_number) REFERENCES accounts (account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS account_sequence (
    name VARCHAR(32) NOT NULL,
    next_value BIGINT NOT NULL,
    PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO account_sequence (name, next_value) VALUES ('account', 1000000001);
