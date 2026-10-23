package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Asignaciones;
import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@WebMvcTest(GestorController.class)
public class GestorControllerAutomaticTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GestorController gestorController;

	@MockBean
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		gestorController.setRestTemplate(restTemplate);
	}

	// ==========================================
	// 1. PRUEBAS DE VISTAS GET Y NAVEGACIÓN
	// ==========================================

	@Test
	@DisplayName("AUT-E01: Carga exitosa de la página principal (Home)")
	public void testHomeViewSuccess() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("home"));
	}

	@Test
	@DisplayName("AUT-E02: Carga exitosa del formulario de registro con modelo usuario")
	public void testRegisterViewSuccess() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("usuario"));
	}

	@Test
	@DisplayName("AUT-E03: Carga exitosa de la página de inicio con modelo proyectos")
	public void testInicioViewSuccess() throws Exception {
		mockMvc.perform(get("/inicio"))
				.andExpect(status().isOk())
				.andExpect(view().name("inicio"))
				.andExpect(model().attributeExists("proyectos"));
	}

	@Test
	@DisplayName("AUT-E04: Carga exitosa de la vista de tutorial")
	public void testTutorialViewSuccess() throws Exception {
		mockMvc.perform(get("/tutorial"))
				.andExpect(status().isOk())
				.andExpect(view().name("tutorial"));
	}

	@Test
	@DisplayName("AUT-E05: Carga exitosa de la vista manual y login")
	public void testManualAndLoginViews() throws Exception {
		mockMvc.perform(get("/manual"))
				.andExpect(status().isOk())
				.andExpect(view().name("manual"));

		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	@DisplayName("AUT-E06: Carga exitosa de /proyecto y /tarea con atributos de modelo")
	public void testProyectoAndTareaViews() {
		Usuario u = new Usuario(1, "Miguel", "miguel@test.com", "1234", "ADMIN", "img.png");
		when(restTemplate.postForObject(anyString(), any(), eq(Usuario[].class)))
				.thenReturn(new Usuario[] { u });

		Model modelProyecto = new ConcurrentModel();
		String viewProyecto = gestorController.getProyecto(modelProyecto);
		assertEquals("proyecto", viewProyecto);
		assertNotNull(modelProyecto.getAttribute("tarea"));
		assertNotNull(modelProyecto.getAttribute("comentarios"));
		List<?> listaUsuarios = (List<?>) modelProyecto.getAttribute("listaUsuarios");
		assertEquals(1, listaUsuarios.size());

		Model modelTarea = new ConcurrentModel();
		String viewTarea = gestorController.getTarea(modelTarea);
		assertEquals("proyecto", viewTarea);
		assertNotNull(modelTarea.getAttribute("tareas"));
	}

	// ==========================================
	// 2. PRUEBAS DE OPERACIONES POST / RESTTEMPLATE
	// ==========================================

	@Test
	@DisplayName("AUT-E07: Registro y verificación de login de usuario")
	public void testSaveAndVerificarUsuario() {
		Usuario nuevo = new Usuario(1, "Ana", "ana@test.com", "secret", "USER", "avatar.png");
		when(restTemplate.postForEntity(anyString(), any(Usuario.class), eq(Integer.class)))
				.thenReturn(ResponseEntity.ok(1));
		when(restTemplate.postForEntity(anyString(), any(Usuario.class), eq(Usuario.class)))
				.thenReturn(ResponseEntity.ok(nuevo));

		ModelAndView mav = gestorController.setSaveUsuario(nuevo);
		assertEquals("redirect:/login", mav.getViewName());

		ResponseEntity<Usuario> loginResp = gestorController.verificarUsuario(nuevo);
		assertEquals(HttpStatus.OK, loginResp.getStatusCode());
		assertEquals("Ana", loginResp.getBody().getNombre());

		when(restTemplate.postForObject(anyString(), any(), eq(Usuario[].class)))
				.thenReturn(new Usuario[] { nuevo });
		Model model = new ConcurrentModel();
		ResponseEntity<Object> usuariosResp = gestorController.consultarUsuarios(model);
		assertEquals(HttpStatus.OK, usuariosResp.getStatusCode());
		assertNotNull(model.getAttribute("listaUsuarios"));
	}

	@Test
	@DisplayName("AUT-E08: CRUD completo de proyectos en controlador cliente")
	public void testProyectosOperations() {
		Proyectos p = new Proyectos(10, "ERP", "Sistema ERP", "2026-01-01", "2026-12-31", "ACTIVO", "azul");
		when(restTemplate.postForEntity(anyString(), any(Proyectos.class), eq(Integer.class)))
				.thenReturn(ResponseEntity.ok(1));
		when(restTemplate.postForObject(anyString(), any(), eq(Proyectos[].class)))
				.thenReturn(new Proyectos[] { p });
		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
				.thenReturn(ResponseEntity.ok(1));
		when(restTemplate.getForObject(anyString(), eq(Proyectos.class), anyMap()))
				.thenReturn(p);

		ModelAndView saveMav = gestorController.setSaveProyecto(p);
		assertEquals("redirect:/inicio", saveMav.getViewName());

		Model model = new ConcurrentModel();
		ResponseEntity<Object> consultResp = gestorController.consultarProyectos(model);
		assertEquals(HttpStatus.OK, consultResp.getStatusCode());
		assertNotNull(model.getAttribute("listaProyectos"));

		ModelAndView deleteMav = gestorController.eliminarProyecto(p);
		assertEquals("redirect:/inicio", deleteMav.getViewName());

		ResponseEntity<Object> updateResp = gestorController.updateProyecto(p);
		assertEquals(HttpStatus.OK, updateResp.getStatusCode());
		Proyectos returned = (Proyectos) updateResp.getBody();
		assertEquals(10, returned.getId_proyecto());
	}

	@Test
	@DisplayName("AUT-E09: CRUD completo de tareas y comentarios en controlador cliente")
	public void testTareasAndComentariosOperations() {
		Tareas t = new Tareas(5, "Backend", "Crear API", "2026-11-01", "ToDo", "PENDIENTE", 10);
		Comentarios c = new Comentarios(3, "1", 5, "Revisado", "2026-10-20 10:00");

		when(restTemplate.postForObject(anyString(), any(), eq(Tareas[].class)))
				.thenReturn(new Tareas[] { t });
		when(restTemplate.postForObject(anyString(), any(), eq(Comentarios[].class)))
				.thenReturn(new Comentarios[] { c });
		when(restTemplate.postForEntity(anyString(), any(Tareas.class), eq(Integer.class)))
				.thenReturn(ResponseEntity.ok(1));
		when(restTemplate.postForEntity(anyString(), any(Comentarios.class), eq(Integer.class)))
				.thenReturn(ResponseEntity.ok(1));
		when(restTemplate.getForObject(anyString(), eq(Tareas.class), anyMap()))
				.thenReturn(t);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
				.thenReturn(ResponseEntity.ok(1));

		Model model = new ConcurrentModel();
		ResponseEntity<List<Tareas>> obtenerTareasResp = gestorController.obtenerTareas(t, model);
		assertEquals(HttpStatus.OK, obtenerTareasResp.getStatusCode());
		assertEquals(1, obtenerTareasResp.getBody().size());

		ResponseEntity<List<Comentarios>> obtenerComentariosResp = gestorController.obtenerComentarios(c, model);
		assertEquals(HttpStatus.OK, obtenerComentariosResp.getStatusCode());
		assertEquals(1, obtenerComentariosResp.getBody().size());

		ModelAndView saveTareaMav = gestorController.setSaveTareas(t);
		assertEquals("redirect:/proyecto", saveTareaMav.getViewName());
		assertEquals(10, saveTareaMav.getModel().get("id_proyecto"));

		String saveComView = gestorController.setSaveComentario(c);
		assertEquals("proyecto", saveComView);

		ResponseEntity<Object> updateTareaResp = gestorController.updateTarea(t);
		assertEquals(HttpStatus.OK, updateTareaResp.getStatusCode());
		assertEquals(5, ((Tareas) updateTareaResp.getBody()).getId_tarea());

		ModelAndView elimTareaMav = gestorController.eliminarTarea(t);
		assertEquals("redirect:/proyecto", elimTareaMav.getViewName());

		ResponseEntity<Integer> verifResp = gestorController.verificarTarea(t);
		assertEquals(1, verifResp.getBody());

		ResponseEntity<Integer> cambiosResp = gestorController.cambiosTareas(t);
		assertEquals(1, cambiosResp.getBody());

		ResponseEntity<Object> consTareasResp = gestorController.consultarTareas();
		assertEquals(HttpStatus.OK, consTareasResp.getStatusCode());

		ResponseEntity<Object> consComResp = gestorController.consultarComentarios();
		assertEquals(HttpStatus.OK, consComResp.getStatusCode());
		verify(restTemplate).getForObject(anyString(), eq(Tareas.class), anyMap());
	}

	// ==========================================
	// 3. PRUEBAS DE MODELOS (POJOs 100% MÉTODOS)
	// ==========================================

	@Test
	@DisplayName("AUT-E10: Cobertura completa de constructores, getters y setters de modelos")
	public void testAllModelClasses() {
		Asignaciones asig = new Asignaciones();
		asig.setId_asignacion(1);
		asig.setId_usuario(2);
		asig.setId_tarea(3);
		asig.setFecha_asignacion("2026-10-20");
		assertEquals(1, asig.getId_asignacion());
		assertEquals(2, asig.getId_usuario());
		assertEquals(3, asig.getId_tarea());
		assertEquals("2026-10-20", asig.getFecha_asignacion());

		Asignaciones asig2 = new Asignaciones(10, 20, 30, "2026-10-21");
		assertEquals(10, asig2.getId_asignacion());

		Comentarios com = new Comentarios();
		com.setId_comentario(1);
		com.setId_usuario("usr1");
		com.setId_tarea(5);
		com.setTexto_comentario("Hola");
		com.setFecha_hora("12:00");
		assertEquals(1, com.getId_comentario());
		assertEquals("usr1", com.getId_usuario());
		assertEquals(5, com.getId_tarea());
		assertEquals("Hola", com.getTexto_comentario());
		assertEquals("12:00", com.getFecha_hora());

		Comentarios com2 = new Comentarios(2, "usr2", 6, "Texto", "13:00");
		assertEquals(2, com2.getId_comentario());

		Proyectos proy = new Proyectos();
		proy.setId_proyecto(1);
		proy.setNombre("P1");
		proy.setDescripcion("Desc");
		proy.setFecha_inicio("2026-01-01");
		proy.setFecha_finalizacion("2026-06-01");
		proy.setEstado_proyecto("ACTIVO");
		proy.setFondo("bg1");
		assertEquals(1, proy.getId_proyecto());
		assertEquals("P1", proy.getNombre());
		assertEquals("Desc", proy.getDescripcion());
		assertEquals("2026-01-01", proy.getFecha_inicio());
		assertEquals("2026-06-01", proy.getFecha_finalizacion());
		assertEquals("ACTIVO", proy.getEstado_proyecto());
		assertEquals("bg1", proy.getFondo());

		Proyectos proy2 = new Proyectos(2, "P2", "D2", "2026-02-01", "2026-07-01", "CERRADO", "bg2");
		assertEquals("P2", proy2.getNombre());

		Tareas tar = new Tareas();
		tar.setId_tarea(1);
		tar.setNombre("T1");
		tar.setDescripcion("TD1");
		tar.setFecha_vencimiento("2026-05-01");
		tar.setLista("Doing");
		tar.setEstado("EN_PROCESO");
		tar.setId_proyecto(9);
		assertEquals(1, tar.getId_tarea());
		assertEquals("T1", tar.getNombre());
		assertEquals("TD1", tar.getDescripcion());
		assertEquals("2026-05-01", tar.getFecha_vencimiento());
		assertEquals("Doing", tar.getLista());
		assertEquals("EN_PROCESO", tar.getEstado());
		assertEquals(9, tar.getId_proyecto());

		Tareas tar2 = new Tareas(2, "T2", "TD2", "2026-05-02", "Done", "COMPLETADA", 10);
		assertEquals("T2", tar2.getNombre());

		Usuario usr = new Usuario();
		usr.setId_usuario(1);
		usr.setNombre("Juan");
		usr.setCorreo_electronico("juan@mail.com");
		usr.setContrasena("pwd");
		usr.setRol("DEV");
		usr.setImagen_usuario("pic.jpg");
		assertEquals(1, usr.getId_usuario());
		assertEquals("Juan", usr.getNombre());
		assertEquals("juan@mail.com", usr.getCorreo_electronico());
		assertEquals("pwd", usr.getContrasena());
		assertEquals("DEV", usr.getRol());
		assertEquals("pic.jpg", usr.getImagen_usuario());

		Usuario usr2 = new Usuario(2, "Maria", "maria@mail.com", "pwd2", "QA", "pic2.jpg");
		assertEquals("Maria", usr2.getNombre());
	}

	// ==========================================
	// 4. CASOS DE FRACASO / CONTROLADOS HTTP
	// ==========================================

	@Test
	@DisplayName("AUT-F01: Petición a recurso no existente retorna 404 Not Found")
	public void testEndpointNotFound() throws Exception {
		mockMvc.perform(get("/recursoInexistente"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("AUT-F02: Método HTTP incorrecto retorna 405 Method Not Allowed")
	public void testMethodNotAllowed() throws Exception {
		mockMvc.perform(get("/eliminarProyecto"))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	@DisplayName("AUT-F03: Petición POST sin body JSON requerido retorna 400 Bad Request")
	public void testMissingRequestBody() throws Exception {
		mockMvc.perform(post("/actualizarTarea")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
}
