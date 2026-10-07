const assert = require('node:assert/strict');
const {parseMoney, compareDecimals, formatMoney} = require('../../main/resources/static/js/money-input.js');
for (const [value, expected] of [
    ['400','400.00'], ['1400.50','1400.50'], ['$1,400.50','1400.50'],
    ['400,50','400.50'], ['.5','0.50'], ['0','0.00'], ['USD $1,400.50','1400.50'],
    ['$1,400','1400.00'], ['000400.5','400.50']
]) { const parsed = parseMoney(value); assert.equal(parsed.error, ''); assert.equal(parsed.raw, expected); }
for (const value of ['1,400','1.400','1.234,50','-100','1e3','texto','400.001','1,23,400','1 400','999999999999999999'])
    assert.ok(parseMoney(value).error, value);
assert.deepEqual(parseMoney(''), {raw:'',error:''});
assert.equal(parseMoney('150.0001',4).raw, '150.0001');
assert.equal(formatMoney('400.00'), '$400.00');
assert.equal(formatMoney('1400.50'), '$1,400.50');
assert.equal(formatMoney('99999999999999999.99'), '$99,999,999,999,999,999.99');
assert.equal(compareDecimals('100.00','99.9999'), 1);
assert.equal(compareDecimals('99999999999999999.98','99999999999999999.99'), -1);
assert.equal(compareDecimals('100','100.0000'), 0);
console.log('Moneda: decimal limpio, formato, precisión exacta, pegado y rechazo de ambigüedad correctos.');
