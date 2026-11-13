ALTER TABLE portal_identidad_revision
 ADD COLUMN oscuro_principal VARCHAR(7),
 ADD COLUMN oscuro_fondo VARCHAR(7),
 ADD COLUMN oscuro_acento VARCHAR(7),
 ADD CONSTRAINT ck_portal_oscuro_completo CHECK (
  (oscuro_principal IS NULL AND oscuro_fondo IS NULL AND oscuro_acento IS NULL)
  OR (oscuro_principal IS NOT NULL AND oscuro_fondo IS NOT NULL AND oscuro_acento IS NOT NULL
   AND oscuro_principal ~ '^#[0-9a-fA-F]{6}$' AND oscuro_fondo ~ '^#[0-9a-fA-F]{6}$' AND oscuro_acento ~ '^#[0-9a-fA-F]{6}$')
 );
