package com.mx.cbtis.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@Controller
public class GestorController {

	private RestTemplate template;

	@Value("${api.gestor.url:https://localhost:8081}")
	private String baseUrl = "https://localhost:8081";

	public GestorController() {
		this.template = new RestTemplate();
	}

	@Autowired(required = false)
	public void setRestTemplate(RestTemplate template) {
		this.template = template;
	}

	@GetMapping("/")
	public String getHome() {
		return "home";
	}

	@GetMapping("/tutorial")
	public String getTutorial() {
		return "tutorial";
	}

	@GetMapping("/manual")
	public String getManual() {
		return "manual";
	}

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}

	@GetMapping("/register")
	public String getRegister(Model modelo) {
		Usuario usuario = new Usuario();
		modelo.addAttribute("usuario", usuario);
		return "register";
	}

	@PostMapping("/saveUsuario")
	public ModelAndView setSaveUsuario(@ModelAttribute Usuario usuario) {
		String urlservicebd = baseUrl + "/addUsuario";
		template.postForEntity(urlservicebd, usuario, Integer.class);
		return new ModelAndView("redirect:/login");
	}

	@GetMapping("/inicio")
	public String getInicio(Model modelo) {
		Proyectos proyectos = new Proyectos();
		modelo.addAttribute("proyectos", proyectos);
		return "inicio";
	}

	@PostMapping("/saveProyecto")
	public ModelAndView setSaveProyecto(@ModelAttribute Proyectos proyecto) {
		String urlservicebd = baseUrl + "/addProyecto";
		template.postForEntity(urlservicebd, proyecto, Integer.class);
		return new ModelAndView("redirect:/inicio");
	}

	@PostMapping("/consultarProyectos")
	public ResponseEntity<Object> consultarProyectos(Model modelo) {
		String urlservicebd = baseUrl + "/buscarProyectos";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Proyectos[] response = template.postForObject(urlservicebd, headers, Proyectos[].class);
		List<Proyectos> proyectos = Arrays.asList(response);
		modelo.addAttribute("listaProyectos", proyectos);
		return new ResponseEntity<Object>(proyectos, HttpStatusCode.valueOf(200));
	}

	@PostMapping("/eliminarProyecto")
	public ModelAndView eliminarProyecto(@RequestBody Proyectos proyecto) {
		String urlservicebd = baseUrl + "/deleteProyecto";
		HttpEntity<Proyectos> request = new HttpEntity<Proyectos>(proyecto);
		template.exchange(urlservicebd, HttpMethod.DELETE, request, Integer.class);
		return new ModelAndView("redirect:/inicio");
	}

	@PostMapping("/actualizarProyecto")
	public ResponseEntity<Object> updateProyecto(@RequestBody Proyectos proyecto) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("id_proyecto", proyecto.getId_proyecto());
		String urlservicebd = baseUrl + "/buscarProyecto?id_proyecto={id_proyecto}";
		Proyectos proyectos = template.getForObject(urlservicebd, Proyectos.class, params);
		return new ResponseEntity<Object>(proyectos, HttpStatusCode.valueOf(200));
	}

	@PostMapping("/verificarLogin")
	public ResponseEntity<Usuario> verificarUsuario(@RequestBody Usuario paramUser) {
		String urlservicebd = baseUrl + "/getUsuarioLogin";
		return template.postForEntity(urlservicebd, paramUser, Usuario.class);
	}

	@PostMapping("/consultarUsuarios")
	public ResponseEntity<Object> consultarUsuarios(Model modelo) {
		String urlservicebd = baseUrl + "/buscarUsuarios";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Usuario[] response = template.postForObject(urlservicebd, headers, Usuario[].class);
		List<Usuario> usuarios = Arrays.asList(response);
		modelo.addAttribute("listaUsuarios", usuarios);
		return new ResponseEntity<Object>(usuarios, HttpStatusCode.valueOf(200));
	}

	@PostMapping("/obtenerTareas")
	public ResponseEntity<List<Tareas>> obtenerTareas(@RequestBody Tareas idProyecto, Model modelo) {
		String urlservicebd = baseUrl + "/getTarea";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Tareas[] response = template.postForObject(urlservicebd, idProyecto, Tareas[].class);
		List<Tareas> listaTareas = Arrays.asList(response);
		modelo.addAttribute("tareas", listaTareas);
		return ResponseEntity.ok(listaTareas);
	}

	@PostMapping("/obtenerComentarios")
	public ResponseEntity<List<Comentarios>> obtenerComentarios(@RequestBody Comentarios idComentario, Model modelo) {
		String urlservicebd = baseUrl + "/getComentario";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Comentarios[] response = template.postForObject(urlservicebd, idComentario, Comentarios[].class);
		List<Comentarios> listaComentarios = Arrays.asList(response);
		modelo.addAttribute("comentarios", listaComentarios);
		return ResponseEntity.ok(listaComentarios);
	}

	@GetMapping("/proyecto")
	public String getProyecto(Model modelo) {
		Tareas tarea = new Tareas();
		modelo.addAttribute("tarea", tarea);

		Comentarios comentario = new Comentarios();
		modelo.addAttribute("comentarios", comentario);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String urlservicebdUsuario = baseUrl + "/buscarUsuarios";
		Usuario[] responseUsuario = template.postForObject(urlservicebdUsuario, headers, Usuario[].class);
		List<Usuario> usuarios = Arrays.asList(responseUsuario);
		modelo.addAttribute("listaUsuarios", usuarios);

		return "proyecto";
	}

	@GetMapping("/tarea")
	public String getTarea(Model modelo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Tareas tareas = new Tareas();
		modelo.addAttribute("tareas", tareas);
		return "proyecto";
	}

	@PostMapping("/saveTarea")
	public ModelAndView setSaveTareas(@ModelAttribute Tareas tareas) {
		String urlservicebd = baseUrl + "/addTareas";
		template.postForEntity(urlservicebd, tareas, Integer.class);
		ModelAndView mav = new ModelAndView("redirect:/proyecto");
		mav.addObject("id_proyecto", tareas.getId_proyecto());
		return mav;
	}

	@PostMapping("/saveComentario")
	public String setSaveComentario(@RequestBody Comentarios comentarios) {
		String urlservicebd = baseUrl + "/addComentario";
		template.postForEntity(urlservicebd, comentarios, Integer.class);
		return "proyecto";
	}

	@PostMapping("/actualizarTarea")
	public ResponseEntity<Object> updateTarea(@RequestBody Tareas tarea) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("id_tarea", tarea.getId_tarea());
		String urlservicebd = baseUrl + "/buscarTarea?id_tarea={id_tarea}";
		Tareas tareas = template.getForObject(urlservicebd, Tareas.class, params);
		return new ResponseEntity<Object>(tareas, HttpStatusCode.valueOf(200));
	}

	@PostMapping("/eliminarTarea")
	public ModelAndView eliminarTarea(@RequestBody Tareas tareas) {
		String urlservicebd = baseUrl + "/deleteTareas";
		HttpEntity<Tareas> request = new HttpEntity<Tareas>(tareas);
		template.exchange(urlservicebd, HttpMethod.DELETE, request, Integer.class);
		return new ModelAndView("redirect:/proyecto");
	}

	@PostMapping("/verificarTarea")
	public ResponseEntity<Integer> verificarTarea(@RequestBody Tareas paramName) {
		String urlservicebd = baseUrl + "/addTareas";
		return template.postForEntity(urlservicebd, paramName, Integer.class);
	}

	@PostMapping("/cambiosTareas")
	public ResponseEntity<Integer> cambiosTareas(@RequestBody Tareas paramDatos) {
		String urlservicebd = baseUrl + "/addTareas";
		return template.postForEntity(urlservicebd, paramDatos, Integer.class);
	}

	@PostMapping("/consultarTareas")
	public ResponseEntity<Object> consultarTareas() {
		String urlservicebd = baseUrl + "/buscarTareas";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Tareas[] response = template.postForObject(urlservicebd, headers, Tareas[].class);
		List<Tareas> tareas = Arrays.asList(response);
		return new ResponseEntity<Object>(tareas, HttpStatusCode.valueOf(200));
	}

	@PostMapping("/consultarComentarios")
	public ResponseEntity<Object> consultarComentarios() {
		String urlservicebd = baseUrl + "/buscarComentarios";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Comentarios[] response = template.postForObject(urlservicebd, headers, Comentarios[].class);
		List<Comentarios> comentarios = Arrays.asList(response);
		return new ResponseEntity<Object>(comentarios, HttpStatusCode.valueOf(200));
	}
}
