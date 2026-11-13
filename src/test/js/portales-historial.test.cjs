const {test}=require('node:test');
const assert=require('node:assert/strict');
const {create}=require('../../main/resources/static/js/portales-historial.js');
const initial={principal:'#194f3d',fondo:'#ffffff',acento:'#ffff00',original:'true'};

test('deshacer y rehacer recuperan colores y modo original sin mutar las copias',()=>{
 const history=create(initial), changed={...initial,principal:'#123456',original:'false'};
 history.record(changed);
 changed.principal='alterado';
 assert.deepEqual(history.undo(),initial);
 const restored=history.redo();
 assert.equal(restored.principal,'#123456');
 restored.fondo='alterado';
 history.undo();
 assert.equal(history.redo().fondo,'#ffffff');
});
test('una captura hexadecimal o arrastre del selector se deshace en un solo paso',()=>{
 const history=create(initial);
 history.record({...initial,principal:'#1'},'hex:principal');
 history.record({...initial,principal:'#123456'},'hex:principal');
 history.endGroup();
 history.record({...initial,principal:'#123456',fondo:'#000000'},'picker:fondo');
 assert.equal(history.undo().principal,'#123456');
 assert.deepEqual(history.undo(),initial);
 assert.equal(history.undo(),null);
});
test('combinaciones completas son atómicas y editar tras deshacer descarta sólo rehacer',()=>{
 const history=create(initial), combo={principal:'#000000',fondo:'#eeeeee',acento:'#cccccc',original:'false'};
 history.record(combo);
 assert.deepEqual(history.undo(),initial);
 assert.deepEqual(history.redo(),combo);
 history.undo();
 history.record({...initial,acento:'#123456'});
 assert.equal(history.canRedo(),false);
 assert.equal(history.redo(),null);
 assert.deepEqual(history.undo(),initial);
});
test('el historial tiene límite y los cambios repetidos no consumen pasos',()=>{
 const history=create({n:0},2);
 for(const n of [1,1,2,3]) history.record({n});
 assert.deepEqual(history.undo(),{n:2});
 assert.deepEqual(history.undo(),{n:1});
 assert.equal(history.canUndo(),false);
 const fresh=create(initial);
 assert.equal(fresh.canUndo(),false);
 assert.equal(fresh.canRedo(),false);
});
