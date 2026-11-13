/* ChefAccess · simulador educativo (sin autenticación ni red).
   Representa reglas del backend; no verifica tokens ni realiza peticiones reales. */
(() => {
  "use strict";
  const endpoints = Object.freeze([
    { method:"GET",path:"/chef", title:"Consultar chefs disponibles",role:"PUBLIC",desc:"Listado de chefs activos, sin datos privados." },
    { method:"GET",path:"/chef/{id}",title:"Consultar perfil público",role:"PUBLIC",desc:"Ficha pública de un chef activo." },
    { method:"POST",path:"/chef",title:"Registro de chef",role:"PUBLIC",desc:"Registro público con contraseña protegida mediante BCrypt." },
    { method:"POST",path:"/cliente",title:"Registro de cliente",role:"PUBLIC",desc:"Registro público de clientes. El rol lo asigna el servidor." },
    { method:"POST",path:"/login",title:"Iniciar sesión",role:"PUBLIC",desc:"Comprobar credenciales y emitir un JWT firmado." },
    { method:"GET",path:"/noticia",title:"Consultar noticias",role:"PUBLIC",desc:"Noticias públicas del portal." },
    { method:"GET",path:"/chef/miPerfil",title:"Perfil privado del chef",role:"CHEF",desc:"Solo el chef autenticado puede consultar su perfil." },
    { method:"PUT",path:"/chef",title:"Actualizar perfil de chef",role:"CHEF",desc:"Modificación de los datos del chef autenticado." },
    { method:"GET",path:"/cliente/miPerfil",title:"Perfil del cliente",role:"CLIENTE",desc:"Consulta privada del cliente autenticado." },
    { method:"POST",path:"/servicio/contratar/{chefId}",title:"Contratar un chef",role:"CLIENTE",desc:"Reserva para cliente con fecha futura y chef disponible." },
    { method:"PUT",path:"/servicio/puntuar/{servicioId}/{puntuacion}",title:"Valorar servicio propio",role:"CLIENTE",desc:"Valorar una vez el servicio propio ya realizado." },
    { method:"POST",path:"/noticia",title:"Publicar noticia",role:"ADMIN",desc:"La publicación de noticias requiere permisos administrativos." },
    { method:"PUT",path:"/banear/{actorId}",title:"Bloquear cuenta",role:"ADMIN",desc:"El administrador puede suspender una cuenta de cliente o chef." }
  ]);

  function checkAccess(role,endpoint,flags={}) {
    const route=typeof endpoint==="number" ? endpoints[endpoint] : endpoint;
    if(!route) return {status:404,reason:"NOT_FOUND",allowed:false};
    if(route.role==="PUBLIC") return {status:200,reason:"OK",allowed:true};
    if(role==="INVITADO"||flags.expired||flags.banned)
      return {status:401,reason:"UNAUTHORIZED",allowed:false};
    if(route.role!==role) return {status:403,reason:"FORBIDDEN",allowed:false};
    return {status:200,reason:"OK",allowed:true};
  }

  function simulatedResponse(role,index,flags={}) {
    const route=endpoints[index];if(!route) return null;
    const result=checkAccess(role,route,flags);
    if(!result.allowed) return {
      status:result.status, error:result.reason,
      message:result.status===401?"Autenticación requerida o cuenta inactiva":"Este rol no tiene permiso para realizar la operación",
      endpoint:route.path
    };
    return route.method==="POST" ? {
      status:201, message:"Operación permitida (simulación)", recurso:route.title, servidor:"No conectado"
    } : {
      status:200, message:"Operación permitida (simulación)", recurso:route.title, servidor:"No conectado"
    };
  }

  const api=Object.freeze({endpoints,checkAccess,simulatedResponse});
  if(typeof window!=="undefined") window.ChefAccessLab=api;
  if(typeof document==="undefined") return;

  const state={role:"CLIENTE",expired:false,banned:false,selected:0,search:"",filter:"all"};
  const $=id=>document.getElementById(id);
  const esc=s=>String(s).replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
  const secureText=(id,value)=>{const node=$(id);if(node)node.textContent=String(value)};

  function statusLabel(endpoint) {
    const result=checkAccess(state.role,endpoint,state);
    return result.allowed ? "Permitido" : result.status===401 ? "Sin sesión" : "Restringido";
  }

  function renderList() {
    const query=state.search.trim().toLowerCase();
    const list=endpoints.map((endpoint,id)=>({...endpoint,id})).filter(endpoint=>{
      const matched=(endpoint.path+" "+endpoint.title+" "+endpoint.method).toLowerCase().includes(query);
      const permission=checkAccess(state.role,endpoint,state);
      return matched&&(state.filter==="all"||(state.filter==="allowed"&&permission.allowed)
        ||(state.filter==="denied"&&!permission.allowed));
    });
    secureText("endpoint-count",list.length+" "+(list.length===1?"ruta":"rutas"));
    $("endpoint-list").innerHTML=list.length ? list.map(ep=>{
      const allowed=checkAccess(state.role,ep,state).allowed;
      return '<button type="button" role="option" aria-selected="'+(state.selected===ep.id)+
        '" class="endpoint-item '+(state.selected===ep.id?"active":"")+'" data-endpoint="'+ep.id+'">'+
        '<span class="endpoint-main"><span class="http-method method-'+ep.method.toLowerCase()+'">'+ep.method+'</span>'+
        '<span><code>'+esc(ep.path)+'</code><small>'+esc(ep.title)+'</small></span></span>'+
        '<span class="permission '+(allowed?"allowed":"denied")+'">'+
        (allowed?"●":"○")+" "+statusLabel(ep)+'</span></button>';
    }).join(""):'<div class="empty-filter">No hay endpoints que coincidan con este filtro.</div>';
  }

  function renderInspector(){
    const endpoint=endpoints[state.selected];
    const result=checkAccess(state.role,endpoint,state);
    const response=simulatedResponse(state.role,state.selected,state);
    secureText("inspector-role","ROL: "+state.role);
    secureText("inspector-method",endpoint.method);
    $("inspector-method").className="method-badge method-"+endpoint.method.toLowerCase();
    secureText("inspector-path",endpoint.path);
    secureText("inspector-description",endpoint.desc);
    secureText("inspector-status",result.allowed?
      ((endpoint.method==="POST"?"201 CREATED":"200 OK")):
      result.status+" "+result.reason);
    $("inspector-status").className="status-code "+(result.allowed?"ok":"bad");
    secureText("inspector-response",JSON.stringify(response,null,2));
  }

  function renderToken(){
    const payload={
      typ:"JWT — ESTRUCTURA DE EJEMPLO",
      sub:state.role==="INVITADO"?"(sin sesión)":"usuario_ejemplo",
      rol:state.role==="INVITADO"?null:state.role,
      algoritmo:"HS256",
      caducado:state.expired,
      cuentaBloqueada:state.banned,
      nota:"No es un token real, no incluye firma ni secreto."
    };
    secureText("token-preview",JSON.stringify(payload,null,2));
  }

  function render(){
    document.querySelectorAll("[data-role]").forEach(el=>{
      const selected=el.dataset.role===state.role;
      el.classList.toggle("is-current",selected);
      el.setAttribute("aria-pressed",String(selected));
    });
    renderList();renderInspector();renderToken();
  }

  document.querySelectorAll("[data-role]").forEach(button=>{
    button.addEventListener("click",()=>{state.role=button.dataset.role;render();});
  });
  $("is-expired").addEventListener("change",event=>{state.expired=event.target.checked;render();});
  $("is-banned").addEventListener("change",event=>{state.banned=event.target.checked;render();});
  $("endpoint-search").addEventListener("input",event=>{state.search=event.target.value;renderList();});
  $("endpoint-filter").addEventListener("change",event=>{state.filter=event.target.value;renderList();});
  $("endpoint-list").addEventListener("click",event=>{
    const button=event.target.closest("[data-endpoint]");
    if(!button)return;
    state.selected=Number(button.dataset.endpoint);
    renderList();renderInspector();
  });
  render();
})();
