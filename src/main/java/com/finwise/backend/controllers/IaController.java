package com.finwise.backend.controllers;

import com.finwise.backend.models.Sugerencia;
import com.finwise.backend.models.Transaccion;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.repositories.SugerenciaRepository;
import com.finwise.backend.repositories.TransaccionRepository;
import com.finwise.backend.repositories.UsuarioRepository;
import com.finwise.backend.security.JwtUtil;
import com.finwise.backend.services.GeminiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "http://localhost:4200")
public class IaController {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;
    private final GeminiService geminiService;
    private final SugerenciaRepository sugerenciaRepository;

    public IaController(JwtUtil jwtUtil,
                        UsuarioRepository usuarioRepository,
                        TransaccionRepository transaccionRepository,
                        GeminiService geminiService, SugerenciaRepository sugerenciaRepository) {
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.transaccionRepository = transaccionRepository;
        this.geminiService = geminiService;
        this.sugerenciaRepository = sugerenciaRepository;
    }

    @GetMapping("/sugerencia")
    public ResponseEntity<?> obtenerSugerencia(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletRequest request
    ) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        LocalDate desde = LocalDate.of(anio, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());

        List<Transaccion> transacciones = transaccionRepository
                .findAllByUsuarioAndFechaBetween(usuario, desde, hasta);

        String resumen = transacciones.stream()
                .collect(Collectors.groupingBy(
                        Transaccion::getCategoria,
                        Collectors.summingDouble(Transaccion::getMonto)
                ))
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " EUR")
                .collect(Collectors.joining(", "));

        String prompt = "Estos son los gastos de un usuario durante el mes: " + resumen +
                ". ¿Qué sugerencias financieras simples le darías para ahorrar o controlar mejor su presupuesto? Sé claro y directo.";

        String respuesta = geminiService.generarSugerencia(prompt);

        // Guardar sugerencia en base de datos
        Sugerencia sugerencia = new Sugerencia();
        sugerencia.setUsuario(usuario);
        sugerencia.setMes(mes);
        sugerencia.setAnio(anio);
        sugerencia.setContenido(respuesta);
        sugerencia.setFechaGeneracion(LocalDate.now().atStartOfDay());
        sugerenciaRepository.save(sugerencia);

        return ResponseEntity.ok(respuesta);
    }


    private String extraerToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumenNatural(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletRequest request
    ) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        LocalDate desde = LocalDate.of(anio, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());

        List<Transaccion> transacciones = transaccionRepository
                .findAllByUsuarioAndFechaBetween(usuario, desde, hasta);

        double totalGastos = transacciones.stream()
                .filter(t -> t.getTipo().name().equals("GASTO"))
                .mapToDouble(Transaccion::getMonto)
                .sum();

        double totalIngresos = transacciones.stream()
                .filter(t -> t.getTipo().name().equals("INGRESO"))
                .mapToDouble(Transaccion::getMonto)
                .sum();

        String prompt = "Genera un resumen financiero amigable para un usuario. En el mes " + mes + "/" + anio +
                " tuvo ingresos por " + totalIngresos + " euros y gastos por " + totalGastos +
                " euros. Describe brevemente si el balance fue positivo o negativo, y ofrece una conclusión general en estilo informal y comprensible.";

        String resumen = geminiService.generarSugerencia(prompt);

        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(HttpServletRequest request) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        List<Sugerencia> historial = sugerenciaRepository.findAllByUsuarioOrderByFechaGeneracionDesc(usuario);

        return ResponseEntity.ok(historial);
    }

}
