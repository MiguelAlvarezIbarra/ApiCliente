package com.mx.cbtis.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(GestorController.class)
public class GestorControllerAutomaticTests {

    @Autowired
    private MockMvc mockMvc;

    // ==========================================
    // 4.1 CASOS AUTOMÁTICOS DE ÉXITO
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
    @DisplayName("AUT-E03: Carga exitosa de la página de inicio/dashboard con modelo proyectos")
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

    // ==========================================
    // 4.2 CASOS AUTOMÁTICOS DE FRACASO / CONTROLADOS
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
        // /eliminarProyecto solo acepta POST
        mockMvc.perform(get("/eliminarProyecto"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("AUT-F03: Petición POST sin body JSON requerido retorna 400 Bad Request")
    public void testMissingRequestBody() throws Exception {
        // /actualizarTarea requiere un @RequestBody Tareas
        mockMvc.perform(post("/actualizarTarea")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
