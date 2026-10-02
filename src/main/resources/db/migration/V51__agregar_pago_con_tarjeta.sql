ALTER TABLE pago DROP CONSTRAINT ck_pago_metodo;

ALTER TABLE pago
    ADD CONSTRAINT ck_pago_metodo
    CHECK (metodo IN ('EFECTIVO', 'TRANSFERENCIA', 'TARJETA'));
