package entity.service;

import entity.dto.UsuarioRequestDTO;
import entity.dto.UsuarioResponseDTO;
import entity.repository.UsuarioRepository;
import entity.usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        usuarios u = new usuarios();
        u.setDni(request.getDni());
        u.setNombre_completo(request.getNombreCompleto());
        u.setGenero(request.getGenero());
        u.setFecha_nacimiento(request.getFechaNacimiento());
        // edad se calculará en PrePersist
        usuarios guardado = usuarioRepository.save(u);
        return toDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtener(Long id) {
        usuarios u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        return toDTO(u);
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        usuarios u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        u.setDni(request.getDni());
        u.setNombre_completo(request.getNombreCompleto());
        u.setGenero(request.getGenero());
        u.setFecha_nacimiento(request.getFechaNacimiento());
        usuarios actualizado = usuarioRepository.save(u);
        return toDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarios u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        usuarioRepository.delete(u);
    }

    private UsuarioResponseDTO toDTO(usuarios u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId_usuario());
        dto.setDni(u.getDni());
        dto.setNombreCompleto(u.getNombre_completo());
        dto.setGenero(u.getGenero());
        dto.setFechaNacimiento(u.getFecha_nacimiento());
        dto.setEdad(u.getEdad());
        return dto;
    }
}
