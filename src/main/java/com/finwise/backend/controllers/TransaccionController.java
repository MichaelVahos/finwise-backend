package com.finwise.backend.controllers;

import com.finwise.backend.dto.*;
import com.finwise.backend.enums.TipoTransaccion;
import com.finwise.backend.models.Transaccion;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.repositories.TransaccionRepository;
import com.finwise.backend.repositories.UsuarioRepository;
import com.finwise.backend.security.JwtUtil;

import com.finwise.backend.services.ReporteExcelService;
import com.finwise.backend.services.ReportePDFService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "http://localhost:4200")
public class TransaccionController {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final ReportePDFService reportePDFService;
    private final ReporteExcelService reporteExcelService;

    public TransaccionController(TransaccionRepository transaccionRepository,
                                 UsuarioRepository usuarioRepository,
                                 JwtUtil jwtUtil, ReportePDFService reportePDFService, ReporteExcelService reporteExcelService) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.reportePDFService = reportePDFService;
        this.reporteExcelService = reporteExcelService;
    }

    /* Crear */
    @PostMapping
    public ResponseEntity<?> crearTransaccion(
            @Valid @RequestBody TransaccionRequest dto,
            HttpServletRequest request) {

        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Transaccion t = new Transaccion();
        t.setFecha(dto.getFecha());
        t.setDescripcion(dto.getDescripcion());
        t.setMonto(dto.getMonto());
        t.setTipo(dto.getTipo());
        t.setCategoria(dto.getCategoria());
        t.setUsuario(usuarioOpt.get());

        Transaccion guardada = transaccionRepository.save(t);
        return ResponseEntity.ok(guardada);
    }

    /** 2. Listar todas */
    @GetMapping
    public ResponseEntity<?> listarTransacciones(HttpServletRequest request) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        List<Transaccion> lista = transaccionRepository.findAllByUsuario(usuarioOpt.get());
        return ResponseEntity.ok(lista);
    }

    /** 3. Filtrar por rango de fechas */
    @GetMapping("/filtro")
    public ResponseEntity<?> filtrarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpServletRequest request) {

        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        List<Transaccion> filtradas =
                transaccionRepository.findAllByUsuarioAndFechaBetween(usuarioOpt.get(), desde, hasta);
        return ResponseEntity.ok(filtradas);
    }

    @GetMapping("/reporte")
    public ResponseEntity<?> reporteMensual(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletRequest request) {

        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        // Rango de fechas
        LocalDate desde = LocalDate.of(anio, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());

        List<Transaccion> transacciones = transaccionRepository
                .findAllByUsuarioAndFechaBetween(usuarioOpt.get(), desde, hasta);

        return ResponseEntity.ok(transacciones);
    }



    @GetMapping("/reporte/resumen")
    public ResponseEntity<?> resumenPorTipo(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletRequest request
    ) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        LocalDate desde = LocalDate.of(anio, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());

        List<Transaccion> transacciones = transaccionRepository
                .findAllByUsuarioAndFechaBetween(usuarioOpt.get(), desde, hasta);

        double ingresos = transacciones.stream()
                .filter(t -> t.getTipo().name().equals("INGRESO"))
                .mapToDouble(Transaccion::getMonto)
                .sum();

        double gastos = transacciones.stream()
                .filter(t -> t.getTipo().name().equals("GASTO"))
                .mapToDouble(Transaccion::getMonto)
                .sum();

        double balance = ingresos - gastos;

        ResumenPorTipoDTO resumen = new ResumenPorTipoDTO();
        resumen.setTotalIngresos(ingresos);
        resumen.setTotalGastos(gastos);
        resumen.setBalance(balance);

        return ResponseEntity.ok(resumen);
    }


    @GetMapping("/reporte/pdf")
    public ResponseEntity<byte[]> generarReportePdf(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletRequest request
    ) throws DocumentException {

        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        // Obtener transacciones del mes
        LocalDate desde = LocalDate.of(anio, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());
        List<Transaccion> transacciones = transaccionRepository
                .findAllByUsuarioAndFechaBetween(usuarioOpt.get(), desde, hasta);

        byte[] pdf = reportePDFService.generarReporteMensual(transacciones);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=reporte_" + mes + "_" + anio + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @GetMapping("/reporte/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletRequest request) throws IOException {

        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        LocalDate desde = LocalDate.of(anio, mes, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());

        List<Transaccion> transacciones = transaccionRepository
                .findAllByUsuarioAndFechaBetween(usuarioOpt.get(), desde, hasta);

        byte[] excel = reporteExcelService.exportarExcel(transacciones);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=transacciones.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }

    @GetMapping("/reporte/categorias")
    public ResponseEntity<?> agruparPorCategoria(HttpServletRequest request) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        List<Transaccion> transacciones = transaccionRepository.findAllByUsuario(usuarioOpt.get());

        // Agrupamos
        Map<String, Double> resumen = transacciones.stream()
                .collect(Collectors.groupingBy(
                        Transaccion::getCategoria,
                        Collectors.summingDouble(Transaccion::getMonto)
                ));

        List<CategoriaResumen> resultado = resumen.entrySet().stream()
                .map(entry -> new CategoriaResumen(entry.getKey(), entry.getValue()))
                .toList();

        return ResponseEntity.ok(resultado);
    }

    /** 4. Actualizar */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTransaccion(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarTransaccionRequest dto,
            HttpServletRequest request) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Optional<Transaccion> txOpt = transaccionRepository.findById(id);
        if (txOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaccion tx = txOpt.get();

        // Validar que la transacción pertenece al usuario
        if (!tx.getUsuario().getId().equals(usuarioOpt.get().getId())) {
            return ResponseEntity.status(403).body("No tienes permiso");
        }

        // Actualizar campos
        tx.setFecha(dto.getFecha());
        tx.setDescripcion(dto.getDescripcion());
        tx.setMonto(dto.getMonto());
        try {
            tx.setTipo(Enum.valueOf(com.finwise.backend.enums.TipoTransaccion.class, dto.getTipo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tipo de transacción inválido (usa 'INGRESO' o 'GASTO')");
        }

        transaccionRepository.save(tx);

        return ResponseEntity.ok("Transacción actualizada");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/prueba")
    public ResponseEntity<String> accesoSoloAdmin() {
        return ResponseEntity.ok("Acceso solo para ADMIN");
    }

    /** 5. Eliminar */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTransaccion(
            @PathVariable Long id,
            HttpServletRequest request) {

        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Optional<Transaccion> txOpt = transaccionRepository.findById(id);
        if (txOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaccion tx = txOpt.get();
        if (!tx.getUsuario().getId().equals(usuarioOpt.get().getId())) {
            return ResponseEntity.status(403).body("No tienes permiso");
        }

        transaccionRepository.delete(tx);
        return ResponseEntity.noContent().build();
    }

    /** Extrae “Bearer xxxxx” → “xxxxx” */
    private String extraerToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }
}