-- Referential integrity applied after Hibernate creates the tables (postgres profile).
-- Adds the foreign key from listings.landlord_id to user_accounts.id so the database
-- rejects listings that point at a non-existent landlord (DBMS-F-03) and prevents
-- deleting a landlord that still owns listings (DBMS-N-11, default RESTRICT).
--
-- Idempotent without PL/pgSQL: drop-if-exists then add, so it is safe to run on every
-- startup alongside spring.jpa.hibernate.ddl-auto=update.
ALTER TABLE listings DROP CONSTRAINT IF EXISTS fk_listings_landlord;
ALTER TABLE listings ADD CONSTRAINT fk_listings_landlord FOREIGN KEY (landlord_id) REFERENCES user_accounts (id);
