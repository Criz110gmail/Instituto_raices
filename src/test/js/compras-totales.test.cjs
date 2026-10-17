const {test}=require('node:test'), assert=require('node:assert/strict');
const {calcularPartidaCompra:c}=require('../../main/resources/static/js/compras.js');
test('compra de prueba: subtotal 350, impuesto 56 y total 406',()=>{
 const a=c('2','100','16'),b=c('3','50','16');
 assert.equal(a.subtotal+b.subtotal,35000n);assert.equal(a.impuesto+b.impuesto,5600n);assert.equal(a.total+b.total,40600n);
});
test('decimales y redondeo por partida coinciden con HALF_UP de Java',()=>{
 assert.deepEqual(c('1.005','1.00','16'),{subtotal:101n,impuesto:16n,total:117n});
 assert.deepEqual(c('0.125','20.00','16'),{subtotal:250n,impuesto:40n,total:290n});
 assert.deepEqual(c('.5','20.00','.5'),{subtotal:1000n,impuesto:5n,total:1005n});
});
