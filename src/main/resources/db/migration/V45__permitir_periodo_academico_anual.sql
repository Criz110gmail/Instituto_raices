ALTER TABLE periodo_academico DROP CONSTRAINT ck_periodo_tipo;
ALTER TABLE periodo_academico ADD CONSTRAINT ck_periodo_tipo
    CHECK (tipo IN ('BIMESTRE', 'TRIMESTRE', 'CUATRIMESTRE', 'SEMESTRE', 'ANUAL', 'OTRO'));
