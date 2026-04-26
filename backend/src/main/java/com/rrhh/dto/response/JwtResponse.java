package com.rrhh.dto.response;

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String rol;
    private Long empleadoId;
    private String nombreCompleto;

    public JwtResponse() {}

    public JwtResponse(String token, Long id, String username, String email, String rol) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.rol = rol;
        this.type = "Bearer";
    }

    // Static builder method
    public static JwtResponseBuilder builder() {
        return new JwtResponseBuilder();
    }

    public static class JwtResponseBuilder {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private String rol;
        private Long empleadoId;
        private String nombreCompleto;

        public JwtResponseBuilder token(String token) { this.token = token; return this; }
        public JwtResponseBuilder type(String type) { this.type = type; return this; }
        public JwtResponseBuilder id(Long id) { this.id = id; return this; }
        public JwtResponseBuilder username(String username) { this.username = username; return this; }
        public JwtResponseBuilder email(String email) { this.email = email; return this; }
        public JwtResponseBuilder rol(String rol) { this.rol = rol; return this; }
        public JwtResponseBuilder empleadoId(Long empleadoId) { this.empleadoId = empleadoId; return this; }
        public JwtResponseBuilder nombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; return this; }

        public JwtResponse build() {
            JwtResponse response = new JwtResponse();
            response.token = this.token;
            response.type = this.type;
            response.id = this.id;
            response.username = this.username;
            response.email = this.email;
            response.rol = this.rol;
            response.empleadoId = this.empleadoId;
            response.nombreCompleto = this.nombreCompleto;
            return response;
        }
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
}
