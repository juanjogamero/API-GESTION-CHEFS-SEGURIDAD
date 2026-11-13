import { readFileSync } from "node:fs";
import { runInNewContext } from "node:vm";
import assert from "node:assert/strict";

const html=readFileSync(new URL("../docs/index.html",import.meta.url),"utf8");
const css=readFileSync(new URL("../docs/styles.css",import.meta.url),"utf8");
const source=readFileSync(new URL("../docs/security.js",import.meta.url),"utf8");

for(const id of ["role-selector","is-expired","is-banned","endpoint-search",
  "endpoint-list","endpoint-filter","inspector-status","token-preview"]) {
  assert.ok(html.includes('id="'+id+'"'),"Elemento inexistente: "+id);
}
assert.match(html,/ChefAccess/);
assert.match(css,/grid-template-columns:247px minmax\(0,1fr\)/);
assert.match(css,/@media\(max-width:620px\)/);
assert.doesNotMatch(html,/src="https:\/\/images\./);

const window={};
runInNewContext(source,{window,console});
const lab=window.ChefAccessLab;
assert.ok(lab);
assert.equal(lab.endpoints.length,13);

const find=(method,path)=>lab.endpoints.find(e=>e.method===method&&e.path===path);
const list=find("GET","/chef");
const profile=find("GET","/chef/miPerfil");
const client=find("POST","/servicio/contratar/{chefId}");
const ban=find("PUT","/banear/{actorId}");
const news=find("POST","/noticia");

assert.equal(lab.checkAccess("INVITADO",list).status,200);
assert.equal(lab.checkAccess("INVITADO",profile).status,401);
assert.equal(lab.checkAccess("CHEF",profile).status,200);
assert.equal(lab.checkAccess("CLIENTE",profile).status,403);
assert.equal(lab.checkAccess("CLIENTE",client).status,200);
assert.equal(lab.checkAccess("CHEF",client).status,403);
assert.equal(lab.checkAccess("ADMIN",ban).status,200);
assert.equal(lab.checkAccess("CLIENTE",ban).status,403);
assert.equal(lab.checkAccess("ADMIN",news).status,200);
assert.equal(lab.checkAccess("CHEF",news).status,403);
assert.equal(lab.checkAccess("CLIENTE",client,{expired:true}).status,401);
assert.equal(lab.checkAccess("CLIENTE",client,{banned:true}).status,401);
assert.equal(lab.checkAccess("CLIENTE",list,{expired:true,banned:true}).status,200);
assert.equal(lab.simulatedResponse("CLIENTE",9).status,201);
assert.equal(lab.simulatedResponse("INVITADO",9).status,401);
console.log("ChefAccess: 13 rutas y controles de roles, token y baneo correctos.");
