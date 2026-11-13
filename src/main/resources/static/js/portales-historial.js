/* Historial local de una edición. Nunca guarda ni publica en el servidor. */
((root) => {
 'use strict';
 function create(initial, limit = 100) {
  const copy = value => JSON.parse(JSON.stringify(value));
  const states = [copy(initial)];
  let cursor = 0, lastGroup = null;
  return {
   record(value, group = null) {
    if (JSON.stringify(value) === JSON.stringify(states[cursor])) return;
    states.splice(cursor + 1);
    if (group !== null && group === lastGroup && cursor > 0) states[cursor] = copy(value);
    else { states.push(copy(value)); cursor++; }
    if (states.length > limit + 1) { states.shift(); cursor--; }
    lastGroup = group;
   },
   endGroup() { lastGroup = null; },
   canUndo() { return cursor > 0; },
   canRedo() { return cursor < states.length - 1; },
   undo() { lastGroup = null; return cursor > 0 ? copy(states[--cursor]) : null; },
   redo() { lastGroup = null; return cursor < states.length - 1 ? copy(states[++cursor]) : null; }
  };
 }
 const api = {create};
 if (typeof module !== 'undefined' && module.exports) module.exports = api;
 else root.NexoPortalHistorial = api;
})(typeof window === 'undefined' ? globalThis : window);
